public interface VehicleService {
    void addVehicle(String vehicleName, Status status);
    void updateVehicleStatus(int vehicleId, Status newStatus);
}
