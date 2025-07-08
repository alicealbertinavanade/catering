package catering.businesslogic.user;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.logging.Logger;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventReceiver;
import catering.businesslogic.kitchen.TaskEventReceiver;
import catering.businesslogic.menu.Menu;
import catering.businesslogic.menu.MenuEventReceiver;
import catering.businesslogic.menu.Section;
import catering.businesslogic.shift.Shift;
import catering.util.LogManager;

public class UserManager {
    private static final Logger LOGGER = LogManager.getLogger(UserManager.class);

    private User currentUser;
    private ArrayList<UserEventReceiver> eventReceivers;

    public UserManager() {
        eventReceivers = new ArrayList<>();
    }

    public void addEventReceiver(UserEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public void modifyUser(String name, String surname, String fiscalCode, String telephone) {
        if (name != null) {
            currentUser.setName(name);
        }
        if (surname != null) {
            currentUser.setSurname(surname);
        }
        if (fiscalCode != null) {
            currentUser.setFiscalCode(fiscalCode);
        }
        if (telephone != null) {
            currentUser.setTelephone(telephone);
        }
        notifyUserChanged(currentUser);
    }

    public void delete() {
        this.notifyUserDeleted(currentUser);
    }

    public Worker createWorker(String name, String surname, String fiscalCode, String telephone) {
        Worker worker = new Worker(name, surname, fiscalCode, telephone);
        this.notifyUserAdded(worker);
        return worker;
    }

    public OccasionalWorker createOccasionalWorker(String name, String surname, String fiscalCode, String telephone) {
        OccasionalWorker occasionalWorker = new OccasionalWorker(name, surname, fiscalCode, telephone);
        this.notifyUserAdded(occasionalWorker);
        return occasionalWorker;
    }

    /**
     * Removes an event receiver
     * 
     * @param receiver The event receiver to remove
     */
    public void removeEventReceiver(UserEventReceiver receiver) {
        eventReceivers.remove(receiver);
    }

    public void fakeLogin(String username) throws UseCaseLogicException {
        LOGGER.info("Attempting login for user: " + username);
        this.currentUser = User.load(username);
        if (this.currentUser == null) {
            LOGGER.warning("Login failed: user not found - " + username);
            throw new UseCaseLogicException("User not found");
        }
        LOGGER.info("User successfully logged in: " + username);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public User promoteUser(User occasionalWorker) throws UseCaseLogicException {
        if (occasionalWorker.isOccasionalUser()) {
            if (currentUser.isOwner()) {
                LOGGER.info("Promoting user: " + occasionalWorker.getUserName());
                return Worker.promoteOccasionalWorker((OccasionalWorker) occasionalWorker);
            } else {
                LOGGER.warning("Only owner can promote users");
                throw new UseCaseLogicException("Only owner can promote users");
            }
        } else {
            LOGGER.info("User is a Worker, no promotion needed");
            return occasionalWorker;
        }
    }

    public VacationRequest requestVacation(Date fromDate, Date toDate) throws UseCaseLogicException {
        if (!currentUser.isOccasionalUser()) {
            VacationRequest request = new VacationRequest(fromDate, toDate, (Worker) currentUser);
            this.notifyVacationRequestAdded(request);
            return request;
        } else {
            LOGGER.info("User is a Occasional Worker, no vacation request needed");
            return null;
        }
    }

    public void approveVacationRequest(VacationRequest vacationRequest) throws UseCaseLogicException {
        LOGGER.info("Approving vacation request: " + vacationRequest.getId());
        if (currentUser.isOwner()) {
            Worker worker = vacationRequest.getUser();
            if (worker != null && worker.hasAssignment(vacationRequest.getFromDate(), vacationRequest.getToDate())) {
                LOGGER.warning("Cannot approve vacation request: user has assignments");
                throw new UseCaseLogicException("User has assignments, cannot approve vacation request");
            }
            vacationRequest.setApproved(true);
            this.notifyVacationRequestStatus(vacationRequest);
        } else {
            LOGGER.warning("Only owner can approve vacation requests");
            throw new UseCaseLogicException("Only owner can approve vacation requests");
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void notifyUserAdded(User u) {
        for (UserEventReceiver er : this.eventReceivers) {
            er.updateUserAdded(u);
        }
    }

    private void notifyUserChanged(User u) {
        for (UserEventReceiver er : this.eventReceivers) {
            er.updateUserChanged(u);
        }
    }

    private void notifyUserDeleted(User u) {
        for (UserEventReceiver er : this.eventReceivers) {
            er.updateUserDeleted(u);
        }
    }

    private void notifyVacationRequestAdded(VacationRequest vr) {
        for (UserEventReceiver er : this.eventReceivers) {
            er.updateVacationRequestAdded(vr);
        }
    }

    private void notifyVacationRequestStatus(VacationRequest vr) {
        for (UserEventReceiver er : this.eventReceivers) {
            er.updateVacationRequestStatus(vr);
        }
    }
}
