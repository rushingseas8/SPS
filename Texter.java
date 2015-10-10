import com.sun.mail.smtp.SMTPTransport;
import java.security.Security;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * A small class that handles the SMS api.
 */
public class Texter {
    public static void send(String number, String message) {
        try {
            Send("glassbeardhack", "glassbeardece", (number + "@txt.att.net"), "", "", message);
        } catch (Exception e) {}
    }

    public static void sendDirect(String address, String message) {
        try {
            Send("glassbeardhack", "glassbeardece", address, "", "", message);
        } catch (Exception e) {}
    }

    public static void sendGroup(String message, String... addresses) {
        /*
         * "8477705977@txt.att.net"
         * "6467501926@txt.att.net"
         * "8473872018@tmomail.net"
         */
        
        for(int i = 0; i < addresses.length; i++) {
            try {
                Send("glassbeardhack", "glassbeardece", addresses[i], "", "", message);
            } catch (Exception e) {}
        }
    }

    private static void Send(final String username, final String password,
    String recipientEmail, String ccEmail, String title, String message)
    throws AddressException, MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
        msg.setSubject("HOME SECURITY ALERT");
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
    }
}