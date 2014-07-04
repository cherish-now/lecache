package com.ledong.middleware.cache;

import org.springframework.stereotype.Service;

import com.ledong.middleware.cache.annotation.LeCache;
@Service
public class TestLeCacheAnn  implements TestCacheHello{
	@LeCache(cacheKey="ledong-cache:getHello_#name#_#age#",expire=10)
	public String getHello(String name,int age){
		System.out.println("调用方法.");
		return "1";
	}
}
