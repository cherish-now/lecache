lecahe
======

基于spring的自定义缓存注解，当前支撑memcache+ehcache


使用方法：

在应用的spring中 import 	<import resource="cacheconfig/ledong-cache.xml"/>文件.


在spring bean 对象 里面的方法绑定注解@LeCache(key=“”,expire=10[秒])

example：
	@LeCache(cacheKey="ledong-cache:getHello_#name#_#age#",expire=10)
	public String getHello(String name,int age){
		System.out.println("调用方法.");
		return "1";
	}