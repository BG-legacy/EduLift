package com.EduLift.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DisplayName("User Model Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should create user with default constructor")
    void shouldCreateUserWithDefaultConstructor() {
        assertNotNull(user);
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertNotNull(user.getPreferences());
        assertEquals("en", user.getPreferences().getLanguage());
        assertEquals("UTC", user.getPreferences().getTimezone());
        assertTrue(user.getPreferences().isEmailNotifications());
        assertFalse(user.getPreferences().isSmsNotifications());
        assertTrue(user.getPreferences().isPushNotifications());
    }

    @Test
    @DisplayName("Should create user with roles and email constructor")
    void shouldCreateUserWithRolesAndEmailConstructor() {
        List<User.Role> roles = Arrays.asList(User.Role.STUDENT);
        String email = "test@example.com";
        
        User testUser = new User(roles, email);
        
        assertNotNull(testUser);
        assertEquals(roles, testUser.getRoles());
        assertEquals(email, testUser.getEmail());
        assertNotNull(testUser.getCreatedAt());
        assertNotNull(testUser.getPreferences());
    }

    @Test
    @DisplayName("Should set and get all user fields correctly")
    void shouldSetAndGetAllFieldsCorrectly() {
        // Test basic fields
        user.setId("test-id");
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setGroupHomeId("gh_001");

        assertEquals("test-id", user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("gh_001", user.getGroupHomeId());

        // Test roles
        List<User.Role> roles = Arrays.asList(User.Role.STUDENT, User.Role.MENTOR);
        user.setRoles(roles);
        assertEquals(roles, user.getRoles());

        // Test risk flags
        List<String> riskFlags = Arrays.asList("academic_risk", "behavioral_risk");
        user.setRiskFlags(riskFlags);
        assertEquals(riskFlags, user.getRiskFlags());
    }

    @Test
    @DisplayName("Should create and set user profile correctly")
    void shouldCreateAndSetUserProfileCorrectly() {
        User.Profile profile = new User.Profile("John", "Doe");
        profile.setPhoneNumber("+1234567890");
        profile.setDateOfBirth("1990-01-15");
        profile.setAddress("123 Main St, City, State");
        profile.setEmergencyContact("Jane Doe");
        profile.setEmergencyPhoneNumber("+0987654321");

        user.setProfile(profile);

        assertNotNull(user.getProfile());
        assertEquals("John", user.getProfile().getFirstName());
        assertEquals("Doe", user.getProfile().getLastName());
        assertEquals("+1234567890", user.getProfile().getPhoneNumber());
        assertEquals("1990-01-15", user.getProfile().getDateOfBirth());
        assertEquals("123 Main St, City, State", user.getProfile().getAddress());
        assertEquals("Jane Doe", user.getProfile().getEmergencyContact());
        assertEquals("+0987654321", user.getProfile().getEmergencyPhoneNumber());
    }

    @Test
    @DisplayName("Should create and set consent flags correctly")
    void shouldCreateAndSetConsentFlagsCorrectly() {
        User.ConsentFlags consentFlags = new User.ConsentFlags();
        LocalDateTime now = LocalDateTime.now();
        
        consentFlags.setDataProcessingConsent(true);
        consentFlags.setCommunicationConsent(true);
        consentFlags.setEmergencyContactConsent(false);
        consentFlags.setPhotoVideoConsent(true);
        consentFlags.setConsentTimestamp(now);

        user.setConsentFlags(consentFlags);

        assertNotNull(user.getConsentFlags());
        assertTrue(user.getConsentFlags().isDataProcessingConsent());
        assertTrue(user.getConsentFlags().isCommunicationConsent());
        assertFalse(user.getConsentFlags().isEmergencyContactConsent());
        assertTrue(user.getConsentFlags().isPhotoVideoConsent());
        assertEquals(now, user.getConsentFlags().getConsentTimestamp());
    }

    @Test
    @DisplayName("Should create and set preferences correctly")
    void shouldCreateAndSetPreferencesCorrectly() {
        User.Preferences preferences = new User.Preferences();
        preferences.setLanguage("es");
        preferences.setTimezone("EST");
        preferences.setEmailNotifications(false);
        preferences.setSmsNotifications(true);
        preferences.setPushNotifications(false);

        user.setPreferences(preferences);

        assertNotNull(user.getPreferences());
        assertEquals("es", user.getPreferences().getLanguage());
        assertEquals("EST", user.getPreferences().getTimezone());
        assertFalse(user.getPreferences().isEmailNotifications());
        assertTrue(user.getPreferences().isSmsNotifications());
        assertFalse(user.getPreferences().isPushNotifications());
    }

    @Test
    @DisplayName("Should test role enum values")
    void shouldTestRoleEnumValues() {
        assertEquals("student", User.Role.STUDENT.getValue());
        assertEquals("mentor", User.Role.MENTOR.getValue());
        assertEquals("counselor", User.Role.COUNSELOR.getValue());
        assertEquals("admin", User.Role.ADMIN.getValue());
    }

    @Test
    @DisplayName("Should generate proper toString representation")
    void shouldGenerateProperToStringRepresentation() {
        user.setId("test-id");
        user.setEmail("test@example.com");
        user.setRoles(Arrays.asList(User.Role.STUDENT));
        user.setGroupHomeId("gh_001");

        String toString = user.toString();
        
        assertTrue(toString.contains("test-id"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("STUDENT"));
        assertTrue(toString.contains("gh_001"));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        user.setProfile(null);
        user.setConsentFlags(null);
        user.setRoles(null);
        user.setRiskFlags(null);

        assertNull(user.getProfile());
        assertNull(user.getConsentFlags());
        assertNull(user.getRoles());
        assertNull(user.getRiskFlags());
    }

    @Test
    @DisplayName("Should create profile with empty constructor")
    void shouldCreateProfileWithEmptyConstructor() {
        User.Profile profile = new User.Profile();
        assertNotNull(profile);
        assertNull(profile.getFirstName());
        assertNull(profile.getLastName());
    }

    @Test
    @DisplayName("Should create consent flags with default values")
    void shouldCreateConsentFlagsWithDefaultValues() {
        User.ConsentFlags consentFlags = new User.ConsentFlags();
        assertNotNull(consentFlags);
        // Default boolean values should be false
        assertFalse(consentFlags.isDataProcessingConsent());
        assertFalse(consentFlags.isCommunicationConsent());
        assertFalse(consentFlags.isEmergencyContactConsent());
        assertFalse(consentFlags.isPhotoVideoConsent());
    }
}

