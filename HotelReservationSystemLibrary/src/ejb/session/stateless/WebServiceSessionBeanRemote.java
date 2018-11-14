/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exceptions.BookingNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import ws.session.BookingNotFoundException_Exception;
import ws.session.InvalidLoginCredentials_Exception;
import ws.session.PartnerNotFoundException_Exception;

/**
 *
 * @author matthealoo
 */
public interface WebServiceSessionBeanRemote {
    
   public ws.session.Partner loginForPartnerWS(String username, String password) throws PartnerNotFoundException_Exception, InvalidLoginCredentials_Exception;


    public List<ws.session.Room> retrieveAllEnabledRoomsWS();

    public List<ws.session.Booking> retrieveAllBookingsWithinDatesWS(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1);

    public List<ws.session.Reservation> retrieveAllReservationFromBookingWS(Long bookingId);

    public ws.session.Booking createNewBookingWS(ws.session.Booking arg0);

    public List<ws.session.RoomType> retrieveAllEnabledRoomTypeWS();

    public ws.session.Reservation createNewReservationWS(ws.session.Reservation reservation);

    public ws.session.Booking retrieveBookingByIdForPartnerWS(Long bookingId, Long partnerId) throws BookingNotFoundException_Exception;

    public List<ws.session.Booking> retrieveAllBookingsForPartnerWS(Long partnerId);

    public void updatePartnerLoginWS(ws.session.Partner partner, boolean loggedIn);

    public BigDecimal calculateReservationCostWS(ws.session.Booking booking, ws.session.RoomType roomType);

    public void updateBookingWS(ws.session.Booking booking);

    public List<ws.session.RoomType> retrieveAllRoomtypeWS();



}
