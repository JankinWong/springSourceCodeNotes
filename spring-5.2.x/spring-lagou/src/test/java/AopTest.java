import com.lagou.edu.aop.dao.Dao;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AopTest {

		@Test
		public void testAop() {
			ApplicationContext ac = new ClassPathXmlApplicationContext("spring-aop.xml");
			Dao dao = (Dao)ac.getBean("daoImpl");
			dao.select();
		}


}
