package com.ledong.middleware.cache.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.ledong.middleware.cache.TestCacheHello;
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class TestCache extends AbstractJUnit4SpringContextTests {
	@Autowired
	TestCacheHello testCacheHello;
	@Test
	public void testAspect(){
		long start=System.currentTimeMillis();

		for(int i=0;i<10000;i++){
		testCacheHello.getHello("liaoy",12);
	
		}
		System.out.println("耗时:"+(System.currentTimeMillis()-start));
	}
	
}
