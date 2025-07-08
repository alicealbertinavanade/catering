package catering.businesslogic.user;

public interface UserEventReceiver {
    public void updateVacationRequestAdded(VacationRequest vr);

    public void updateVacationRequestStatus(VacationRequest vr);

    public void updateUserAdded(User u);

    public void updateUserChanged(User u);

    public void updateUserDeleted(User u);

}
