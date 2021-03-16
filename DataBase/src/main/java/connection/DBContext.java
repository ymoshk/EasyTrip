package connection;

import javax.persistence.*;
import java.io.Closeable;
import java.io.IOException;

public class DBContext implements Closeable {
    private EntityManagerFactory entityManagerFactory;


    public DBContext() {
        entityManagerFactory = Persistence
                .createEntityManagerFactory("EasyTrip");
    }

    @Override
    public void close() {
//        entityManagerFactory.close();
    }

    public void addTest(EntityTest testToAdd) {
        EntityManager em = entityManagerFactory.createEntityManager();
        // Used to issue transactions on the EntityManager
        EntityTransaction et = null;


        try {
            // Get transaction and start
            et = em.getTransaction();
            et.begin();

            // Create and set values for new customer


            // Save the customer object
            em.persist(testToAdd);
            et.commit();
        } catch (Exception ex) {
            // If there is an exception rollback changes
            if (et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        } finally {
            // Close EntityManager
            em.close();
        }
    }
}
