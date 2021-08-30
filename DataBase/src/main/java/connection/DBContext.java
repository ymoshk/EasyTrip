package connection;

import log.LogsManager;
import model.Model;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A singleton class to connect and process CRUD operations over the DB.
 * This class can only be accessed from 'connection' package.
 */
class DBContext {

    DBContext() {
        // Just to load the session factory as the server loads.
        Session sessionObj = SessionFactoryUtil.getInstance().getNewSession();
        sessionObj.close();
    }

    static DBContext getInstance() {
        return new DBContext();
    }

    /**
     * A method to receive a single model object from the DB.
     *
     * @param modelClass The class type of the requested object - must be 'Model' or inherit from 'Model'.
     * @param id         Represent the id of the requested object inside the DB - long int.
     * @return An 'optional' object contains the requested object or an empty value if nothing was found.
     */
    Optional<Model> findById(Class<?> modelClass, long id) {
        Session sessionObj = SessionFactoryUtil.getInstance().getNewSession();
        Optional<Model> result = Optional.empty();

        if (modelClass.getSuperclass() == Model.class || modelClass == Model.class) {
            Model modelFound = (Model) sessionObj.load(modelClass, id);

            if (modelFound != null) {
                result = Optional.of(modelFound);
            }
        }

        sessionObj.close();
        return result;
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
        try (Session sessionObj = SessionFactoryUtil.getInstance().getNewSession()) {
            Query query = sessionObj.createQuery(queryString);
            return (List<? extends Model>) query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ArrayList<>();
    }

    List<? extends Model> selectQuery(String queryString, int limit) {
        try (Session sessionObj = SessionFactoryUtil.getInstance().getNewSession()) {
            Query query = sessionObj.createQuery(queryString);
            query.setFirstResult(0);
            query.setMaxResults(limit);
            return (List<? extends Model>) query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @param objectToAdd A model to insert into the DB. The model will be mapped automatically to the relevant table.
     */
    boolean insert(Model objectToAdd) {
        try (Session sessionObj = SessionFactoryUtil.getInstance().getNewSession()) {
            Transaction transaction = sessionObj.beginTransaction();
            objectToAdd.setCreateTime(LocalDateTime.now());
            objectToAdd.setUpdateTime(LocalDateTime.now());
            sessionObj.save(objectToAdd);
            transaction.commit();

            return true;
        } catch (Exception ex) {
            LogsManager.logException(ex);
        }

        return false;
    }


    /**
     * @param modelCollection A collection of models to insert into the DB.
     *                        The models will be mapped automatically to the relevant table.
     */
    void insertAll(Collection<? extends Model> modelCollection) {
        Transaction transaction;

        try (Session sessionObj = SessionFactoryUtil.getInstance().getNewSession()) {
            for (Model model : modelCollection) {
                transaction = sessionObj.beginTransaction();
                model.setCreateTime(LocalDateTime.now());
                model.setUpdateTime(LocalDateTime.now());
                sessionObj.save(model);
                transaction.commit();
            }
        } catch (Exception ex) {
            LogsManager.logException(ex);
        }
    }

    /**
     * @param modelToRemove the model to remove from the DB.
     *                      If the model couldn't be found in the DB, the method will end without an error.
     */
    void delete(Model modelToRemove) {
        if (modelToRemove != null) {
            try (Session sessionObj = SessionFactoryUtil.getInstance().getNewSession()) {
                Transaction transaction = sessionObj.beginTransaction();
                sessionObj.delete(modelToRemove);
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
    void deleteAll(Collection<? extends Model> modelCollection) {
        if (modelCollection != null) {
            try (Session sessionObj = SessionFactoryUtil.getInstance().getNewSession()) {
                Transaction transaction = sessionObj.beginTransaction();
                modelCollection.forEach(sessionObj::delete);
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
    void update(Model updatedModel) {
        if (updatedModel != null) {
            try (Session sessionObj = SessionFactoryUtil.getInstance().getNewSession()) {
                Transaction transaction = sessionObj.beginTransaction();
                updatedModel.setUpdateTime(LocalDateTime.now());
                sessionObj.update(updatedModel);
                transaction.commit();
            } catch (Exception ex) {
                LogsManager.logException(ex);
            }
        }
    }
}




