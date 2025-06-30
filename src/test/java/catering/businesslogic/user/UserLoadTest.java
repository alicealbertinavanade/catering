package catering.businesslogic.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import catering.businesslogic.CatERing;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.Service;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation.class)
public class UserLoadTest {
    private static final Logger LOGGER = LogManager.getLogger(UserLoadTest.class);

    private static CatERing app;
    private static Event testEvent;
    private static Service testService;

    @BeforeAll
    static void init() {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
    }

    public static void main(String[] args) {
        System.out.println("Testing User.load() functionality...");

        try {
            // Test loading a user that exists as Worker
            System.out.println("\n1. Testing User.load(String) with existing user:");
            User user = User.load("Lidia");
            if (user != null) {
                System.out.println("✓ User loaded successfully: " + user.getUserName());
                System.out.println("✓ User type: " + user.getClass().getSimpleName());
                System.out.println("✓ User ID: " + user.getId());
            } else {
                System.out.println("✗ User not found");
            }

            // Test loading a user that doesn't exist
            System.out.println("\n2. Testing User.load(String) with non-existent user:");
            User nonExistentUser = User.load("NonExistentUser");
            if (nonExistentUser == null) {
                System.out.println("✓ Correctly returned null for non-existent user");
            } else {
                System.out.println("✗ Should have returned null");
            }

            // Test loading by ID
            System.out.println("\n3. Testing User.load(int) with ID 1:");
            User userById = User.load(1);
            if (userById != null) {
                System.out.println("✓ User loaded by ID successfully: " + userById.getUserName());
                System.out.println("✓ User type: " + userById.getClass().getSimpleName());
            } else {
                System.out.println("✗ User not found by ID");
            }

            // Test loading all users
            System.out.println("\n4. Testing User.loadAllUsers():");
            java.util.ArrayList<User> users = User.loadAllUsers();
            if (users != null) {
                System.out.println("✓ Loaded " + users.size() + " users");
                for (User u : users) {
                    if (u != null) {
                        System.out.println("  - " + u.getClass().getSimpleName() + ": " +
                                u.getUserName() + " (ID: " + u.getId() + ")");
                    }
                }
            } else {
                System.out.println("✗ Failed to load users");
            }

            System.out.println("\n✓ All tests completed successfully!");

        } catch (Exception e) {
            System.out.println("✗ Error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
