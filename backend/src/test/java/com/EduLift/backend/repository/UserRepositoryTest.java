package com.EduLift.backend.repository;

import com.EduLift.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestPropertySource(properties = {
    "spring.data.mongodb.database=edulift_test"
})
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        userRepository.deleteAll();

        // Create test users
        testUser1 = createTestUser(
            "user1@example.com",
            "testuser1",
            Arrays.asList(User.Role.STUDENT),
            "gh_001",
            Arrays.asList("academic_risk")
        );

        testUser2 = createTestUser(
            "user2@example.com",
            "testuser2",
            Arrays.asList(User.Role.MENTOR, User.Role.COUNSELOR),
            "gh_001",
            Arrays.asList("behavioral_risk")
        );

        testUser3 = createTestUser(
            "user3@example.com",
            "testuser3",
            Arrays.asList(User.Role.ADMIN),
            "gh_002",
            Arrays.asList("academic_risk", "emotional_risk")
        );

        // Save test users
        userRepository.saveAll(Arrays.asList(testUser1, testUser2, testUser3));
    }

    private User createTestUser(String email, String username, List<User.Role> roles, 
                               String groupHomeId, List<String> riskFlags) {
        User user = new User(roles, email);
        user.setUsername(username);
        user.setGroupHomeId(groupHomeId);
        user.setRiskFlags(riskFlags);

        // Set up profile
        User.Profile profile = new User.Profile("Test", "User");
        profile.setPhoneNumber("+1234567890");
        user.setProfile(profile);

        // Set up consent flags
        User.ConsentFlags consentFlags = new User.ConsentFlags();
        consentFlags.setDataProcessingConsent(true);
        consentFlags.setCommunicationConsent(email.contains("user1") ? false : true);
        consentFlags.setConsentTimestamp(LocalDateTime.now());
        user.setConsentFlags(consentFlags);

        return user;
    }

    @Test
    @DisplayName("Should save and find user by email")
    void shouldSaveAndFindUserByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("user1@example.com");
        
        assertTrue(foundUser.isPresent());
        assertEquals("user1@example.com", foundUser.get().getEmail());
        assertEquals("testuser1", foundUser.get().getUsername());
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        Optional<User> foundUser = userRepository.findByUsername("testuser2");
        
        assertTrue(foundUser.isPresent());
        assertEquals("user2@example.com", foundUser.get().getEmail());
        assertTrue(foundUser.get().getRoles().contains(User.Role.MENTOR));
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        assertTrue(userRepository.existsByEmail("user1@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should check if user exists by username")
    void shouldCheckIfUserExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser1"));
        assertFalse(userRepository.existsByUsername("nonexistentuser"));
    }

    @Test
    @DisplayName("Should find users by role")
    void shouldFindUsersByRole() {
        List<User> students = userRepository.findByRolesContaining(User.Role.STUDENT);
        List<User> mentors = userRepository.findByRolesContaining(User.Role.MENTOR);
        List<User> admins = userRepository.findByRolesContaining(User.Role.ADMIN);

        assertEquals(1, students.size());
        assertEquals("user1@example.com", students.get(0).getEmail());

        assertEquals(1, mentors.size());
        assertEquals("user2@example.com", mentors.get(0).getEmail());

        assertEquals(1, admins.size());
        assertEquals("user3@example.com", admins.get(0).getEmail());
    }

    @Test
    @DisplayName("Should find users by group home ID")
    void shouldFindUsersByGroupHomeId() {
        List<User> usersInGh001 = userRepository.findByGroupHomeId("gh_001");
        List<User> usersInGh002 = userRepository.findByGroupHomeId("gh_002");

        assertEquals(2, usersInGh001.size());
        assertEquals(1, usersInGh002.size());
        assertEquals("user3@example.com", usersInGh002.get(0).getEmail());
    }

    @Test
    @DisplayName("Should find users by group home ID and role")
    void shouldFindUsersByGroupHomeIdAndRole() {
        List<User> studentsInGh001 = userRepository.findByGroupHomeIdAndRolesContaining("gh_001", User.Role.STUDENT);
        List<User> mentorsInGh001 = userRepository.findByGroupHomeIdAndRolesContaining("gh_001", User.Role.MENTOR);

        assertEquals(1, studentsInGh001.size());
        assertEquals("user1@example.com", studentsInGh001.get(0).getEmail());

        assertEquals(1, mentorsInGh001.size());
        assertEquals("user2@example.com", mentorsInGh001.get(0).getEmail());
    }

    @Test
    @DisplayName("Should find users by risk flags")
    void shouldFindUsersByRiskFlags() {
        List<User> academicRiskUsers = userRepository.findByRiskFlagsIn(Arrays.asList("academic_risk"));
        List<User> behavioralRiskUsers = userRepository.findByRiskFlagsIn(Arrays.asList("behavioral_risk"));

        assertEquals(2, academicRiskUsers.size()); // user1 and user3
        assertEquals(1, behavioralRiskUsers.size()); // user2
    }

    @Test
    @DisplayName("Should find users by multiple roles")
    void shouldFindUsersByMultipleRoles() {
        List<User> studentsAndMentors = userRepository.findByRolesIn(
            Arrays.asList(User.Role.STUDENT, User.Role.MENTOR)
        );

        assertEquals(2, studentsAndMentors.size());
        // Should include user1 (STUDENT) and user2 (MENTOR)
    }

    @Test
    @DisplayName("Should check if users exist by group home ID")
    void shouldCheckIfUsersExistByGroupHomeId() {
        assertTrue(userRepository.existsByGroupHomeId("gh_001"));
        assertTrue(userRepository.existsByGroupHomeId("gh_002"));
        assertFalse(userRepository.existsByGroupHomeId("gh_999"));
    }

    @Test
    @DisplayName("Should count users by role")
    void shouldCountUsersByRole() {
        long studentCount = userRepository.countByRolesContaining(User.Role.STUDENT);
        long mentorCount = userRepository.countByRolesContaining(User.Role.MENTOR);
        long counselorCount = userRepository.countByRolesContaining(User.Role.COUNSELOR);
        long adminCount = userRepository.countByRolesContaining(User.Role.ADMIN);

        assertEquals(1, studentCount);
        assertEquals(1, mentorCount);
        assertEquals(1, counselorCount); // user2 has both MENTOR and COUNSELOR
        assertEquals(1, adminCount);
    }

    @Test
    @DisplayName("Should count users by group home ID")
    void shouldCountUsersByGroupHomeId() {
        long countGh001 = userRepository.countByGroupHomeId("gh_001");
        long countGh002 = userRepository.countByGroupHomeId("gh_002");

        assertEquals(2, countGh001);
        assertEquals(1, countGh002);
    }

    @Test
    @DisplayName("Should find users by data processing consent")
    void shouldFindUsersByDataProcessingConsent() {
        List<User> consentUsers = userRepository.findByDataProcessingConsent(true);
        List<User> noConsentUsers = userRepository.findByDataProcessingConsent(false);

        assertEquals(3, consentUsers.size()); // All test users have data processing consent
        assertEquals(0, noConsentUsers.size());
    }

    @Test
    @DisplayName("Should find users by communication consent")
    void shouldFindUsersByCommunicationConsent() {
        List<User> communicationConsentUsers = userRepository.findByCommunicationConsent(true);
        List<User> noCommunicationConsentUsers = userRepository.findByCommunicationConsent(false);

        assertEquals(2, communicationConsentUsers.size()); // user2 and user3
        assertEquals(1, noCommunicationConsentUsers.size()); // user1
    }

    @Test
    @DisplayName("Should find users by preference language")
    void shouldFindUsersByPreferenceLanguage() {
        List<User> englishUsers = userRepository.findByPreferenceLanguage("en");
        
        assertEquals(3, englishUsers.size()); // All users have default language "en"
    }

    @Test
    @DisplayName("Should find users with email notifications enabled")
    void shouldFindUsersWithEmailNotificationsEnabled() {
        List<User> emailNotificationUsers = userRepository.findUsersWithEmailNotificationsEnabled();
        
        assertEquals(3, emailNotificationUsers.size()); // All users have default email notifications enabled
    }

    @Test
    @DisplayName("Should handle empty results gracefully")
    void shouldHandleEmptyResultsGracefully() {
        List<User> nonExistentRole = userRepository.findByGroupHomeId("gh_999");
        List<User> nonExistentRiskFlag = userRepository.findByRiskFlagsIn(Arrays.asList("non_existent_risk"));

        assertTrue(nonExistentRole.isEmpty());
        assertTrue(nonExistentRiskFlag.isEmpty());
    }

    @Test
    @DisplayName("Should maintain data integrity with required fields")
    void shouldMaintainDataIntegrityWithRequiredFields() {
        // Try to save user without required fields - this should work but validation might catch it
        User invalidUser = new User();
        invalidUser.setUsername("invaliduser");
        // Missing required fields: roles, email, createdAt (createdAt is set in constructor)

        // This test verifies that the model allows creation but validation annotations should catch issues
        assertNotNull(invalidUser.getCreatedAt()); // Should be set by constructor
        assertNull(invalidUser.getRoles()); // Required field not set
        assertNull(invalidUser.getEmail()); // Required field not set
    }
}
