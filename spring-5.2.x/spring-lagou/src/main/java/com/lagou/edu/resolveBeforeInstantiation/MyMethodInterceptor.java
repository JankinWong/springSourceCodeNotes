package com.lagou.edu.resolveBeforeInstantiation;


import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyMethodInterceptor implements MethodInterceptor {


	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("MyMethodInterceptor.interceptor(): " + method + "--->");
		/** 这里会调用具体的类的方法 */
		Object invokeSuper = methodProxy.invokeSuper(o, objects);
		System.out.println("<---MyMethodInterceptor.interceptor(): " + method);
		return invokeSuper;
	}
}
