package startup;

import cache.ItineraryCache;
import connection.DataEngine;
import constant.Constants;
import user.UserContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StartupServlet implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();

        context.setAttribute(Constants.DATA_ENGINE, DataEngine.getInstance());
        context.setAttribute(Constants.ITINERARY_CACHE, new ItineraryCache());
        context.setAttribute(Constants.USERS_CONTEXT, new UserContext());
//        context.setAttribute(Constants.FLIGHT_ENGINE, new FlightEngine());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        DataEngine dataEngine = (DataEngine) servletContextEvent.getServletContext()
                .getAttribute(Constants.DATA_ENGINE);

        ServletContext context = servletContextEvent.getServletContext();

        ((ItineraryCache) context.getAttribute(Constants.ITINERARY_CACHE)).close();

        dataEngine.close();
    }
}
