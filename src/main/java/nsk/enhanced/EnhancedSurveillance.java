package nsk.enhanced;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.ChatEvent.Command;
import nsk.enhanced.System.Hibernate.ChatEvent.Message;
import nsk.enhanced.System.Hibernate.ChatEvent.Original;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.Hibernate.WorldEntity;
import nsk.enhanced.System.MemoryService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class EnhancedSurveillance extends JavaPlugin {

    private EnhancedLogger enhancedLogger;

    private SessionFactory sessionFactory;

    @Override
    public void onEnable() {

        ES.setInstance(this);
        enhancedLogger = new EnhancedLogger(this);

        MemoryService.initializeServices(1, 1);

        ServerConfiguration.load();

        configureHibernate();

        loadEntityFromDatabase(Character.class, Character.getCharacters());
        loadEntityFromDatabase(WorldEntity.class, WorldEntity.getWorlds());

        EventsConfiguration.load();

    }

    @Override
    public void onDisable() {

        for (Player player : getServer().getOnlinePlayers()) {

            Map<String, String> eventData = new LinkedHashMap<>();
            eventData.put("ip", player.getAddress().getAddress().getHostAddress());

            Event e = new Event("quit", player, player.getLocation(), eventData);

            try {
                MonitorManager.saveEvent(e);
            } catch (Exception ex) {
                enhancedLogger.severe("Failed to save PlayerEvents/quit - " + ex.getMessage());
            }

        }

        MemoryService.shutdownAllServices();
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //




    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void configureHibernate() {
        enhancedLogger.warning("Configuring Hibernate...");
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

            if (cfg.buildSessionFactory() != null) {
                sessionFactory = cfg.buildSessionFactory();
            } else {
                throw new IllegalStateException("Could not create session factory");
            }

        } catch (Exception e) {
            enhancedLogger.severe("Could not create session factory - " + e.getMessage());
        }
        enhancedLogger.fine("Hibernate loaded");
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    public <T> void saveEntity(T entity) {
        //enhancedLogger.warning("Preparing to save entity: " + entity.getClass().getSimpleName());
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            //enhancedLogger.info("Saving entity: " + entity.getClass().getSimpleName());
            session.saveOrUpdate(entity);
            session.getTransaction().commit();
            //enhancedLogger.fine("Saved entity: " + entity.getClass().getSimpleName());
        } catch (Exception e) {
            enhancedLogger.severe("Saving entity failed - " + e.getMessage());
        }
    }
    public <T> CompletableFuture<Void> saveEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {
            enhancedLogger.warning("Saving entity: " + entity);
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                enhancedLogger.info("Saving entity: " + entity.getClass().getSimpleName());
                session.saveOrUpdate(entity);
                session.getTransaction().commit();
                enhancedLogger.fine("Saved entity: " + entity.getClass().getSimpleName());
                session.close();
            } catch (Exception e) {
                enhancedLogger.severe("Saving entity failed - " + e.getMessage());
            }

        });
    }
    public <T> CompletableFuture<Void> saveAllEntitiesFromListAsync(List<T> entities) {

        return CompletableFuture.runAsync(() -> {
            enhancedLogger.warning("Saving entities from the list: " + entities);
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                for (T entity : entities) {
                    enhancedLogger.info("Saving entity: " + entity.getClass().getSimpleName());
                    session.saveOrUpdate(entity);
                }

                session.getTransaction().commit();
                enhancedLogger.fine("Saved entities from the list: " + entities);
                session.close();
            } catch (Exception e) {
                enhancedLogger.severe("Saving entities failed - " + e.getMessage());
            }
        });
    }
    public <T> CompletableFuture<Boolean> saveAllEntitiesWithRetry(List<T> entities, int maxAttempts) {

        return saveAllEntitiesFromListAsync(entities).handle((result, ex) -> {
            if (ex == null) {
                return CompletableFuture.completedFuture(true);
            } else if (maxAttempts > 1) {
                enhancedLogger.warning("Save failed, retrying... Attempts left: " + maxAttempts);
                return saveAllEntitiesWithRetry(entities, maxAttempts - 1);
            } else {
                enhancedLogger.severe("Save failed after maximum attempts: " + ex);
                return CompletableFuture.completedFuture(false);
            }
        }).thenCompose(result -> result);

    }

    public <T> void deleteEntity(T entity) {
        enhancedLogger.warning("Preparing to delete entity: " + entity.getClass().getSimpleName());
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            enhancedLogger.info("Deleting entity: " + entity.getClass().getSimpleName());
            session.delete(entity);
            session.getTransaction().commit();
            enhancedLogger.fine("Deleted entity: " + entity.getClass().getSimpleName());
        } catch (Exception e) {
            enhancedLogger.severe("Deleting entity failed - " + e.getMessage());
        }
    }
    public <T> CompletableFuture<Void> deleteEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {
            enhancedLogger.warning("Preparing to delete entity: " + entity.getClass().getSimpleName());
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                enhancedLogger.info("Deleting entity: " + entity.getClass().getSimpleName());
                session.delete(entity);
                session.getTransaction().commit();
                enhancedLogger.fine("Deleted entity: " + entity.getClass().getSimpleName());
                session.close();
            } catch (Exception e) {
                enhancedLogger.severe("Deleting entity failed - " + e.getMessage());
            }
        });
    }
    public <T> CompletableFuture<Void> deleteAllEntitiesFromListAsync(List<T> entities) {

        return CompletableFuture.runAsync(() -> {
            enhancedLogger.warning("Preparing to delete entities from the list: " + entities);
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                for (T entity : entities) {
                    enhancedLogger.info("Deleting entity: " + entity.getClass().getSimpleName());
                    session.delete(entity);
                }

                session.getTransaction().commit();
                enhancedLogger.fine("Deleted entities from the list: " + entities);
                session.close();
            } catch (Exception e) {
                enhancedLogger.severe("Deleting entities failed - " + e.getMessage());
            }
        });
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private <T> void loadEntityFromDatabase(Class<T> entity, List<T> list) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery( entity );
            query.from( entity );

            List<T> result = session.createQuery(query).getResultList();
            list.addAll(result);

            session.getTransaction().commit();
        } catch (Exception e) {
            enhancedLogger.severe("Loading " + entity.getSimpleName() + " failed - " + e.getMessage());
        }
    }


}
