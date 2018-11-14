/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import ws.session.RoomType;
import ws.session.Room;

import exceptions.BookingNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;
import ws.session.Booking;
import ws.session.BookingNotFoundException_Exception;
import ws.session.HolidayWebService_Service;
import ws.session.InvalidLoginCredentials_Exception;
import ws.session.Partner;
import ws.session.PartnerNotFoundException_Exception;
import ws.session.Reservation;

/**
 *
 * @author matthealoo
 */
@Stateless
@Local(WebServiceSessionBeanLocal.class)
@Remote(WebServiceSessionBeanRemote.class)
public class WebServiceSessionBean implements WebServiceSessionBeanRemote, WebServiceSessionBeanLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8000/HolidayWebService/HolidayWebService.wsdl")
    private HolidayWebService_Service service;

    private Booking createNewBooking(ws.session.Booking arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.createNewBooking(arg0);
    }

    private Reservation createNewReservation(ws.session.Reservation arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.createNewReservation(arg0);
    }

    private Partner loginForPartner(java.lang.String arg0, java.lang.String arg1) throws PartnerNotFoundException_Exception, InvalidLoginCredentials_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.loginForPartner(arg0, arg1);
    }

    private java.util.List<ws.session.Booking> retrieveAllBookingsForPartner(java.lang.Long arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllBookingsForPartner(arg0);
    }

    private java.util.List<ws.session.Booking> retrieveAllBookingsWithinDates(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllBookingsWithinDates(arg0, arg1);
    }

    private java.util.List<ws.session.RoomType> retrieveAllEnabledRoomType() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllEnabledRoomType();
    }

    private java.util.List<ws.session.Room> retrieveAllEnabledRooms() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllEnabledRooms();
    }

    private java.util.List<ws.session.Reservation> retrieveAllReservationFromBooking(java.lang.Long arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllReservationFromBooking(arg0);
    }

    private Booking retrieveBookingByIdForPartner(java.lang.Long arg0, java.lang.Long arg1) throws BookingNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveBookingByIdForPartner(arg0, arg1);
    }

    private void updatePartnerLogin(ws.session.Partner arg0, boolean arg1) {
        ws.session.HolidayWebService_Service service = new ws.session.HolidayWebService_Service();
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        port.updatePartnerLogin(arg0, arg1);
    }

    private BigDecimal calculateReservationCost(ws.session.Booking arg0, ws.session.RoomType arg1) {
        ws.session.HolidayWebService_Service service = new ws.session.HolidayWebService_Service();
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.calculateReservationCost(arg0, arg1);
    }

    private void updateBooking(ws.session.Booking arg0) {
        ws.session.HolidayWebService_Service service = new ws.session.HolidayWebService_Service();
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        port.updateBooking(arg0);
    }

    private java.util.List<ws.session.RoomType> retrieveAllRoomtype() {
        ws.session.HolidayWebService_Service service = new ws.session.HolidayWebService_Service();
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllRoomtype();
    }

    @Override
    public Partner loginForPartnerWS(String username, String password) throws PartnerNotFoundException_Exception, InvalidLoginCredentials_Exception {
        Partner partner = loginForPartner(username, password);
        partner.getBookings().clear();
        return partner;
    }

    //Search room
    @Override
    public List<ws.session.Room> retrieveAllEnabledRoomsWS() {
        List<Room> rooms = retrieveAllEnabledRooms();

        for (Room room : rooms) {
            room.setRoomType(null);
            room.getReservations().clear();
        }

        return rooms;
    }
    
    //Search room
    @Override
    public List<ws.session.Booking> retrieveAllBookingsWithinDatesWS(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        List<ws.session.Booking> bookings = retrieveAllBookingsWithinDates(arg0, arg1);

        for (Booking b : bookings) {
            b.setPartner(null);
            b.getReservation().clear();
            b.setGuest(null);
        }
        return bookings;
    }

        @Override
    //Search room & view my reservation
    public List<ws.session.Reservation> retrieveAllReservationFromBookingWS(Long bookingId) {

        List<Reservation> list = retrieveAllReservationFromBooking(bookingId);

        for (Reservation r : list) {
            r.setBooking(null);
            r.setRoom(null);
            r.setInitialRoomType(null);
            r.setFinalRoomType(null);
        }

        return list;
    }

        @Override
    //reserve room
    public ws.session.Booking createNewBookingWS(ws.session.Booking arg0) {
        return createNewBooking(arg0);
    }
    
    @Override
    //reserve room
    public List<ws.session.RoomType> retrieveAllEnabledRoomTypeWS() {

        List<RoomType> list = retrieveAllEnabledRoomType();

        for (RoomType rt : list) {
            rt.getRoomRates().clear();
            rt.getRooms().clear();
        }
        return list;
    }
    
    @Override
    public ws.session.Reservation createNewReservationWS(ws.session.Reservation reservation) {
        Reservation r = createNewReservation(reservation);
        r.setBooking(null);
        r.setInitialRoomType(null);
        r.setFinalRoomType(null);
        r.setRoom(null);
        return r;
    }

        @Override
    //view my reservation
    public ws.session.Booking retrieveBookingByIdForPartnerWS(Long bookingId, Long partnerId) throws BookingNotFoundException_Exception {

        Booking booking = retrieveBookingByIdForPartner(bookingId, partnerId);
        booking.setPartner(null);
        List<Reservation> reservationlist = booking.getReservation();
        for (Reservation reservation : reservationlist) {
            reservation.setBooking(null);
        }

        return booking;
    }

        @Override
    //view all my reservation
    public List<ws.session.Booking> retrieveAllBookingsForPartnerWS(Long partnerId) {

        List<Booking> bookings = retrieveAllBookingsForPartner(partnerId);

        for (Booking b : bookings) {
            b.setPartner(null);
            List<Reservation> reservationlist = b.getReservation();
            for (Reservation reservation : reservationlist) {
                reservation.setBooking(null);
            }
        }
        return bookings;
    }

        @Override
    public void updatePartnerLoginWS(ws.session.Partner partner, boolean loggedIn) {

        updatePartnerLogin(partner, loggedIn);
    }

        @Override
    public BigDecimal calculateReservationCostWS(ws.session.Booking booking, ws.session.RoomType roomType) {
        return calculateReservationCost(booking, roomType);
    }

        @Override
    public void updateBookingWS(ws.session.Booking booking) {
        updateBooking(booking);
    }

        @Override
    public List<ws.session.RoomType> retrieveAllRoomtypeWS() {
        List<RoomType> list = retrieveAllRoomtype();
        for (RoomType rt : list) {
            rt.getRoomRates().clear();
            rt.getRooms().clear();
        }
        return list;
    }

}
