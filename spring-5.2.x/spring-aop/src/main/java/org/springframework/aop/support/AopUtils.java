/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.aop.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Utility methods for AOP support code.
 *
 * <p>Mainly for internal use within Spring's AOP support.
 *
 * <p>See {@link org.springframework.aop.framework.AopProxyUtils} for a
 * collection of framework-specific AOP utility methods which depend
 * on internals of Spring's AOP framework implementation.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see org.springframework.aop.framework.AopProxyUtils
 */
public abstract class AopUtils {

	/**
	 * Check whether the given object is a JDK dynamic proxy or a CGLIB proxy.
	 * <p>This method additionally checks if the given object is an instance
	 * of {@link SpringProxy}.
	 * @param object the object to check
	 * @see #isJdkDynamicProxy
	 * @see #isCglibProxy
	 */
	public static boolean isAopProxy(@Nullable Object object) {
		return (object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) ||
				object.getClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)));
	}

	/**
	 * Check whether the given object is a JDK dynamic proxy.
	 * <p>This method goes beyond the implementation of
	 * {@link Proxy#isProxyClass(Class)} by additionally checking if the
	 * given object is an instance of {@link SpringProxy}.
	 * @param object the object to check
	 * @see java.lang.reflect.Proxy#isProxyClass
	 */
	public static boolean isJdkDynamicProxy(@Nullable Object object) {
		return (object instanceof SpringProxy && Proxy.isProxyClass(object.getClass()));
	}

	/**
	 * Check whether the given object is a CGLIB proxy.
	 * <p>This method goes beyond the implementation of
	 * {@link ClassUtils#isCglibProxy(Object)} by additionally checking if
	 * the given object is an instance of {@link SpringProxy}.
	 * @param object the object to check
	 * @see ClassUtils#isCglibProxy(Object)
	 */
	public static boolean isCglibProxy(@Nullable Object object) {
		return (object instanceof SpringProxy &&
				object.getClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR));
	}

	/**
	 * Determine the target class of the given bean instance which might be an AOP proxy.
	 * <p>Returns the target class for an AOP proxy or the plain class otherwise.
	 * @param candidate the instance to check (might be an AOP proxy)
	 * @return the target class (or the plain class of the given object as fallback;
	 * never {@code null})
	 * @see org.springframework.aop.TargetClassAware#getTargetClass()
	 * @see org.springframework.aop.framework.AopProxyUtils#ultimateTargetClass(Object)
	 */
	public static Class<?> getTargetClass(Object candidate) {
		Assert.notNull(candidate, "Candidate object must not be null");
		Class<?> result = null;
		if (candidate instanceof TargetClassAware) {
			result = ((TargetClassAware) candidate).getTargetClass();
		}
		if (result == null) {
			result = (isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
		}
		return result;
	}

	/**
	 * Select an invocable method on the target type: either the given method itself
	 * if actually exposed on the target type, or otherwise a corresponding method
	 * on one of the target type's interfaces or on the target type itself.
	 * @param method the method to check
	 * @param targetType the target type to search methods on (typically an AOP proxy)
	 * @return a corresponding invocable method on the target type
	 * @throws IllegalStateException if the given method is not invocable on the given
	 * target type (typically due to a proxy mismatch)
	 * @since 4.3
	 * @see MethodIntrospector#selectInvocableMethod(Method, Class)
	 */
	public static Method selectInvocableMethod(Method method, @Nullable Class<?> targetType) {
		if (targetType == null) {
			return method;
		}
		Method methodToUse = MethodIntrospector.selectInvocableMethod(method, targetType);
		if (Modifier.isPrivate(methodToUse.getModifiers()) && !Modifier.isStatic(methodToUse.getModifiers()) &&
				SpringProxy.class.isAssignableFrom(targetType)) {
			throw new IllegalStateException(String.format(
					"Need to invoke method '%s' found on proxy for target class '%s' but cannot " +
					"be delegated to target bean. Switch its visibility to package or protected.",
					method.getName(), method.getDeclaringClass().getSimpleName()));
		}
		return methodToUse;
	}

	/**
	 * Determine whether the given method is an "equals" method.
	 * @see java.lang.Object#equals
	 */
	public static boolean isEqualsMethod(@Nullable Method method) {
		return ReflectionUtils.isEqualsMethod(method);
	}

	/**
	 * Determine whether the given method is a "hashCode" method.
	 * @see java.lang.Object#hashCode
	 */
	public static boolean isHashCodeMethod(@Nullable Method method) {
		return ReflectionUtils.isHashCodeMethod(method);
	}

	/**
	 * Determine whether the given method is a "toString" method.
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(@Nullable Method method) {
		return ReflectionUtils.isToStringMethod(method);
	}

	/**
	 * Determine whether the given method is a "finalize" method.
	 * @see java.lang.Object#finalize()
	 */
	public static boolean isFinalizeMethod(@Nullable Method method) {
		return (method != null && method.getName().equals("finalize") &&
				method.getParameterCount() == 0);
	}

	/**
	 * Given a method, which may come from an interface, and a target class used
	 * in the current AOP invocation, find the corresponding target method if there
	 * is one. E.g. the method may be {@code IFoo.bar()} and the target class
	 * may be {@code DefaultFoo}. In this case, the method may be
	 * {@code DefaultFoo.bar()}. This enables attributes on that method to be found.
	 * <p><b>NOTE:</b> In contrast to {@link org.springframework.util.ClassUtils#getMostSpecificMethod},
	 * this method resolves Java 5 bridge methods in order to retrieve attributes
	 * from the <i>original</i> method definition.
	 * @param method the method to be invoked, which may come from an interface
	 * @param targetClass the target class for the current invocation.
	 * May be {@code null} or may not even implement the method.
	 * @return the specific target method, or the original method if the
	 * {@code targetClass} doesn't implement it or is {@code null}
	 * @see org.springframework.util.ClassUtils#getMostSpecificMethod
	 */
	public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
		Class<?> specificTargetClass = (targetClass != null ? ClassUtils.getUserClass(targetClass) : null);
		Method resolvedMethod = ClassUtils.getMostSpecificMethod(method, specificTargetClass);
		// If we are dealing with method with generic parameters, find the original method.
		return BridgeMethodResolver.findBridgedMethod(resolvedMethod);
	}

	/**
	 * Can the given pointcut apply at all on the given class?
	 * <p>This is an important test as it can be used to optimize
	 * out a pointcut for a class.
	 * @param pc the static or dynamic pointcut to check
	 * @param targetClass the class to test
	 * @return whether the pointcut can apply on any method
	 */
	public static boolean canApply(Pointcut pc, Class<?> targetClass) {
		return canApply(pc, targetClass, false);
	}

	/**
	 * Can the given pointcut apply at all on the given class?
	 * <p>This is an important test as it can be used to optimize
	 * out a pointcut for a class.
	 * @param pc the static or dynamic pointcut to check
	 * @param targetClass the class to test
	 * @param hasIntroductions whether or not the advisor chain
	 * for this bean includes any introductions
	 * @return whether the pointcut can apply on any method
	 */
	//判断 PointcutAdvisor 类型的 Advisor 能够应用于某个 Bean
	//该方法的处理过程如下：
	//1.使用 Pointcut 的 ClassFilter 匹配 targetClass，不通过则直接返回 false
	//2.获取 Pointcut 的 MethodMatcher 方法匹配器，保存至 methodMatcher
	//3.如果 methodMatcher 为 TrueMethodMatcher，则默认都通过，返回 true
	//4.如果 methodMatcher 为 IntroductionAwareMethodMatcher，则进行转换，保存至 introductionAwareMethodMatcher
	//     .AspectJExpressionPointcut 就是 IntroductionAwareMethodMatcher 的实现类
	//5.获取目标类、以及实现的所有接口，并添加至 classes 集合中
	//     1.如果不是 java.lang.reflect.Proxy 的子类，则获取 targetClass 目标类的 Class 对象（如果目标类是 CGLIB 代理对象，则获取其父类的 Class 对象，也就得到了目标类）
	//     2.获取 targetClass 目标类实现的所有接口，如果目标类本身是一个接口，那么就取这个目标类
	//6.遍历上面的 classes 集合
	//     1.获取这个 Class 对象的所有方法
	//     2.遍历上一步获取到的所有方法
	//     3.使用 methodMatcher 方法匹配器对该方法进行匹配，优先使用 introductionAwareMethodMatcher 方法匹配器，匹配成功则直接返回 true，说明有一个方法满足条件即可
	//           .AspectJExpressionPointcut 底层就是通过 AspectJ 的表达式处理进行处理的
	//7.一个方法都没匹配成功则返回 false，表示这个 Advisor 不能应用到这个 Bean 上面
	//总结下来，PointcutAdvisor 是根据 Pointcut 的 ClassFilter 对目标类进行过滤，
	//如果通过的话，则通过 MethodMatcher 方法匹配器对目标类的方法进行匹配，有一个方法满足条件就表示这个 PointcutAdvisor 可以应用于目标类
	public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
		Assert.notNull(pc, "Pointcut must not be null");
		// <1> 使用 ClassFilter 匹配 `targetClass`
		if (!pc.getClassFilter().matches(targetClass)) {
			return false;
		}

		// <2> 获取 MethodMatcher 方法匹配器
		MethodMatcher methodMatcher = pc.getMethodMatcher();
		// <3> 如果方法匹配器为 TrueMethodMatcher，则默认都通过
		if (methodMatcher == MethodMatcher.TRUE) {
			// No need to iterate the methods if we're matching any method anyway...
			return true;
		}
		// <4> 如果方法匹配器为 IntroductionAwareMethodMatcher，则进行转换
		// AspectJExpressionPointcut 就是 IntroductionAwareMethodMatcher 的实现类
		IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
		if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
			introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
		}
		/*
		 * <5> 获取目标类、以及实现的所有接口，并添加至 `classes` 集合中
		 */
		Set<Class<?>> classes = new LinkedHashSet<>();
		// <5.1> 如果不是 java.lang.reflect.Proxy 的子类
		if (!Proxy.isProxyClass(targetClass)) {
			// 获取目标类的 Class 对象（如果目标类是 CGLIB 代理对象，则获取其父类的 Class 对象，也就得到了目标类）
			classes.add(ClassUtils.getUserClass(targetClass));
		}
		// <5.2> 获取目标类实现的所有接口，如果目标类本身是一个接口，那么就取这个目标类
		classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));

		/*
		 * <6> 遍历上面的 `classes` 集合
		 */
		for (Class<?> clazz : classes) {
			// <6.1> 获取这个 Class 对象的所有方法
			Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
			// <6.2> 遍历上一步获取到的所有方法
			for (Method method : methods) {
				// <6.3> 使用方法匹配器对该方法进行匹配，如果匹配成功则直接返回 `true`
				// AspectJExpressionPointcut 底层就是通过 AspectJ 进行处理的
				if (introductionAwareMethodMatcher != null ?
						introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions) :
						methodMatcher.matches(method, targetClass)) {
					return true;
				}
			}
		}
		// <7> 一个方法都没匹配则返回 `false`，表示这个 Advisor 不能应用到这个 Bean 上面
		return false;
	}

	/**
	 * Can the given advisor apply at all on the given class?
	 * This is an important test as it can be used to optimize
	 * out a advisor for a class.
	 * @param advisor the advisor to check
	 * @param targetClass class we're testing
	 * @return whether the pointcut can apply on any method
	 */
	public static boolean canApply(Advisor advisor, Class<?> targetClass) {
		return canApply(advisor, targetClass, false);
	}

	/**
	 * Can the given advisor apply at all on the given class?
	 * <p>This is an important test as it can be used to optimize out a advisor for a class.
	 * This version also takes into account introductions (for IntroductionAwareMethodMatchers).
	 * @param advisor the advisor to check
	 * @param targetClass class we're testing
	 * @param hasIntroductions whether or not the advisor chain for this bean includes
	 * any introductions
	 * @return whether the pointcut can apply on any method
	 */
	// 是否能够应用到当前Bean上面
	//1.如果 IntroductionAdvisor 类型的 Advisor 则通过 ClassFilter 类过滤器进行判断即可；
	//2.如果是 PointcutAdvisor 类型的 Advisor 则需要调用 canApply(..) 的重载方法进行判断；
	//3.否则，没有 Pointcut，也就是没有筛选条件，则都符合条件
	public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
		if (advisor instanceof IntroductionAdvisor) {
			/*
			 * 从 IntroductionAdvisor 中获取 ClassFilter 类过滤器，判断这个目标类是否符合条件
			 */
			return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
		}
		else if (advisor instanceof PointcutAdvisor) {
			/*
			 * 根据 Pointcut 中的 ClassFilter 和 MethodFilter 进行过滤
			 * 例如 Aspect 的实现类 AspectJExpressionPointcut
			 */
			PointcutAdvisor pca = (PointcutAdvisor) advisor;
			return canApply(pca.getPointcut(), targetClass, hasIntroductions);
		}
		else {
			// It doesn't have a pointcut so we assume it applies.
			// 否则，没有 Pointcut，也就是没有筛选条件，则都符合条件
			return true;
		}
	}

	/**
	 * Determine the sublist of the {@code candidateAdvisors} list
	 * that is applicable to the given class.
	 * @param candidateAdvisors the Advisors to evaluate
	 * @param clazz the target class
	 * @return sublist of Advisors that can apply to an object of the given class
	 * (may be the incoming List as-is)
	 */
	//该方法的处理过程如下：
	//1.遍历所有的 Advisor 对象，找到能够应用当前 Bean 的 IntroductionAdvisor 对象，放入 eligibleAdvisors 集合中，需要满足下面两个条件
	//  .是 IntroductionAdvisor 类型
	//  .能够应用到当前 Bean 中，通过其 ClassFilter 进行过滤
	//2.遍历所有的 Advisor 对象，找到能够应用当前 Bean 的 Advisor 对象，放入 eligibleAdvisors 集合中；如果是 IntroductionAdvisor 类型，则会跳过，因为上面已经判断过
	//3.返回能够应用到当前 Bean 的所有 Advisor 对象
	public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
		if (candidateAdvisors.isEmpty()) {
			return candidateAdvisors;
		}
		List<Advisor> eligibleAdvisors = new ArrayList<>();
		/*
		 * <1> 遍历所有的 Advisor 对象
		 * 找到能够应用当前 Bean 的 IntroductionAdvisor 对象，放入 `eligibleAdvisors` 集合中
		 */
		for (Advisor candidate : candidateAdvisors) {
			// 如果是 IntroductionAdvisor 类型并且能够应用到当前 Bean 中，通过其 ClassFilter 进行过滤
			if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
				eligibleAdvisors.add(candidate);
			}
		}
		boolean hasIntroductions = !eligibleAdvisors.isEmpty();
		/*
		 * <2> 遍历所有的 Advisor 对象
		 * 如果是 IntroductionAdvisor 类型，则会跳过，因为上面已经判断过
		 * 找到能够应用当前 Bean 的 Advisor 对象，放入 `eligibleAdvisors` 集合中
		 */
		for (Advisor candidate : candidateAdvisors) {
			if (candidate instanceof IntroductionAdvisor) {
				// already processed
				continue;
			}
			// 判断是否能够应用到这个 Bean 上面
			if (canApply(candidate, clazz, hasIntroductions)) {
				eligibleAdvisors.add(candidate);
			}
		}
		// <3> 返回能够应用到当前 Bean 的所有 Advisor 对象
		return eligibleAdvisors;
	}

	/**
	 * Invoke the given target via reflection, as part of an AOP method invocation.
	 * @param target the target object
	 * @param method the method to invoke
	 * @param args the arguments for the method
	 * @return the invocation result, if any
	 * @throws Throwable if thrown by the target method
	 * @throws org.springframework.aop.AopInvocationException in case of a reflection error
	 */
	//通过反射执行目标方法
	@Nullable
	public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args)
			throws Throwable {

		// Use reflection to invoke the method.
		try {
			ReflectionUtils.makeAccessible(method);
			//通过反射执行目标方法
			return method.invoke(target, args);
		}
		catch (InvocationTargetException ex) {
			// Invoked method threw a checked exception.
			// We must rethrow it. The client won't see the interceptor.
			throw ex.getTargetException();
		}
		catch (IllegalArgumentException ex) {
			throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" +
					method + "] on target [" + target + "]", ex);
		}
		catch (IllegalAccessException ex) {
			throw new AopInvocationException("Could not access method [" + method + "]", ex);
		}
	}

}
