package catering.businesslogic.vacationRequest;

import java.util.ArrayList;
import java.util.Date;

import catering.businesslogic.CatERing;
import catering.businesslogic.user.User;

public class VacationRequestManager {
    VacationRequest currentVacationRequest;
    private ArrayList<VacationRequestReceiver> receivers;

    public VacationRequestManager() {
        receivers = new ArrayList<>();
    }

    public VacationRequest getCurrentVacationRequest() {
        return this.currentVacationRequest;
    }

    public VacationRequest createVacationRequest() {
        return this.createVacationRequest();
    }

    public void setCurrentVacationRequest(VacationRequest currentVacationRequest) {
        this.currentVacationRequest = currentVacationRequest;
    }

    public void addReceiver(VacationRequestReceiver receiver) {
        receivers.add(receiver);
    }

    public void removeReceiver(VacationRequestReceiver receiver) {
        receivers.remove(receiver);
    }

    public void notifyVacationRequestStatusChanged(boolean status) {
        for (VacationRequestReceiver receiver : receivers) {
            receiver.updateVacationRequestStatusChanged(status);
        }
    }

}
