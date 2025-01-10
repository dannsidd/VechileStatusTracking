import java.util.ArrayList;
import java.util.List;

public class StatusSubject {

    private List<StatusObserver> observers = new ArrayList<>();
    private Status status;

    public void addObserver(StatusObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(StatusObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(int vehicleId, Status newStatus) {
        for (StatusObserver observer : observers) {
            observer.update(vehicleId, newStatus);
        }
    }

    public void setStatus(int vehicleId, Status newStatus) {
        this.status = newStatus;
        notifyObservers(vehicleId, newStatus);
    }
}
