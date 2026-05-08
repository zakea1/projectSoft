package Service_Layer;


import Persistence_Layer.UserAccountLoginChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserLoginServiceTest {

    private UserLoginService service;

    @BeforeEach
    void setUp() {
        service = UserLoginService.getInstance();
    }

    // ============================
    // اختبارات Singleton Pattern
    // ============================

    @Test
    @DisplayName("getInstance should always return the same instance")
    void testSingletonInstance() {
        UserLoginService instance1 = UserLoginService.getInstance();
        UserLoginService instance2 = UserLoginService.getInstance();
        assertSame(instance1, instance2, "Singleton should return the same instance");
    }

    @Test
    @DisplayName("getInstance should not return null")
    void testSingletonNotNull() {
        assertNotNull(UserLoginService.getInstance(), "Singleton instance should not be null");
    }

    // ============================
    // اختبارات checkLogin - بيانات صحيحة
    // ============================

    @Test
    @DisplayName("Should return success message with valid credentials")
    void testCheckLogin_ValidCredentials() {
        try (MockedStatic<UserAccountLoginChecker> mockedStatic = mockStatic(UserAccountLoginChecker.class)) {
            UserAccountLoginChecker mockChecker = mock(UserAccountLoginChecker.class);
            mockedStatic.when(UserAccountLoginChecker::getInstance).thenReturn(mockChecker);
            when(mockChecker.checkCredentials("admin@test.com", "password123")).thenReturn(true);

            String result = service.checkLogin("admin@test.com", "password123");

            assertEquals("Login successful", result);
            verify(mockChecker).checkCredentials("admin@test.com", "password123");
        }
    }

    // ============================
    // اختبارات checkLogin - بيانات خاطئة
    // ============================

    @Test
    @DisplayName("Should return error message with invalid password")
    void testCheckLogin_InvalidPassword() {
        try (MockedStatic<UserAccountLoginChecker> mockedStatic = mockStatic(UserAccountLoginChecker.class)) {
            UserAccountLoginChecker mockChecker = mock(UserAccountLoginChecker.class);
            mockedStatic.when(UserAccountLoginChecker::getInstance).thenReturn(mockChecker);
            when(mockChecker.checkCredentials("admin@test.com", "wrongpass")).thenReturn(false);

            String result = service.checkLogin("admin@test.com", "wrongpass");

            assertEquals("Invalid email or password", result);
        }
    }

    @Test
    @DisplayName("Should return error message with invalid email")
    void testCheckLogin_InvalidEmail() {
        try (MockedStatic<UserAccountLoginChecker> mockedStatic = mockStatic(UserAccountLoginChecker.class)) {
            UserAccountLoginChecker mockChecker = mock(UserAccountLoginChecker.class);
            mockedStatic.when(UserAccountLoginChecker::getInstance).thenReturn(mockChecker);
            when(mockChecker.checkCredentials("wrong@test.com", "password123")).thenReturn(false);

            String result = service.checkLogin("wrong@test.com", "password123");

            assertEquals("Invalid email or password", result);
        }
    }

    @Test
    @DisplayName("Should return error message with empty credentials")
    void testCheckLogin_EmptyCredentials() {
        try (MockedStatic<UserAccountLoginChecker> mockedStatic = mockStatic(UserAccountLoginChecker.class)) {
            UserAccountLoginChecker mockChecker = mock(UserAccountLoginChecker.class);
            mockedStatic.when(UserAccountLoginChecker::getInstance).thenReturn(mockChecker);
            when(mockChecker.checkCredentials("", "")).thenReturn(false);

            String result = service.checkLogin("", "");

            assertEquals("Invalid email or password", result);
        }
    }

    @Test
    @DisplayName("Should return error message with null credentials")
    void testCheckLogin_NullCredentials() {
        try (MockedStatic<UserAccountLoginChecker> mockedStatic = mockStatic(UserAccountLoginChecker.class)) {
            UserAccountLoginChecker mockChecker = mock(UserAccountLoginChecker.class);
            mockedStatic.when(UserAccountLoginChecker::getInstance).thenReturn(mockChecker);
            when(mockChecker.checkCredentials(null, null)).thenReturn(false);

            String result = service.checkLogin(null, null);

            assertEquals("Invalid email or password", result);
        }
    }

    // ============================
    // التأكد من استدعاء الـ Checker
    // ============================

    @Test
    @DisplayName("checkLogin should call checkCredentials exactly once")
    void testCheckLogin_CallsCheckerOnce() {
        try (MockedStatic<UserAccountLoginChecker> mockedStatic = mockStatic(UserAccountLoginChecker.class)) {
            UserAccountLoginChecker mockChecker = mock(UserAccountLoginChecker.class);
            mockedStatic.when(UserAccountLoginChecker::getInstance).thenReturn(mockChecker);
            when(mockChecker.checkCredentials(anyString(), anyString())).thenReturn(true);

            service.checkLogin("user@test.com", "pass");

            verify(mockChecker, times(1)).checkCredentials("user@test.com", "pass");
        }
    }
}
