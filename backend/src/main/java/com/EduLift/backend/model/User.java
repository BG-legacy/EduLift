package com.EduLift.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * User model for EduLift application
 * Fields: id, roles [student|mentor|counselor|admin], groupHomeId, profile, consentFlags,
 * preferences, riskFlags[], createdAt.
 */
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @NotNull
    @Indexed
    @Field("roles")
    private List<Role> roles;
    
    @Indexed
    @Field("groupHomeId")
    private String groupHomeId;
    
    @Field("profile")
    private Profile profile;
    
    @Field("consentFlags")
    private ConsentFlags consentFlags;
    
    @Field("preferences")
    private Preferences preferences;
    
    @Field("riskFlags")
    private List<String> riskFlags;
    
    @NotNull
    @Field("createdAt")
    private LocalDateTime createdAt;
    
    // Legacy fields - keeping for backward compatibility
    private String username;
    
    @Email
    @NotBlank
    @Indexed(unique = true)
    private String email;
    
    private String firstName;
    private String lastName;
    private LocalDateTime updatedAt;
    
    /**
     * User roles enum
     */
    public enum Role {
        STUDENT("student"),
        MENTOR("mentor"),
        COUNSELOR("counselor"),
        ADMIN("admin");
        
        private final String value;
        
        Role(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * User profile information
     */
    public static class Profile {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String dateOfBirth;
        private String address;
        private String emergencyContact;
        private String emergencyPhoneNumber;
        private Map<String, Object> additionalInfo;
        
        // Constructors
        public Profile() {}
        
        public Profile(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        
        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
        
        public String getEmergencyPhoneNumber() { return emergencyPhoneNumber; }
        public void setEmergencyPhoneNumber(String emergencyPhoneNumber) { this.emergencyPhoneNumber = emergencyPhoneNumber; }
        
        public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
        public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }
    }
    
    /**
     * User consent flags
     */
    public static class ConsentFlags {
        private boolean dataProcessingConsent;
        private boolean communicationConsent;
        private boolean emergencyContactConsent;
        private boolean photoVideoConsent;
        private LocalDateTime consentTimestamp;
        
        // Constructors
        public ConsentFlags() {}
        
        // Getters and Setters
        public boolean isDataProcessingConsent() { return dataProcessingConsent; }
        public void setDataProcessingConsent(boolean dataProcessingConsent) { this.dataProcessingConsent = dataProcessingConsent; }
        
        public boolean isCommunicationConsent() { return communicationConsent; }
        public void setCommunicationConsent(boolean communicationConsent) { this.communicationConsent = communicationConsent; }
        
        public boolean isEmergencyContactConsent() { return emergencyContactConsent; }
        public void setEmergencyContactConsent(boolean emergencyContactConsent) { this.emergencyContactConsent = emergencyContactConsent; }
        
        public boolean isPhotoVideoConsent() { return photoVideoConsent; }
        public void setPhotoVideoConsent(boolean photoVideoConsent) { this.photoVideoConsent = photoVideoConsent; }
        
        public LocalDateTime getConsentTimestamp() { return consentTimestamp; }
        public void setConsentTimestamp(LocalDateTime consentTimestamp) { this.consentTimestamp = consentTimestamp; }
    }
    
    /**
     * User preferences
     */
    public static class Preferences {
        private String language;
        private String timezone;
        private boolean emailNotifications;
        private boolean smsNotifications;
        private boolean pushNotifications;
        private Map<String, Object> customPreferences;
        
        // Constructors
        public Preferences() {
            // Default values
            this.language = "en";
            this.timezone = "UTC";
            this.emailNotifications = true;
            this.smsNotifications = false;
            this.pushNotifications = true;
        }
        
        // Getters and Setters
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        
        public boolean isEmailNotifications() { return emailNotifications; }
        public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }
        
        public boolean isSmsNotifications() { return smsNotifications; }
        public void setSmsNotifications(boolean smsNotifications) { this.smsNotifications = smsNotifications; }
        
        public boolean isPushNotifications() { return pushNotifications; }
        public void setPushNotifications(boolean pushNotifications) { this.pushNotifications = pushNotifications; }
        
        public Map<String, Object> getCustomPreferences() { return customPreferences; }
        public void setCustomPreferences(Map<String, Object> customPreferences) { this.customPreferences = customPreferences; }
    }
    
    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.preferences = new Preferences(); // Initialize with defaults
    }
    
    // Constructor with parameters
    public User(String username, String email, String firstName, String lastName) {
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Constructor with required fields
    public User(List<Role> roles, String email) {
        this();
        this.roles = roles;
        this.email = email;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    public String getGroupHomeId() {
        return groupHomeId;
    }
    
    public void setGroupHomeId(String groupHomeId) {
        this.groupHomeId = groupHomeId;
    }
    
    public Profile getProfile() {
        return profile;
    }
    
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    public ConsentFlags getConsentFlags() {
        return consentFlags;
    }
    
    public void setConsentFlags(ConsentFlags consentFlags) {
        this.consentFlags = consentFlags;
    }
    
    public Preferences getPreferences() {
        return preferences;
    }
    
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
    
    public List<String> getRiskFlags() {
        return riskFlags;
    }
    
    public void setRiskFlags(List<String> riskFlags) {
        this.riskFlags = riskFlags;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Legacy fields - getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", roles=" + roles +
                ", groupHomeId='" + groupHomeId + '\'' +
                ", profile=" + profile +
                ", consentFlags=" + consentFlags +
                ", preferences=" + preferences +
                ", riskFlags=" + riskFlags +
                ", createdAt=" + createdAt +
                ", email='" + email + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
