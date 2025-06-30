package catering.businesslogic.vacationRequest;

import java.util.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.logging.Logger;

import catering.businesslogic.user.User;
import catering.util.LogManager;

public class VacationRequest {
    private boolean approved;
    private Date fromDate;
    private Date toDate;
    private User user;

    private static final Logger LOGGER = LogManager.getLogger(VacationRequest.class);

    private VacationRequest() {
    }

    public VacationRequest(Date fromDate, Date toDate, User user) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.user = user;
    }
}
