package Service_Layer;


import Persistence_Layer.UserAccountExistsChecker;
import Persistence_Layer.UserAccountSaver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRegistrationServiceTest {

    private UserRegistrationService service;

    @BeforeEach
    void setUp() {
        service = UserRegistrationService.getInstance();
    }

    // ============================
    // اختبارات Singleton
    // ============================

    @Test
    @DisplayName("getInstance should always return the same instance")
    void testSingletonInstance() {
        UserRegistrationService i1 = UserRegistrationService.getInstance();
        UserRegistrationService i2 = UserRegistrationService.getInstance();
        assertSame(i1, i2);
    }

    @Test
    @DisplayName("getInstance should not return null")
    void testSingletonNotNull() {
        assertNotNull(UserRegistrationService.getInstance());
    }

    // ============================
    // اختبارات registerUser - تسجيل ناجح
    // ============================

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterUser_Success() throws IOException {
        try (MockedStatic<UserAccountExistsChecker> existsStatic = mockStatic(UserAccountExistsChecker.class);
             MockedStatic<UserAccountSaver> saverStatic = mockStatic(UserAccountSaver.class)) {

            UserAccountExistsChecker mockChecker = mock(UserAccountExistsChecker.class);
            UserAccountSaver mockSaver = mock(UserAccountSaver.class);

            existsStatic.when(UserAccountExistsChecker::getInstance).thenReturn(mockChecker);
            saverStatic.when(UserAccountSaver::getInstance).thenReturn(mockSaver);

            when(mockChecker.userExists("new@test.com")).thenReturn(false);

            String result = service.registerUser("new@test.com", "pass123");

            assertEquals("Account created successfully!", result);
            verify(mockSaver).saveUser("new@test.com", "pass123");
        }
    }

    // ============================
    // اختبارات registerUser - الحساب موجود
    // ============================

    @Test
    @DisplayName("Should return error when account already exists")
    void testRegisterUser_AlreadyExists() throws IOException {
        try (MockedStatic<UserAccountExistsChecker> existsStatic = mockStatic(UserAccountExistsChecker.class);
             MockedStatic<UserAccountSaver> saverStatic = mockStatic(UserAccountSaver.class)) {

            UserAccountExistsChecker mockChecker = mock(UserAccountExistsChecker.class);
            UserAccountSaver mockSaver = mock(UserAccountSaver.class);

            existsStatic.when(UserAccountExistsChecker::getInstance).thenReturn(mockChecker);
            saverStatic.when(UserAccountSaver::getInstance).thenReturn(mockSaver);

            when(mockChecker.userExists("existing@test.com")).thenReturn(true);

            String result = service.registerUser("existing@test.com", "pass123");

            assertEquals("Account already exists!", result);
            verify(mockSaver, never()).saveUser(anyString(), anyString());
        }
    }

    // ============================
    // اختبارات registerUser - خطأ في الحفظ
    // ============================

    @Test
    @DisplayName("Should return error when saving fails with IOException")
    void testRegisterUser_SaveIOException() throws IOException {
        try (MockedStatic<UserAccountExistsChecker> existsStatic = mockStatic(UserAccountExistsChecker.class);
             MockedStatic<UserAccountSaver> saverStatic = mockStatic(UserAccountSaver.class)) {

            UserAccountExistsChecker mockChecker = mock(UserAccountExistsChecker.class);
            UserAccountSaver mockSaver = mock(UserAccountSaver.class);

            existsStatic.when(UserAccountExistsChecker::getInstance).thenReturn(mockChecker);
            saverStatic.when(UserAccountSaver::getInstance).thenReturn(mockSaver);

            when(mockChecker.userExists("new@test.com")).thenReturn(false);
            doThrow(new IOException("Disk full")).when(mockSaver).saveUser("new@test.com", "pass123");

            String result = service.registerUser("new@test.com", "pass123");

            assertTrue(result.contains("Error saving account"));
            assertTrue(result.contains("Disk full"));
        }
    }

    // ============================
    // اختبارات إضافية
    // ============================

    @Test
    @DisplayName("Should not save user when account already exists")
    void testRegisterUser_NoSaveWhenExists() throws IOException {
        try (MockedStatic<UserAccountExistsChecker> existsStatic = mockStatic(UserAccountExistsChecker.class);
             MockedStatic<UserAccountSaver> saverStatic = mockStatic(UserAccountSaver.class)) {

            UserAccountExistsChecker mockChecker = mock(UserAccountExistsChecker.class);
            UserAccountSaver mockSaver = mock(UserAccountSaver.class);

            existsStatic.when(UserAccountExistsChecker::getInstance).thenReturn(mockChecker);
            saverStatic.when(UserAccountSaver::getInstance).thenReturn(mockSaver);

            when(mockChecker.userExists("existing@test.com")).thenReturn(true);

            service.registerUser("existing@test.com", "pass123");

            verify(mockSaver, never()).saveUser(anyString(), anyString());
        }
    }

    @Test
    @DisplayName("Should call saveUser with correct email and password")
    void testRegisterUser_CorrectCredentialsSaved() throws IOException {
        try (MockedStatic<UserAccountExistsChecker> existsStatic = mockStatic(UserAccountExistsChecker.class);
             MockedStatic<UserAccountSaver> saverStatic = mockStatic(UserAccountSaver.class)) {

            UserAccountExistsChecker mockChecker = mock(UserAccountExistsChecker.class);
            UserAccountSaver mockSaver = mock(UserAccountSaver.class);

            existsStatic.when(UserAccountExistsChecker::getInstance).thenReturn(mockChecker);
            saverStatic.when(UserAccountSaver::getInstance).thenReturn(mockSaver);

            when(mockChecker.userExists("user@email.com")).thenReturn(false);

            service.registerUser("user@email.com", "securePass!");

            verify(mockSaver).saveUser("user@email.com", "securePass!");
        }
    }

    @Test
    @DisplayName("Should call userExists with correct email")
    void testRegisterUser_ChecksCorrectEmail() {
        try (MockedStatic<UserAccountExistsChecker> existsStatic = mockStatic(UserAccountExistsChecker.class);
             MockedStatic<UserAccountSaver> saverStatic = mockStatic(UserAccountSaver.class)) {

            UserAccountExistsChecker mockChecker = mock(UserAccountExistsChecker.class);
            UserAccountSaver mockSaver = mock(UserAccountSaver.class);

            existsStatic.when(UserAccountExistsChecker::getInstance).thenReturn(mockChecker);
            saverStatic.when(UserAccountSaver::getInstance).thenReturn(mockSaver);

            when(mockChecker.userExists("check@test.com")).thenReturn(true);

            service.registerUser("check@test.com", "pass");

            verify(mockChecker).userExists("check@test.com");
        }
    }
}
