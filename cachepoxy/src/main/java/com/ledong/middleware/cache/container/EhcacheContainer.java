package com.ledong.middleware.cache.container;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.ledong.middleware.cache.logger.CacheLogger.Logger;
import com.ledong.middleware.cache.logger.CacheLoggerFactory;
/**
 * ehcache 容器
 * @author liaoyong
 *
 */
public class EhcacheContainer  implements Container{
	protected final Logger logger = CacheLoggerFactory.getLogger(EhcacheContainer.class);
	private final static String globleKey="ledong_ehcache_cachepoxy_";
	private static Map<String,Cache> cacheIdentifMap=new HashMap<String,Cache>();
	private static CacheManager singletonManager= CacheManager.create();
	static EhcacheContainer ehcacheContainer=new EhcacheContainer();
	public static Container getInstande(){
		return ehcacheContainer;
	}
	/**
	 * 返回对象
	 * @param cacheKey
	 * @param expire
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(String cacheKey,int expire){
		String cacheIdentif=globleKey+expire;
		Cache cache=cacheIdentifMap.get(cacheIdentif);
		if(cache==null){
			return null;
		}
		Element element=cache.get(cacheKey);
		if(element==null)
			return null;
		logger.info(("命中缓存ehcache:"+cacheKey));
		return (T)element.getObjectValue();
	}
	/**
	 * 存储cache
	 * @param cacheKey
	 * @param o
	 * @param expire
	 */
	public void put(String cacheKey,Object o,int expire){
		String cacheIdentif=globleKey+expire;
		Cache memoryOnlyCache=cacheIdentifMap.get(cacheIdentif);
		if(memoryOnlyCache==null){
			 memoryOnlyCache = new Cache(cacheIdentif, 2000, true, false, expire, expire); 
			 synchronized (memoryOnlyCache) {
				 if(!singletonManager.cacheExists(cacheIdentif)){
					 singletonManager.addCache(memoryOnlyCache);
				 }
			}
			 cacheIdentifMap.put(cacheIdentif, memoryOnlyCache);
		}
		Element ele=new Element(cacheKey, o);
		memoryOnlyCache.put(ele);
	}
	
}
