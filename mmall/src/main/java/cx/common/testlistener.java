package cx.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class testlistener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("初始化了--------------------------");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("销毁了--------------------------");
	}
}
