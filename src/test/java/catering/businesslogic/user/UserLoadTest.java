package catering.businesslogic.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.Service;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation.class)
public class UserLoadTest {
    private static final Logger LOGGER = LogManager.getLogger(UserLoadTest.class);

    private static CatERing app;
    private static User owner;
    private static User worker1;
    private static User occasionalWorker1;
    private static Event testEvent;
    private static Service testService;

    @BeforeAll
    static void init() {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
    }

    @BeforeEach
    void setup() {
        try {
            // Set up the chef user
            owner = Worker.load("Proprietario");
            assertNotNull(owner, "Owner user should be loaded");
            assertTrue(owner.isOwner(), "User should have Owner role");

            worker1 = Worker.load("Worker1");
            assertNotNull(worker1, "Worker1 should be loaded");
            assertTrue(!worker1.isOccasionalUser(), "User is a Worker");

            occasionalWorker1 = OccasionalWorker.load("OccasionalWorker1");
            assertNotNull(occasionalWorker1, "Occasional worker should be loaded");
            assertTrue(occasionalWorker1.isOccasionalUser(), "User is Occasional Worker");

            // Set up event and service
            testEvent = Event.loadByName("Gala Aziendale Annuale");
            assertNotNull(testEvent, "Test event should be loaded");

            testService = Service.loadByName("Pranzo Buffet Aziendale");
            assertNotNull(testService, "Test service should be loaded");

            // Login as owner
            app.getUserManager().fakeLogin(owner.getUserName());

            assertEquals(owner, app.getUserManager().getCurrentUser(),
                    "Current user should be the owner");

        } catch (UseCaseLogicException e) {
            LOGGER.severe(e.getMessage());
            fail("Setup failed: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    void testAssignTaskForUser() {
        LOGGER.info("Testing task assignment for user");

        try {
            // Create summary sheet
            SummarySheet sheet = app.getTaskManager().generateSummarySheet(testEvent, testService, true);

            // Verify summary sheet was created properly
            assertNotNull(sheet, "Summary sheet should not be null");
            assertNotNull(sheet.getTaskList(), "Task list should not be null");
            assertTrue(sheet.getTaskList().size() > 0, "Task list should contain tasks");

            LOGGER.info("Created summary sheet: " + sheet.toString());
        } catch (UseCaseLogicException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
