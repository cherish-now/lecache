package com.ledong.middleware.cache.logger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ledong.middleware.cache.logger.CacheLogger.Logger;
import com.ledong.middleware.cache.logger.log4j.Log4jAdpter;
import com.ledong.middleware.cache.logger.slf4j.Slf4jAdpter;


public class CacheLoggerFactory {

		private CacheLoggerFactory() {
		}

		private static volatile LoggerAdpter LOGGER_ADAPTER;
		
		private static final ConcurrentMap<String, LeDongLogger> LOGGERS = new ConcurrentHashMap<String, LeDongLogger>();

		static {
		   
	    		try {
	    			setLoggerAdapter(new Log4jAdpter());
	            } catch (Throwable e1) {
	                try {
	                	setLoggerAdapter(new Slf4jAdpter());
	                } catch (Throwable e2) {
	                   
	                }
	            }
	    	
		}
		
		public static void setLoggerAdapter(LoggerAdpter loggerAdapter) {
			if (loggerAdapter != null) {
				Logger logger = loggerAdapter.getLogger(CacheLoggerFactory.class.getName());
				logger.info("using logger: " + loggerAdapter.getClass().getName());
				CacheLoggerFactory.LOGGER_ADAPTER = loggerAdapter;
				for (Map.Entry<String, LeDongLogger> entry : LOGGERS.entrySet()) {
					entry.getValue().setLogger(LOGGER_ADAPTER.getLogger(entry.getKey()));
				}
			}
		}

		public static Logger getLogger(Class<?> key) {
			LeDongLogger logger = LOGGERS.get(key.getName());
			if (logger == null) {
				LOGGERS.putIfAbsent(key.getName(), new LeDongLogger(LOGGER_ADAPTER.getLogger(key)));
				logger = LOGGERS.get(key.getName());
			}
			return logger;
		}

		public static Logger getLogger(String key) {
			LeDongLogger logger = LOGGERS.get(key);
			if (logger == null) {
				LOGGERS.putIfAbsent(key, new LeDongLogger(LOGGER_ADAPTER.getLogger(key)));
				logger = LOGGERS.get(key);
			}
			return logger;
		}
		public static void setLevel(Level level) {
			LOGGER_ADAPTER.setLevel(level);
		}

		
		public static Level getLevel() {
			return LOGGER_ADAPTER.getLevel();
		}
		
		public static File getFile() {
			return LOGGER_ADAPTER.getFile();
		}

}
