package ejb.session.stateless;

import entity.Booking;
import entity.Reservation;
import exceptions.ReservationNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@Local(ReservationControllerLocal.class)
@Remote(ReservationControllerRemote.class)
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    
    @Override
    public Reservation createNewReservation(Reservation reservation) {

            em.persist(reservation);
            em.flush();
            Booking booking = reservation.getBooking();
            booking.getReservation().add(reservation);
            em.merge(booking);
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
        Query query = em.createQuery("SELECT r FROM Reservation r JOIN r.booking b WHERE b.bookingId = :inBookingId");
         query.setParameter("inBookingId", bookingId);

        return query.getResultList();     
    }
    
    @Override
    public List<Reservation> retrieveAllReservationFromStartDate(Date date) {
        Query query = em.createQuery("SELECT r FROM Reservation r JOIN r.booking b WHERE b.startDate=:startDate");
        query.setParameter("startDate", date);
        return query.getResultList();
    }
    
    @Override
    public List<Reservation> retrieveAllReservationFromEndDate(Date date) {
        Query query = em.createQuery("SELECT r FROM Reservation r JOIN r.booking b WHERE b.endDate=:endDate");
        query.setParameter("endDate", date);
        return query.getResultList();
    }
    
    @Override
    public void updateReservation(Reservation reservation) {
        em.merge(reservation);
    }
}


