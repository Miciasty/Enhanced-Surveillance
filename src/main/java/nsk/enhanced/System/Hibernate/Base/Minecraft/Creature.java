package nsk.enhanced.System.Hibernate.Base.Minecraft;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import org.hibernate.Session;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Entity
@Table(name = "surveillance_creatures")
public class Creature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Default constructor for JPA.
     */
    public Creature() {}

    /**
     * Constructs a {@link Creature} instance with the given name.
     *
     * @param name the name of the creature
     */
    public Creature(String name) {
        this.name = name;
    }

    /**
     * Returns the ID of the {@link Creature}.
     *
     * @return the creature ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the {@link Creature}.
     *
     * @return the creature name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the {@link Creature}.
     *
     * @param name creature's new name
     */
    public void setName(String name) {
        this.name = name;
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Retrieves a {@link Creature} from the database by its ID.
     *
     * @param id the ID of the creature
     * @return the {@link Creature} object, or null if no result is found
     */
    public static Creature getCreatureById(int id) {
        Creature creature = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Creature> query = builder.createQuery(Creature.class);
            Root<Creature> root = query.from(Creature.class);

            query.select(root).where(builder.equal(root.get("id"), id));

            try {
                creature = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe("No creature found with id: " + id);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load creature with id: " + id + ", " + e.getMessage());
        }

        return creature;
    }

    /**
     * Retrieves a {@link Creature} from the database by its name.
     *
     * @param name the name of the creature
     * @return the {@link Creature} object, or null if no result is found
     */
    public static Creature getCreatureByName(String name) {
        Creature creature = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Creature> query = builder.createQuery(Creature.class);
            Root<Creature> root = query.from(Creature.class);

            query.select(root).where(builder.equal(root.get("name"), name));

            try {
                creature = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe("No creature found with name: " + name);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load creature with name: " + name + ", " + e.getMessage());
        }

        return creature;
    }

    /**
     * Adds a new {@link Creature} to the database.
     *
     * @param creature the {@link Creature} to add
     */
    public static void addCreature(Creature creature) {
        try {
            DatabaseService.saveEntity(creature);
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to add creature: " + e.getMessage());
        }
    }

    /**
     * Removes a {@link Creature} from the database.
     *
     * @param creature the {@link Creature} to remove
     */
    public static void removeCreature(Creature creature) {
        try {
            DatabaseService.deleteEntity(creature);
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to remove creature: " + e.getMessage());
        }
    }
    
}
