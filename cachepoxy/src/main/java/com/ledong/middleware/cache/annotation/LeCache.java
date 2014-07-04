package com.ledong.middleware.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ledong.middleware.cache.StorageStrategy;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LeCache {
	/**
	 * 存储策略
	 * @return
	 */
	StorageStrategy s() default StorageStrategy.Memcache_Ehcache;
	/**
	 * 缓存key名称
	 * @return
	 */
	String cacheKey();

	/**
	 * cache 缓存时间
	 * @return
	 */
	int expire() default 10 ;
}
