package com.ledong.middleware.cache.container;

import com.ledong.middleware.cache.logger.CacheLoggerFactory;
import com.ledong.middleware.cache.logger.CacheLogger.Logger;

/**
 * cache 处理工厂
 * @author liaoyong
 *
 */
public class CacheFactory implements Container{
	static CacheFactory cacheFactory=new CacheFactory();
	protected static final Logger logger = CacheLoggerFactory.getLogger(CacheFactory.class);
	public static CacheFactory getInstande(){
		return cacheFactory;
	}
	@SuppressWarnings("unchecked")
	public <T> T getObject(String cacheKey, int expire) {
		//先获取ehcache
		Object obj=EhcacheContainer.getInstande().getObject(cacheKey, expire);
		if(obj==null){
			//获取memcache
			obj=MemcacheContainer.getInstande().getObject(cacheKey, expire);
			if(obj!=null)
				logger.debug("key="+cacheKey+",命中memcache缓存");
		}
		return (T)obj;
	}

	public void put(String cacheKey, Object o, int expire) {
		EhcacheContainer.getInstande().put(cacheKey, o, expire);
		MemcacheContainer.getInstande().put(cacheKey, o, expire);
	}

}
