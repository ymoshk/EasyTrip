package connection;

import log.LogsManager;
import model.Model;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A singleton class to connect and process CRUD operations over the DB.
 * This class can only be accessed from 'connection' package.
 */
class DBContext implements Closeable {

    private static DBContext instance = null;
    private static DBContext usersInstance = null;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private DBContext() {
        entityManagerFactory = Persistence
                .createEntityManagerFactory("EasyTrip");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    // only one thread can execute this method at the same time.
    static synchronized DBContext getInstance() {
        if (instance == null) {
            instance = new DBContext();
        }
        return instance;
    }

    static synchronized DBContext getUsersInstance() {
        if (usersInstance == null) {
            usersInstance = new DBContext();
        }
        return usersInstance;
    }

    /**
     * A method to close the release any resource of the DBContext
     */
    @Override
    public void close() {
        this.entityManagerFactory.close();
        this.entityManager.close();
        instance = null;
    }

    /**
     * A method to receive a single model object from the DB.
     *
     * @param modelClass The class type of the requested object - must be 'Model' or inherit from 'Model'.
     * @param id         Represent the id of the requested object inside the DB - long int.
     * @return An 'optional' object contains the requested object or an empty value if nothing was found.
     */
    Optional<Model> findById(Class<?> modelClass, long id) {
        if (modelClass.getSuperclass() == Model.class || modelClass == Model.class) {
            Model modelFound = (Model) entityManager.find(modelClass, id);

            if (modelFound != null) {
                return Optional.of(modelFound);
            }
        }

        return Optional.empty();
    }

    /**
     * @param queryString the query to process as a string.
     * @return A list that contains any record that match the search.
     * <p>
     * Examples:
     * "FROM EntityTest" | get all the records from the table that's mapped to the class 'EntityTest'
     * "FROM EntityTest Where age < 2" | get any 'EntityTest' record from the DB that match the condition "age < 2"
     * "SELECT max(age) FROM EntityTest"
     * <p>
     * More info can be found at https://www.javatpoint.com/hql
     */
    List<? extends Model> selectQuery(String queryString) {
        try {
            Query query = this.entityManager.createQuery(queryString);
            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    List<? extends Model> selectQuery(String queryString, int limit) {
        try {
            Query query = this.entityManager.createQuery(queryString);
            query.setFirstResult(0);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }


    Query createQuery(String queryString) {
        return this.entityManager.createQuery(queryString);
    }

    List<? extends Model> selectQuery(Query query) {
        try {
            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param objectToAdd A model to insert into the DB. The model will be mapped automatically to the relevant table.
     */
    synchronized boolean insert(Model objectToAdd) {
        flushManager();
        EntityTransaction transaction = this.entityManager.getTransaction();
        objectToAdd.setCreateTime(LocalDateTime.now());
        objectToAdd.setUpdateTime(LocalDateTime.now());

        try {
            transaction.begin();
            this.entityManager.persist(objectToAdd);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            LogsManager.logException(ex);
            return false;
        }
    }

    void flushManager() {
        try {
            this.entityManager.flush();
        } catch (Exception ignore) {
        }
    }

    /**
     * @param modelCollection A collection of models to insert into the DB.
     *                        The models will be mapped automatically to the relevant table.
     */
    synchronized void insertAll(Collection<? extends Model> modelCollection) {
        EntityTransaction transaction = this.entityManager.getTransaction();


            for (Model model : modelCollection) {
                try {
                flushManager();
                transaction.begin();
                model.setCreateTime(LocalDateTime.now());
                model.setUpdateTime(LocalDateTime.now());
                this.entityManager.persist(model);
                transaction.commit();
                } catch (Exception ex) {
                    LogsManager.logException(ex);
                    System.out.println(ex.getMessage());
                }
            }

    }

    /**
     * @param modelToRemove the model to remove from the DB.
     *                      If the model couldn't be found in the DB, the method will end without an error.
     */
    synchronized void delete(Model modelToRemove) {
        flushManager();

        if (modelToRemove != null) {
            EntityTransaction transaction = this.entityManager.getTransaction();
            try {
                transaction.begin();
                entityManager.remove(modelToRemove);
                transaction.commit();
            } catch (Exception ex) {
                LogsManager.logException(ex);
            }
        }
    }

    /**
     * @param modelCollection A collection of models to remove from the DB.
     *                        If any of the models couldn't be found in the DB it will be ignored.
     */
    synchronized void deleteAll(Collection<? extends Model> modelCollection) {
        flushManager();

        if (modelCollection != null) {
            EntityTransaction transaction = this.entityManager.getTransaction();
            try {
                transaction.begin();
                modelCollection.forEach(entityManager::remove);
                transaction.commit();
            } catch (Exception ex) {
                LogsManager.logException(ex);
            }
        }
    }

    /**
     * @param updatedModel A model object to update.
     *                     Use the model's setters to modify any of it's value.
     *                     Use this method once to save these changes.
     */
    synchronized void update(Model updatedModel) {
        flushManager();

        if (updatedModel != null) {
            EntityTransaction transaction = this.entityManager.getTransaction();
            updatedModel.setUpdateTime(LocalDateTime.now());
            try {
                transaction.begin();
                transaction.commit();
            } catch (Exception ex) {
                LogsManager.logException(ex);
            }
        }
    }
}




