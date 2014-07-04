package com.ledong.middleware.cache.logger.slf4j;

import java.io.File;

import com.ledong.middleware.cache.logger.CacheLogger.Logger;
import com.ledong.middleware.cache.logger.Level;
import com.ledong.middleware.cache.logger.LoggerAdpter;

public class Slf4jAdpter implements LoggerAdpter {
	public Logger getLogger(String key) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(Slf4jAdpter.class));
	}

    public Logger getLogger(Class<?> key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    private Level level;
    
    private File file;

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
