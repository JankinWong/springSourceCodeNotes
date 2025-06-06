/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.springframework.aop.SpringProxy;

/**
 * Default {@link AopProxyFactory} implementation, creating either a CGLIB proxy
 * or a JDK dynamic proxy.
 *
 * <p>Creates a CGLIB proxy if one the following is true for a given
 * {@link AdvisedSupport} instance:
 * <ul>
 * <li>the {@code optimize} flag is set
 * <li>the {@code proxyTargetClass} flag is set
 * <li>no proxy interfaces have been specified
 * </ul>
 *
 * <p>In general, specify {@code proxyTargetClass} to enforce a CGLIB proxy,
 * or specify one or more interfaces to use a JDK dynamic proxy.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 12.03.2004
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {

	private static final long serialVersionUID = 7930414337282325166L;

    //创建 AOP 代理类的过程如下：
	//1.判断是否满足下面三个条件的其中一个，则进行下面的处理
	//是否需要优化，默认为 false；
	//是否使用类代理，也就是CGLIB动态代理（默认为false），在前面的 AbstractAutoProxyCreator#createProxy(..) 方法中有提到过；
	//目标类有没有实现接口；
	//   1.获取目标类
	//   2.如果目标类是一个接口或者是 java.lang.reflect.Proxy 的子类，则还是使用JDK 动态代理，创建一个 JdkDynamicAopProxy 对象
	//   3.否则，使用 CGLIB 动态代理，创建一个 ObjenesisCglibAopProxy 对象
	//2.否则，使用JDK 动态代理，创建一个 JdkDynamicAopProxy 对象
	@Override
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		/*
		 * <1> 判断是否满足下面三个条件的其中一个
		 */
		if (config.isOptimize() // 需要优化，默认为 `false`
				|| config.isProxyTargetClass() //是否使用类代理，也就是CGLIB动态代理（默认为false）
				|| hasNoUserSuppliedProxyInterfaces(config)) { // 目标类有没有实现接口

			// <1.1> 获取目标类
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			/*
			 * <1.2> 如果目标类是一个接口或者是 java.lang.reflect.Proxy 的子类
			 * 则还是使用 JDK 动态代理，创建一个 JdkDynamicAopProxy 对象，传入 AdvisedSupport 配置管理器，并返回
			 */
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			// <1.3> 使用 CGLIB 动态代理，创建一个  ObjenesisCglibAopProxy 对象，传入 AdvisedSupport 配置管理器，并返回
			return new ObjenesisCglibAopProxy(config);
		}
		// <2> 否则
		else {
			// 使用 JDK 动态代理，创建一个 JdkDynamicAopProxy 对象，传入 AdvisedSupport 配置管理器，并返回
			return new JdkDynamicAopProxy(config);
		}
	}

	/**
	 * Determine whether the supplied {@link AdvisedSupport} has only the
	 * {@link org.springframework.aop.SpringProxy} interface specified
	 * (or no proxy interfaces specified at all).
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
		Class<?>[] ifcs = config.getProxiedInterfaces();
		return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
	}

}
