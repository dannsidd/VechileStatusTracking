import java.util.logging.Logger;

public class VehicleLogger {
    private static final Logger logger = Logger.getLogger(VehicleLogger.class.getName());

    public static void logStatusChange(int vehicleId, Status oldStatus, Status newStatus) {
        logger.info("Vehicle " + vehicleId + " changed from " + oldStatus + " to " + newStatus);
    }
}
