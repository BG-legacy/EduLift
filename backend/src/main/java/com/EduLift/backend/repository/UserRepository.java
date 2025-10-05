package com.EduLift.backend.repository;

import com.EduLift.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by username
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    // New query methods for the updated schema
    
    /**
     * Find users by role
     */
    List<User> findByRolesContaining(User.Role role);
    
    /**
     * Find users by group home ID
     */
    List<User> findByGroupHomeId(String groupHomeId);
    
    /**
     * Find users by group home ID and role
     */
    List<User> findByGroupHomeIdAndRolesContaining(String groupHomeId, User.Role role);
    
    /**
     * Find users with specific risk flags
     */
    @Query("{'riskFlags': { $in: ?0 }}")
    List<User> findByRiskFlagsIn(List<String> riskFlags);
    
    /**
     * Find users by multiple roles
     */
    @Query("{'roles': { $in: ?0 }}")
    List<User> findByRolesIn(List<User.Role> roles);
    
    /**
     * Check if user exists by group home ID
     */
    boolean existsByGroupHomeId(String groupHomeId);
    
    /**
     * Count users by role
     */
    long countByRolesContaining(User.Role role);
    
    /**
     * Count users by group home ID
     */
    long countByGroupHomeId(String groupHomeId);
    
    /**
     * Find users with consent flags
     */
    @Query("{'consentFlags.dataProcessingConsent': ?0}")
    List<User> findByDataProcessingConsent(boolean consent);
    
    /**
     * Find users with communication consent
     */
    @Query("{'consentFlags.communicationConsent': ?0}")
    List<User> findByCommunicationConsent(boolean consent);
    
    /**
     * Find users by preference language
     */
    @Query("{'preferences.language': ?0}")
    List<User> findByPreferenceLanguage(String language);
    
    /**
     * Find users with email notifications enabled
     */
    @Query("{'preferences.emailNotifications': true}")
    List<User> findUsersWithEmailNotificationsEnabled();
}
