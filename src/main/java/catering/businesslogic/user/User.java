package catering.businesslogic.user;

import java.util.ArrayList;
import java.util.Set;

public interface User {
    public static enum Role {
        CUOCO, CHEF, ORGANIZZATORE, SERVIZIO, PROPRIETARIO
    };

    String getUserName();

    String getFiscalCode();

    String getTelephone();

    boolean isOccasionalUser();

    void setUsername(String username);

    int getId();

    void setId(int id);

    boolean addRole(Role role);

    boolean isCook();

    boolean isChef();

    boolean isOrganizer();

    boolean isService();

    boolean isOwner();

    Set<Role> getRoles();

    // Factory methods for loading users
    static User load(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        try {
            // First try to load as Worker
            Worker worker = Worker.load(username);
            if (worker != null && worker.getId() > 0) {
                return worker;
            }
        } catch (Exception e) {
            // Log error but continue trying OccasionalWorker
            System.err.println("Error loading as Worker: " + e.getMessage());
        }

        try {
            // If not found as Worker, try OccasionalWorker
            OccasionalWorker occasionalWorker = OccasionalWorker.load(username);
            if (occasionalWorker != null && occasionalWorker.getId() > 0) {
                return occasionalWorker;
            }
        } catch (Exception e) {
            // Log error but continue
            System.err.println("Error loading as OccasionalWorker: " + e.getMessage());
        }

        return null; // User not found
    }

    static User load(int uid) {
        if (uid <= 0) {
            return null;
        }

        try {
            // First try to load as Worker
            Worker worker = Worker.load(uid);
            if (worker != null && worker.getId() > 0) {
                return worker;
            }
        } catch (Exception e) {
            // Log error but continue trying OccasionalWorker
            System.err.println("Error loading Worker by ID: " + e.getMessage());
        }

        try {
            // If not found as Worker, try OccasionalWorker
            OccasionalWorker occasionalWorker = OccasionalWorker.load(uid);
            if (occasionalWorker != null && occasionalWorker.getId() > 0) {
                return occasionalWorker;
            }
        } catch (Exception e) {
            // Log error but continue
            System.err.println("Error loading OccasionalWorker by ID: " + e.getMessage());
        }

        return null; // User not found
    }

    static ArrayList<User> loadAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();

        try {
            // Load all Workers
            ArrayList<Worker> workers = Worker.loadAllUsers();
            if (workers != null) {
                allUsers.addAll(workers);
            }
        } catch (Exception e) {
            System.err.println("Error loading Workers: " + e.getMessage());
        }

        try {
            // Load all OccasionalWorkers
            ArrayList<OccasionalWorker> occasionalWorkers = OccasionalWorker.loadAllUsers();
            if (occasionalWorkers != null) {
                allUsers.addAll(occasionalWorkers);
            }
        } catch (Exception e) {
            System.err.println("Error loading OccasionalWorkers: " + e.getMessage());
        }

        return allUsers;
    }

}
