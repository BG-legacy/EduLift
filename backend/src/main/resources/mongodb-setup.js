// MongoDB Setup Script for EduLift Users Collection
// Run this script in MongoDB shell or MongoDB Compass

// Switch to the EduLift database
use edulift;

// Drop existing users collection if it exists (CAUTION: This will delete all data!)
// db.users.drop();

// Create the users collection with schema validation
db.createCollection("users", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      title: "User Schema Validation",
      required: ["roles", "email", "createdAt"],
      properties: {
        _id: {
          bsonType: "objectId",
          description: "Unique identifier for the user"
        },
        roles: {
          bsonType: "array",
          description: "User roles - required field",
          minItems: 1,
          uniqueItems: true,
          items: {
            bsonType: "string",
            enum: ["student", "mentor", "counselor", "admin"]
          }
        },
        groupHomeId: {
          bsonType: "string",
          description: "Group home identifier - indexed field",
          minLength: 1,
          maxLength: 100
        },
        profile: {
          bsonType: "object",
          description: "User profile information",
          properties: {
            firstName: {
              bsonType: "string",
              maxLength: 50
            },
            lastName: {
              bsonType: "string",
              maxLength: 50
            },
            phoneNumber: {
              bsonType: "string"
            },
            dateOfBirth: {
              bsonType: "string"
            },
            address: {
              bsonType: "string",
              maxLength: 200
            },
            emergencyContact: {
              bsonType: "string",
              maxLength: 100
            },
            emergencyPhoneNumber: {
              bsonType: "string"
            },
            additionalInfo: {
              bsonType: "object"
            }
          }
        },
        consentFlags: {
          bsonType: "object",
          description: "User consent information",
          properties: {
            dataProcessingConsent: {
              bsonType: "bool"
            },
            communicationConsent: {
              bsonType: "bool"
            },
            emergencyContactConsent: {
              bsonType: "bool"
            },
            photoVideoConsent: {
              bsonType: "bool"
            },
            consentTimestamp: {
              bsonType: "date"
            }
          }
        },
        preferences: {
          bsonType: "object",
          description: "User preferences",
          properties: {
            language: {
              bsonType: "string",
              enum: ["en", "es", "fr", "de", "it", "pt", "zh", "ja", "ko", "ar"]
            },
            timezone: {
              bsonType: "string",
              maxLength: 50
            },
            emailNotifications: {
              bsonType: "bool"
            },
            smsNotifications: {
              bsonType: "bool"
            },
            pushNotifications: {
              bsonType: "bool"
            },
            customPreferences: {
              bsonType: "object"
            }
          }
        },
        riskFlags: {
          bsonType: "array",
          description: "Array of risk indicators",
          items: {
            bsonType: "string",
            enum: [
              "academic_risk",
              "behavioral_risk", 
              "attendance_risk",
              "emotional_risk",
              "substance_risk",
              "family_risk",
              "financial_risk",
              "health_risk",
              "social_risk",
              "housing_risk"
            ]
          }
        },
        createdAt: {
          bsonType: "date",
          description: "User creation timestamp - required field"
        },
        email: {
          bsonType: "string",
          description: "User email - unique indexed field",
          maxLength: 255
        },
        username: {
          bsonType: "string",
          description: "Legacy field - username",
          maxLength: 50
        },
        firstName: {
          bsonType: "string",
          description: "Legacy field - first name",
          maxLength: 50
        },
        lastName: {
          bsonType: "string",
          description: "Legacy field - last name",
          maxLength: 50
        },
        updatedAt: {
          bsonType: "date",
          description: "Last update timestamp"
        }
      }
    }
  },
  validationLevel: "strict",
  validationAction: "error"
});

print("✅ Users collection created with schema validation");

// Create indexes for optimal query performance
print("Creating indexes...");

// Create unique index on email field
db.users.createIndex(
  { "email": 1 }, 
  { 
    unique: true, 
    sparse: true,
    name: "email_unique_index"
  }
);
print("✅ Email unique index created");

// Create index on groupHomeId field
db.users.createIndex(
  { "groupHomeId": 1 },
  { name: "groupHomeId_index" }
);
print("✅ GroupHomeId index created");

// Create index on roles field (for array queries)
db.users.createIndex(
  { "roles": 1 },
  { name: "roles_index" }
);
print("✅ Roles index created");

// Create compound index for common queries
db.users.createIndex(
  { "roles": 1, "groupHomeId": 1 },
  { name: "roles_groupHomeId_compound_index" }
);
print("✅ Compound roles-groupHomeId index created");

// Create index on createdAt for sorting and range queries
db.users.createIndex(
  { "createdAt": -1 },
  { name: "createdAt_desc_index" }
);
print("✅ CreatedAt descending index created");

// Create index on riskFlags for filtering
db.users.createIndex(
  { "riskFlags": 1 },
  { name: "riskFlags_index" }
);
print("✅ RiskFlags index created");

// Display all indexes
print("\n📋 All indexes created:");
db.users.getIndexes().forEach(function(index) {
  print("  - " + index.name + ": " + JSON.stringify(index.key));
});

print("\n🎉 MongoDB Users collection setup completed successfully!");
print("\n📖 Collection validation rules are now active.");
print("   Only documents matching the schema will be accepted.");
print("\n🔍 Required fields: roles, email, createdAt");
print("🏷️  Indexed fields: email (unique), groupHomeId, roles, createdAt, riskFlags");

// Example of a valid user document
print("\n📝 Example valid user document:");
print(JSON.stringify({
  roles: ["student"],
  groupHomeId: "gh_001",
  profile: {
    firstName: "Jane",
    lastName: "Doe",
    phoneNumber: "+1234567890"
  },
  consentFlags: {
    dataProcessingConsent: true,
    communicationConsent: true,
    consentTimestamp: new Date()
  },
  preferences: {
    language: "en",
    timezone: "UTC",
    emailNotifications: true,
    smsNotifications: false,
    pushNotifications: true
  },
  riskFlags: ["academic_risk"],
  email: "jane.doe@example.com",
  createdAt: new Date()
}, null, 2));
