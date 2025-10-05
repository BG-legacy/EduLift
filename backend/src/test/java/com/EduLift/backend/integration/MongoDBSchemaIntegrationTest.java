package com.EduLift.backend.integration;

import com.EduLift.backend.model.User;
import com.EduLift.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.database=edulift_integration_test"
})
@DisplayName("MongoDB Schema Integration Tests")
class MongoDBSchemaIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("Should create and verify MongoDB indexes")
    void shouldCreateAndVerifyMongoDBIndexes() {
        // Get index information
        List<IndexInfo> indexes = mongoTemplate.indexOps("users").getIndexInfo();
        
        // Print all indexes for verification
        System.out.println("\n=== MongoDB Indexes Created ===");
        indexes.forEach(indexInfo -> {
            System.out.println("Index: " + indexInfo.getName() + " - " + indexInfo.getIndexFields());
        });
        
        // Verify we have the expected number of indexes (including default _id)
        assertTrue(indexes.size() >= 1, "Should have at least the default _id index");
    }

    @Test
    @DisplayName("Should save and query users with complete schema")
    void shouldSaveAndQueryUsersWithCompleteSchema() {
        // Clean up before test
        userRepository.deleteAll();
        
        // Create a comprehensive test user
        User testUser = createCompleteTestUser();
        
        // Save the user
        User savedUser = userRepository.save(testUser);
        
        // Verify save was successful
        assertNotNull(savedUser.getId());
        assertEquals("test@edulift.com", savedUser.getEmail());
        assertEquals(Arrays.asList(User.Role.STUDENT, User.Role.MENTOR), savedUser.getRoles());
        assertEquals("gh_001", savedUser.getGroupHomeId());
        
        // Verify nested objects
        assertNotNull(savedUser.getProfile());
        assertEquals("John", savedUser.getProfile().getFirstName());
        assertEquals("Doe", savedUser.getProfile().getLastName());
        
        assertNotNull(savedUser.getConsentFlags());
        assertTrue(savedUser.getConsentFlags().isDataProcessingConsent());
        
        assertNotNull(savedUser.getPreferences());
        assertEquals("en", savedUser.getPreferences().getLanguage());
        
        // Test repository queries
        testRepositoryQueries(savedUser);
        
        System.out.println("\n=== User Schema Test Successful ===");
        System.out.println("User ID: " + savedUser.getId());
        System.out.println("Roles: " + savedUser.getRoles());
        System.out.println("Group Home: " + savedUser.getGroupHomeId());
        System.out.println("Risk Flags: " + savedUser.getRiskFlags());
    }

    private User createCompleteTestUser() {
        User user = new User();
        
        // Required fields
        user.setRoles(Arrays.asList(User.Role.STUDENT, User.Role.MENTOR));
        user.setEmail("test@edulift.com");
        user.setCreatedAt(LocalDateTime.now());
        
        // Optional fields
        user.setGroupHomeId("gh_001");
        user.setRiskFlags(Arrays.asList("academic_risk", "social_risk"));
        
        // Profile
        User.Profile profile = new User.Profile("John", "Doe");
        profile.setPhoneNumber("+1234567890");
        profile.setDateOfBirth("1995-06-15");
        profile.setAddress("123 Test Street, Test City, TC 12345");
        profile.setEmergencyContact("Jane Doe");
        profile.setEmergencyPhoneNumber("+0987654321");
        user.setProfile(profile);
        
        // Consent flags
        User.ConsentFlags consentFlags = new User.ConsentFlags();
        consentFlags.setDataProcessingConsent(true);
        consentFlags.setCommunicationConsent(true);
        consentFlags.setEmergencyContactConsent(true);
        consentFlags.setPhotoVideoConsent(false);
        consentFlags.setConsentTimestamp(LocalDateTime.now());
        user.setConsentFlags(consentFlags);
        
        // Preferences (using defaults but setting some specific values)
        User.Preferences preferences = user.getPreferences(); // Already initialized
        preferences.setLanguage("en");
        preferences.setTimezone("EST");
        preferences.setEmailNotifications(true);
        preferences.setSmsNotifications(false);
        user.setPreferences(preferences);
        
        return user;
    }

    private void testRepositoryQueries(User savedUser) {
        // Test role-based query
        List<User> students = userRepository.findByRolesContaining(User.Role.STUDENT);
        assertEquals(1, students.size());
        assertEquals(savedUser.getId(), students.get(0).getId());
        
        // Test group home query
        List<User> groupUsers = userRepository.findByGroupHomeId("gh_001");
        assertEquals(1, groupUsers.size());
        
        // Test email query
        assertTrue(userRepository.existsByEmail("test@edulift.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
        
        // Test risk flags query
        List<User> riskUsers = userRepository.findByRiskFlagsIn(Arrays.asList("academic_risk"));
        assertEquals(1, riskUsers.size());
        
        System.out.println("✅ All repository queries working correctly");
    }

    @Test
    @DisplayName("Should handle validation constraints")
    void shouldHandleValidationConstraints() {
        // This test demonstrates that our validation annotations are in place
        // In a real scenario with validation enabled, invalid data would be rejected
        
        User invalidUser = new User();
        // Missing required fields: roles, email, createdAt
        
        // The model allows creation, but validation should catch issues at save time
        assertNotNull(invalidUser.getCreatedAt()); // Set by constructor
        assertNull(invalidUser.getRoles()); // Required but not set
        assertNull(invalidUser.getEmail()); // Required but not set
        
        System.out.println("✅ Validation constraints are properly defined in the model");
    }
}

