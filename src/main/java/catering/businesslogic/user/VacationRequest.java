package catering.businesslogic.user;

import java.sql.Date;
import java.util.logging.Logger;

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

    public int getId() {
        return id;
    }

    public Worker getUser() {
        return user;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean setApproved(boolean approved) {
        this.approved = approved;
        return this.approved;
    }

    public void approve() {
        if (id == 0)
            return;

        String query = "UPDATE VacationRequests SET approved = 1 WHERE id = ?";

        PersistenceManager.executeUpdate(query, id);
        return;
    }

    /**
     * Saves a new vacation request to the database
     * 
     * @return true if successful, false otherwise
     */
    public void save() {
        if (id != 0)
            return; // Already exists

        String query = "INSERT INTO VacationRequests (user_id, from_date, to_date) VALUES (?, ?, ?)";

        PersistenceManager.executeUpdate(query, user.getId(), fromDate, toDate);
        id = PersistenceManager.getLastId();
        return;
    }

}
