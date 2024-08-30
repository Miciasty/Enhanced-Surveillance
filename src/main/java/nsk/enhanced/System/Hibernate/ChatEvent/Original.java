package nsk.enhanced.System.Hibernate.ChatEvent;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import org.hibernate.Session;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Formatter;

/**
 * The {@link Original} class represents the original form of a {@link Message} and {@link Command} sent by a player on the server,
 * stored in the `surveillance_originals` database table.
 * This class is responsible for managing the storage and retrieval of these original messages.
 */
@Entity
@Table(name = "surveillance_originals")
public class Original {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "message")
    private String message;

    /**
     * Default constructor for JPA.
     */
    public Original() { }

    /**
     * Constructs a new {@link Original} instance with the specified message.
     *
     * @param message the original message content
     */
    public Original(String message) {
        this.message = message;
    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    /**
     * Returns the ID of this {@link Original}.
     *
     * @return the ID of the original message
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the {@link Original} message content.
     *
     * @return the original message as a String
     */
    public String getMessage() {
        return message;
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Retrieves an {@link Original} class by its ID from the database.
     *
     * @param id the ID of the {@link Original} message to retrieve
     * @return the {@link Original} class with the specified ID, or null if not found
     */
    public static Original getMessage(int id) {
        Original originalMessage = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Original> query = builder.createQuery(Original.class);
            Root<Original> root = query.from(Original.class);

            query.select(root).where(builder.equal(root.get("id"), id));

            try {
                originalMessage = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().severe(e.getMessage());
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load original message with id:" + id + ", " + e.getMessage());
        }

        return originalMessage;
        }

    /**
     * Retrieves an {@link Original} class by its content from the database.
     * If the message does not exist in the database, a new {@link Original} is created, saved, and returned.
     *
     * @param message the content of the original message to retrieve or create
     * @return the {@link Original} class with the specified content
     */
    public static Original getMessage(String message) {
        Original originalMessage = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Original> query = builder.createQuery(Original.class);
            Root<Original> root = query.from(Original.class);

            query.select(root).where(builder.equal(root.get("message"), message));

            try {
                originalMessage = session.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                EnhancedLogger.log().warning("No original message found!");

                originalMessage = new Original(message);
                EnhancedLogger.log().info("Saving new original message: " + message);

                DatabaseService.saveEntity(originalMessage);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load original message " + e.getMessage());
        }

        return originalMessage;
    }

    /**
     * Generates a SHA-256 hash of the provided message.
     *
     * @param message the message to hash
     * @return the SHA-256 hash of the message as a hexadecimal string, or null if an error occurs
     */
    public static String getSHA256Hash(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHex(hash);

        } catch (Exception e) {
            EnhancedLogger.log().severe(e.getMessage());
            return null;
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes the byte array to convert
     * @return the hexadecimal string representation of the byte array
     */
    private static String byteArrayToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
