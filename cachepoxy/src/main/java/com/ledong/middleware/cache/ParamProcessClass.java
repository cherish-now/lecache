package com.ledong.middleware.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.ReflectionUtils;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import com.ledong.middleware.cache.annotation.LeCache;

/**
 * 参数处理类
 * @author liaoyong
 *
 */
public class ParamProcessClass {
	final static Pattern pattern=java.util.regex.Pattern.compile("#([^#]+)#");
	static ClassPool classPool=null;
	static{
		classPool=ClassPool.getDefault();
		ClassPool.getDefault().insertClassPath(new ClassClassPath(ParamProcessClass.class));
	}
	/**
	 * 获取缓存key name
	 * @param str
	 * @return
	 */
	public static String[] getCacheKeyMap(String str){
		//@key
		Matcher matcher=pattern.matcher(str);
		List<String> cacheParam=new ArrayList<String>();
		while(matcher.find()){
			cacheParam.add(matcher.group());
		}
		return cacheParam.toArray(new String[]{});
	}
	public static LeCache getLeCacheAnn(Class clas,String memthodName) throws NotFoundException{
		CtClass cc = classPool.get(clas.getName());
	    CtMethod cm =cc.getDeclaredMethod(memthodName);
	    try {
	    	LeCache leCache=(LeCache)
					cm.getAnnotation(com.ledong.middleware.cache.annotation.LeCache.class);
			return leCache;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    return null;
	}
	public static String[] getParamName(Class clas,String memthodName) throws NotFoundException{
		CtClass cc = classPool.get(clas.getName());
	    CtMethod cm =cc.getDeclaredMethod(memthodName);
		 //使用javaassist的反射方法获取方法的参数名
		MethodInfo methodInfo = cm.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
		String[] paramNames = new String[cm.getParameterTypes().length];
	    int staticIndex = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
		for (int i = 0; i < paramNames.length; i++){
		paramNames[i] = attr.variableName(i + staticIndex);
		}
		
	    return paramNames;
	}
}
