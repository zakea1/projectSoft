package Domain_Layer;

public class NotificationMessage {
    private String recipientEmail;
    private String subject;
    private String body;

    public NotificationMessage(String recipientEmail, String subject, String body) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
    }

    public String getRecipientEmail() { return recipientEmail; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
}
