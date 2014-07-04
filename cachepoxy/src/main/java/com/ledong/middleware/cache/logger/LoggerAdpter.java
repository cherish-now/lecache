package com.ledong.middleware.cache.logger;

import java.io.File;

import com.ledong.middleware.cache.logger.CacheLogger.Logger;


public interface LoggerAdpter {
	
	Logger getLogger(Class<?> key);

	Logger getLogger(String key);
	
	void setLevel(Level level);
	
	Level getLevel();
	
	File getFile();
	
	void setFile(File file);

}
