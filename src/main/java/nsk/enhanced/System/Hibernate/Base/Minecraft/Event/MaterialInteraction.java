package nsk.enhanced.System.Hibernate.Base.Minecraft.Event;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Coordinates;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Creature;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Material;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.Location;
import org.hibernate.Session;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * <p>
 * The {@link MaterialInteraction} class represents an interaction event that involves a player interacting
 * with a block in the game world.
 * </p>
 *
 * <p>
 * This class captures player interactions where:
 * <ul>
 *     <li>A player uses a {@link Material} (an item) to interact with a {@link Material} block in the game world.</li>
 *     <li>The interaction happens at specific {@link Coordinates}.</li>
 * </ul>
 * The interaction is always linked to an {@link Event}.
 * </p>
 */
@Entity
@Table(name = "surveillance_interactions")
public class MaterialInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = true)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "block_id", nullable = true)
    private Material block;

    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    // --- --- --- --- --- --- CONSTRUCTORS --- --- --- --- --- --- //

    /**
     * Default constructor for JPA.
     */
    public MaterialInteraction() {
    }

    /**
     * Constructor for creating a new {@link MaterialInteraction} instance.
     *
     * @param event       The {@link Event} associated with the interaction.
     * @param material    The name of the material (item) used by the player during the interaction.
     * @param block       The name of the block the player interacted with.
     * @param location    The {@link Location} where the interaction took place, used to retrieve the {@link Coordinates}.
     */
    public MaterialInteraction(Event event, String material, String block, Location location) {
        this.event          = event;
        this.material       = Material.getMaterialByName(material);
        this.block          = Material.getMaterialByName(material);
        this.coordinates    = Coordinates.getCoordinatesByLocation(location);
    }

    // --- --- --- --- --- --- GETTERS --- --- --- --- --- --- //

    /**
     * Returns the ID of this {@link MaterialInteraction}.
     *
     * @return the interaction ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the {@link Event} associated with this {@link MaterialInteraction}.
     *
     * @return the associated {@link Event}
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Returns the {@link Material} (item) used by the player in this interaction.
     *
     * @return the {@link Material} used by the player, or null if none
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Returns the block {@link Material} the player interacted with.
     *
     * @return the {@link Material} block involved in the interaction, or null if none
     */
    public Material getBlock() {
        return block;
    }

    /**
     * Returns the {@link Coordinates} where this {@link MaterialInteraction} occurred.
     *
     * @return the {@link Coordinates} of the interaction
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Retrieves a {@link MaterialInteraction} from the database by its ID.
     *
     * @param id The ID of the interaction.
     * @return The {@link MaterialInteraction} with the specified ID, or null if not found.
     */
    public static MaterialInteraction getInteractionById(int id) {
        MaterialInteraction interaction = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MaterialInteraction> query = builder.createQuery(MaterialInteraction.class);
            Root<MaterialInteraction> root = query.from(MaterialInteraction.class);

            query.select(root).where(builder.equal(root.get("id"), id));

            try {
                interaction = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe("No interaction found with ID: " + id);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load interaction with ID: " + id + " - " + e.getMessage());
        }

        return interaction;
    }

}