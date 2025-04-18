package com.lagou.edu.resolveBeforeInstantiation;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;

public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
	//如果你自己定义一个代理对象，并返回，那么就会使用你定义的代理对象，
    //如果返回null，spring就会走正常实例化流程给你创建对象
   @Override
   public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
      /** 创建代理 */
      System.out.println("当前beanName：" + beanName + "--> 执行：postProcessBeforeInstantiation 实例化之前");
      //如果是BeanInstantiation，就通过Enhancer来创建一个CGLIB的代理对象
      if (beanClass == BeanInstantiation.class) {
		  // 创建动态代理类的增强类
         Enhancer enhancer = new Enhancer();
		  //  设置被动态代理类所代理的 被代理类
         enhancer.setSuperclass(beanClass);
		  // 设置方法拦截器
         enhancer.setCallback(new MyMethodInterceptor());
         BeanInstantiation beanInstantiation = (BeanInstantiation) enhancer.create();
         System.out.println("创建代理对象：" + beanInstantiation);
         return beanInstantiation;
      }
      //否则，直接返回null，这样就可以继续后面实例化、属性注入、初始化的正常创建bean了
      return null;
   }
 
   @Override
   public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
      System.out.println("=== postProcessAfterInstantiation ===");
      return false;
   }
 
   @Override
   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      System.out.println("=== postProcessBeforeInitialization ===");
      return bean;
   }
 
   @Override
   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      System.out.println("=== postProcessAfterInitialization ===");
      return bean;
   }
 
   /**
    * 对当前属性值的相关处理工作
    *
    * @param pvs      the property values that the factory is about to apply (never {@code null})
    * @param bean     the bean instance created, but whose properties have not yet been set
    * @param beanName the name of the bean
    * @return
    * @throws BeansException
    */
   @Override
   public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
      System.out.println("=== postProcessProperties ===");
      return pvs;
   }
}