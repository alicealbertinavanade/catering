package catering.businesslogic.kitchen;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.user.User;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

/**
 * Represents a task assignment to a shift and optionally a cook
 */
public class Assignment {

    private int id;
    private Shift shift;
    private Task task;
    private User user;
    private SummarySheet summarySheet;

    // Constructors
    public Assignment(Task task, Shift shift, User user) {
        this.task = task;
        this.shift = shift;
        this.user = user;
    }

    public Assignment(Task task, Shift shift) {
        this.task = task;
        this.shift = shift;
        this.user = null;
    }

    Assignment() {
    }

    // Public accessors and mutators
    public Shift getShift() {
        return shift;
    }

    public SummarySheet getSummarySheet() {
        return summarySheet;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the task associated with this assignment
     * 
     * @return The Task object
     */
    public Task getTask() {
        return task;
    }

    /**
     * Get the ID of this assignment
     * 
     * @return The assignment ID
     */
    public int getId() {
        return id;
    }

    // Database-related code below this point
    public static Assignment loadAssignment(int id) {
        Assignment[] assignHolder = new Assignment[1]; // Use array to allow modification in lambda
        String query = "SELECT * FROM Assignments WHERE id = ?";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Assignment assign = new Assignment();
                assign.id = rs.getInt("id");
                assign.shift = Shift.loadItemById(rs.getInt("shift_id"));
                assign.task = Task.loadTaskById(rs.getInt("task_id"));
                assign.user = User.load(rs.getInt("user_id"));
                assignHolder[0] = assign;
            }
        }, id); // Pass id as parameter

        return assignHolder[0];
    }

    /**
     * Loads all assignments for a specific summary sheet
     * 
     * @param id The summary sheet ID
     * @return List of assignments for the summary sheet
     */
    public static ArrayList<Assignment> loadAllAssignmentsBySumSheetId(int id) {
        String query = "SELECT * FROM Assignments WHERE sumsheet_id = ?";
        ArrayList<Assignment> assignments = new ArrayList<>();
        ArrayList<Integer> shiftIds = new ArrayList<>();
        ArrayList<Integer> taskIds = new ArrayList<>();
        ArrayList<Integer> userIds = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                // Create a new Assignment object for each row
                Assignment a = new Assignment();
                a.id = rs.getInt("id");

                assignments.add(a);

                shiftIds.add(rs.getInt("shift_id"));
                taskIds.add(rs.getInt("task_id"));
                userIds.add(rs.getInt("user_id"));
            }
        }, id); // Pass id as parameter

        for (int i = 0; i < shiftIds.size(); i++) {
            Assignment a = assignments.get(i);
            a.user = User.load(userIds.get(i));
            a.task = Task.loadTaskById(taskIds.get(i));
            a.shift = Shift.loadItemById(shiftIds.get(i));
        }

        return assignments;
    }

    public static ArrayList<Assignment> loadAllAssignmentsByUserId(int id) {
        String query = "SELECT * FROM Assignments WHERE user_id = ?";
        ArrayList<Assignment> assignments = new ArrayList<>();
        ArrayList<Integer> shiftIds = new ArrayList<>();
        ArrayList<Integer> taskIds = new ArrayList<>();
        ArrayList<Integer> userIds = new ArrayList<>();
        ArrayList<Integer> summarySheetIds = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                // Create a new Assignment object for each row
                Assignment a = new Assignment();
                a.id = rs.getInt("id");

                assignments.add(a);

                shiftIds.add(rs.getInt("shift_id"));
                taskIds.add(rs.getInt("task_id"));
                userIds.add(rs.getInt("user_id"));
                summarySheetIds.add(rs.getInt("sumsheet_id"));
            }
        }, id); // Pass id as parameter

        for (int i = 0; i < shiftIds.size(); i++) {
            Assignment a = assignments.get(i);
            a.user = User.load(userIds.get(i));
            a.task = Task.loadTaskById(taskIds.get(i));
            a.shift = Shift.loadItemById(shiftIds.get(i));
            a.summarySheet = SummarySheet.getSummarySheetById(summarySheetIds.get(i));
        }

        return assignments;
    }

    public static ArrayList<Assignment> loadAllAssignmentsByShift(Shift s) {
        String query = "SELECT * FROM Assignments WHERE shift_id = ?";
        ArrayList<Assignment> assignments = new ArrayList<>();
        ArrayList<Integer> shiftIds = new ArrayList<>();
        ArrayList<Integer> taskIds = new ArrayList<>();
        ArrayList<Integer> userIds = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                // Create a new Assignment object for each row
                Assignment a = new Assignment();
                a.id = rs.getInt("id");

                assignments.add(a);

                shiftIds.add(rs.getInt("shift_id"));
                taskIds.add(rs.getInt("task_id"));
                userIds.add(rs.getInt("user_id"));
            }
        }, s.getId()); // Pass s as parameter

        for (int i = 0; i < shiftIds.size(); i++) {
            Assignment a = assignments.get(i);
            a.user = User.load(userIds.get(i));
            a.task = Task.loadTaskById(taskIds.get(i));
            a.shift = Shift.loadItemById(shiftIds.get(i));

        }

        return assignments;
    }

    /**
     * Updates an existing assignment in the database
     * 
     * @param a The assignment to update
     */
    public static void updateAssignment(Assignment a) {
        String upd = "UPDATE Assignment SET shift_id = ?, user_id = ? WHERE id = ?";
        PersistenceManager.executeUpdate(upd,
                a.shift.getId(),
                (a.user == null ? 0 : a.user.getId()),
                a.id);
    }

    /**
     * Deletes an assignment from the database
     * 
     * @param a The assignment to delete
     */
    public static void deleteAssignment(Assignment a) {
        String query = "DELETE FROM Assignments WHERE id = ?";
        PersistenceManager.executeUpdate(query, a.id);
    }

    public static void deleteAllAssignmentByUser(User user) {
        String query = "DELETE FROM Assignments WHERE user_id = ?";
        PersistenceManager.executeUpdate(query, user.getId());
    }

    /**
     * Saves a batch of new assignments for a summary sheet
     * 
     * @param id             The summary sheet ID
     * @param assignmentList The list of assignments to save
     */
    public static void saveAllNewAssignment(int id, ArrayList<Assignment> assignmentList) {
        String secInsert = "INSERT INTO Assignments (sumsheet_id, shift_id, task_id, user_id) VALUES (?, ?, ?, ?);";
        PersistenceManager.executeBatchUpdate(secInsert, assignmentList.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, id);
                ps.setInt(2, assignmentList.get(batchCount).shift.getId());
                ps.setInt(3, assignmentList.get(batchCount).task.getId());
                ps.setInt(4, assignmentList.get(batchCount).user.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                assignmentList.get(count).id = rs.getInt(1);

            }
        });
    }

    /**
     * Saves a single new assignment for a summary sheet
     * 
     * @param id The summary sheet ID
     * @param a  The assignment to save
     */
    public void save(int id) {
        String query = "INSERT INTO Assignments (sumsheet_id, shift_id, task_id, user_id) VALUES (?, ?, ?, ?)";
        PersistenceManager.executeUpdate(query,
                id,
                shift.getId(),
                task.getId(),
                (user == null ? 0 : user.getId()));
        this.id = PersistenceManager.getLastId();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task: ").append(getTask() != null ? getTask().getDescription() : "none");
        sb.append(", User: ").append(getUser() != null ? getUser().getUserName() : "unassigned");

        Shift shift = getShift();
        if (shift != null) {
            sb.append(", Shift: ").append(shift.getDate())
                    .append(" (").append(shift.getStartTime())
                    .append("-").append(shift.getEndTime()).append(")");
        }
        return sb.toString();
    }
}
