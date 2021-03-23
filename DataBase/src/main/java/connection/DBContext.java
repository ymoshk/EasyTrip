package connection;

import log.LogsManager;
import model.Model;

import javax.persistence.*;
import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A singleton class to connect and process CRUD operations over the DB.
 */
public class DBContext implements Closeable {

    private static DBContext instance = null;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    private DBContext() {
        entityManagerFactory = Persistence
                .createEntityManagerFactory("EasyTrip");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    // only one thread can execute this method at the same time.
    public static synchronized DBContext getInstance() {
        if (instance == null) {
            instance = new DBContext();
        }
        return instance;
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
     * @param modelClass The class type of the requested object - must be 'Model' or inherit from 'Model'.
     * @return A list of all the records inside the DB of the requested class
     * or an empty list if there are no records of the requested class.
     */
    public List<?> getToList(Class<?> modelClass) {
        try {
            Query query = this.entityManager.createQuery("from " + modelClass.getSimpleName());
            return query.getResultList();
        } catch (Exception ex) {
            LogsManager.logException(ex);
            return new ArrayList<>();
        }
    }

    /**
     * @param modelClass The class type of the requested object - must be 'Model' or inherit from 'Model'.
     * @return A stream of all the records inside the DB of the requested class
     * or an empty stream if there are no records of the requested class.
     */
    public Stream<?> getToStream(Class<?> modelClass) {
        return getToList(modelClass).stream();
    }

    /**
     * A method to receive a single model object from the DB.
     *
     * @param modelClass The class type of the requested object - must be 'Model' or inherit from 'Model'.
     * @param id         Represent the id of the requested object inside the DB - long int.
     * @return An 'optional' object contains the requested object or an empty value if nothing was found.
     */
    public Optional<Model> findById(Class<?> modelClass, long id) {
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
    public List<?> selectQuery(String queryString) {
        try {
            Query query = this.entityManager.createQuery(queryString);
            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param objectToAdd A model to insert into the DB. The model will be mapped automatically to the relevant table.
     */
    public void insert(Model objectToAdd) {
        EntityTransaction transaction = this.entityManager.getTransaction();
        objectToAdd.setCreateTime(LocalDateTime.now());
        objectToAdd.setUpdateTime(LocalDateTime.now());
        try {
            transaction.begin();
            this.entityManager.persist(objectToAdd);
            transaction.commit();
        } catch (Exception ex) {
            LogsManager.logException(ex);
        }
    }

    /**
     * @param modelCollection A collection of models to insert into the DB.
     *                        The models will be mapped automatically to the relevant table.
     */
    public void insertAll(Collection<Model> modelCollection) {
        EntityTransaction transaction = this.entityManager.getTransaction();
        try {
            transaction.begin();
            for (Model model : modelCollection) {
                model.setCreateTime(LocalDateTime.now());
                model.setUpdateTime(LocalDateTime.now());
                this.entityManager.persist(model);
            }
            transaction.commit();
        } catch (Exception ex) {
            LogsManager.logException(ex);
        }
    }

    /**
     * @param modelToRemove the model to remove from the DB.
     *                      If the model couldn't be found in the DB, the method will end without an error.
     */
    public void delete(Model modelToRemove) {
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
    public void deleteAll(Collection<Model> modelCollection) {
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
    public void update(Model updatedModel) {
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




