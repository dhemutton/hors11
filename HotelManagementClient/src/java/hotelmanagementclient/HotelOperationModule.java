package hotelmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import static enums.EmployeeTypeEnum.OPERATIONSMANAGER;
import static enums.ExceptionTypeEnum.TYPE1;
import static enums.ExceptionTypeEnum.TYPE2;
import enums.RateTypeEnum;
import static enums.RateTypeEnum.NORMAL;
import static enums.RateTypeEnum.PEAK;
import static enums.RateTypeEnum.PROMO;
import static enums.RateTypeEnum.PUBLISHED;
import exceptions.RoomExistException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateExistException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeCannotHaveDuplicatePublishedOrNormalException;
import exceptions.RoomTypeExistException;
import exceptions.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class HotelOperationModule {

    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private EmployeeControllerRemote employeeControllerRemote;

    public HotelOperationModule() {
    }

    public HotelOperationModule(RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote,
            ReservationControllerRemote reservationControllerRemote, EmployeeControllerRemote employeeControllerRemote) {
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.employeeControllerRemote = employeeControllerRemote;
    }

    public void runHotelOperationsModule(Employee employee) {
        Scanner sc = new Scanner(System.in);
        try {
            if (employee.getEmployeeType().equals(OPERATIONSMANAGER)) { //Operations Manager
                while (true) {
                    System.out.println("*** HoRS :: Hotel Operations :: Operations Manager ***\n");

                    System.out.println("Room Type Management");
                    System.out.println();
                    System.out.println("1. Create new room type");
                    System.out.println("2. View room type details");
                    System.out.println("3. View all room types");

                    System.out.println("******************************************************************");
                    System.out.println();

                    System.out.println("Room Management");
                    System.out.println();
                    System.out.println("4. Create new room");
                    System.out.println("5. Update room");
                    System.out.println("6. Delete room");
                    System.out.println("7. View all rooms");

                    System.out.println("******************************************************************");
                    System.out.println();

                    System.out.println("8. View room allocation Exception Report"); //show type 1 and type 2
                    System.out.println("9. Logout");

                    int choice = sc.nextInt();
                    if (choice == 1) {
                        doCreateRoomType();
                    } else if (choice == 2) {
                        doViewRoomTypeDetails();
                    } else if (choice == 3) {
                        doViewAllRoomTypes();

                    } else if (choice == 4) {
                        doCreateRoom();
                    } else if (choice == 5) {
                        doUpdateRoomDetails();
                    } else if (choice == 6) {
                        doDeleteRoom();
                    } else if (choice == 7) {
                        doViewAllRoom();
                    } else if (choice == 8) {
                        doRoomAllocationExceptionReport();
                    } else if (choice == 9) {
                        System.out.println("Logging out...");
                        employeeControllerRemote.updateEmployeeLogin(employee, false);
                        break;
                    } else {
                        System.out.println("Invalid entry. Please try again");
                    }
                }
            } else {                //Sales Manager
                while (true) {
                    System.out.println("*** HoRS :: Hotel Operations :: Sales Manager ***\n");

                    System.out.println("1. Create new room rate");
                    System.out.println("2. View room rate details");
                    System.out.println("3. View all room rates");

                    System.out.println("4. Exit");
                    int choice = sc.nextInt();
                    if (choice == 1) {
                        doCreateRoomRate();
                    } else if (choice == 2) {
                        doViewRoomRateDetails();
                    } else if (choice == 3) {
                        doViewAllRoomRate();
                    } else if (choice == 4) {
                        System.out.println("Logging out...");
                        employeeControllerRemote.updateEmployeeLogin(employee, false);
                        break;
                    } else {
                        System.out.println("Invalid entry. Please try again");
                    }
                }
            }
        } catch (InputMismatchException ex) {
            System.out.println("An error has occurred while creating the new employee: " + ex.getMessage() + "!\n");
        }
    }

    //when we create room type we don't attach room rate yet
    private void doCreateRoomType() {
        RoomType roomType = new RoomType();
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("*** HoRS ::Hotel Operations :: Create New Room Type ***\n");
            System.out.print("Enter room type name> ");
            roomType.setName(scanner.nextLine().trim());
            List<RoomType> ranking = roomTypeControllerRemote.retrieveAllRoomtype();
            while (true) {
                System.out.print("Enter Rank (1 to " + (ranking.size() + 1) + ") > ");
                int option = scanner.nextInt();
                if (option == ranking.size() + 1) {
                    roomType.setRanking(option);
                    break;
                } else if (option <= ranking.size() && option > 0) {
                    roomTypeControllerRemote.createRankings(option);
                    roomType.setRanking(option);
                    break;
                } else {
                    System.out.println("Invalid entry. please try again");
                }
                scanner.nextLine();
            }
            scanner.nextLine();
            System.out.print("Enter description> ");
            roomType.setDescription(scanner.nextLine().trim());
            System.out.print("Enter size of room type (enter in square metres) > ");
            roomType.setSize(scanner.nextInt());
            scanner.nextLine();
            System.out.print("Enter bed details> ");
            roomType.setBed(scanner.nextLine().trim());
            System.out.print("Enter capacity> ");
            roomType.setCapacity(scanner.nextLine().trim());
            System.out.print("Enter amenities> ");
            roomType.setAmenities(scanner.nextLine().trim());
            roomType.setIsEnabled(Boolean.TRUE); //Default setting: only set to FALSE if intended to delete but isUsed is true
            roomType.setIsUsed(Boolean.FALSE); //since no one can book yet

            roomType = roomTypeControllerRemote.createRoomType(roomType);
            System.out.println("Room type is enabled. (Update room type to disable)");
            System.out.println("Room type cannot be used. (Requires minimally a room and a normal room rate to be used)");

            System.out.println("New room type:  " + roomType.getName() + " created successfully!" + "\n");
        } catch (RoomTypeExistException | InputMismatchException ex) {
            System.out.println("An error has occurred while creating the new room type: " + ex.getMessage() + "!\n");
        }
    }

    private void doViewRoomTypeDetails() {
        System.out.println("*** HoRS ::Hotel Operations :: View Room Type Details ***\n");
        System.out.println("Enter the room type name: ");

        Scanner sc = new Scanner(System.in);
        try {

            RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByName(sc.nextLine().trim());
            System.out.println("\nRoom Type Details: ");
            System.out.println();
            System.out.println("Name: " + roomType.getName());
            System.out.println("Description: " + roomType.getDescription());
            System.out.println("Bed: " + roomType.getBed());
            System.out.println("Capacity: " + roomType.getCapacity());
            System.out.println("Amenities: " + roomType.getAmenities());
            System.out.println("Size: " + roomType.getSize());
            System.out.println("Rank: " + roomType.getRanking());
            System.out.println("Enabled?  " + roomType.getIsEnabled());
            System.out.println("Is Used?  " + roomType.getIsUsed());
            System.out.println("*************************************");

            System.out.println();

            System.out.println("Room rates: ");

            if (roomType.getRoomRates().isEmpty()) {
                System.out.println("No room rates");
            } else {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                System.out.println("------------------------------------------------------------------------------------------------------------------");

                System.out.printf("%-40s %-20s %-15s %-20s %-20s\n", "Room Rate Name", "Rate Type", "Rate/Night", "Start Date", "End Date");
                System.out.println();

                System.out.println("------------------------------------------------------------------------------------------------------------------");

                for (RoomRate roomRate : roomType.getRoomRates()) {
                    if (roomRate.getRateType() == PROMO || roomRate.getRateType() == PEAK) {
                        System.out.printf("%-40s %-20s %-15.2f %-20s %-20s\n", roomRate.getName(), roomRate.getRateType().toString(), roomRate.getRatePerNight(), df.format(roomRate.getStartDate()), df.format(roomRate.getEndDate()));
                    } else {
                        System.out.printf("%-40s %-20s %-15.2f %-20s %-20s\n", roomRate.getName(), roomRate.getRateType().toString(), roomRate.getRatePerNight(), null, null);
                    }
                }
                System.out.println();
                System.out.println("***************************************************************************************************************************");

            }
            System.out.println();

            System.out.println("Rooms: ");

            if (roomType.getRooms().isEmpty()) {
                System.out.println("No rooms of this room type");
            } else {

                System.out.printf("%-15s%-10s%-20s%-20s\n", "Room number", "Room ID", "Enabled?", "Vacant?");
                for (Room room : roomType.getRooms()) {
                    System.out.printf("%-15s%-10d%-20s%-20s\n", room.getRoomNumber(), room.getRoomId(), room.getIsEnabled(), room.getIsVacant());
                }
                System.out.println();
                System.out.println("*************************************");
            }
            while (true) {
                System.out.println("Choose one of the following options");
                System.out.println("1. Update room type details");
                System.out.println("2. Delete room type");
                System.out.println("3. Exit");
                int choice = sc.nextInt();
                if (choice == 1) {
                    doUpdateRoomTypeDetails(roomType);
                    break;
                } else if (choice == 2) {
                    doDeleteRoomType(roomType);
                    break;
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Invalid option selected. Please try again");
                }
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room type " + ex.getMessage() + "!\n");
        } catch (InputMismatchException ex) {
            System.out.println("An error has occurred while retrieving the room type " + ex.getMessage() + "!\n");
        }
    }

    private void doUpdateRoomTypeDetails(RoomType roomType) {
        System.out.println("*** HoRS ::Hotel Operations :: Editing Room Type Details ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setName(input);
        }

        List<RoomType> ranking = roomTypeControllerRemote.retrieveAllRoomtype();
        System.out.println("Change Rank? (Enter 'Y' to change rank)");
        if (scanner.nextLine().trim().equals("Y")) {
            while (true) {
                System.out.print("Enter Rank (1 to " + ranking.size() + ")> ");
                try {
                    int option = scanner.nextInt();

                    if (option <= ranking.size() && option > 0) {
                        roomTypeControllerRemote.updateRankings(roomType.getRanking(), option);
                        roomType.setRanking(option);
                        break;
                    } else {
                        System.out.println("Invalid entry. please try again");
                        scanner.nextLine();
                    }
                } catch (InputMismatchException ex) {
                    System.out.println("Input mismatch. Try again.");
                    scanner.nextLine();
                }
            }
        }

        System.out.print("Enter Description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setDescription(input);
        }

        System.out.print("Enter Bed Details (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setBed(input);
        }

        System.out.print("Enter Capacity (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setCapacity(input);
        }

        System.out.print("Enter Amenities (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setAmenities(input);
        }

        System.out.print("Enter Size (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            int newSize = Integer.parseInt(input);
            roomType.setSize(newSize);
        }
        System.out.println("Disable room type? (Enter 'Y' to disable, else will be enabled)");
        if (scanner.nextLine().trim().equals("Y")) {
            System.out.println("Disabled room type  " + roomType.getName() + " !");
            roomType.setIsEnabled(Boolean.FALSE);
        } else {
            roomType.setIsEnabled(Boolean.TRUE);
            System.out.println("Enabled room type  " + roomType.getName() + " !");

        }
        roomTypeControllerRemote.updateRoomType(roomType);
        System.out.println("Room type " + roomType.getName() + " updated successfully! \n");
    }

    private void doDeleteRoomType(RoomType roomType) {
        Scanner sc = new Scanner(System.in);
        if (roomType.getIsUsed() == false) {
            roomType.setIsEnabled(Boolean.FALSE);
            roomTypeControllerRemote.deleteRoomType(roomType);
            System.out.println("Room type successfully deleted! ");

        } else {
            System.out.println("Room type " + roomType.getName() + " is in use. Unable to delete room type record");
            System.out.println("Room type has to have no rooms to be safely deleted.");
            System.out.println("Do you want to disable room type now? (Enter 'Y' to disable, else will be enabled)");
            if (sc.nextLine().trim().equals("Y")) {
                System.out.println("Disabled room type  " + roomType.getName() + " !");
                roomType.setIsEnabled(Boolean.FALSE);
                roomTypeControllerRemote.updateRoomType(roomType);
            }
        }
    }

    private void doViewAllRoomTypes() {
        System.out.println("*** HoRS :: Hotel Operations :: View All Room Types ***\n");

        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomtype();
        System.out.printf("%-5s%-20s%-5s\n", "Rank", "Name", "ID");
        for (RoomType roomType : roomTypes) {
            System.out.printf("%-5d%-20s%-5d\n", roomType.getRanking(), roomType.getName(), roomType.getRoomTypeId());
        }
        System.out.println("*********************************************************************");
    }

    private void doCreateRoom() {
        Room room = new Room();
        Scanner scanner = new Scanner(System.in);
        Long roomTypeId;
        try {
            System.out.println("*** HoRS ::Hotel Operations :: Create New Room ***\n");
            System.out.print("Enter room number (4 digits only): ");
            room.setRoomNumber(scanner.nextLine().trim());
            System.out.println("Select room type: ");
            List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllEnabledRoomType();

            for (int i = 0; i < roomTypes.size(); i++) {
                System.out.println((i + 1) + ". " + roomTypes.get(i).getName());
            }
            while (true) {
                int input = scanner.nextInt();
                if (input > 0 && input <= roomTypes.size()) {
                    roomTypeId = roomTypes.get(input - 1).getRoomTypeId();
                    break;
                } else {
                    System.out.println("Incorrect input, please try again.");
                }
            }
            room.setIsVacant(Boolean.TRUE);
            room.setIsEnabled(Boolean.TRUE);
            room = roomControllerRemote.createRoom(room, roomTypeId);

            System.out.println("Room is set to vacant. (Update room to set as occupied)");
            System.out.println("Room is enabled. (Update room to disable)");

            //check if roomtype can be used after creation of room
            RoomType rt = roomTypeControllerRemote.retrieveRoomTypeById(roomTypeId);
            Boolean canBeUsed = false;
            for (RoomRate rr : rt.getRoomRates()) {
                if (rr.getRateType().equals(NORMAL)) {
                    rt.setIsUsed(Boolean.TRUE);
                    canBeUsed = true;
                }
            }
            if (canBeUsed) {
                roomTypeControllerRemote.updateRoomType(rt);
                System.out.println("Room type " + rt.getName() + " is set to used since there is a normal room rate and a room for this room type.");
            } else {
                System.out.println("Room type " + rt.getName() + " is still set to unused since there is a no normal room rate for this room type.");
            }
            System.out.println("New room:  " + room.getRoomNumber() + " created successfully!" + "\n");

        } catch (RoomExistException ex) {
            System.out.println("An error has occurred while creating the new room: " + ex.getMessage() + "!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving existing room type: " + ex.getMessage() + "!\n");
        }
    }

    private void doUpdateRoomDetails() {
        System.out.println("*** HoRS ::Hotel Operations :: Editing Room Details ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;
        Long oldroomTypeId;
        Long newroomTypeId;

        try {
            System.out.println("Which room would you like to edit details of?  (Enter room number) ");
            input = scanner.nextLine().trim();
            Room room = roomControllerRemote.retrieveRoomByRoomNum(input);
            oldroomTypeId = room.getRoomType().getRoomTypeId();
            newroomTypeId = room.getRoomType().getRoomTypeId();

            System.out.println("Current Details: ");
            System.out.println("Room Number: " + room.getRoomNumber());
            System.out.println("Room Type: " + room.getRoomType().getName());
            System.out.println("Enabled?  " + room.getIsEnabled());
            System.out.println("Vacant? " + room.getIsVacant());

            System.out.println("*********************************************************************");

            System.out.println("Enter New Room Number (blank if no change)> ");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                room.setRoomNumber(input);
            }

            System.out.println("Enable Room?  (Enter 'Y' to enable, else will be disabled) ");
            input = scanner.nextLine().trim();
            if (input.equals("Y")) {
                room.setIsEnabled(Boolean.TRUE);
            } else {
                room.setIsEnabled(Boolean.FALSE);
            }
            System.out.println("Set to vacant?  (Enter 'Y' to set vacant, else will be occupied) ");
            if (scanner.nextLine().trim().equals("Y")) {
                room.setIsVacant(Boolean.TRUE);
            } else {
                room.setIsVacant(Boolean.FALSE);
            }

            System.out.println("Change room type?  (Enter 'Y' to change) ");

            if (scanner.nextLine().trim().equals("Y")) {
                System.out.println("Select room type: ");
                List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomtype();

                for (int i = 0; i < roomTypes.size(); i++) {
                    System.out.println((i + 1) + ". " + roomTypes.get(i).getName());
                }
                while (true) {
                    int choice = scanner.nextInt();
                    choice--;
                    if (choice >= 0 && choice < roomTypes.size()) {
                        newroomTypeId = roomTypes.get(choice).getRoomTypeId();
                        break;
                    } else {
                        System.out.println("Incorrect input, please try again.");
                    }
                }
                //check if roomtype has no more rooms after the room type change
                RoomType rt = roomTypeControllerRemote.retrieveRoomTypeById(oldroomTypeId);
                if (rt.getRooms().size() == 1) {
                    rt.setIsUsed(Boolean.FALSE);
                    roomTypeControllerRemote.updateRoomType(rt);
                    System.out.println("Previous room type " + rt.getName() + " is set to unused since there is no more rooms for this room type.");
                }

                //check if new roomtype will have a room after the room type change
                RoomType newrt = roomTypeControllerRemote.retrieveRoomTypeById(newroomTypeId);
                Boolean canBeUsed = false;
                if (newrt.getRooms().size() == 0) { //0 since havent add this new room creation yet
                    for (RoomRate rr : newrt.getRoomRates()) {
                        if (rr.getRateType().equals(NORMAL)) {
                            newrt.setIsUsed(Boolean.TRUE);
                            canBeUsed = true;
                        }
                    }
                    if (canBeUsed) {
                        roomTypeControllerRemote.updateRoomType(newrt);
                        System.out.println("New room type " + newrt.getName() + " is set to used since there is a normal room rate and a room for this room type.");
                    } else {
                        System.out.println("New room type " + newrt.getName() + " is still set to unused since there is a no normal room rate for this room type.");
                    }
                }
            }
            roomControllerRemote.updateRoom(room, oldroomTypeId, newroomTypeId);
            System.out.println("Room updated successfully!: \n");

        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room " + ex.getMessage() + "!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room type " + ex.getMessage() + "!\n");
        }
    }

    private void doDeleteRoom() {

        System.out.println("*** HoRS ::Hotel Operations :: Deleting Room Details ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;

        try {
            System.out.println("Which room would you like to delete?  (Enter room number) ");
            input = scanner.nextLine().trim();
            Room room = roomControllerRemote.retrieveRoomByRoomNum(input);

            System.out.println("Current Details: ");
            System.out.println("Room Number: " + room.getRoomNumber());
            System.out.println("Room Type: " + room.getRoomType().getName());
            System.out.println("Enabled?  " + room.getIsEnabled());
            System.out.println("Vacant? " + room.getIsVacant());
            System.out.println("*********************************************************************");

            if (room.getIsVacant()) {
                System.out.println("Delete room?  (Enter 'Y' to delete) ");
                if (scanner.nextLine().trim().equals("Y")) {

                    //check if roomtype has no more rooms after the room deletion
                    RoomType rt = roomTypeControllerRemote.retrieveRoomTypeById(room.getRoomType().getRoomTypeId());
                    if (rt.getRooms().size() == 1) {
                        rt.setIsUsed(Boolean.FALSE);
                        roomTypeControllerRemote.updateRoomType(rt);
                        System.out.println("Previous room type " + rt.getName() + " is set to unused since there is no more rooms for this room type.");
                    }

                    roomControllerRemote.deleteRoom(room.getRoomId());
                    System.out.println("Successfully deleted room record.");
                }
            } else {
                System.out.println("Room is occupied, unable to delete room record.");
                System.out.println("Do you want to disable room now? (Enter 'Y' to disable)");
                if (scanner.nextLine().trim().equals("Y")) {
                    System.out.println("Disabled room " + room.getRoomNumber() + " !");
                    room.setIsEnabled(Boolean.FALSE);
                    roomControllerRemote.mergeRoom(room);
                }
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room : " + ex.getMessage() + "!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room type : " + ex.getMessage() + "!\n");

        }
    }

    private void doViewAllRoom() {
        System.out.println("*** HoRS ::Hotel Operations :: View All Rooms ***\n");
        List<Room> list = roomControllerRemote.retrieveAllRooms();
        System.out.printf("%-15s%-10s%-25s%-20s%-20s\n", "Room number", "Room ID", "Room Type", "Enabled?", "Vacant?");
        for (Room room : list) {
            System.out.printf("%-15s%-10d%-25s%-20s%-20s\n", room.getRoomNumber(), room.getRoomId(), room.getRoomType().getName(), room.getIsEnabled(), room.getIsVacant());
        }
        System.out.println();

    }

    private void doRoomAllocationExceptionReport() {
        System.out.println("*** HoRS ::Hotel Operations :: Viewing Room Allocation Exception Report ***\n");

        Date today = new Date(); //get today's date
        System.out.println("Today's exception report (" + today + "): ");

        //Obtain a list of all starting reservations(checking in)
        List<Reservation> todayReservationList = reservationControllerRemote.retrieveAllReservationFromStartDate(today);
        List<Reservation> type1 = new ArrayList<>();
        List<Reservation> type2 = new ArrayList<>();
        for (Reservation reservation : todayReservationList) {
            if (reservation.getExceptionType().equals(TYPE1)) {
                type1.add(reservation);
            } else if (reservation.getExceptionType().equals(TYPE2)) {
                type2.add(reservation);
            }
        }
        System.out.println("Type 1: ");
        if (type1.size() == 0) {
            System.out.println("No type 1 exceptions today.");
        }
        for (Reservation reservation : type1) {
            System.out.println("Reservation Id: " + reservation.getId());
            System.out.println("Initial Room Type: " + reservation.getInitialRoomType().getName());
            System.out.println("Final Room Type: " + reservation.getFinalRoomType().getName());
            System.out.println("*****************************************************");
        }

        System.out.println();
        System.out.println("Type 2: ");
        if (type1.size() == 0) {
            System.out.println("No type 2 exceptions today.");
        }
        for (Reservation reservation : type2) {
            System.out.println("Reservation Id: " + reservation.getId());
        }

        System.out.println("*****************************************************");
    }

    private void doCreateRoomRate() {
        RoomRate roomRate = new RoomRate();
        Scanner scanner = new Scanner(System.in);
        Long roomTypeId = null;
        int choice = 0;
        try {
            System.out.println("*** HoRS ::Hotel Operations :: Create New Room Rate ***\n");
            System.out.print("Enter name of room rate: ");
            roomRate.setName(scanner.nextLine().trim());
            System.out.println("Select rate type: ");
            System.out.println("1. Published");
            System.out.println("2. Normal");
            System.out.println("3. Peak");
            System.out.println("4. Promo");
            //User input room rate type
            while (true) {
                choice = scanner.nextInt();

                if (choice == 1) {
                    roomRate.setRateType(RateTypeEnum.PUBLISHED);
                    roomRate.setForPartner(Boolean.FALSE);
                    roomRate.setEndDate(null);
                    roomRate.setStartDate(null);
                    break;
                } else if (choice == 2) {
                    roomRate.setRateType(RateTypeEnum.NORMAL);
                    roomRate.setForPartner(Boolean.TRUE);
                    roomRate.setEndDate(null);
                    roomRate.setStartDate(null);
                    break;

                } else if (choice == 3) {
                    roomRate.setRateType(RateTypeEnum.PEAK);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else if (choice == 4) {
                    roomRate.setRateType(RateTypeEnum.PROMO);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else {
                    System.out.println("Incorrect input, please try again.");
                }
            }
            //User input room type
            System.out.println("Select room type to apply this room rate for: ");
            List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomtype();
            for (int i = 0; i < roomTypes.size(); i++) {
                System.out.println((i + 1) + ". " + roomTypes.get(i).getName());
            }
            while (true) {
                choice = scanner.nextInt();
                choice--;
                if (choice >= 0 && choice < roomTypes.size()) {
                    roomTypeId = roomTypes.get(choice).getRoomTypeId();
                    break;
                } else {
                    System.out.println("Incorrect input, please try again.");
                }
            }
            scanner.nextLine();
            //If room rate is PROMO/PEAK, prompt for start and end date
            Date endDate, startDate;
            while (true) {
                System.out.println("Enter Rate Per Night: ");
                BigDecimal amt = scanner.nextBigDecimal();
                scanner.nextLine();

                if (amt.compareTo(new BigDecimal(0)) >= 0) {
                    roomRate.setRatePerNight(amt);
                    break;
                }
                System.out.println("Enter a valid amount!");
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            if (roomRate.getRateType() == PROMO || roomRate.getRateType() == PEAK) {
                while (true) {
                    while (true) {
                        System.out.println("Please enter start date (dd/mm/yyyy):");
                        String start = scanner.nextLine().trim();
                        if (start.length() == 10) {
                            try {
                                startDate = formatter.parse(start);
                                roomRate.setStartDate(startDate);
                                break;
                            } catch (ParseException ex) {
                                System.out.println("Incorrect date format.");
                            }
                        } else {
                            System.out.println("Incorrect date format.");
                        }
                    }
                    while (true) {
                        System.out.println("Enter end date (format: dd/mm/yyyy):");
                        String end = scanner.nextLine().trim();
                        if (end.length() == 10) {
                            try {
                                endDate = formatter.parse(end);
                                if (startDate.before(endDate)) {
                                    roomRate.setEndDate(endDate);
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
                    if (!roomRateControllerRemote.validateRoomRatePeriod(roomRate.getRateType(), startDate, endDate).isEmpty()) {
                        System.out.println("Invalid period entered. Conflict with existing period of the same rate type.");
                    } else {
                        break;
                    }
                }
            }
            if (roomRate.getRateType() == PUBLISHED || roomRate.getRateType() == NORMAL) {
                roomRate.setIsValid(Boolean.TRUE);
                System.out.println("Room rate is enabled. (Update room rate to disable)");
                System.out.println("Room rate is valid.");
            }
            //check if roomtype has a room after normal room rate creation
            RoomType rt = roomTypeControllerRemote.retrieveRoomTypeById(roomTypeId);
            if (roomRate.getRateType().equals(NORMAL)) {
                if (rt.getRooms().isEmpty()) {
                    rt.setIsUsed(Boolean.FALSE);
                    roomTypeControllerRemote.updateRoomType(rt);
                    System.out.println("Room type " + rt.getName() + " is still set to unused since there is no rooms for this room type.");
                } else {
                    rt.setIsUsed(Boolean.TRUE);
                    System.out.println("Room type " + rt.getName() + " is set to used since there is at least a room and a normal room rate for this room type.");
                }
                roomTypeControllerRemote.updateRoomType(rt);
            }
            roomRate = roomRateControllerRemote.createRoomRate(roomRate, roomTypeId);

            System.out.println("New room rate:  " + roomRate.getName() + " created successfully!" + "\n");

        } catch (RoomRateExistException | RoomTypeCannotHaveDuplicatePublishedOrNormalException | RoomTypeNotFoundException | InputMismatchException ex) {
            System.out.println("An error has occurred while creating the new room rate: " + ex.getMessage() + "\n");
        }

    }

    private void doViewAllRoomRate() {
        System.out.println("*** HoRS ::Hotel Operations :: View All Room Rates ***\n");
        List<RoomRate> list = roomRateControllerRemote.retrieveAllRoomRates();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("------------------------------------------------------------------------------------------------------------------");

        System.out.printf("%-40s %-20s %-15s %-20s %-20s\n", "Room Rate Name", "Rate Type", "Rate/Night", "Start Date", "End Date");
        System.out.println();

        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (RoomRate roomRate : list) {
            if (roomRate.getRateType() == PROMO || roomRate.getRateType() == PEAK) {
                System.out.printf("%-40s %-20s %-15.2f %-20s %-20s\n", roomRate.getName(), roomRate.getRateType().toString(), roomRate.getRatePerNight(), df.format(roomRate.getStartDate()), df.format(roomRate.getEndDate()));
            } else {
                System.out.printf("%-40s %-20s %-15.2f %-20s %-20s\n", roomRate.getName(), roomRate.getRateType().toString(), roomRate.getRatePerNight(), null, null);
            }
        }
        System.out.println();
        System.out.println("***************************************************************************************************************************");

    }

    private void doViewRoomRateDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS ::Hotel Operations :: View Room Rate Details ***\n");
        String input;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        try {
            System.out.println("Which room rate would you like to view details of?  (Enter room rate name) ");
            input = sc.nextLine().trim();
            RoomRate roomRate = roomRateControllerRemote.retrieveRoomRateByName(input);

            System.out.println("Room Rate Details: ");
            System.out.println("Room Rate Name: " + roomRate.getName());
            System.out.println("Room Rate Type: " + roomRate.getRateType());
            System.out.println("Room Type: " + roomRate.getRoomType().getName());

            System.out.println("Rate Per Night: " + roomRate.getRatePerNight());
            if (roomRate.getRateType() == PROMO || roomRate.getRateType() == PEAK) {
                System.out.println("Room Rate Start Date: " + df.format(roomRate.getStartDate()));
                System.out.println("Room Rate End Date: " + df.format(roomRate.getEndDate()));
            }
            System.out.println("Room Rate Enabled? " + roomRate.getIsEnabled());
            System.out.println("Room Rate Valid? " + roomRate.getIsValid());
            System.out.println();

            System.out.println("*********************************************************************");
            while (true) {
                System.out.println("Choose following options");
                System.out.println("1. Update room rate details");
                System.out.println("2. Delete room rate");
                System.out.println("3. Exit");
                int choice = sc.nextInt();
                if (choice == 1) {
                    doUpdateRoomRateDetails(roomRate);
                    break;
                } else if (choice == 2) {
                    doDeleteRoomRate(roomRate);
                    break;
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Invalid option selected. Please try again");
                }
            }
        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room rate " + ex.getMessage() + "!\n");
        } catch (InputMismatchException ex) {
            System.out.println("Invalid input! \n");

        }
    }

    private void doUpdateRoomRateDetails(RoomRate roomRate) {
        Long roomTypeId = roomRate.getRoomType().getRoomTypeId();
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS ::Hotel Operations :: Edit Room Rate Details ***\n");

        String input;
        System.out.println("Enter New Room Rate Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomRate.setName(input);
        }

        System.out.println("Change Rate Per Night?  (Enter 'Y' to change) ");
        if (scanner.nextLine().trim().equals("Y")) {
            while (true) {
                try {
                    System.out.println("Enter Rate Per Night: ");
                    BigDecimal amt = scanner.nextBigDecimal();
                    if (amt.compareTo(new BigDecimal(0)) >= 0) {
                        roomRate.setRatePerNight(amt);
                        break;
                    }
                    scanner.nextLine();
                    System.out.println("Enter a valid amount!");
                } catch (NumberFormatException ex) {
                    System.out.println("Enter a valid amount!");

                }
            }
        }
        Date startDate = new Date();
        Date endDate;
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (roomRate.getRateType().equals(PEAK) || roomRate.getRateType().equals(PROMO)) {

            while (true) {
                while (true) {
                    System.out.println("Please enter start date (dd/mm/yyyy):");
                    String start = scanner.nextLine().trim();
                    if (start.length() == 10) {
                        try {
                            startDate = formatter.parse(start);
                            if (startDate.after(today)) {
                                break;
                            } else {
                                System.out.println("Please enter a date starting from " + today);
                            }

                        } catch (ParseException ex) {
                            System.out.println("Incorrect date format.");
                        }
                    } else {
                        System.out.println("Incorrect date format.");
                    }
                }
                while (true) {
                    System.out.println("Enter end date (format: dd/mm/yyyy):");
                    String end = scanner.nextLine().trim();
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
                if (!roomRateControllerRemote.validateRoomRatePeriod(roomRate.getRateType(), startDate, endDate).isEmpty()) {
                    System.out.println("Invalid period entered. Conflict with room rate");
                }
            }
        }

        if (roomRate.getRateType() == PUBLISHED || roomRate.getRateType() == NORMAL) {
            roomRate.setIsValid(Boolean.TRUE);
            System.out.println("Room rate is valid.");
        } else if (roomRate.getStartDate().before(today) && roomRate.getEndDate().after(today) || roomRate.getStartDate().equals(today) || roomRate.getEndDate().equals(today)) {
            roomRate.setIsValid(Boolean.TRUE);
            System.out.println("Room rate is valid.");
        } else {
            roomRate.setIsValid(Boolean.FALSE);
            System.out.println("Room rate is not valid yet. Stated period is in the future.");

        }

        roomRate.setIsEnabled(Boolean.TRUE);
        System.out.println("Room rate " + roomRate.getName() + " is enabled. (Update room rate to disable)");

        roomRateControllerRemote.updateRoomRate(roomRate, roomTypeId);
        System.out.println("Room rate updated successfully! \n");
    }

    private void doDeleteRoomRate(RoomRate roomRate) {
        System.out.println("*** HoRS ::Hotel Operations :: Deleting Room Rate ***\n");
        Scanner scanner = new Scanner(System.in);
        if (!roomRate.getIsValid()) {
            System.out.println("Delete room rate?  (Enter 'Y' to delete) ");
            if (scanner.nextLine().trim().equals("Y")) {
                roomRateControllerRemote.deleteRoomRate(roomRate.getRoomRateId());
                System.out.println("Successfully deleted room rate record.");
            }
        } else {
            System.out.println("Room rate is valid, unable to delete room rate record.");
            System.out.println("Normal and published room rates cannot be deleted but you can update their details.");
            System.out.println("Do you want to disable room rate now? (Enter 'Y' to disable)");
            if (scanner.nextLine().trim().equals("Y")) {
                System.out.println("Disabled room rate " + roomRate.getName() + " !");
                roomRate.setIsEnabled(Boolean.FALSE);
                roomRateControllerRemote.mergeRoomRate(roomRate);
            }
        }
    }
}
