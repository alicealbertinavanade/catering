package catering.persistence;

import catering.businesslogic.user.User;
import catering.businesslogic.user.UserEventReceiver;
import catering.businesslogic.user.VacationRequest;

public class UserPersistence implements UserEventReceiver {

    @Override
    public void updateVacationRequestAdded(VacationRequest vacationRequest) {
        vacationRequest.save();
    }

    @Override
    public void updateVacationRequestStatus(VacationRequest vacationRequest) {
        vacationRequest.approve();
    }

    @Override
    public void updateUserAdded(User user) {
        User.save(user);
    }

    @Override
    public void updateUserChanged(User user) {
        User.update(user);
    }

    @Override
    public void updateUserDeleted(User user) {
        User.delete(user);
    }
}
