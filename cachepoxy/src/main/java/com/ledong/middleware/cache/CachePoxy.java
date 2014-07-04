package com.ledong.middleware.cache;

import javassist.NotFoundException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.ledong.middleware.cache.annotation.LeCache;
import com.ledong.middleware.cache.container.CacheFactory;
import com.ledong.middleware.cache.logger.CacheLogger.Logger;
import com.ledong.middleware.cache.logger.CacheLoggerFactory;

/**
 * 缓存代理
 * 
 * @author liaoyong
 * 
 */
@Component
@Aspect
public class CachePoxy {
	protected static final Logger logger = CacheLoggerFactory
			.getLogger(CachePoxy.class);

	@Pointcut("@annotation(com.ledong.middleware.cache.annotation.LeCache)")
	public void cacheMethod() {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Around("cacheMethod()")
	public Object methodCachePoxy(ProceedingJoinPoint point)
			throws SecurityException, NoSuchMethodException, NotFoundException {
		logger.info("poxyMethod:" + point.getSignature().getName());
		LeCache leCache = ParamProcessClass.getLeCacheAnn(point.getTarget()
				.getClass(), point.getSignature().getName());
		Object obj = null;
		if (leCache == null) {
			try {
				obj = point.proceed();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} else {
			try {
				String[] method_paramName = ParamProcessClass.getParamName(
						point.getTarget().getClass(), point.getSignature()
								.getName());
				String newCacheKey = getCacheKey(method_paramName,
						point.getArgs(), leCache.cacheKey());
				logger.info("开始获取newCacheKey=" + newCacheKey + "的缓存");
				obj = CacheFactory.getInstande().getObject(newCacheKey,
						leCache.expire());
				if (obj == null) {
					try {
						logger.info("开始调用原始方法"+point.getSignature().getName());
						obj = point.proceed();
						if (obj != null) {
							CacheFactory.getInstande().put(newCacheKey, obj,
									leCache.expire());
						}
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				}

			} catch (Exception ex) {
				logger.error("处理方法名称失败:" + point.getSignature().getName()
						+ ",e:" + ex);
				try {
					obj = point.proceed();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}

		}
		return obj;

	}

	public String getCacheKey(String[] method_paramName, Object[] paramVal,
			String originalCacheKey) {
		if (method_paramName == null)
			return originalCacheKey;
		int i = 0;
		for (String v : method_paramName) {
			if (paramVal[i] != null)
				originalCacheKey = originalCacheKey.replace("#" + v + "#",
						paramVal[i].toString());
			i++;
		}
		return originalCacheKey;
	}
}
