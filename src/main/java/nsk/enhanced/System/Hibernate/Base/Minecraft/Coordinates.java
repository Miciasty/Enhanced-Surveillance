package nsk.enhanced.System.Hibernate.Base.Minecraft;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import org.bukkit.Location;
import org.hibernate.Session;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Entity
@Table(name = "surveillance_coordinates")
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int x, y, z;

    /**
     * Default constructor for JPA.
     */
    public Coordinates() {}

    public Coordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Retrieves {@link Coordinates} based on the provided ID.
     *
     * @param id The ID of the coordinates to retrieve.
     * @return The {@link Coordinates} object with the specified ID, or null if not found.
     */
    public static Coordinates getCoordinates(int id) {
        Coordinates coordinates = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Coordinates> query = builder.createQuery(Coordinates.class);
            Root<Coordinates> root = query.from(Coordinates.class);

            query.select(root).where(builder.equal(root.get("id"), id));

            try {
                coordinates = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe(e.getMessage());
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load coordinates with id:" + id + ", " + e.getMessage());
        }

        return coordinates;
    }

    /**
     * Retrieves {@link Coordinates} based on the provided x, y, and z values.
     *
     * @param x The x-coordinate value.
     * @param y The y-coordinate value.
     * @param z The z-coordinate value.
     * @return The {@link Coordinates} object matching the specified coordinates, or null if not found.
     */
    public static Coordinates getCoordinatesByXYZ(int x, int y, int z) {
        Coordinates coordinates = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Coordinates> query = builder.createQuery(Coordinates.class);
            Root<Coordinates> root = query.from(Coordinates.class);

            query.select(root).where(
                    builder.equal(root.get("x"), x),
                    builder.equal(root.get("y"), y),
                    builder.equal(root.get("z"), z)
            );

            try {
                coordinates = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe("No coordinates found for x=" + x + ", y=" + y + ", z=" + z + ": " + e.getMessage());
                EnhancedLogger.log().fine("Creating new coordinates with x=" + x + ", y=" + y + ", z=" + z);

                coordinates = new Coordinates(x, y, z);
                addCoordinates(coordinates);

            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load coordinates for x=" + x + ", y=" + y + ", z=" + z + ": " + e.getMessage());
        }

        return coordinates;
    }

    /**
     * <p>
     * Retrieves {@link Coordinates} based on the provided {@link Location} object.
     * </p>
     *
     * <p>This method extracts the x, y, and z values from the given {@link Location}
     * and uses them to find the corresponding {@link Coordinates} in the database.
     * If no matching coordinates are found, the method will return null.</p>
     *
     * @param location The {@link Location} object from which the x, y, and z values will be extracted.
     * @return The {@link Coordinates} object that matches the specified x, y, and z values, or null if not found.
     */
    public static Coordinates getCoordinatesByLocation(Location location) {

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return getCoordinatesByXYZ(x, y, z);
    }

    /**
     * Adds new {@link Coordinates} in the database.
     *
     * @param coordinates the {@link Coordinates} to add
     */
    public static void addCoordinates(Coordinates coordinates) {
        try {
            DatabaseService.saveEntity(coordinates);
        } catch (Exception e) {
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

    /**
     * Removes the specified {@link Coordinates} from the database.
     *
     * @param coordinates the {@link Coordinates} to remove
     */
    public static void removeCoordinates(Coordinates coordinates) {
        try {
            DatabaseService.deleteEntity(coordinates);
        } catch (Exception e) {
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

}
