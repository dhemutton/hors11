/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import exceptions.ReservationNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author matthealoo
 */
@Stateless
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    
    @Override
    public Reservation createNewReservation(Reservation reservation) {

            em.persist(reservation);
            em.flush();

            return reservation;
        } 
    

   
    @Override
    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        if (reservation != null) {
            return reservation;
        } else {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " does not exist");
        }   
    }

    @Override
    public List<Reservation> retrieveAllReservationFromBooking(Long bookingId) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.bookingId = :inBookingId");
         query.setParameter("BookingId", bookingId);

        return query.getResultList();     
    }
}
