package startup;

import connection.DataEngine;
import constant.Constants;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StartupServlet implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext()
                .setAttribute(Constants.DATA_ENGINE, DataEngine.getInstance());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        DataEngine dataEngine = (DataEngine) servletContextEvent.getServletContext()
                .getAttribute(Constants.DATA_ENGINE);
        dataEngine.close();
    }
}
