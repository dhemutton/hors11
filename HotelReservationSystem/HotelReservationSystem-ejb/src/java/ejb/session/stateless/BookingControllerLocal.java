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

/**
 *
 * @author matthealoo
 */
public interface BookingControllerLocal {

    public Booking createNewBooking(Booking booking);

    public Booking retrieveBookingById(Long bookingId) throws BookingNotFoundException;

    public List<Booking> retrieveAllBookingsOnStartDate(Date startDate);

    public void updateBooking(Booking booking);

    public List<Booking> retrieveAllBookingsOnEndDate(Date endDate);

    public List<Booking> retrieveAllBookingsWithinDates(Date startDate, Date endDate);

}
