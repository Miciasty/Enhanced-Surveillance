package nsk.enhanced;

import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.*;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class EnhancedSurveillance extends JavaPlugin {

    private EnhancedLogger enhancedLogger;

    private File configFile;
    private FileConfiguration config;

    private File translationsFile;
    private FileConfiguration translations;

    private SessionFactory sessionFactory;

    // private List<Character> characters = new ArrayList<>();
    // private List<WorldEntity> worlds = new ArrayList<>();

    @Override
    public void onEnable() {

        ES.setInstance(this);
        enhancedLogger = new EnhancedLogger(this);

        MemoryService.initializeServices(1, 1);

        loadConfiguration();
        //loadTranslations();

        configureHibernate();

        // loadCharactersFromDatabase();
        // loadWorldsFromDatabase();

        loadBukkitPlayerEventListeners();

    }

    @Override
    public void onDisable() {


        MemoryService.shutdownAllServices();
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void loadBukkitPlayerEventListeners() {
        enhancedLogger.warning("Preparing to load Bukkit PlayerEvent listeners...");

        try {getServer().getPluginManager().registerEvents(new JoinEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("JoinEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new QuitEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("QuitEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new MoveEvent(), this);} catch (Exception e) {enhancedLogger.severe("MoveEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new ChatEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("ChatEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new InteractEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("InteractEvent listener is not loaded!");
        }

    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void loadConfiguration() {
        enhancedLogger.warning("Loading configuration...");
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        //boolean isEnabled = config.getBoolean("EnhancedOres.settings.enabled");
        //enhancedLogger.info("Config enabled: " + isEnabled);
    }
    private FileConfiguration getConfigFile() {
        return config;
    }
    private void saveConfigFile() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            enhancedLogger.log(Level.SEVERE, "Failed to save config file", e);
        }
    }

    private void loadTranslations() {
        enhancedLogger.warning("Loading translations...");
        translationsFile = new File(getDataFolder(), "translations.yml");
        if (!translationsFile.exists()) {
            translationsFile.getParentFile().mkdirs();
            saveResource("translations.yml", false);
        }

        translations = YamlConfiguration.loadConfiguration(translationsFile);

        //boolean isEnabled = translations.getBoolean("EnhancedOres.settings.enabled");
        //enhancedLogger.info("Translations enabled: " + isEnabled);
    }
    public FileConfiguration getTranslationsFile() {
        return translations;
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void reloadConfiguration() {
        try {
            loadTranslations();
            loadConfiguration();
            enhancedLogger.fine("Reloaded configuration");

        } catch (Exception e) {
            enhancedLogger.severe("Failed to reload configuration. - " + e);
        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void configureHibernate() {
        enhancedLogger.warning("Configuring Hibernate...");
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

    public EnhancedLogger getEnhancedLogger() {
        return enhancedLogger;
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

    /*private void loadCharactersFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Character> query = builder.createQuery(Character.class);
            query.from(Character.class);

            List<Character> result = session.createQuery(query).getResultList();
            characters.addAll(result);

            session.getTransaction().commit();
        } catch (Exception e) {
            enhancedLogger.severe("Loading characters failed - " + e.getMessage());
        }
    }
    private void loadWorldsFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<WorldEntity> query = builder.createQuery(WorldEntity.class);
            query.from(WorldEntity.class);

            List<WorldEntity> result = session.createQuery(query).getResultList();
            worlds.addAll(result);

            session.getTransaction().commit();
        } catch (Exception e) {
            enhancedLogger.severe("Loading worlds failed - " + e.getMessage());
        }
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void addCharacter(Character character) {
        characters.add(character);
    }

    public List<WorldEntity> getWorlds() {
        return worlds;
    }

    public void addWorld(WorldEntity worldEntity) {
        worlds.add(worldEntity);
    }*/

}
