package catering.businesslogic.kitchen;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import catering.businesslogic.recipe.KitchenProcess;
import catering.businesslogic.recipe.Preparation;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.recipe.SupportOperation;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;
import catering.util.LogManager;

public class Task {

    private int id;
    private String description;
    private SummarySheet summarySheet;
    private KitchenProcess kitchenProcess;
    private int quantity;
    private int portions;
    private boolean ready;
    private int type;
    private static final Logger LOGGER = LogManager.getLogger(Task.class);

    private Task() {
    }

    public Task(KitchenProcess rec) {
        this(rec, rec.getName(), rec.getType());
    }

    public Task(KitchenProcess rec, String desc, int typeProcess) {
        id = 0;
        kitchenProcess = rec;
        type = typeProcess;
        description = desc;
        ready = false;
        quantity = 0;
        portions = 0;
    }

    public Task(String desc) {
        id = 0;
        description = desc;
        ready = false;
    }

    public Task(Task mi) {
        this.id = 0;
        this.description = mi.description;
        this.kitchenProcess = mi.kitchenProcess;
        this.type = mi.type;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static void saveAllNewTasks(int id, ArrayList<Task> taskList) {
        String secInsert = "INSERT INTO Tasks (sumsheet_id, kitchenproc_id, description, type, position, ready, quantity, portions) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        for (Task task : taskList) {
            PersistenceManager.executeUpdate(secInsert,
                    id,
                    task.kitchenProcess.getId(),
                    task.description,
                    task.type,
                    taskList.indexOf(task),
                    task.ready,
                    task.quantity,
                    task.portions);
            task.id = PersistenceManager.getLastId();
        }
    }

    public static void saveNewTask(int id, Task task, int taskPosition) {
        String query = "INSERT INTO Tasks (sumsheet_id, kitchenproc_id, description, type, position, ready, quantity, portions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PersistenceManager.executeUpdate(query,
                id,
                task.kitchenProcess.getId(),
                task.getDescription(),
                task.kitchenProcess.getType(),
                taskPosition,
                task.ready,
                task.quantity,
                task.portions);

        task.id = PersistenceManager.getLastId();
    }

    public static ArrayList<Task> loadAllTasksBySumSheetId(int id) {
        String query = "SELECT * FROM Tasks WHERE sumsheet_id = ? ORDER BY position";
        ArrayList<Task> taskArrayList = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {

                Task t = new Task();
                t.id = rs.getInt("id");

                t.summarySheet = SummarySheet.loadSummarySheetById(rs.getInt("sumsheet_id"));
                t.description = rs.getString("description");
                t.portions = rs.getInt("portions");
                t.ready = rs.getBoolean("ready");
                t.quantity = rs.getInt("quantity");
                ids.add(rs.getInt("kitchenproc_id")); // Changed from kitchen_proc_id
                t.type = rs.getInt("type");
                taskArrayList.add(t);
            }
        }, id); // Pass id as parameter

        for (int i = 0; i < ids.size(); i++) {
            Task t = taskArrayList.get(i);
            if (t.type == 1) {
                t.kitchenProcess = Preparation.loadPreparationById(ids.get(i));
            } else if (t.type == 2) {
                t.kitchenProcess = Recipe.loadRecipe(ids.get(i));
            } else if (t.type == 3) {
                t.kitchenProcess = SupportOperation.loadSupportOperationById(ids.get(i));
            }
        }

        return taskArrayList;
    }

    public static Task loadTaskById(int id) {
        String query = "SELECT * FROM Tasks WHERE id = ?";
        Task[] taskHolder = new Task[1]; // Use array to allow modification in lambda
        ArrayList<Integer> ids = new ArrayList<>(1);

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                if (taskHolder[0] != null)
                    return; // Only handle the first result

                Task t = new Task();
                t.id = rs.getInt("id");

                t.description = rs.getString("description");
                t.portions = rs.getInt("portions");
                t.ready = rs.getBoolean("ready");
                t.quantity = rs.getInt("quantity");

                t.type = rs.getInt("type");
                ids.add(rs.getInt("kitchenproc_id")); // Changed from kitchen_proc_id
                taskHolder[0] = t;
            }
        }, id); // Pass id as parameter

        if (taskHolder[0] == null) {
            return null; // No task found with the given ID
        }

        Task t = taskHolder[0];
        if (t.type == 1) {
            t.kitchenProcess = Preparation.loadPreparationById(ids.get(0));
        } else if (t.type == 2) {
            t.kitchenProcess = Recipe.loadRecipe(ids.get(0));
        } else if (t.type == 3) {
            t.kitchenProcess = SupportOperation.loadSupportOperationById(ids.get(0));
        }

        return t;
    }

    public static void updateTaskChanged(Task task) {
        String query = "UPDATE Tasks SET description = ?, quantity = ?, portions = ?, ready = ? WHERE id = ?";

        PersistenceManager.executeUpdate(query,
                task.getDescription(),
                task.quantity,
                task.portions,
                task.ready,
                task.id);
    }

    public void setReady() {
        ready = true;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(isReady() ? "[✓] " : "[ ] ")
                .append(getDescription());

        if (getQuantity() > 0 || getPortions() > 0) {
            sb.append(" (");
            if (getQuantity() > 0)
                sb.append("Qty: ").append(getQuantity());
            if (getQuantity() > 0 && getPortions() > 0)
                sb.append(", ");
            if (getPortions() > 0)
                sb.append("Portions: ").append(getPortions());
            sb.append(")");
        }
        return sb.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public KitchenProcess getKitchenProcess() {
        return kitchenProcess;
    }

    public void setKitchenProcess(KitchenProcess kitchenProcess) {
        this.kitchenProcess = kitchenProcess;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPortions() {
        return portions;
    }

    public boolean isReady() {
        return ready;
    }
}