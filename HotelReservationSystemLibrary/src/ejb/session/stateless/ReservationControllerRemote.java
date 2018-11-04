/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import exceptions.ReservationNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author matthealoo
 */
@Remote
public interface ReservationControllerRemote {

    public Reservation createNewReservation(Reservation reservation);

    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException;

    public List<Reservation> retrieveAllReservationFromBooking(Long bookingId);

    public List<Reservation> retrieveAllReservationFromStartDate(Date date);

    public List<Reservation> retrieveAllReservationFromEndDate(Date date);
    
    public void updateReservation(Reservation reservation);
}
