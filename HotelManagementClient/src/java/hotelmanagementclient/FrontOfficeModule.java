package hotelmanagementclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import ejb.session.stateless.SelfInvokeDailyControllerRemote;
import entity.Booking;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import static enums.BookingStatusEnum.COMPLETED;
import static enums.BookingStatusEnum.PENDING;
import static enums.BookingTypeEnum.WALKIN;
import static enums.ExceptionTypeEnum.UNASSIGNED;
import exceptions.BookingNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class FrontOfficeModule {

    private ReservationControllerRemote reservationControllerRemote;
    private BookingControllerRemote bookingControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private EmployeeControllerRemote employeeControllerRemote;
    private SelfInvokeDailyControllerRemote selfInvokeDailyControllerRemote;

    public FrontOfficeModule() {
    }

    public FrontOfficeModule(ReservationControllerRemote reservationControllerRemote, BookingControllerRemote bookingControllerRemote, RoomControllerRemote roomControllerRemote,
            RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, EmployeeControllerRemote employeeControllerRemote, SelfInvokeDailyControllerRemote selfInvokeDailyControllerRemote) {
        this.reservationControllerRemote = reservationControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.employeeControllerRemote = employeeControllerRemote;
        this.selfInvokeDailyControllerRemote = selfInvokeDailyControllerRemote;
    }

    public void runFrontOfficeModule(Employee loginEmployee) {
        System.out.println("*** HoRS :: Front Office ::  Guest Relations Officer ***\n");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Make walk in reservation");
            System.out.println("2. Check in guest");
            System.out.println("3. Check out guest");
            System.out.println("4. *SECRET METHOD* Self-invoke room allocation");
            System.out.println("5. Logout");
            int choice = sc.nextInt();
            if (choice == 1) {
                doWalkInSearchRoom();
            } else if (choice == 2) {
                doCheckInGuest();
            } else if (choice == 3) {
                doCheckOutGuest();
            } else if (choice == 4) {
                doSecretMethod();
            } else if (choice == 5) {
                System.out.println("Logging out...");

                System.out.println("(╯°□°）╯︵ ┻━┻)");

                System.out.println("Goodbye~");

                employeeControllerRemote.updateEmployeeLogin(loginEmployee, false);
                break;
            } else {
                System.out.println("Invalid entry. Please try again");
            }
        }
    }

    private void doWalkInSearchRoom() {
        System.out.println("*** HoRS :: Front Office Module:: Search Room ***\n");
        Date startDate = new Date();
        Scanner sc = new Scanner(System.in);
        Date endDate = null;
        List<Reservation> reservationList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        
        while (true) {
            System.out.println("Enter end date (format: dd/mm/yyyy):");
            String end = sc.nextLine().trim();
            if (end.length() == 10) {
                try {
                    endDate = formatter.parse(end);
                    if (startDate.before(endDate)) {
                        break;
                    } else {
                        System.out.println("End date is before or equal to start date! Please re-enter end date.");
                    }
                } catch (ParseException ex) {;
                    System.out.println("Incorrect date format.");
                }
            } else {
                System.out.println("Incorrect date format.");
            }
        }

        List<Booking> bookingList = bookingControllerRemote.retrieveAllBookingsWithinDates(startDate, endDate);
        for (Booking booking : bookingList) {
            reservationList.addAll(reservationControllerRemote.retrieveAllReservationFromBooking(booking.getBookingId()));
        }

        //EDITED to show room type inventory
        HashMap<Long, Integer> map = new HashMap<>();
        for (RoomType rt : roomTypeControllerRemote.retrieveAllEnabledAndIsUsedRoomType()) {
            int maxRoomInventory = roomControllerRemote.retrieveAllEnabledRoomsFromRoomType(rt).size();
            map.put(rt.getRoomTypeId(), maxRoomInventory);
        }

        for (Reservation r : reservationList) {
            Long id = r.getFinalRoomType().getRoomTypeId(); //WALKIN special cos after 2am
            map.put(id, map.get(id) - 1);
        }
        int roomsLeft = 0;
        List<RoomType> roomTypeList = roomTypeControllerRemote.retrieveAllEnabledAndIsUsedRoomTypesForWalkIn();

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
            Booking booking = new Booking(WALKIN, PENDING, startDate, endDate);

            try {
                System.out.format("%-5s %30s %17s %27.2f ",
                        rt.getRoomTypeId(), rt.getName(), map.get(rt.getRoomTypeId()), roomRateControllerRemote.calculateReservationCost(booking, roomTypeControllerRemote.retrieveRoomTypeById(rt.getRoomTypeId())));
                System.out.println();
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room Type not found!");
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------");

        if (availableRoomTypeIds.size() > 0) {
            System.out.println("Would you like to make a reservation? (Enter 'Y' to reserve)");
            if (sc.nextLine().trim().equals("Y")) {
                doReserveRoom(map, startDate, endDate, roomsLeft);
            }
        } else {
            System.out.println("No more rooms are available during this period");
        }
    }

    private void doCheckInGuest() {
        System.out.println("*** HoRS :: Front Office Module :: Check in Guest ***\n");
        Long bookingId;
        try {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("1. Online Booking");
                System.out.println("2. Walk-in Booking");
                System.out.println("3. Partner Booking");
                int choice = sc.nextInt();
                if (choice == 1) {
                    System.out.println("Please enter booking ID");
                    bookingId = sc.nextLong();
                    System.out.println("Please enter guest ID");
                    Long guestId = sc.nextLong();
                    Booking booking = bookingControllerRemote.retrieveBookingByIdForGuest(bookingId, guestId);
                    break;
                } else if (choice == 2) {
                    System.out.println("Please enter booking ID");
                    bookingId = sc.nextLong();
                    Booking booking = bookingControllerRemote.retrieveBookingById(bookingId);
                    break;
                } else if (choice == 3) {
                    System.out.println("Please enter booking ID");
                    bookingId = sc.nextLong();
                    System.out.println("Please enter partner ID");
                    Long partnerId = sc.nextLong();
                    Booking booking = bookingControllerRemote.retrieveBookingByIdForPartner(bookingId, partnerId);
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }

            List<Reservation> reservationList = reservationControllerRemote.retrieveAllReservationFromBooking(bookingId);
            System.out.println("Your allocated rooms are:");
            for (Reservation reservation : reservationList) {
                reservation.setIsCheckedIn(Boolean.TRUE);
                reservationControllerRemote.updateReservation(reservation);
                System.out.println("Room number: " + reservation.getRoom().getRoomNumber());
            }
            System.out.println("Checked in guest successfully!");
        } catch (BookingNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the reservation " + ex.getMessage() + "!\n");
        }

    }

    private void doCheckOutGuest() {
        System.out.println("*** HoRS :: Front Office Module :: Check Out Guest ***\n");

        try {
            Scanner sc = new Scanner(System.in);
            Date inputDate = null;
            System.out.println("Please enter room number");
            String number = sc.nextLine().trim();
            Room room = roomControllerRemote.retrieveRoomByRoomNum(number);
            room.setIsVacant(Boolean.TRUE);
            roomControllerRemote.mergeRoom(room);
            System.out.println("Please enter your reservation ID");
            Long reservationID = sc.nextLong();
            Reservation reservation = reservationControllerRemote.retrieveReservationById(reservationID);
            reservation.setIsCheckedOut(Boolean.TRUE);
            reservation.setIsCheckedIn(Boolean.FALSE);
            reservationControllerRemote.updateReservation(reservation);
            System.out.println("Checked out guest successfully!");

        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation not found!");
        } catch (RoomNotFoundException ex) {
            System.out.println("Room not found!");
        }
    }

    private void doReserveRoom(HashMap<Long, Integer> map, Date startDate, Date endDate, int roomsLeft) {
        System.out.println("*** HoRS ::  Front Office Module :: Room Reservation ***\n");
        HashMap<Long, Integer> choiceMap = new HashMap<>();
        for (Long potentialChoiceId : map.keySet()) {
            choiceMap.put(potentialChoiceId, 0);
        }
        List<Reservation> choiceReservationList = new ArrayList<>();
        BigDecimal totalCost = new BigDecimal(0);
        Scanner sc = new Scanner(System.in);
        int quantity = 0;
        Booking booking = new Booking(WALKIN, COMPLETED, startDate, endDate);
        while (true) {
            try {
                System.out.println("How many rooms do you want to reserve? (Maximum: " + roomsLeft + ")");
                quantity = sc.nextInt();
                if (quantity <= 0 || quantity > roomsLeft) {
                    System.out.println("Invalid entry. Please try again");
                } else {
                    break;
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid entry. Please try again");
                sc.nextLine();
            }
        }

        sc.nextLine();
        List<RoomType> roomTypeList = roomTypeControllerRemote.retrieveAllEnabledAndIsUsedRoomTypesForWalkIn();

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
                        System.out.println("Making a reservation for " + roomTypeList.get(choice - 1).getName() + ".  Cost : $" + roomRateControllerRemote.calculateReservationCost(booking, roomTypeList.get(choice - 1)));
                        System.out.println("Add to cart? (Enter 'Y' to confirm)");
                        if (sc.nextLine().trim().equals("Y")) {
                            map.put(roomTypeList.get(choice - 1).getRoomTypeId(), map.get(roomTypeList.get(choice - 1).getRoomTypeId()) - 1); //deduct room type inventory on map
                            choiceMap.put(roomTypeList.get(choice - 1).getRoomTypeId(), choiceMap.get(roomTypeList.get(choice - 1).getRoomTypeId()) + 1); //add to the choice cart
                            List<Room> availableRooms = roomControllerRemote.retrieveAllVacantRooms();
                            Room temp = null;
                            for(Room room : availableRooms) {
                                if(room.getRoomType().equals(roomTypeList.get(choice - 1))) {
                                    temp=room;
                                    room.setIsVacant(Boolean.FALSE);
                                    roomControllerRemote.mergeRoom(room);
                                }
                            }
                            choiceReservationList.add(new Reservation(roomTypeList.get(choice - 1), roomTypeList.get(choice - 1), temp, booking, UNASSIGNED)); //add to reservation list
                            totalCost = totalCost.add(roomRateControllerRemote.calculateReservationCost(booking, roomTypeList.get(choice - 1)));
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

        System.out.println("Displaying Your Cart...");
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.printf("%-30s %25s %23s ", "ROOM TYPE NAME", "NO. OF BOOKED ROOMS", "TOTAL COST/ROOM");
        System.out.println();

        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Long choiceId : choiceMap.keySet()) {
            try {
                if (choiceMap.get(choiceId) != 0) {
                    System.out.format("%-30s %18s %23.2f ", roomTypeControllerRemote.retrieveRoomTypeById(choiceId).getName(), choiceMap.get(choiceId), roomRateControllerRemote.calculateReservationCost(booking, roomTypeControllerRemote.retrieveRoomTypeById(choiceId)));
                    System.out.println();
                }
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room Type not found!");
            }
        }

        System.out.println("-----------------------------------------------------------------------------------------------");

        System.out.println("Final Cost: " + totalCost);
        System.out.println();

        System.out.println("Confirm reservation? (Enter 'Y' to confirm)");
        if (sc.nextLine().trim().equals("Y")) {
            booking.setCost(totalCost);
            booking = bookingControllerRemote.createNewBooking(booking); //input to database
            for (Reservation r : choiceReservationList) {
                r.setBooking(booking);
                reservationControllerRemote.createNewReservation(r); //input to database
            }
            System.out.println("Reservation created! Reservation id : " + booking.getBookingId());
            //NEED TO ASSIGN GUEST
        }
    }

    private void doSecretMethod() {
        selfInvokeDailyControllerRemote.dailyReservationRoomAssignment();
//        selfInvokeDailyControllerRemote.deleteAllRoomRates();
//        selfInvokeDailyControllerRemote.deleteAllRoomTypes();
//        selfInvokeDailyControllerRemote.deleteAllRooms();
        System.out.println("Secret Method done hehe!");
    }

}
