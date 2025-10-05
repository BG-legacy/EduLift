package com.EduLift.backend.manual;

import com.EduLift.backend.model.User;
import com.EduLift.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Manual demonstration of MongoDB schema functionality
 * Run this as a standalone application to test the schema
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.EduLift.backend")
public class MongoSchemaDemo implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(MongoSchemaDemo.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n🚀 MongoDB Schema Demo Starting...");
        
        try {
            // 1. Test MongoDB Connection
            testConnection();
            
            // 2. Show MongoDB Indexes
            showIndexes();
            
            // 3. Test User Creation and Schema
            testUserSchema();
            
            // 4. Test Repository Queries
            testRepositoryQueries();
            
            System.out.println("\n✅ MongoDB Schema Demo Completed Successfully!");
            
        } catch (Exception e) {
            System.err.println("\n❌ Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Exit the application
        System.exit(0);
    }

    private void testConnection() {
        System.out.println("\n📡 Testing MongoDB Connection...");
        try {
            // Test basic connection
            String dbName = mongoTemplate.getDb().getName();
            System.out.println("✅ Connected to database: " + dbName);
            
            // Get collection stats
            if (mongoTemplate.collectionExists("users")) {
                long userCount = mongoTemplate.getCollection("users").countDocuments();
                System.out.println("📊 Users collection exists with " + userCount + " documents");
            } else {
                System.out.println("📊 Users collection will be created on first insert");
            }
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            throw e;
        }
    }

    private void showIndexes() {
        System.out.println("\n🔍 MongoDB Indexes:");
        try {
            List<IndexInfo> indexes = mongoTemplate.indexOps("users").getIndexInfo();
            
            if (indexes.isEmpty()) {
                System.out.println("📝 No custom indexes found - they will be created when the application starts");
            } else {
                for (IndexInfo indexInfo : indexes) {
                    System.out.println("📌 " + indexInfo.getName() + ": " + indexInfo.getIndexFields());
                    if (indexInfo.isUnique()) {
                        System.out.println("   🔒 UNIQUE constraint");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting indexes: " + e.getMessage());
        }
    }

    private void testUserSchema() {
        System.out.println("\n👤 Testing User Schema...");
        
        // Clean up any existing test data
        userRepository.deleteAll();
        
        // Create a comprehensive test user
        User testUser = createCompleteTestUser();
        
        // Save the user
        User savedUser = userRepository.save(testUser);
        
        System.out.println("✅ User saved with ID: " + savedUser.getId());
        System.out.println("📧 Email: " + savedUser.getEmail());
        System.out.println("🏷️  Roles: " + savedUser.getRoles());
        System.out.println("🏠 Group Home: " + savedUser.getGroupHomeId());
        System.out.println("⚠️  Risk Flags: " + savedUser.getRiskFlags());
        
        // Verify nested objects
        if (savedUser.getProfile() != null) {
            System.out.println("👥 Profile: " + savedUser.getProfile().getFirstName() + " " + savedUser.getProfile().getLastName());
        }
        
        if (savedUser.getConsentFlags() != null) {
            System.out.println("✅ Consent: Data=" + savedUser.getConsentFlags().isDataProcessingConsent() + 
                             ", Communication=" + savedUser.getConsentFlags().isCommunicationConsent());
        }
        
        if (savedUser.getPreferences() != null) {
            System.out.println("🌐 Preferences: Language=" + savedUser.getPreferences().getLanguage() + 
                             ", Email notifications=" + savedUser.getPreferences().isEmailNotifications());
        }
    }

    private void testRepositoryQueries() {
        System.out.println("\n🔍 Testing Repository Queries...");
        
        // Test role-based query
        List<User> students = userRepository.findByRolesContaining(User.Role.STUDENT);
        System.out.println("👨‍🎓 Found " + students.size() + " students");
        
        // Test group home query
        List<User> groupUsers = userRepository.findByGroupHomeId("gh_demo_001");
        System.out.println("🏠 Found " + groupUsers.size() + " users in group home gh_demo_001");
        
        // Test email existence
        boolean emailExists = userRepository.existsByEmail("john.doe@edulift.demo");
        System.out.println("📧 Email exists: " + emailExists);
        
        // Test risk flags query
        List<User> riskUsers = userRepository.findByRiskFlagsIn(Arrays.asList("academic_risk"));
        System.out.println("⚠️  Found " + riskUsers.size() + " users with academic risk");
        
        // Test count queries
        long studentCount = userRepository.countByRolesContaining(User.Role.STUDENT);
        System.out.println("📊 Total students: " + studentCount);
    }

    private User createCompleteTestUser() {
        User user = new User();
        
        // Required fields
        user.setRoles(Arrays.asList(User.Role.STUDENT, User.Role.MENTOR));
        user.setEmail("john.doe@edulift.demo");
        user.setCreatedAt(LocalDateTime.now());
        
        // Optional fields
        user.setGroupHomeId("gh_demo_001");
        user.setRiskFlags(Arrays.asList("academic_risk", "social_risk"));
        
        // Profile
        User.Profile profile = new User.Profile("John", "Doe");
        profile.setPhoneNumber("+1-555-123-4567");
        profile.setDateOfBirth("1995-03-15");
        profile.setAddress("123 Demo Street, Test City, TC 12345");
        profile.setEmergencyContact("Jane Doe");
        profile.setEmergencyPhoneNumber("+1-555-987-6543");
        user.setProfile(profile);
        
        // Consent flags
        User.ConsentFlags consentFlags = new User.ConsentFlags();
        consentFlags.setDataProcessingConsent(true);
        consentFlags.setCommunicationConsent(true);
        consentFlags.setEmergencyContactConsent(true);
        consentFlags.setPhotoVideoConsent(false);
        consentFlags.setConsentTimestamp(LocalDateTime.now());
        user.setConsentFlags(consentFlags);
        
        // Preferences (already initialized with defaults)
        User.Preferences preferences = user.getPreferences();
        preferences.setLanguage("en");
        preferences.setTimezone("EST");
        preferences.setEmailNotifications(true);
        preferences.setSmsNotifications(false);
        user.setPreferences(preferences);
        
        return user;
    }
}

