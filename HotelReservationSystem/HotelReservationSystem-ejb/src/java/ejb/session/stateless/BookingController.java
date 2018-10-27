 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import exceptions.BookingNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author matthealoo
 */
@Stateless
@Remote(BookingControllerRemote.class)
@Local(BookingControllerLocal.class)
public class BookingController implements BookingControllerRemote, BookingControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    
    @Override
    public Booking createNewBooking(Booking booking) {
            em.persist(booking);
            em.flush();

            return booking;   
    }

    @Override
    public Booking retrieveBookingById(Long bookingId) throws BookingNotFoundException {
            Booking booking = em.find(Booking.class, bookingId);
        
        if (booking != null) {
            return booking;
        } else {
            throw new BookingNotFoundException("Booking ID " + bookingId + " does not exist");
        }  
    }

    @Override
    public List<Booking> retrieveAllBookingsOnDate(Date startDate) {
        Query query = em.createQuery("SELECT b FROM Booking WHERE r.startDate=:startDate");
        query.setParameter("startDate", startDate);
        return query.getResultList();   
    }

    @Override
    public void updateBooking(Booking booking) {
        em.merge(booking);
    }

    
}
