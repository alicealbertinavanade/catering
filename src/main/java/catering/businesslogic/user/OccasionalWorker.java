package catering.businesslogic.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

public class OccasionalWorker implements User {
    private int id;
    private String username;
    private String name;
    private String surname;
    private String fiscalCode;
    private String telephone;
    private int isOccasionalUser;
    private Set<Role> roles;

    /**
     * Default constructor for loading from DB
     */
    private OccasionalWorker() {
        roles = new HashSet<>();
    }

    /**
     * Creates a new occasional worker with the given name
     * 
     * @param name The worker name
     */
    public OccasionalWorker(String name, String surname, String fiscalCode, String telephone) {
        this();
        this.id = 0;
        this.name = name;
        this.surname = surname;
        this.fiscalCode = fiscalCode;
        this.telephone = telephone;
    }

    @Override
    public boolean isOccasionalUser() {
        return true;
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
    public boolean isService() {
        return roles.contains(Role.SERVIZIO);
    }

    @Override
    public boolean isOrganizer() {
        return false;
    }

    @Override
    public boolean isOwner() {
        return false;
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

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean addRole(Role role) {
        if (role != null && role != Role.PROPRIETARIO && role != Role.ORGANIZZATORE && !roles.contains(role)) {
            roles.add(role);
            return true;
        }
        return false;
    }

    public boolean removeRole(Role role) {
        return roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public Set<Role> getRoles() {
        return new HashSet<>(this.roles);
    }

    // Getters and setters for additional fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
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

    // STATIC METHODS FOR PERSISTENCE

    public static OccasionalWorker load(int uid) {
        OccasionalWorker load = new OccasionalWorker();
        String userQuery = "SELECT * FROM Users WHERE id = ?";

        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.username = rs.getString("username");
                load.name = rs.getString("name");
                load.surname = rs.getString("surname");
                load.fiscalCode = rs.getString("fiscal_code");
                load.telephone = rs.getString("telephone");
            }
        }, uid);

        if (load.id > 0) {
            loadRolesForUser(load);
        }
        return load;
    }

    public static OccasionalWorker load(String username) {
        OccasionalWorker u = new OccasionalWorker();
        String userQuery = "SELECT * FROM Users WHERE username = ?";

        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
                u.name = rs.getString("name");
                u.surname = rs.getString("surname");
                u.fiscalCode = rs.getString("fiscal_code");
                u.telephone = rs.getString("telephone");
            }
        }, username);

        if (u.id > 0) {
            loadRolesForUser(u);
        }
        return u;
    }

    public static ArrayList<OccasionalWorker> loadAllUsers() {
        String userQuery = "SELECT * FROM Users WHERE is_occasional_user = 1";
        ArrayList<OccasionalWorker> users = new ArrayList<>();

        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                OccasionalWorker u = new OccasionalWorker();
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
                u.name = rs.getString("name");
                u.surname = rs.getString("surname");
                u.fiscalCode = rs.getString("fiscal_code");
                u.telephone = rs.getString("telephone");
                loadRolesForUser(u);
                users.add(u);
            }
        });

        return users;
    }

    // Helper method to load roles for an occasional worker
    private static void loadRolesForUser(OccasionalWorker u) {
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
                    case 3:
                        u.roles.add(Role.SERVIZIO);
                        break;
                }
            }
        }, u.id);
    }

    // Database persistence methods
    public boolean save() {
        String query = "INSERT INTO Users (username, name, surname, fiscal_code, telephone, is_occasional_user) VALUES (?, ?, ?, ?, ?, 1)";
        int[] generatedId = new int[1];

        PersistenceManager.executeUpdate(query, generatedId, username, name, surname, fiscalCode, telephone);

        if (generatedId[0] > 0) {
            this.id = generatedId[0];
            saveUserRoles();
            return true;
        }
        return false;
    }

    public boolean update() {
        String query = "UPDATE Users SET username = ?, name = ?, surname = ?, fiscal_code = ?, telephone = ? WHERE id = ?";

        PersistenceManager.executeUpdate(query, username, name, surname, fiscalCode, telephone, id);
        saveUserRoles();
        return true;
    }

    public boolean delete() {
        String deleteRoles = "DELETE FROM UserRoles WHERE user_id = ?";
        String deleteUser = "DELETE FROM Users WHERE id = ?";

        PersistenceManager.executeUpdate(deleteRoles, id);
        PersistenceManager.executeUpdate(deleteUser, id);
        return true;
    }

    private void saveUserRoles() {
        // First, delete existing roles
        String deleteQuery = "DELETE FROM UserRoles WHERE user_id = ?";
        PersistenceManager.executeUpdate(deleteQuery, id);

        // Then insert current roles
        String insertQuery = "INSERT INTO UserRoles (user_id, role_id) VALUES (?, ?)";
        for (Role role : roles) {
            String roleId = getRoleStringId(role);
            PersistenceManager.executeUpdate(insertQuery, id, roleId);
        }
    }

    private String getRoleStringId(Role role) {
        switch (role) {
            case CUOCO:
                return "0";
            case CHEF:
                return "1";
            case SERVIZIO:
                return "3";
            default:
                return "0";
        }
    }
}
