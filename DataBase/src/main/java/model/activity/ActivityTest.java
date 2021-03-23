package model.activity;

import connection.DBContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ActivityTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("EasyTrip");
        EntityManager em = emf.createEntityManager();

        Activity activity = new Activity(1L, "Beach", "Swim", 22, 22,
                4.5,"http://beach.com", 0, "EUR", "http://picture.com");

        em.getTransaction().begin();
        em.persist(activity);
        em.getTransaction().commit();
        System.out.println(activity);
    }
}
