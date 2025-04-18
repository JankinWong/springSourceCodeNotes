import com.lagou.edu.LagouBean;
import com.lagou.edu.ioc.beans.Address;
import com.lagou.edu.ioc.beans.Person;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author 应癫
 */
public class IocTest {

	/**
	 *  Ioc 容器源码分析基础案例
	 */
	@Test
	public void testIoC() {
		// ApplicationContext是容器的高级接口，BeanFacotry（顶级容器/根容器，规范了/定义了容器的基础行为）
		// Spring应用上下文，官方称之为 IoC容器（错误的认识：容器就是map而已；准确来说，map是ioc容器的一个成员，
		// 叫做单例池, singletonObjects,容器是一组组件和过程的集合，包括BeanFactory、单例池、BeanPostProcessor等以及之间的协作流程）

		/**
		 * Ioc容器创建管理Bean对象的，Spring Bean是有生命周期的
		 * 构造器执行、初始化方法执行、Bean后置处理器的before/after方法、：AbstractApplicationContext#refresh#finishBeanFactoryInitialization
		 * Bean工厂后置处理器初始化、方法执行：AbstractApplicationContext#refresh#invokeBeanFactoryPostProcessors
		 * Bean后置处理器初始化：AbstractApplicationContext#refresh#registerBeanPostProcessors
		 */

//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		ApplicationContext applicationContext =  new AnnotationConfigApplicationContext("com.lagou.edu");
		Person person = applicationContext.getBean(Person.class);

//		Person person = applicationContext.getBean(Person.class);
		Address address = applicationContext.getBean(Address.class);
		System.out.println("person:"+person);
		System.out.println("address:"+address);
//		person.setAge(18);
//		person.setName("小马哥");
//		person.setId(1);
//		person.setAddress(address);
//		address.setStreet("北京10胡同");
//		address.setCity("北京");
		System.out.println("id:"+person.getId());
		System.out.println("name:"+person.getName());
		System.out.println("age:"+person.getAge());
		System.out.println("person 包含的 address:"+person.getAddress());
		System.out.println("street:"+address.getStreet());
		System.out.println("city:"+address.getCity());

//		applicationContext.getBean(Person.class);
//		System.out.println("person:"+person);
//		applicationContext.getBean(Address.class);
//		System.out.println("address:"+address);

	}

	/**
	 *  Ioc 容器源码分析基础案例
	 */
	@Test
	public void testAOP() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		LagouBean lagouBean = applicationContext.getBean(LagouBean.class);
		lagouBean.print();
	}
//	@Test
//	public void testResolveBeforeInstantiation(){
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("resolveBeforeInstantiation.xml");
//		BeanInstantiation beanInstantiation = (BeanInstantiation) applicationContext.getBean("beanInstantiation");
//		beanInstantiation.simple();
//	}
}
