package nsk.enhanced.System.Hibernate.Base.Minecraft;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import org.hibernate.Session;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Entity
@Table(name = "surveillance_materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Default constructor for JPA.
     */
    public Material() {}

    /**
     * Constructs a {@link Material} instance with the given ID and name.
     *
     * @param name the name of the material
     */
    public Material(String name) {
        this.name = name;
    }

    /**
     * Returns the ID of the {@link Material}.
     *
     * @return the material ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the {@link Material}.
     *
     * @return the material name
     */
    public String getName() {
        return name;
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Retrieves a {@link Material} from the database by its ID.
     *
     * @param id the ID of the material
     * @return the {@link Material} object, or null if no result is found
     */
    public static Material getMaterialById(int id) {
        Material material = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Material> query = builder.createQuery(Material.class);
            Root<Material> root = query.from(Material.class);

            query.select(root).where(builder.equal(root.get("id"), id));

            try {
                material = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe("No material found with id: " + id);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load material with id: " + id + ", " + e.getMessage());
        }

        return material;
    }

    /**
     * Retrieves a {@link Material} from the database by its name.
     *
     * @param name the name of the material
     * @return the {@link Material} object, or null if no result is found
     */
    public static Material getMaterialByName(String name) {
        Material material = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Material> query = builder.createQuery(Material.class);
            Root<Material> root = query.from(Material.class);

            query.select(root).where(builder.equal(root.get("name"), name));

            try {
                material = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe("No material found with name: " + name);
                EnhancedLogger.log().fine("Creating material with name: " + name);

                material = new Material(name);
                addMaterial(material);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load material with name: " + name + ", " + e.getMessage());
        }

        return material;
    }

    /**
     * Adds new {@link Material} in the database.
     *
     * @param material the {@link Material} to add
     */
    public static void addMaterial(Material material) {
        try {
            DatabaseService.saveEntity(material);
        } catch (Exception e) {
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

    /**
     * Removes the specified {@link Material} from the database.
     *
     * @param material the {@link Material} to remove
     */
    public static void removeMaterial(Material material) {
        try {
            DatabaseService.deleteEntity(material);
        } catch (Exception e) {
            EnhancedLogger.log().severe(e.getMessage());
        }
    }
}
