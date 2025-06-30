package catering.businesslogic.recipe;

import catering.businesslogic.kitchen.SummarySheet;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;
import catering.util.LogManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Preparation represents an intermediate food preparation step.
 * It implements KitchenProcess and has attributes specific to intermediate
 * steps.
 */
public class SupportOperation implements KitchenProcess {
    private static final Logger LOGGER = LogManager.getLogger(SupportOperation.class);

    private int id;
    private String name;
    private String description;

    /**
     * Default constructor for loading from DB
     */
    private SupportOperation() {
    }

    /**
     * Creates a new process with the given name
     * 
     * @param name The process name
     */
    public SupportOperation(String name) {
        id = 0;
        this.name = name;
        this.description = "";
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this preparation
     * 
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getType() {
        return 3; // This is not a recipe
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for this preparation
     * 
     * @param description The description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        SupportOperation other = (SupportOperation) obj;

        // If both processes have valid IDs, compare by ID
        if (this.id > 0 && other.id > 0) {
            return this.id == other.id;
        }

        // Otherwise, compare by name and description
        boolean nameMatch = (this.name == null && other.name == null) ||
                (this.name != null && this.name.equals(other.name));

        boolean descMatch = (this.description == null && other.description == null) ||
                (this.description != null && this.description.equals(other.description));

        return nameMatch && descMatch;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // Use ID if it's valid
        if (id > 0) {
            result = prime * result + id;
        } else {
            // Otherwise use name and description
            result = prime * result + (name != null ? name.hashCode() : 0);
            result = prime * result + (description != null ? description.hashCode() : 0);
        }

        return result;
    }

    /**
     * Loads all preparations from the database
     * 
     * @return List of all preparations
     */
    public static ArrayList<SupportOperation> loadAllSupportOperations() {
        ArrayList<SupportOperation> processes = new ArrayList<>();

        String query = "SELECT * FROM SupportOperations";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                SupportOperation proc = new SupportOperation(rs.getString("name"));
                proc.id = rs.getInt("id");

                // Load additional properties if they exist in DB
                try {
                    proc.description = rs.getString("description");
                } catch (SQLException e) {
                    proc.description = "";
                }

                processes.add(proc);
            }
        });

        // Sort processes by name
        Collections.sort(processes, new Comparator<SupportOperation>() {
            @Override
            public int compare(SupportOperation o1, SupportOperation o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });

        LOGGER.info("return processes");
        return processes;
    }

    /**
     * Gets all preparations from the database
     * 
     * @return List of all preparations
     */
    public static ArrayList<SupportOperation> getAllSupportOperations() {
        return loadAllSupportOperations();
    }

    /**
     * Loads a process by its ID
     * 
     * @param id The process ID
     * @return The loaded process or null if not found
     */
    public static SupportOperation loadSupportOperationById(int id) {
        SupportOperation[] processHolder = new SupportOperation[1]; // Use array to allow modification in lambda
        String query = "SELECT * FROM SupportOperations WHERE id = ?";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                SupportOperation process = new SupportOperation();
                process.name = rs.getString("name");
                process.id = id;
                // Load additional properties if they exist in DB
                try {
                    process.description = rs.getString("description");
                } catch (SQLException e) {
                    process.description = "";
                }
                processHolder[0] = process;
            }
        }, id); // Pass id as parameter

        return processHolder[0];
    }

    /**
     * Saves a new process to the database
     * 
     * @return true if successful, false otherwise
     */
    public boolean save() {
        if (id != 0)
            return false; // Already exists

        String query = "INSERT INTO SupportOperations (name, description) VALUES(?, ?)";

        PersistenceManager.executeUpdate(query, name, description);
        id = PersistenceManager.getLastId();
        return true;
    }

    /**
     * Updates an existing process in the database
     * 
     * @return true if successful, false otherwise
     */
    public boolean update() {
        if (id == 0)
            return false; // Not in DB

        String query = "UPDATE SupportOperations SET name = ?, description = ? WHERE id = ?";

        int rows = PersistenceManager.executeUpdate(query, name, description, id);
        return rows > 0;
    }

}
