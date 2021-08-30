package connection;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.Closeable;

public class SessionFactoryUtil implements Closeable {
    private static SessionFactoryUtil instance = null;
    private final SessionFactory sessionFactory;

    private SessionFactoryUtil() {
        this.sessionFactory = buildSessionFactory();

    }

    // only one thread can execute this method at the same time.
    static synchronized SessionFactoryUtil getInstance() {
        if (instance == null) {
            instance = new SessionFactoryUtil();
        }
        return instance;
    }

    public org.hibernate.Session getNewSession() {
        return this.sessionFactory.openSession();
    }


    private SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(model.activity.ActivityAmadeus.class);
            configuration.addAnnotatedClass(model.attraction.Attraction.class);
            configuration.addAnnotatedClass(model.attraction.AttractionImage.class);
            configuration.addAnnotatedClass(model.attraction.amusement.park.AmusementPark.class);
            configuration.addAnnotatedClass(model.attraction.park.Park.class);
            configuration.addAnnotatedClass(model.attraction.aquarium.Aquarium.class);
            configuration.addAnnotatedClass(model.attraction.art.gallery.ArtGallery.class);
            configuration.addAnnotatedClass(model.attraction.bar.Bar.class);
            configuration.addAnnotatedClass(model.attraction.beach.Beach.class);
            configuration.addAnnotatedClass(model.attraction.cafe.Cafe.class);
            configuration.addAnnotatedClass(model.attraction.casino.Casino.class);
            configuration.addAnnotatedClass(model.attraction.hotel.Hotel.class);
            configuration.addAnnotatedClass(model.attraction.market.Market.class);
            configuration.addAnnotatedClass(model.attraction.museum.Museum.class);
            configuration.addAnnotatedClass(model.attraction.nightClub.NightClub.class);
            configuration.addAnnotatedClass(model.attraction.restaurant.Restaurant.class);
            configuration.addAnnotatedClass(model.attraction.shopping.mall.ShoppingMall.class);
            configuration.addAnnotatedClass(model.attraction.spa.Spa.class);
            configuration.addAnnotatedClass(model.attraction.topSight.TopSight.class);
            configuration.addAnnotatedClass(model.attraction.touristAttraction.TouristAttraction.class);
            configuration.addAnnotatedClass(model.attraction.zoo.Zoo.class);
            configuration.addAnnotatedClass(model.IATACode.IATACode.class);
            configuration.addAnnotatedClass(model.itinerary.ItineraryModel.class);
            configuration.addAnnotatedClass(model.location.City.class);
            configuration.addAnnotatedClass(model.location.Country.class);
            configuration.addAnnotatedClass(model.location.Airport.class);
            configuration.addAnnotatedClass(model.travel.Travel.class);
            configuration.addAnnotatedClass(model.user.GuestUser.class);
            configuration.addAnnotatedClass(model.user.RegisteredUser.class);
            configuration.addAnnotatedClass(model.user.User.class);
            configuration.addAnnotatedClass(model.Model.class);


            System.out.println("Hibernate Configuration loaded");

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            System.out.println("Hibernate serviceRegistry created");

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactoryUtil creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override
    public void close() {
        this.sessionFactory.close();
    }
}
