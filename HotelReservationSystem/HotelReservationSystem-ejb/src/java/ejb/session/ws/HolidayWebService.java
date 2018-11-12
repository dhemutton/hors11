/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.BookingControllerLocal;
import ejb.session.stateless.PartnerControllerLocal;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Booking;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exceptions.BookingNotFoundException;
import exceptions.InvalidLoginCredentials;
import exceptions.PartnerNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author matthealoo
 */
@WebService(serviceName = "HolidayWebService")
@Stateless()
public class HolidayWebService {

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private ReservationControllerLocal reservationControllerLocal;

    @EJB
    private PartnerControllerLocal partnerControllerLocal;

    @EJB
    private BookingControllerLocal bookingControllerLocal;

    /**
     * Web service operation
     */
    //Login
    public Partner loginForPartner(String username, String password) throws InvalidLoginCredentials, PartnerNotFoundException {

        return partnerControllerLocal.partnerLogin(username, password);
    }

    //Search room
    public List<Room> retrieveAllEnabledRooms() {
        
        List<Room> rooms = roomControllerLocal.retrieveAllEnabledRooms();
        
        for (Room room: rooms) {
            room.setRoomType(null);
        }
        
        return rooms;
    }

    //Search room
    public List<Booking> retrieveAllBookingsWithinDates(Date startDate, Date endDate) {
        return bookingControllerLocal.retrieveAllBookingsWithinDates(startDate, endDate);
    }

    //Search room & view my reservation
    public List<Reservation> retrieveAllReservationFromBooking(Long bookingId) {
        return reservationControllerLocal.retrieveAllReservationFromBooking(bookingId);
    }

    //reserve room
    public Booking createNewBooking(Booking booking) {
        return bookingControllerLocal.createNewBooking(booking);
    }

    //reserve room
    public List<RoomType> retrieveAllEnabledRoomType() {
        
        List<RoomType> list = roomTypeControllerLocal.retrieveAllEnabledRoomType();
        
        for (RoomType rt : list) {
            rt.setRoomRates(null);
            rt.setRooms(null);
        }
        return list;
    }

    //reserve room
    public Reservation createNewReservation(Reservation reservation) {
        return reservationControllerLocal.createNewReservation(reservation);
    }

    //view my reservation
    public Booking retrieveBookingByIdForPartner(Long bookingId, Long partnerId) throws BookingNotFoundException {
        return bookingControllerLocal.retrieveBookingByIdForPartner(bookingId, partnerId);
    }

    //view all my reservation
    public List<Booking> retrieveAllBookingsForPartner(Long partnerId) {
        return bookingControllerLocal.retrieveAllBookingsForPartner(partnerId);
    }
    
    
}
