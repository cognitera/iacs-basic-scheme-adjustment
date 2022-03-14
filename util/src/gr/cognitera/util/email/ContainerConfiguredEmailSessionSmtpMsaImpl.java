package gr.cognitera.util.email;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;

import org.junit.Assert;

// SMTP Message Submission Agent
public class ContainerConfiguredEmailSessionSmtpMsaImpl extends SmtpMsaSessionAgnosticBase {

    private final String jndiName;

    public ContainerConfiguredEmailSessionSmtpMsaImpl(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override
    protected Session getSession() {
        try {
            InitialContext ic = new InitialContext();
            Session session = (Session)ic.lookup(this.jndiName);
            Assert.assertNotNull(session);
            return session;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
