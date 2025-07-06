package catering.businesslogic.vacationRequest;

import java.util.Date;
import java.util.logging.Logger;

import catering.businesslogic.user.Worker;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

public class VacationRequest {
    private int id;
    private boolean approved;
    private Date fromDate;
    private Date toDate;
    private Worker user;

    private static final Logger LOGGER = LogManager.getLogger(VacationRequest.class);

    private VacationRequest() {
    }

    public VacationRequest(Date fromDate, Date toDate, Worker user) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.user = user;
    }

    /**
     * Saves a new worker to the database
     * 
     * @return true if successful, false otherwise
     */
    public boolean save() {
        if (id != 0)
            return false; // Already exists

        String query = "INSERT INTO VacationRequests (user_id, from_date, to_date) VALUES (?, ?, ?)";

        PersistenceManager.executeUpdate(query, user.getId(), fromDate, toDate);
        id = PersistenceManager.getLastId();

        return id > 0 ? true : false;
    }
}
