package nsk.enhanced.System.Hibernate.ChatEvent;

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

@Entity
@Table(name = "surveillance_originals")
public class Original {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "message")
    private String message;

    public Original() { }

    public Original(String message) {
        this.message = message;
    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    public static Original getMessage(int id) {
        Original originalMessage = null;

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
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

    public static Original getMessage(String message) {
        Original originalMessage = null;

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
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

                ES.getInstance().saveEntity(originalMessage);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load original message " + e.getMessage());
        }

        return originalMessage;
    }

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
