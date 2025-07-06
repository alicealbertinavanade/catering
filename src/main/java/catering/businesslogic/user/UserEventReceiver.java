package catering.businesslogic.user;

import catering.businesslogic.kitchen.Task;

public interface UserEventReceiver {
    public void updateTaskAdded(User u, Task kt);

}
