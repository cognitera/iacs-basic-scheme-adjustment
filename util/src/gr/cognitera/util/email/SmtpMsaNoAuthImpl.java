package gr.cognitera.util.email;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;




// SMTP Message Submission Agent
public class SmtpMsaNoAuthImpl extends SmtpMsaSessionAgnosticBase {

    private final String host;

    public SmtpMsaNoAuthImpl(String _host) {
        this.host = _host;
    }

    @Override
    protected Session getSession() {
        Properties props = System.getProperties();
        // Setup mail server
        props.setProperty("mail.smtp.auth"           , "false");
        props.setProperty("mail.smtp.starttls.enable", "false");
        props.setProperty("mail.smtp.host"           , this.host);
        props.setProperty("mail.smtp.port"           , "25");
        // Get the default Session object.
        return Session.getDefaultInstance(props);
    }


    public void postOld(String from, String to, String subject, String body, List<ByteArrayDataSource> ds) throws MessagingException {
        Properties props = System.getProperties();

        // Setup mail server
        props.setProperty("mail.smtp.auth"           , "false");
        props.setProperty("mail.smtp.starttls.enable", "false");
        props.setProperty("mail.smtp.host"           , this.host);
        props.setProperty("mail.smtp.port"           , "25");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(props);

        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(body);
        Transport t = session.getTransport("smtp");
        try {
            t.connect();
            t.sendMessage(message, message.getAllRecipients());
        } finally {
            t.close();
        }
    }


}
