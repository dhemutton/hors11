 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Guest;
import entity.Partner;
import exceptions.BookingNotFoundException;
import java.util.ArrayList;
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
    public Booking retrieveBookingByIdForGuest(Long bookingId, Long guestId) throws BookingNotFoundException {
            Booking booking = em.find(Booking.class, bookingId);
        
        if (booking != null && booking.getGuest().getGuestId().equals(guestId)) {
            return booking;
        } else {
            throw new BookingNotFoundException("Booking ID " + bookingId + " does not exist");
        }  
    }

    
    @Override
    public Booking retrieveBookingByIdForPartner(Long bookingId, Long partnerId) throws BookingNotFoundException {
            Booking booking = em.find(Booking.class, bookingId);
        
        if (booking != null  && booking.getPartner().getPartnerId().equals(partnerId)) {
            return booking;
        } else {
            throw new BookingNotFoundException("Booking ID " + bookingId + " does not exist");
        }  
    }

    
    
    @Override
    public List<Booking> retrieveAllBookingsOnStartDate(Date startDate) {
        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.startDate=:startDate");
        query.setParameter("startDate", startDate);
        return query.getResultList();   
    }
    
    @Override
    public List<Booking> retrieveAllBookingsOnEndDate(Date endDate) {
        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.endDate=:endDate");
        query.setParameter("endDate", endDate);
        return query.getResultList();   
    }
    
    @Override
    public List<Booking> retrieveAllBookingsWithinDates(Date startDate, Date endDate) {
        List<Booking> finalList = new ArrayList<>();
        Query query1 = em.createQuery("SELECT DISTINCT b FROM Booking b WHERE b.startDate BETWEEN :startDate AND :endDate"); //list1
        Query query2 = em.createQuery("SELECT DISTINCT b FROM Booking b WHERE b.endDate BETWEEN :startDate AND :endDate"); //list2
        Query query3 = em.createQuery("SELECT DISTINCT b FROM Booking b WHERE :startDate BETWEEN b.startDate AND b.endDate AND :endDate BETWEEN b.startDate AND b.endDate"); //list3
       // Query query4 = em.createQuery("SELECT DISTINCT b FROM Booking b WHERE b.startDate BETWEEN (:startDate AND :endDate) AND b.endDate BETWEEN (:startDate AND :endDate)");
        query1.setParameter("startDate", startDate);
        query1.setParameter("endDate", endDate);
        query2.setParameter("startDate", startDate);
        query2.setParameter("endDate", endDate);
        query3.setParameter("startDate", startDate);
        query3.setParameter("endDate", endDate);
//        query4.setParameter("startDate", startDate);
//        query4.setParameter("endDate", endDate);
        
        finalList.addAll(query1.getResultList());
        List<Booking> list2 = query2.getResultList();
        for (Booking booking : list2 ) {
            if (!finalList.contains(booking)) {
                finalList.add(booking);
            }
        }
        List<Booking> list3 = query3.getResultList();
        for (Booking booking : list3 ) {
            if (!finalList.contains(booking)) {
                finalList.add(booking);
            }
        }
//        finalList.addAll(query4.getResultList());
        for (Booking booking: finalList) {
            booking.getReservation().size();
        }

        return finalList;   
    }

    @Override
    public void updateBooking(Booking booking) {
        em.merge(booking);
    }

     @Override
    public List<Booking> retrieveAllBookingsForGuest(Long guestId) {
                Guest guest = em.find(Guest.class, guestId);
                guest.getBookings().size();
        return guest.getBookings();   
    }
    
    @Override
    public List<Booking> retrieveAllBookingsForPartner(Long partnerId) {
                Partner partner = em.find(Partner.class, partnerId);
                partner.getBookings().size();
        return partner.getBookings();   
    }
}
