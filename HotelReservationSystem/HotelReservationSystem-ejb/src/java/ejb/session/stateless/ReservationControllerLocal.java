/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Reservation;
import exceptions.ReservationNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author matthealoo
 */
@Local
public interface ReservationControllerLocal {

    public Reservation createNewReservation(Reservation reservation);

    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException;

    public List<Reservation> retrieveAllReservationFromBooking(Long bookingId);

    public List<Reservation> retrieveAllReservationFromStartDate(Date date);

    public List<Reservation> retrieveAllReservationFromEndDate(Date date);

    public void updateReservation(Reservation reservation);

}
