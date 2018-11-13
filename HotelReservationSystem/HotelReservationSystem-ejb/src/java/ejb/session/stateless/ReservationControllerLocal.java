package ejb.session.stateless;

import entity.Reservation;
import exceptions.ReservationNotFoundException;
import java.util.Date;
import java.util.List;

public interface ReservationControllerLocal {

    public Reservation createNewReservation(Reservation reservation);

    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException;

    public List<Reservation> retrieveAllReservationFromBooking(Long bookingId);

    public List<Reservation> retrieveAllReservationFromStartDate(Date date);

    public List<Reservation> retrieveAllReservationFromEndDate(Date date);

    public void updateReservation(Reservation reservation);

}
