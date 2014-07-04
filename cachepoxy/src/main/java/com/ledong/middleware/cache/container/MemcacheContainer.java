package com.ledong.middleware.cache.container;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.ledong.middleware.cache.CacheConfigUtil;
import com.ledong.middleware.cache.logger.CacheLogger.Logger;
import com.ledong.middleware.cache.logger.CacheLoggerFactory;

import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import net.spy.memcached.transcoders.SerializingTranscoder;

/**
 * memcache 缓存容器
 * 
 * @author liaoyong
 * 
 */
public class MemcacheContainer implements Container {
	static ExecutorService executorService = Executors
			.newSingleThreadExecutor();
	protected static final Logger logger = CacheLoggerFactory.getLogger(MemcacheContainer.class);
	private final static String globleKey = "ledong_ehcache_cachepoxy_";
	static MemcachedClient memcachedClient;
	static MemcacheContainer memcacheContainer = new MemcacheContainer();

	public static Container getInstande() {
		return memcacheContainer;
	}

	static {
		if (!CacheConfigUtil.getMemcacheIp().equalsIgnoreCase("")) {
			MemcachedClientFactoryBean memcachedClientFactoryBean = new MemcachedClientFactoryBean();
			memcachedClientFactoryBean.setMaxReconnectDelay(100);
			memcachedClientFactoryBean.setServers(CacheConfigUtil
					.getMemcacheIp());
			memcachedClientFactoryBean.setProtocol(Protocol.BINARY);
			SerializingTranscoder transcoder = new SerializingTranscoder();
			transcoder.setCompressionThreshold(1024);
			memcachedClientFactoryBean.setTranscoder(transcoder);
			memcachedClientFactoryBean.setOpTimeout(1000);
			memcachedClientFactoryBean.setTimeoutExceptionThreshold(100);
			memcachedClientFactoryBean.setLocatorType(Locator.CONSISTENT);
			memcachedClientFactoryBean.setFailureMode(FailureMode.Redistribute);
			memcachedClientFactoryBean.setUseNagleAlgorithm(false);
			try {
				memcachedClient = (MemcachedClient) memcachedClientFactoryBean
						.getObject();
				logger.info("ledong-memcache-containe:memcache 容器初始化成功.");
			} catch (Exception e) {
				logger.error("ledong-memcache-containe:memcache 容器初始化失败.", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(final String cacheKey, int expire) {
		if (memcachedClient != null) {
			Callable<T> call = new Callable<T>() {
				public T call() throws Exception {
					try {
						Object obj = memcachedClient.get(globleKey + cacheKey);
						if (obj != null)
							return (T) obj;
					} catch (Exception ex) {
						logger.error("返回memcache失败:getObject-" + cacheKey);
					}
					return null;
				}
			};
			Future<T> future = executorService.submit(call);
			try {
				T t = future.get(1, TimeUnit.SECONDS);
				return t;
			} catch (Exception e) {
				logger.error("执行Future异常:getObject-" + cacheKey + ",e:" + e);
			}
		}
		return null;
	}

	public void put(final String cacheKey, final Object o, final int expire) {
		if (memcachedClient != null) {
			Callable<Boolean> call = new Callable<Boolean>() {
				public Boolean call() throws Exception {
					logger.info("当前memcache-key:"+globleKey + cacheKey);
					try {
						return memcachedClient.set(globleKey + cacheKey,
								expire, o).get();
					} catch (Exception ex) {
						logger.error("执行Future-memcachedClient.set异常:"
								+ cacheKey + ",e:" + ex);
					}
					return false;
				}
			};
			Future<Boolean> futrue = executorService.submit(call);
			try {
				if (!futrue.get(2, TimeUnit.SECONDS)) {
					logger.error("执行Future-memcachedClient.set 返回false"
							+ cacheKey);
				}
			} catch (Exception e) {
				logger.error("执行Future异常:put" + cacheKey + ",e:" + e);
			}
		}
	}

}
