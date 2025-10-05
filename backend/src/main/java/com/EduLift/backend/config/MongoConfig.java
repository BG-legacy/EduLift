package com.EduLift.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import jakarta.annotation.PostConstruct;

/**
 * MongoDB Configuration class that loads environment variables from .env file
 * and creates indexes for the User collection
 */
@Configuration
public class MongoConfig {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            // Load .env file from the root of the classpath or project
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Set system properties so Spring can access them
            dotenv.entries().forEach(entry -> {
                if (System.getProperty(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });

        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }
        
        // Create MongoDB indexes
        createUserIndexes();
    }
    
    /**
     * Creates indexes for the users collection
     * Indexes: email unique, groupHomeId, roles
     */
    private void createUserIndexes() {
        try {
            IndexOperations indexOps = mongoTemplate.indexOps("users");
            
            // Create unique index on email field
            Index emailIndex = new Index()
                    .on("email", org.springframework.data.domain.Sort.Direction.ASC)
                    .unique();
            indexOps.createIndex(emailIndex);
            
            // Create index on groupHomeId field
            Index groupHomeIdIndex = new Index()
                    .on("groupHomeId", org.springframework.data.domain.Sort.Direction.ASC);
            indexOps.createIndex(groupHomeIdIndex);
            
            // Create index on roles field (for array queries)
            Index rolesIndex = new Index()
                    .on("roles", org.springframework.data.domain.Sort.Direction.ASC);
            indexOps.createIndex(rolesIndex);
            
            // Create compound index for common queries
            Index compoundIndex = new Index()
                    .on("roles", org.springframework.data.domain.Sort.Direction.ASC)
                    .on("groupHomeId", org.springframework.data.domain.Sort.Direction.ASC);
            indexOps.createIndex(compoundIndex);
            
            // Create index on createdAt for sorting and range queries
            Index createdAtIndex = new Index()
                    .on("createdAt", org.springframework.data.domain.Sort.Direction.DESC);
            indexOps.createIndex(createdAtIndex);
            
            System.out.println("MongoDB indexes created successfully for users collection");
            
        } catch (Exception e) {
            System.err.println("Warning: Could not create MongoDB indexes: " + e.getMessage());
        }
    }
}
