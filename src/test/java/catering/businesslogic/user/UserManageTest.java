package catering.businesslogic.user;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Time;
import java.util.logging.Logger;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.Service;
import catering.businesslogic.kitchen.Assignment;
import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.vacationRequest.VacationRequest;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation.class)
public class UserManageTest {
    private static final Logger LOGGER = LogManager.getLogger(UserManageTest.class);

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
    void testAssignTaskForWorker() {
        LOGGER.info("Testing task assignment for worker");

        try {
            // Create summary sheet
            SummarySheet sheet = app.getTaskManager().generateSummarySheet(testEvent, testService, true);
            assertNotNull(sheet, "Summary sheet should not be null");
            assertTrue(sheet.getTaskList().size() > 0, "Task list should contain tasks");

            // Get the first task
            Task taskToAssign = sheet.getTaskList().get(0);
            assertNotNull(taskToAssign, "Task to assign should not be null");

            Shift shift = Shift.loadItemById(1);

            // Assign the task
            Assignment assignment = app.getTaskManager().assignTask(taskToAssign, shift, worker1);

            // Verify assignment
            assertNotNull(assignment, "Assignment should not be null");
            assertEquals(taskToAssign, assignment.getTask(), "Assignment should reference the correct task");
            assertEquals(worker1, assignment.getUser(), "Assignment should reference the correct worker");
            assertEquals(shift, assignment.getShift(), "Assignment should reference the correct shift");

        } catch (UseCaseLogicException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testAssignTaskForOccasionalWorker() {
        LOGGER.info("Testing task assignment for occasional worker");

        try {
            // Create summary sheet
            SummarySheet sheet = app.getTaskManager().generateSummarySheet(testEvent, testService, true);
            assertNotNull(sheet, "Summary sheet should not be null");
            assertTrue(sheet.getTaskList().size() > 0, "Task list should contain tasks");
            // Get the first task
            Task taskToAssign = sheet.getTaskList().get(0);
            assertNotNull(taskToAssign, "Task to assign should not be null");

            Shift shift = Shift.loadItemById(1);

            // Assign the task
            Assignment assignment = app.getTaskManager().assignTask(taskToAssign, shift, occasionalWorker1);
            // Verify assignment
            assertNotNull(assignment, "Assignment should not be null");
            assertEquals(taskToAssign, assignment.getTask(), "Assignment should reference the correct task");
            assertEquals(occasionalWorker1, assignment.getUser(),
                    "Assignment should reference the correct occasional worker");
            assertEquals(shift, assignment.getShift(), "Assignment should reference the correct shift");

        } catch (UseCaseLogicException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testVacationRequestAndApprovation() {
        LOGGER.info("Testing vacation request and approvation");
        try {
            Date fromDate = Date.valueOf("2023-12-01");
            Date toDate = Date.valueOf("2023-12-10");
            app.getUserManager().fakeLogin(worker1.getUserName());

            VacationRequest vacationRequest = app.getUserManager().requestVacation(fromDate, toDate);

            app.getUserManager().fakeLogin(owner.getUserName());
            // Approve the vacation request
            // app.getUserManager().approveVacationRequest(vacationRequest);

        } catch (UseCaseLogicException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testPromoteOccasionalWorker() {
        LOGGER.info("Testing promotion of occasional worker");

        try {
            User workerPromoted = app.getUserManager().promoteUser(occasionalWorker1);
            assertTrue(!workerPromoted.isOccasionalUser(),
                    "Occasional worker should be promoted");

        } catch (UseCaseLogicException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

    }
}
