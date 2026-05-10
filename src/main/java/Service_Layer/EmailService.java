package Service_Layer;


import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

import Domain_Layer.NotificationMessage;
import io.github.cdimascio.dotenv.Dotenv;

public class EmailService {

    private final String username;
    private final String password;
    private static EmailService instance;
    private EmailService() {
		this.username = "";
		this.password = "";
    	  
    }
 

   
    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void sendEmail(String to, String subject, String body) {
    	
        // SMTP configuration
        Properties props = new Properties();
        
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // e.g., Gmail SMTP
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Build email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Send email
            Transport.send(message);

            System.out.println("Email sent successfully to " + to);

        } catch (MessagingException e) {
          
            throw new RuntimeException(" Failed to send email", e);
        }
    }
    
    
    public void sendNotification(NotificationMessage msg) {
        sendEmail(msg.getRecipientEmail(), msg.getSubject(), msg.getBody());
    }

    // نسخة محسّنة من run
    public static void run(NotificationMessage msg) {
        Dotenv dotenv = Dotenv.load();  
        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");

        EmailService emailService = new EmailService(username, password);
        emailService.sendNotification(msg);
    }

}
