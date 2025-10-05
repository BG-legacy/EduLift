# EduLift MongoDB Schema Documentation

## Users Collection Schema

This document describes the MongoDB schema implementation for the `users` collection in the EduLift application.

### Schema Overview

The `users` collection stores comprehensive user information including roles, profiles, consent flags, preferences, and risk assessments.

### Required Fields

- `roles`: Array of user roles (student, mentor, counselor, admin)
- `email`: Unique email address
- `createdAt`: User creation timestamp

### Schema Structure

```javascript
{
  _id: ObjectId,                    // Auto-generated unique identifier
  roles: [String],                  // Required: ["student", "mentor", "counselor", "admin"]
  groupHomeId: String,              // Indexed: Group home identifier
  profile: {                        // User profile information
    firstName: String,
    lastName: String,
    phoneNumber: String,
    dateOfBirth: String,            // Format: YYYY-MM-DD
    address: String,
    emergencyContact: String,
    emergencyPhoneNumber: String,
    additionalInfo: Object
  },
  consentFlags: {                   // Consent management
    dataProcessingConsent: Boolean,
    communicationConsent: Boolean,
    emergencyContactConsent: Boolean,
    photoVideoConsent: Boolean,
    consentTimestamp: Date
  },
  preferences: {                    // User preferences
    language: String,               // Default: "en"
    timezone: String,               // Default: "UTC"
    emailNotifications: Boolean,    // Default: true
    smsNotifications: Boolean,      // Default: false
    pushNotifications: Boolean,     // Default: true
    customPreferences: Object
  },
  riskFlags: [String],              // Risk assessment indicators
  createdAt: Date,                  // Required: Creation timestamp
  email: String,                    // Required: Unique email
  
  // Legacy fields (for backward compatibility)
  username: String,
  firstName: String,
  lastName: String,
  updatedAt: Date
}
```

### Indexes

The following indexes are automatically created for optimal query performance:

1. **Email Unique Index**: `{ email: 1 }` - Ensures email uniqueness
2. **GroupHomeId Index**: `{ groupHomeId: 1 }` - Fast queries by group home
3. **Roles Index**: `{ roles: 1 }` - Efficient role-based queries
4. **Compound Index**: `{ roles: 1, groupHomeId: 1 }` - Combined role/group queries
5. **CreatedAt Index**: `{ createdAt: -1 }` - Sorted queries by creation date
6. **RiskFlags Index**: `{ riskFlags: 1 }` - Risk assessment queries

### Validation Rules

#### Role Validation
- Must contain at least one role
- Valid roles: `student`, `mentor`, `counselor`, `admin`
- No duplicate roles allowed

#### Email Validation
- Must be a valid email format
- Maximum 255 characters
- Unique across all users

#### Risk Flags
Valid risk flag values:
- `academic_risk`
- `behavioral_risk`
- `attendance_risk`
- `emotional_risk`
- `substance_risk`
- `family_risk`
- `financial_risk`
- `health_risk`
- `social_risk`
- `housing_risk`

#### Language Preferences
Supported languages: `en`, `es`, `fr`, `de`, `it`, `pt`, `zh`, `ja`, `ko`, `ar`

### Java Implementation

#### User Model
The `User` class in `com.EduLift.backend.model.User` implements the complete schema with:
- Spring Data MongoDB annotations
- Jakarta validation constraints
- Nested classes for complex objects
- Enum for role validation

#### Repository Methods
The `UserRepository` interface provides query methods for:
- Role-based queries
- Group home filtering
- Risk flag searches
- Consent flag queries
- Preference-based filtering

### Setup Instructions

#### 1. Automatic Setup (Spring Boot)
The MongoDB configuration will automatically create indexes when the application starts.

#### 2. Manual Setup (MongoDB Shell)
Run the setup script:
```bash
mongo < src/main/resources/mongodb-setup.js
```

#### 3. Using MongoDB Compass
Import and execute the setup script in MongoDB Compass.

### Usage Examples

#### Creating a New User
```java
User user = new User();
user.setRoles(Arrays.asList(User.Role.STUDENT));
user.setEmail("student@example.com");
user.setGroupHomeId("gh_001");

User.Profile profile = new User.Profile("John", "Doe");
user.setProfile(profile);

User.ConsentFlags consent = new User.ConsentFlags();
consent.setDataProcessingConsent(true);
user.setConsentFlags(consent);

userRepository.save(user);
```

#### Querying Users
```java
// Find students in a specific group home
List<User> students = userRepository.findByGroupHomeIdAndRolesContaining(
    "gh_001", User.Role.STUDENT
);

// Find users with specific risk flags
List<User> atRiskUsers = userRepository.findByRiskFlagsIn(
    Arrays.asList("academic_risk", "behavioral_risk")
);

// Find users with email notifications enabled
List<User> emailUsers = userRepository.findUsersWithEmailNotificationsEnabled();
```

### Data Compliance

#### Consent Management
- All consent flags are explicitly tracked
- Consent timestamps are recorded
- Individual consent types are manageable

#### Data Retention
- No TTL (Time To Live) is set on user documents
- User data is retained indefinitely for compliance
- Consider implementing soft deletes for privacy compliance

#### Risk Assessment
- Risk flags are standardized and validated
- Multiple risk indicators can be assigned
- Historical risk data is preserved

### Performance Considerations

1. **Compound Indexes**: Role + GroupHomeId queries are optimized
2. **Sparse Indexes**: Email index ignores null values
3. **Array Indexes**: Efficient queries on roles and riskFlags arrays
4. **Descending Index**: CreatedAt index supports newest-first queries

### Migration from Legacy Schema

The implementation maintains backward compatibility with legacy fields:
- `username`, `firstName`, `lastName` are preserved
- New applications should use the `profile` object
- Gradual migration can be implemented

### Monitoring and Maintenance

1. **Index Usage**: Monitor index performance with `db.users.explain()`
2. **Validation Errors**: Track schema validation failures
3. **Data Quality**: Regular audits of required fields
4. **Performance**: Monitor query execution times

### Security Considerations

1. **Email Uniqueness**: Prevents duplicate accounts
2. **Role Validation**: Ensures proper authorization
3. **Consent Tracking**: Supports GDPR compliance
4. **Data Validation**: Prevents malformed data entry
