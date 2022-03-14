package gr.cognitera.util.email;

import java.util.List;

import java.io.UnsupportedEncodingException;

import javax.mail.util.ByteArrayDataSource;
import javax.mail.MessagingException;

// SMTP Message Submission Agent
public interface ISmtpMsa {


    void post(String from, String fromPersonal, String to, String subject, String body, boolean htmlMail, List<ByteArrayDataSource> ds) throws MessagingException , UnsupportedEncodingException;
    void post(String from, String fromPersonal, String to, String subject, String body, boolean htmlMail)                               throws MessagingException, UnsupportedEncodingException;


}
