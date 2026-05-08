package Service_Layer;


import Domain_Layer.NotificationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private EmailService service;

    @BeforeEach
    void setUp() {
        service = EmailService.getInstance();
    }

    // ============================
    // اختبارات Singleton Pattern
    // ============================

    @Test
    @DisplayName("getInstance should always return the same instance")
    void testSingletonInstance() {
        EmailService instance1 = EmailService.getInstance();
        EmailService instance2 = EmailService.getInstance();
        assertSame(instance1, instance2, "Singleton should return the same instance");
    }

    @Test
    @DisplayName("getInstance should not return null")
    void testSingletonNotNull() {
        EmailService instance = EmailService.getInstance();
        assertNotNull(instance, "Singleton instance should not be null");
    }

    // ============================
    // اختبارات Constructor
    // ============================

    @Test
    @DisplayName("Parameterized constructor should create instance with credentials")
    void testParameterizedConstructor() {
        EmailService emailService = new EmailService("test@gmail.com", "pass123");
        assertNotNull(emailService, "Should create instance with username and password");
    }

    // ============================
    // اختبارات sendEmail
    // ============================

    @Test
    @DisplayName("sendEmail should throw RuntimeException with invalid credentials")
    void testSendEmail_InvalidCredentials() {
        EmailService emailService = new EmailService("invalid@gmail.com", "wrongpass");

        assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail("recipient@gmail.com", "Test Subject", "Test Body");
        }, "Should throw RuntimeException when email sending fails");
    }

    @Test
    @DisplayName("sendEmail should throw RuntimeException with empty credentials")
    void testSendEmail_EmptyCredentials() {
        EmailService emailService = new EmailService("", "");

        assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail("recipient@gmail.com", "Test Subject", "Test Body");
        }, "Should throw RuntimeException with empty credentials");
    }

    // ============================
    // اختبارات sendNotification
    // ============================

    @Test
    @DisplayName("sendNotification should call sendEmail with correct parameters")
    void testSendNotification_CallsSendEmail() {
        // نعمل Spy على EmailService عشان نتتبع استدعاء sendEmail
        EmailService spyService = Mockito.spy(new EmailService("test@gmail.com", "pass123"));

        // نمنع الإرسال الفعلي
        doNothing().when(spyService).sendEmail(anyString(), anyString(), anyString());

        NotificationMessage msg = mock(NotificationMessage.class);
        when(msg.getRecipientEmail()).thenReturn("user@gmail.com");
        when(msg.getSubject()).thenReturn("Appointment Reminder");
        when(msg.getBody()).thenReturn("Your appointment is tomorrow");

        spyService.sendNotification(msg);

        // نتأكد إن sendEmail انادت بالقيم الصحيحة
        verify(spyService).sendEmail(
                "user@gmail.com",
                "Appointment Reminder",
                "Your appointment is tomorrow"
        );
    }

    @Test
    @DisplayName("sendNotification should use correct recipient email from message")
    void testSendNotification_CorrectRecipient() {
        EmailService spyService = Mockito.spy(new EmailService("test@gmail.com", "pass123"));
        doNothing().when(spyService).sendEmail(anyString(), anyString(), anyString());

        NotificationMessage msg = mock(NotificationMessage.class);
        when(msg.getRecipientEmail()).thenReturn("specific@email.com");
        when(msg.getSubject()).thenReturn("Subject");
        when(msg.getBody()).thenReturn("Body");

        spyService.sendNotification(msg);

        verify(spyService).sendEmail(eq("specific@email.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("sendNotification should use correct subject from message")
    void testSendNotification_CorrectSubject() {
        EmailService spyService = Mockito.spy(new EmailService("test@gmail.com", "pass123"));
        doNothing().when(spyService).sendEmail(anyString(), anyString(), anyString());

        NotificationMessage msg = mock(NotificationMessage.class);
        when(msg.getRecipientEmail()).thenReturn("user@gmail.com");
        when(msg.getSubject()).thenReturn("Important Subject");
        when(msg.getBody()).thenReturn("Body content");

        spyService.sendNotification(msg);

        verify(spyService).sendEmail(anyString(), eq("Important Subject"), anyString());
    }

    @Test
    @DisplayName("sendNotification should use correct body from message")
    void testSendNotification_CorrectBody() {
        EmailService spyService = Mockito.spy(new EmailService("test@gmail.com", "pass123"));
        doNothing().when(spyService).sendEmail(anyString(), anyString(), anyString());

        NotificationMessage msg = mock(NotificationMessage.class);
        when(msg.getRecipientEmail()).thenReturn("user@gmail.com");
        when(msg.getSubject()).thenReturn("Subject");
        when(msg.getBody()).thenReturn("Detailed body message here");

        spyService.sendNotification(msg);

        verify(spyService).sendEmail(anyString(), anyString(), eq("Detailed body message here"));
    }
}
