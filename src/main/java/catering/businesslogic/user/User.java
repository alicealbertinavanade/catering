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

    void setName(String name);

    void setSurname(String surname);

    void setFiscalCode(String fiscalCode);

    void setTelephone(String telephone);

    int getId();

    void setId(int id);

    boolean addRole(Role role);

    boolean isCook();

    boolean isChef();

    boolean isOrganizer();

    boolean isService();

    boolean isOwner();

    Set<Role> getRoles();

    static boolean delete(User user) {
        Worker worker = Worker.load(user.getId());
        if (worker != null && worker.getId() > 0) {
            return worker.delete();
        }

        OccasionalWorker occasionalWorker = OccasionalWorker.load(user.getId());
        if (occasionalWorker != null && occasionalWorker.getId() > 0) {
            return occasionalWorker.delete();
        }
        return true;
    }

    static boolean save(User user) {
        Worker worker = Worker.load(user.getId());
        if (worker != null && worker.getId() > 0) {
            return worker.save();
        }

        OccasionalWorker occasionalWorker = OccasionalWorker.load(user.getId());
        if (occasionalWorker != null && occasionalWorker.getId() > 0) {
            return occasionalWorker.save();
        }
        return true;
    }

    static boolean update(User user) {
        Worker worker = Worker.load(user.getId());
        if (worker != null && worker.getId() > 0) {
            return worker.update();
        }

        OccasionalWorker occasionalWorker = OccasionalWorker.load(user.getId());
        if (occasionalWorker != null && occasionalWorker.getId() > 0) {
            return occasionalWorker.update();
        }
        return true;
    }

    static User load(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        // First try to load as Worker
        Worker worker = Worker.load(username);
        if (worker != null && worker.getId() > 0) {
            return worker;
        }

        // If not found as Worker, try OccasionalWorker
        OccasionalWorker occasionalWorker = OccasionalWorker.load(username);
        if (occasionalWorker != null && occasionalWorker.getId() > 0) {
            return occasionalWorker;
        }

        return null; // User not found
    }

    static User load(int uid) {
        if (uid <= 0) {
            return null;
        }

        Worker worker = Worker.load(uid);
        if (worker != null && worker.getId() > 0) {
            return worker;
        }

        OccasionalWorker occasionalWorker = OccasionalWorker.load(uid);
        if (occasionalWorker != null && occasionalWorker.getId() > 0) {
            return occasionalWorker;
        }

        return null; // User not found
    }

    static ArrayList<User> loadAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();

        // Load all Workers
        ArrayList<Worker> workers = Worker.loadAllUsers();
        if (workers != null) {
            allUsers.addAll(workers);
        }

        // Load all OccasionalWorkers
        ArrayList<OccasionalWorker> occasionalWorkers = OccasionalWorker.loadAllUsers();
        if (occasionalWorkers != null) {
            allUsers.addAll(occasionalWorkers);
        }

        return allUsers;
    }

}
