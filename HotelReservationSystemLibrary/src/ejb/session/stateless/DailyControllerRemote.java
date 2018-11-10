package ejb.session.stateless;

public interface DailyControllerRemote {
    
    public void persist(Object object);

    public void dailyReservationRoomAssignment();

    public void deleteAllRoomRates();

    public void deleteAllRooms();

    public void deleteAllRoomTypes();
    
}
