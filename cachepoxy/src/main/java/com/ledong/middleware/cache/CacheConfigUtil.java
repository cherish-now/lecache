package com.ledong.middleware.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ledong.middleware.cache.container.MemcacheContainer;
import com.ledong.middleware.cache.logger.CacheLogger.Logger;
import com.ledong.middleware.cache.logger.CacheLoggerFactory;

public class CacheConfigUtil {
	protected static final Logger logger = CacheLoggerFactory.getLogger(MemcacheContainer.class);
	static String memcache_url = "";

	public static String getMemcacheIp() {
		return memcache_url;
	}

	static {
		Properties properties = new Properties();
		InputStream inputStream = CacheConfigUtil.class.getClassLoader()
				.getResourceAsStream("lecache.properties");
		if (inputStream == null) {
			inputStream = CacheConfigUtil.class.getClassLoader()
					.getResourceAsStream("appconfig.properties");
		}
		if (inputStream != null) {

			try {
				properties.load(inputStream);
				for (Object object : properties.keySet()) {
					if (object.toString().equalsIgnoreCase("memcached.url")) {
						memcache_url = properties
								.getProperty(object.toString());
						break;
					}
				}
			
			} catch (IOException e) {
			}
		}
		logger.info("lecache加载memcahe配置，memcached.url="+ memcache_url);
	}

}
