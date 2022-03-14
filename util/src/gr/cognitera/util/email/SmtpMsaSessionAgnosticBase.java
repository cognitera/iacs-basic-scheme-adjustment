package gr.cognitera.util.email;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import java.io.UnsupportedEncodingException;

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



public abstract class SmtpMsaSessionAgnosticBase implements ISmtpMsa {


    abstract protected Session getSession();
    
    @Override
    public void post(final String from, final String fromPersonal, final String to, final String subject, final String body, final boolean htmlMail) throws MessagingException, UnsupportedEncodingException {
        post(from, fromPersonal, to, subject, body, htmlMail, new ArrayList<ByteArrayDataSource>());
    }

    @Override
    public void post(final String from, final String fromPersonal, final String to, final String subject, final String body, final boolean htmlMail, final List<ByteArrayDataSource> dss) throws MessagingException, UnsupportedEncodingException {
        Session session = getSession();
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from, fromPersonal));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        // Create the message part 
        BodyPart messageBodyPart = new MimeBodyPart();
        // Fill the message
        if (htmlMail)
            messageBodyPart.setContent(body==null?"":body, "text/html; charset=utf-8");
        else
            messageBodyPart.setText(body==null?"":body);
         
        // Create a multipart message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        for (ByteArrayDataSource ds: dss) {
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(ds));
            messageBodyPart.setFileName(ds.getName());
            multipart.addBodyPart(messageBodyPart);
        }

        // Send the complete message parts
        message.setContent(multipart );

        Transport t = session.getTransport("smtp");
        try {
            t.connect();
            t.sendMessage(message, message.getAllRecipients());
        } finally {
            t.close();
        }
    }

}
