package catering.businesslogic.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

public class Worker implements User {
    private int id;
    private String username;
    private String name;
    private String surname;
    private String address;
    private String telephone;
    private String fiscalCode;
    private int vacationDays;
    private Set<Role> roles;

    /**
     * Default constructor for loading from DB
     */
    private Worker() {
        roles = new HashSet<>();
    }

    /**
     * Creates a new worker with the given name
     * 
     * @param name The worker name
     */
    public Worker(String name, String surname, String address, String fiscalCode, String telephone) {
        id = 0;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.fiscalCode = fiscalCode;
        this.telephone = telephone;
        this.vacationDays = 0;
        this.roles = new HashSet<>();
    }

    @Override
    public boolean isCook() {
        return roles.contains(Role.CUOCO);
    }

    @Override
    public boolean isChef() {
        return roles.contains(Role.CHEF);
    }

    @Override
    public boolean isOrganizer() {
        return roles.contains(Role.ORGANIZZATORE);
    }

    @Override
    public boolean isService() {
        return roles.contains(Role.SERVIZIO);
    }

    @Override
    public boolean isOwner() {
        return roles.contains(Role.PROPRIETARIO);
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(" ").append(username);

        if (!roles.isEmpty()) {
            sb.append(" : ");
            for (Role r : roles) {
                sb.append(r.toString()).append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * Sets the username for this worker
     * 
     * @param username The new username
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Adds a role to this worker
     * 
     * @param role The role to add
     * @return true if the role was added, false if it was already present
     */
    @Override
    public boolean addRole(Role role) {
        return this.roles.add(role);
    }

    /**
     * Removes a role from this worker
     * 
     * @param role The role to remove
     * @return true if the role was removed, false if it wasn't present
     */
    public boolean removeRole(Role role) {
        return this.roles.remove(role);
    }

    /**
     * Checks if the worker has a specific role
     * 
     * @param role The role to check
     * @return true if the worker has the role, false otherwise
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    /**
     * Gets all roles assigned to this worker
     * 
     * @return A set containing all worker roles
     */
    public Set<Role> getRoles() {
        return new HashSet<>(this.roles); // Return a copy to prevent external modification
    }

    // STATIC METHODS FOR PERSISTENCE

    public static Worker load(int uid) {
        Worker load = new Worker();
        String userQuery = "SELECT * FROM Users WHERE id = ?";

        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.username = rs.getString("username");
            }
        }, uid); // Pass uid as parameter

        if (load.id > 0) {
            loadRolesForUser(load);
        }
        return load;
    }

    public static Worker load(String username) {
        Worker u = new Worker();
        String userQuery = "SELECT * FROM Users WHERE username = ?";

        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
            }
        }, username); // Pass username as parameter

        if (u.id > 0) {
            loadRolesForUser(u);
        }
        return u;
    }

    public static ArrayList<Worker> loadAllUsers() {
        String userQuery = "SELECT * FROM Users";
        ArrayList<Worker> workers = new ArrayList<>();

        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Worker u = new Worker();
                u.id = rs.getInt("id");
                u.username = rs.getString("username");

                // Load roles for this worker
                loadRolesForUser(u);
                workers.add(u);
            }
        });

        return workers;
    }

    // Helper method to load roles for a worker
    private static void loadRolesForUser(Worker u) {
        String roleQuery = "SELECT * FROM UserRoles WHERE user_id = ?";

        PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int role = rs.getInt("role_id");
                switch (role) {
                    case 0:
                        u.roles.add(Role.CUOCO);
                        break;
                    case 1:
                        u.roles.add(Role.CHEF);
                        break;
                    case 2:
                        u.roles.add(Role.ORGANIZZATORE);
                        break;
                    case 3:
                        u.roles.add(Role.SERVIZIO);
                        break;
                    case 4:
                        u.roles.add(Role.PROPRIETARIO);
                        break;
                }
            }
        }, u.id); // Pass u.id as parameter
    }

    /**
     * Saves a new worker to the database
     * 
     * @return true if successful, false otherwise
     */
    public boolean save() {
        if (id != 0)
            return false; // Already exists

        String query = "INSERT INTO User (username) VALUES(?)";

        PersistenceManager.executeUpdate(query, username);
        id = PersistenceManager.getLastId();

        if (id > 0) {
            // Save roles
            saveUserRoles();
            return true;
        }
        return false;
    }

    /**
     * Updates an existing worker in the database
     * 
     * @return true if successful, false otherwise
     */
    public boolean update() {
        if (id == 0)
            return false; // Not in DB

        String query = "UPDATE Workers SET username = ? WHERE id = ?";

        int rows = PersistenceManager.executeUpdate(query, username, id);

        // Update worker roles
        saveUserRoles();

        return rows > 0;
    }

    /**
     * Deletes a worker from the database
     * 
     * @return true if successful, false otherwise
     */
    public boolean delete() {
        if (id == 0)
            return false; // Not in DB

        // First delete worker roles
        String deleteRolesQuery = "DELETE FROM UserRoles WHERE user_id = ?";
        PersistenceManager.executeUpdate(deleteRolesQuery, id);

        // Then delete worker
        String deleteUserQuery = "DELETE FROM Users WHERE id = ?";
        int rows = PersistenceManager.executeUpdate(deleteUserQuery, id);

        if (rows > 0) {
            id = 0;
            return true;
        }
        return false;
    }

    /**
     * Saves worker roles to the database
     */
    private void saveUserRoles() {
        if (id == 0)
            return; // Worker not saved yet

        // First delete existing roles
        String deleteQuery = "DELETE FROM UserRoles WHERE user_id = ?";
        PersistenceManager.executeUpdate(deleteQuery, id);

        // Then insert new roles
        for (Role role : roles) {
            String roleId = getRoleStringId(role);
            String insertQuery = "INSERT INTO UserRoles (user_id, role_id) VALUES(?, ?)";
            PersistenceManager.executeUpdate(insertQuery, id, roleId);
        }
    }

    /**
     * Converts Role enum to string ID for database
     */
    private String getRoleStringId(Role role) {
        switch (role) {
            case CUOCO:
                return "c";
            case CHEF:
                return "h";
            case ORGANIZZATORE:
                return "o";
            case SERVIZIO:
                return "s";
            case PROPRIETARIO:
                return "p";
            default:
                return "";
        }
    }

    /**
     * Determines if this worker is equal to another object.
     * Two workers are considered equal if they have the same ID or, if ID is 0,
     * the same workername.
     *
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Worker other = (Worker) obj;

        // If both workers have valid IDs, compare by ID
        if (this.id > 0 && other.id > 0) {
            return this.id == other.id;
        }

        // Otherwise, if either ID is 0, compare by workername
        return this.username != null && this.username.equals(other.username);
    }

    /**
     * Generates a hash code for this worker.
     * The hash code is based on ID if it's valid (> 0), or workername otherwise.
     *
     * @return A hash code value for this worker
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // Use ID if it's valid
        if (id > 0) {
            result = prime * result + id;
        } else {
            // Otherwise use workername
            result = prime * result + (username != null ? username.hashCode() : 0);
        }

        return result;
    }

    public int getVacationDays() {
        return vacationDays;
    }

    public void setVacationDays(int vacationDays) {
        this.vacationDays = vacationDays;
    }
}
