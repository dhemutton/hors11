/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;
import ws.session.Booking;
import ws.session.BookingNotFoundException_Exception;
import ws.session.HolidayWebService_Service;
import ws.session.InvalidLoginCredentials_Exception;
import ws.session.Partner;
import ws.session.PartnerNotFoundException_Exception;
import ws.session.Reservation;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import static ws.session.BookingStatusEnum.PENDING;
import static ws.session.BookingTypeEnum.PARTNER;
import static ws.session.ExceptionTypeEnum.UNASSIGNED;
import ws.session.RoomType;
import ws.session.RoomTypeNotFoundException_Exception;

/**
 *
 * @author matthealoo
 */
public class Main {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8000/HolidayWebService/HolidayWebService.wsdl")
    private static HolidayWebService_Service service;
    private static Boolean loggedIn = false;
    private static Partner partner;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        runApp();
    }

    public static void runApp() {
        /**
         * @param args the command line arguments
         */
        System.out.println("*** Welcome to Holiday Reservation Client  ***\n");
        Scanner sc = new Scanner(System.in);
        Boolean again = true;
        while (again) {
            try {
                while (!loggedIn) {
                    System.out.println("1. Partner Login");
                    System.out.println("2. Search Hotel Room");
                    System.out.println("3. Exit");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) {
                        doPartnerLogin();
                    } else if (choice == 2) {
                        doSearchRoom();
                    } else if (choice == 3) {
                        again = false;
                        break;
                    } else {
                        System.out.println("Invalid entry. Please try again");
                    }
                }

                while (loggedIn && partner.isIsManager()) {

                    System.out.println("1. Partner Search Hotel Room");
                    System.out.println("2. View Partner Reservation Details");
                    System.out.println("3. View All Partner Reservations");
                    System.out.println("4. Logout");
                    int choice = sc.nextInt();
                    sc.nextLine();
                    if (choice == 1) {
                        doSearchRoom();
                    } else if (choice == 2) {
                        doViewMyReservation(partner.getPartnerId());
                    } else if (choice == 3) {
                        doViewAllMyReservation(partner.getPartnerId());
                    } else if (choice == 4) {
                        System.out.println("Logging out...");

                        System.out.println("(╯°□°）╯︵ ┻━┻)");

                        System.out.println("Goodbye~");
                        loggedIn = false;
                        updatePartnerLogin(partner, false);
                        break;
                    } else {
                        System.out.println("Invalid entry. Please try again");
                    }
                }
            } catch (InputMismatchException ex) {
                System.out.println("Input mismatch. Please try again.");
            }
        }
    }

    private static void doPartnerLogin() {
        System.out.println("*** HoRS :: Holiday Reservation Client :: Login As Partner ***\n");

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = sc.nextLine().trim();
        System.out.println("Please enter password");
        String password = sc.nextLine().trim();

        try {
            partner = loginForPartner(username, password);
            if (!partner.isIsLogin()) {
                loggedIn = true;
                updatePartnerLogin(partner, true);
                System.out.println("Login successful! Redirecting...");

            } else {
                System.out.println("Partner employee is already logged in.");
            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving guest: " + ex.getMessage() + "\n");
        } catch (InvalidLoginCredentials_Exception ex) {
            System.out.println("Invalid password entered. Please try again");
        }
    }

    private static void doSearchRoom() {
        System.out.println("*** HoRS :: Holiday Reservation Client :: Search Room ***\n");

        Scanner sc = new Scanner(System.in);
        Date startDate = null, endDate = null;
        List<Reservation> reservationList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Please enter start date (dd/mm/yyyy)");
        Boolean again = true;

        while (again) {
            String start = sc.nextLine().trim();
            if (start.length() == 10) {
                try {
                    startDate = formatter.parse(start);
                    again = false;
                } catch (ParseException ex) {
                    again = true;
                    System.out.println("Incorrect date format.");
                }
            } else {
                again = true;
                System.out.println("Incorrect date format.");
            }
        }

        System.out.println("Enter end date (format: dd/mm/yyyy) >");
        again = true;
        while (again) {
            String end = sc.nextLine().trim();
            if (end.length() == 10) {
                try {
                    endDate = formatter.parse(end);
                    if (startDate.before(endDate)) {
                        again = false;
                    } else {
                        again = true;
                        System.out.println("End date is before start date! Please re-enter end date.");
                    }
                } catch (ParseException ex) {
                    again = true;
                    System.out.println("Incorrect date format.");
                }
            } else {
                again = true;
                System.out.println("Incorrect date format.");
            }
        }

        GregorianCalendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        GregorianCalendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        try {
            XMLGregorianCalendar startXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(startCalendar);
            XMLGregorianCalendar endXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(endCalendar);

            List<Long> bookingList = retrieveAllBookingsWithinDates(startXmlCalendar, endXmlCalendar);
            for (Long bookingId : bookingList) {
                reservationList.addAll(retrieveAllReservationFromBooking(bookingId));
            }

            List<RoomType> roomTypeList = retrieveAllEnabledAndIsUsedRoomType();

            //EDITED to show room type inventory
            HashMap<Long, Integer> map = new HashMap<>();
            for (RoomType rt : roomTypeList) {
                int maxRoomInventory = retrieveAllEnabledRoomsFromRoomType(rt).size();
                map.put(rt.getRoomTypeId(), maxRoomInventory);
            }

            for (Reservation r : reservationList) {
                Long id = r.getInitialRoomType().getRoomTypeId();
                map.put(id, map.get(id) - 1);
            }
            int roomsLeft = 0;

            List<Long> availableRoomTypeIds = new ArrayList<>();
            for (RoomType rt : roomTypeList) {
                int vacancy = map.get(rt.getRoomTypeId());
                if (vacancy > 0) {
                    roomsLeft = roomsLeft + vacancy;
                    availableRoomTypeIds.add(rt.getRoomTypeId());
                }
            }
            System.out.println();
            System.out.println("Displaying Available Room Types...");
            System.out.println("-----------------------------------------------------------------------------------------------");
            System.out.printf("%-5s %30s %25s %23s ", "ID", "ROOM TYPE NAME", "AVAILABLE ROOMS", "TOTAL COST/ROOM");
            System.out.println();
            System.out.println("-----------------------------------------------------------------------------------------------");
            for (RoomType rt : roomTypeList) {
                Booking booking = new Booking();
                booking.setBookingStatus(PENDING);
                booking.setBookingType(PARTNER);
                booking.setStartDate(startXmlCalendar);
                booking.setEndDate(endXmlCalendar);

                System.out.format("%-5s %30s %17s %27.2f ",
                        rt.getRoomTypeId(), rt.getName(), map.get(rt.getRoomTypeId()), calculateReservationCost(booking, rt.getRoomTypeId()));
                System.out.println();

            }
            System.out.println("-----------------------------------------------------------------------------------------------");

            if (availableRoomTypeIds.size() > 0) {
                if (loggedIn) {
                    System.out.println("Would you like to make a reservation? (Enter 'Y' to reserve)");
                    if (sc.nextLine().trim().equals("Y")) {
                        doReserveRoom(map, startDate, endDate, roomsLeft);
                    } else {
                        System.out.println();
                    }
                } else {
                    System.out.println("Login to make a reservation.");
                }
            } else {
                System.out.println("No more rooms are available during this period");
            }
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Data type conversion to XML Gregorian Calendar error!");

        }
    }

    private static void doReserveRoom(HashMap<Long, Integer> map, Date startDate, Date endDate, int roomsLeft) {
        System.out.println("*** HoRS :: Holiday Reservation Client :: Room Reservation ***\n");
        HashMap<Long, Integer> choiceMap = new HashMap<>();
        for (Long potentialChoiceId : map.keySet()) {
            choiceMap.put(potentialChoiceId, 0);
        }
        List<Reservation> choiceReservationList = new ArrayList<>();
        BigDecimal totalCost = new BigDecimal(0);
        Scanner sc = new Scanner(System.in);
        int quantity = 0;

        Booking booking = new Booking();
        booking.setBookingType(PARTNER);
        booking.setBookingStatus(PENDING);
        booking.setPartner(partner);

        GregorianCalendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        GregorianCalendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        try {
            XMLGregorianCalendar startXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(startCalendar);
            XMLGregorianCalendar endXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(endCalendar);
            booking.setStartDate(startXmlCalendar);
            booking.setEndDate(endXmlCalendar);

            while (true) {
                System.out.println("How many rooms do you want to reserve? (Maximum: " + roomsLeft + ")");
                quantity = sc.nextInt();
                if (quantity <= 0 || quantity > roomsLeft) {
                    System.out.println("Invalid entry. Please try again");
                } else {
                    break;
                }
            }
        } catch (InputMismatchException ex) {
            System.out.println("Invalid entry. Please try again");
            sc.nextLine();

        } catch (DatatypeConfigurationException ex) {
            System.out.println("Data type conversion to XML Gregorian Calendar error!");

        }

        sc.nextLine();
        List<RoomType> roomTypeList = retrieveAllEnabledAndIsUsedRoomType();

        for (int i = 0; i < quantity; i++) {
            int choice;
            while (true) {
                try {
                    System.out.println("Select room type to reserve for reservation (" + (i + 1) + "/" + quantity + "): ");

                    for (int j = 1; j <= roomTypeList.size(); j++) {
                        System.out.println(j + ". " + roomTypeList.get(j - 1).getName());
                    }
                    choice = sc.nextInt();
                    sc.nextLine();
                    if (choice < 1 || choice > roomTypeList.size()) {
                        System.out.println("Invalid entry. Please try again");
                    } else if (map.get(roomTypeList.get(choice - 1).getRoomTypeId()) == 0) {
                        System.out.println("Room Type " + roomTypeList.get(choice - 1).getName() + " is fully booked during this period. Please reserve a different room.");
                    } else {
                        System.out.println("Making a reservation for " + roomTypeList.get(choice - 1).getName() + ".  Cost : $" + calculateReservationCost(booking, roomTypeList.get(choice - 1).getRoomTypeId()));
                        System.out.println("Add to cart? (Enter 'Y' to confirm)");
                        if (sc.nextLine().trim().equals("Y")) {
                            map.put(roomTypeList.get(choice - 1).getRoomTypeId(), map.get(roomTypeList.get(choice - 1).getRoomTypeId()) - 1); //deduct room type inventory on map
                            choiceMap.put(roomTypeList.get(choice - 1).getRoomTypeId(), choiceMap.get(roomTypeList.get(choice - 1).getRoomTypeId()) + 1); //add to the choice cart
                            Reservation reservation = new Reservation();
                            reservation.setInitialRoomType(roomTypeList.get(choice - 1));
                            reservation.setBooking(booking);
                            reservation.setExceptionType(UNASSIGNED);
                            choiceReservationList.add(reservation); //add to reservation list
                            totalCost = totalCost.add(calculateReservationCost(booking, roomTypeList.get(choice - 1).getRoomTypeId()));
                            break;
                        }
                    }
                } catch (InputMismatchException ex) {
                    System.out.println("Invalid entry. Please try again");
                    sc.nextLine();
                }
            }
        }

        System.out.println();

        System.out.println(
                "Displaying Your Cart...");
        System.out.println(
                "-----------------------------------------------------------------------------------------------");
        System.out.printf(
                "%-30s %25s %23s ", "ROOM TYPE NAME", "NO. OF BOOKED ROOMS", "TOTAL COST/ROOM");
        System.out.println();

        System.out.println(
                "-----------------------------------------------------------------------------------------------");
        for (Long choiceId
                : choiceMap.keySet()) {
            try {
                if (choiceMap.get(choiceId) != 0) {
                    System.out.format("%-30s %18s %23.2f ",
                            retrieveRoomTypeById(choiceId).getName(), choiceMap.get(choiceId), calculateReservationCost(booking, choiceId));
                    System.out.println();
                }
            } catch (RoomTypeNotFoundException_Exception ex) {
                System.out.println("Room Type not found!");
            }
        }

        System.out.println(
                "-----------------------------------------------------------------------------------------------");

        System.out.println(
                "Final Cost: " + totalCost);
        System.out.println();

        System.out.println(
                "Confirm reservation? (Enter 'Y' to confirm)");
        if (sc.nextLine()
                .trim().equals("Y")) {
            booking.setCost(totalCost);
            booking = createNewBooking(booking); //input to database
            for (Reservation r : choiceReservationList) {
                r.setBooking(booking);
                createNewReservation(r); //input to database
            }
            System.out.println("Reservation created! Reservation id : " + booking.getBookingId());
        }
    }

    private static void doViewMyReservation(Long partnerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Holiday Reservation Client :: View My Reservation ***\n");
        System.out.println("Which reservation would you like to view?  (Enter reservation id) ");
        Long bookingId = scanner.nextLong();
        try {
            Booking booking = retrieveBookingByIdForPartner(bookingId, partnerId);
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Start Date: " + booking.getStartDate());
            System.out.println("End Date: " + booking.getEndDate());
            System.out.println("Booking Type: " + booking.getBookingType());
            System.out.println("Booking Status: " + booking.getBookingStatus());
            System.out.println("Total Cost: " + booking.getCost());
            List<Reservation> reservations = retrieveAllReservationFromBooking(booking.getBookingId());
            List<RoomType> ranking = retrieveAllRoomtype();
            int[] quantityEach = new int[ranking.size()];
            for (int i = 0; i < quantityEach.length; i++) {
                quantityEach[i] = 0;
            }
            for (Reservation reservation : reservations) {
                int rank = reservation.getInitialRoomType().getRanking();
                rank--;
                quantityEach[rank]++;
            }
            System.out.println("\nRooms reserved:");
            for (int i = 0; i < quantityEach.length; i++) {
                System.out.println(ranking.get(i).getName() + ": " + quantityEach[i]);
            }
            System.out.println("\nTotal number of rooms reserved: " + reservations.size());
            System.out.println("*********************************************************************");
            System.out.println();

        } catch (BookingNotFoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving the reservation " + ex.getMessage() + "!\n");
        }
    }

    private static void doViewAllMyReservation(Long partnerId) {
        System.out.println("*** HoRS :: Holiday Reservation Client :: View All My Reservations ***\n");

        List<Booking> list = retrieveAllBookingsForPartner(partnerId);
        List<Long> bookingIds = new ArrayList<>();
        for (Booking b : list) {
            bookingIds.add(b.getBookingId());
        }
        if (bookingIds.size() == 0) {
            System.out.println("No past reservations made.");
        } else {
            for (int i = 0; i < bookingIds.size(); i++) {
                System.out.println((i + 1) + ". Booking ID: " + list.get(i).getBookingId());
                System.out.println("Start Date: " + list.get(i).getStartDate());
                System.out.println("End Date: " + list.get(i).getEndDate());
                System.out.println("Booking Type: " + list.get(i).getBookingType());
                System.out.println("Booking Status: " + list.get(i).getBookingStatus());
                System.out.println("Total Cost: " + list.get(i).getCost());
                List<Reservation> reservations = retrieveAllReservationFromBooking(bookingIds.get(i));
                System.out.println("Number of rooms reserved: " + reservations.size());
                List<RoomType> ranking = retrieveAllRoomtype();
                int[] quantityEach = new int[ranking.size()];
                for (int j = 0; j < quantityEach.length; j++) {
                    quantityEach[j] = 0;
                }
                for (Reservation reservation : reservations) {
                    int rank = reservation.getInitialRoomType().getRanking();
                    rank--;
                    quantityEach[rank]++;
                }
                System.out.println("\nRooms reserved:");
                for (int k = 0; k < quantityEach.length; k++) {
                    System.out.println(ranking.get(k).getName() + ": " + quantityEach[k]);
                }
                System.out.println("\nTotal number of rooms reserved: " + reservations.size());
                System.out.println("*********************************************************************");
                System.out.println();

            }
        }
    }

    private static Booking createNewBooking(ws.session.Booking arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.createNewBooking(arg0);
    }

    private static Reservation createNewReservation(ws.session.Reservation arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.createNewReservation(arg0);
    }

    private static Partner loginForPartner(java.lang.String arg0, java.lang.String arg1) throws PartnerNotFoundException_Exception, InvalidLoginCredentials_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.loginForPartner(arg0, arg1);
    }

    private static java.util.List<ws.session.Booking> retrieveAllBookingsForPartner(java.lang.Long arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllBookingsForPartner(arg0);
    }

    private static java.util.List<ws.session.Room> retrieveAllEnabledRooms() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllEnabledRooms();
    }

    private static java.util.List<ws.session.Reservation> retrieveAllReservationFromBooking(java.lang.Long arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllReservationFromBooking(arg0);
    }

    private static Booking retrieveBookingByIdForPartner(java.lang.Long arg0, java.lang.Long arg1) throws BookingNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveBookingByIdForPartner(arg0, arg1);
    }

    private static void updatePartnerLogin(ws.session.Partner arg0, boolean arg1) {
        ws.session.HolidayWebService_Service service = new ws.session.HolidayWebService_Service();
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        port.updatePartnerLogin(arg0, arg1);
    }

    private static void updateBooking(ws.session.Booking arg0) {
        ws.session.HolidayWebService_Service service = new ws.session.HolidayWebService_Service();
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        port.updateBooking(arg0);
    }

    private static java.util.List<ws.session.RoomType> retrieveAllRoomtype() {
        ws.session.HolidayWebService_Service service = new ws.session.HolidayWebService_Service();
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllRoomtype();
    }

    private static void updatePartner(ws.session.Partner arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        port.updatePartner(arg0);
    }

    private static java.util.List<java.lang.Long> retrieveAllBookingsWithinDates(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllBookingsWithinDates(arg0, arg1);
    }

    private static BigDecimal calculateReservationCost(ws.session.Booking arg0, java.lang.Long arg1) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.calculateReservationCost(arg0, arg1);
    }

    private static java.util.List<ws.session.RoomType> retrieveAllEnabledAndIsUsedRoomType() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllEnabledAndIsUsedRoomType();
    }

    private static RoomType retrieveRoomTypeById(java.lang.Long arg0) throws RoomTypeNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveRoomTypeById(arg0);
    }

    private static java.util.List<ws.session.Room> retrieveAllEnabledRoomsFromRoomType(ws.session.RoomType arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllEnabledRoomsFromRoomType(arg0);
    }

    
}
