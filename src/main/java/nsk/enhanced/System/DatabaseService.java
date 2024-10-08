package nsk.enhanced.System;

import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.Hibernate.Base.Messages.*;
import nsk.enhanced.System.Hibernate.Base.Messages.Event.Command;
import nsk.enhanced.System.Hibernate.Base.Messages.Event.Death;
import nsk.enhanced.System.Hibernate.Base.Messages.Event.Kick;
import nsk.enhanced.System.Hibernate.Base.Messages.Event.Message;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Coordinates;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Creature;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Event.CreatureInteraction;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Event.MaterialInteraction;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Material;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.Hibernate.WorldEntity;
import org.bukkit.configuration.file.FileConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link DatabaseService} class is responsible for managing Hibernate configuration
 * and providing utility methods to interact with the database, such as saving, deleting
 * and loading entities. It supports both synchronous and asynchronous operations.
 */
public class DatabaseService {

    private static SessionFactory sessionFactory;

    /**
     * Configures Hibernate based on the settings specified in the plugin's configuration file.
     * This method initialize the Hibernate SessionFactory, which is used to create session for
     * interacting with the database.
     */
    public static void configureHibernate() {
        EnhancedLogger.log().warning("Configuring Hibernate...");
        FileConfiguration config = ServerConfiguration.getConfig();

        try {
            String dialect  = config.getString("EnhancedSurveillance.database.dialect");

            String address  = config.getString("EnhancedSurveillance.database.address");
            String port     = config.getString("EnhancedSurveillance.database.port");
            String database = config.getString("EnhancedSurveillance.database.database");

            String username = config.getString("EnhancedSurveillance.database.username");
            String password = config.getString("EnhancedSurveillance.database.password");

            String show_sql     = config.getString("EnhancedSurveillance.hibernate.show_sql");
            String format_sql   = config.getString("EnhancedSurveillance.hibernate.format_sql");
            String sql_comments = config.getString("EnhancedSurveillance.hibernate.sql_comments");

            Configuration cfg = new Configuration()
                    .setProperty("hibernate.dialect", "org.hibernate.dialect." + dialect)
                    .setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")

                    .setProperty("hibernate.connection.url", "jdbc:mysql://" + address + ":" + port + "/" + database)
                    .setProperty("hibernate.connection.username", username)
                    .setProperty("hibernate.connection.password", password)

                    .setProperty("hibernate.hbm2ddl.auto", "update")
                    .setProperty("hibernate.show_sql", show_sql)
                    .setProperty("hibernate.format_sql", format_sql)
                    .setProperty("hibernate.use_sql_comments", sql_comments);

            cfg.addAnnotatedClass(Event.class);
            cfg.addAnnotatedClass(Character.class);
            cfg.addAnnotatedClass(WorldEntity.class);

            cfg.addAnnotatedClass(Original.class);
            cfg.addAnnotatedClass(Message.class);
            cfg.addAnnotatedClass(Command.class);
            cfg.addAnnotatedClass(Death.class);
            cfg.addAnnotatedClass(Kick.class);

            cfg.addAnnotatedClass(Coordinates.class);
            cfg.addAnnotatedClass(Creature.class);
            cfg.addAnnotatedClass(Material.class);
            cfg.addAnnotatedClass(CreatureInteraction.class);
            cfg.addAnnotatedClass(MaterialInteraction.class);

            if (cfg.buildSessionFactory() != null) {
                sessionFactory = cfg.buildSessionFactory();
            } else {
                throw new IllegalStateException("Could not create session factory");
            }

        } catch (Exception e) {
            EnhancedLogger.log().severe("Could not create session factory - " + e.getMessage());
        }
        EnhancedLogger.log().fine("Hibernate loaded");
    }

