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

/* For this client using GMail make sure you enable "Access for less secure apps" in your Google
   account as explained here:

    http://stackoverflow.com/a/31919138/274677

    So basically go here:
        https://www.google.com/settings/security/lesssecureapps

    ... and enable access for less secure apps. Just make sure you are logged in at the right
    Gmail account when you do so.
 */
public class GMailSMTP extends SmtpMsaImpl {

    public GMailSMTP(String _username, String _password) {
        super("smtp.gmail.com", 587, _username, _password);
    }
}
