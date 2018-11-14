package hotelreservationclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import exceptions.BookingNotFoundException;
import exceptions.EmployeeExistException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import javax.ejb.EJB;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author matthealoo
 */
public class Main {

    @EJB(name = "GuestControllerRemote")
    private static GuestControllerRemote guestControllerRemote;

    @EJB(name = "ReservationControllerRemote")
    private static ReservationControllerRemote reservationControllerRemote;

    @EJB(name = "BookingControllerRemote")
    private static BookingControllerRemote bookingControllerRemote;

    @EJB(name = "RoomControllerRemote")
    private static RoomControllerRemote roomControllerRemote;

    @EJB(name = "RoomRateControllerRemote")
    private static RoomRateControllerRemote roomRateControllerRemote;

    @EJB(name = "RoomTypeControllerRemote")
    private static RoomTypeControllerRemote roomTypeControllerRemote;
    
    public static void main(String[] args) throws EmployeeNotFoundException, RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException, EmployeeExistException, ReservationNotFoundException, BookingNotFoundException {
        MainApp mainApp = new MainApp(guestControllerRemote, reservationControllerRemote, bookingControllerRemote, roomControllerRemote, 
                                      roomRateControllerRemote, roomTypeControllerRemote);
        mainApp.runApp();
    }
}