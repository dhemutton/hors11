package hotelmanagementclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import ejb.session.stateless.SelfInvokeDailyControllerRemote;
import javax.ejb.EJB;

public class Main {

    @EJB
    private static SelfInvokeDailyControllerRemote selfInvokeDailyControllerRemote;

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

    @EJB(name = "PartnerControllerRemote")
    private static PartnerControllerRemote partnerControllerRemote;

    @EJB(name = "EmployeeControllerRemote")
    private static EmployeeControllerRemote employeeControllerRemote;
    
    
    
    public static void main(String[] args)  {
        MainApp mainApp = new MainApp(guestControllerRemote, reservationControllerRemote, bookingControllerRemote, roomControllerRemote, roomRateControllerRemote, roomTypeControllerRemote, partnerControllerRemote, employeeControllerRemote, selfInvokeDailyControllerRemote);
        mainApp.runApp();
    }
    
}