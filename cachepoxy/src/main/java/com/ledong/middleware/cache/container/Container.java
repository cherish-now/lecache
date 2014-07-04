package com.ledong.middleware.cache.container;

public interface Container {
	 <T> T getObject(String cacheKey,int expire);
	 void put(String cacheKey,Object o,int expire);
	 
}