    /**
     * Closes the Hibernate SessionFactory, releasing all resources associated with it.
     * This method is called in {@link EnhancedSurveillance#onDisable()} on plugin disable.
     */
    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Returns the Hibernate SessionFactory instance.
     * @return the SessionFactory instance.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Saves and updates the given entity in the database.
     *
     * @param entity the entity to be saved or updated
     */
    public static <T> void saveEntity(T entity) {
        //EnhancedLogger.log().warning("Preparing to save entity: " + entity.getClass().getSimpleName());
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            //EnhancedLogger.log().info("Saving entity: " + entity.getClass().getSimpleName());
            session.saveOrUpdate(entity);
            session.getTransaction().commit();
            //EnhancedLogger.log().fine("Saved entity: " + entity.getClass().getSimpleName());
        } catch (Exception e) {
            EnhancedLogger.log().severe("Saving entity failed - " + e.getMessage());
        }
    }

    /**
     * Asynchronously saves or updates the given entity in the database.
     *
     * @param entity the entity to be saved or updated
     * @return a CompletableFuture representing the synchronous operation
     */
    public static <T> CompletableFuture<Void> saveEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {
            EnhancedLogger.log().warning("Saving entity: " + entity);
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                EnhancedLogger.log().info("Saving entity: " + entity.getClass().getSimpleName());
                session.saveOrUpdate(entity);
                session.getTransaction().commit();
                EnhancedLogger.log().fine("Saved entity: " + entity.getClass().getSimpleName());
                session.close();
            } catch (Exception e) {
                EnhancedLogger.log().severe("Saving entity failed - " + e.getMessage());
            }

        });
    }

    /**
     * Asynchronously saves or updates all entities in the given list in the database.
     *
     * @param entities the list of entities to be saved or updated
     * @return a CompletableFuture representing the synchronous operation
     */
    public static <T> CompletableFuture<Void> saveAllEntitiesFromListAsync(List<T> entities) {

        return CompletableFuture.runAsync(() -> {
            EnhancedLogger.log().warning("Saving entities from the list: " + entities);
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                for (T entity : entities) {
                    EnhancedLogger.log().info("Saving entity: " + entity.getClass().getSimpleName());
                    session.saveOrUpdate(entity);
                }

                session.getTransaction().commit();
                EnhancedLogger.log().fine("Saved entities from the list: " + entities);
                session.close();
            } catch (Exception e) {
                EnhancedLogger.log().severe("Saving entities failed - " + e.getMessage());
            }
        });
    }

    /**
     * Asynchronously saves or updated all entities in the given list in the database,
     * with the ability to retry the operation a specified number of times if it fails.
     *
     * @param entities the list of entities to be saved or updated
     * @param maxAttempts the maximum number of retry attempts
     * @return a CompletableFuture representing the synchronous operation
     */
    public static <T> CompletableFuture<Boolean> saveAllEntitiesWithRetry(List<T> entities, int maxAttempts) {

        return saveAllEntitiesFromListAsync(entities).handle((result, ex) -> {
            if (ex == null) {
                return CompletableFuture.completedFuture(true);
            } else if (maxAttempts > 1) {
                EnhancedLogger.log().warning("Save failed, retrying... Attempts left: " + maxAttempts);
                return saveAllEntitiesWithRetry(entities, maxAttempts - 1);
            } else {
                EnhancedLogger.log().severe("Save failed after maximum attempts: " + ex);
                return CompletableFuture.completedFuture(false);
            }
        }).thenCompose(result -> result);

    }

    /**
     * Deletes the given entity from the database.
     *
     * @param entity the entity to be deleted
     */
    public static <T> void deleteEntity(T entity) {
        EnhancedLogger.log().warning("Preparing to delete entity: " + entity.getClass().getSimpleName());
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            EnhancedLogger.log().info("Deleting entity: " + entity.getClass().getSimpleName());
            session.delete(entity);
            session.getTransaction().commit();
            EnhancedLogger.log().fine("Deleted entity: " + entity.getClass().getSimpleName());
        } catch (Exception e) {
            EnhancedLogger.log().severe("Deleting entity failed - " + e.getMessage());
        }
    }

    /**
     * Asynchronously deletes the given entity from the database.
     *
     * @param entity the entity to be deleted
     * @return a CompletableFuture representing the asynchronous operation
     */
    public static <T> CompletableFuture<Void> deleteEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {
            EnhancedLogger.log().warning("Preparing to delete entity: " + entity.getClass().getSimpleName());
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                EnhancedLogger.log().info("Deleting entity: " + entity.getClass().getSimpleName());
                session.delete(entity);
                session.getTransaction().commit();
                EnhancedLogger.log().fine("Deleted entity: " + entity.getClass().getSimpleName());
                session.close();
            } catch (Exception e) {
                EnhancedLogger.log().severe("Deleting entity failed - " + e.getMessage());
            }
        });
    }

    /**
     * Asynchronously deletes all entities in the given list from the database.
     *
     * @param entities the list of entities to be deleted
     * @return a CompletableFuture representing the asynchronous operation
     */
    public static <T> CompletableFuture<Void> deleteAllEntitiesFromListAsync(List<T> entities) {

        return CompletableFuture.runAsync(() -> {
            EnhancedLogger.log().warning("Preparing to delete entities from the list: " + entities);
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                for (T entity : entities) {
                    EnhancedLogger.log().info("Deleting entity: " + entity.getClass().getSimpleName());
                    session.delete(entity);
                }

                session.getTransaction().commit();
                EnhancedLogger.log().fine("Deleted entities from the list: " + entities);
                session.close();
            } catch (Exception e) {
                EnhancedLogger.log().severe("Deleting entities failed - " + e.getMessage());
            }
        });
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Loads all entities of the specified type from the database and adds them to the provided list.
     *
     * @param entity the class of the entities to be loaded
     * @param list the list where the loaded entities will be added
     */
    public static  <T> void loadEntityFromDatabase(Class<T> entity, List<T> list) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery( entity );
            query.from( entity );

            List<T> result = session.createQuery(query).getResultList();
            list.addAll(result);

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Loading " + entity.getSimpleName() + " failed - " + e.getMessage());
        }
    }
    
}
