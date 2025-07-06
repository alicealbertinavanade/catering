package catering.businesslogic.user;

import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Logger;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.kitchen.Assignment;
import catering.businesslogic.vacationRequest.VacationRequest;
import catering.util.LogManager;

public class UserManager {
    private static final Logger LOGGER = LogManager.getLogger(UserManager.class);

    private User currentUser;

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
            LOGGER.info("Promoting user: " + occasionalWorker.getUserName());
            return Worker.promoteOccasionalWorker((OccasionalWorker) occasionalWorker);
        } else {
            LOGGER.info("User is a Worker, no promotion needed");
            return occasionalWorker;
        }
    }

    public VacationRequest requestVacation(Date fromDate, Date toDate) throws UseCaseLogicException {
        if (!currentUser.isOccasionalUser()) {
            return Worker.requestVacation((Worker) currentUser, fromDate, toDate);
        } else {
            LOGGER.info("User is a Occasional Worker, no vacation request needed");
            return null;
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
