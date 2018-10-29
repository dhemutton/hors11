/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelmanagementclient;

import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import static enums.EmployeeTypeEnum.OPERATIONSMANAGER;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author matthealoo
 */
class HotelOperationModule {

    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;

    public HotelOperationModule() {
    }

    public HotelOperationModule(RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
    }

    public void runHotelOperationsModule(Employee employee) throws RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException {
        Scanner sc = new Scanner(System.in);
        if (employee.getEmployeeType().equals(OPERATIONSMANAGER)) {
            while (true) {
                System.out.println("1. Create new room type");
                System.out.println("2. View all room types");
                System.out.println("3. Creeat new room");
                System.out.println("4. View all rooms");
                System.out.println("5. View room allocation exception report");
                System.out.println("6. Exit");
                int choice = sc.nextInt();
                if (choice == 1) {
                    doCreateRoomType();
                } else if (choice == 2) {
                    doViewAllRoomTypes();
                } else if (choice == 3) {
                    doCreateRoom();
                } else if (choice == 4) {
                    doViewAllRoom();
                } else if (choice == 5) {
                    doRoomAllocationExceptionReport();
                } else if (choice == 6) {
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        } else {
            while (true) {
                System.out.println("1. Create new room rate");
                System.out.println("2. View all room rate");
                System.out.println("3. Exit");
                int choice = sc.nextInt();
                if (choice == 1) {
                    doCreateRoomRate();
                } else if (choice == 2) {
                    doViewAllRoomRate();
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        }
    }

    private void doCreateRoomType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllRoomTypes() throws RoomTypeNotFoundException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter ID of room type for more details(Enter 0 to exit)");
            List<RoomType> roomTypeList = roomTypeControllerRemote.retrieveAllRoomtype();
            String title1 = "Room type ID";
            String title2 = "Room type";
            System.out.printf("%-15s %-15s %n", title1, title2);
            for (int i = 0; i < roomTypeList.size(); i++) {
                System.out.printf("%-15d %-15s %n", roomTypeList.get(i).getRoomTypeId(), roomTypeList.get(i).getName());
            }
            Long option = sc.nextLong();
            if (option == 0) {
                break;
            } else {
                doViewRoomTypeDetails(option);
            }
        }
    }

    private void doCreateRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllRoom() throws RoomNotFoundException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter ID of room for more details(Enter 0 to exit)");
            List<Room> roomList = roomControllerRemote.retrieveAllRooms();
            String title1 = "Room ID";
            String title2 = "Room Number";
            System.out.printf("%-15s %-15s %n", title1, title2);
            for (int i = 0; i < roomList.size(); i++) {
                System.out.printf("%-15d %-15s %n", roomList.get(i).getRoomId(), roomList.get(i).getRoomNumber());
            }
            Long option = sc.nextLong();
            if (option == 0) {
                break;
            } else {
                doViewRoomDetails(option);
            }
        }
    }

    private void doRoomAllocationExceptionReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCreateRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllRoomRate() throws RoomRateNotFoundException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter ID of room type for more details(Enter 0 to exit)");
            List<RoomRate> roomRateList = roomRateControllerRemote.retrieveAllRoomRates();
            String title1 = "Room rate ID";
            String title2 = "Room rate";
            System.out.printf("%-15s %-15s %n", title1, title2);
            for (int i = 0; i < roomRateList.size(); i++) {
                System.out.printf("%-15d %n", roomRateList.get(i).getRoomRateId());
            }
            Long option = sc.nextLong();
            if (option == 0) {
                break;
            } else {
                doViewRoomRateDetails(option);
            }
        }
    }

    private void doViewRoomTypeDetails(Long option) throws RoomTypeNotFoundException {
        Scanner sc = new Scanner(System.in);
        RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeById(option);
        System.out.println("Print out room type details here");
        while (true) {
            System.out.println("Choose following options");
            System.out.println("1. Update room type details");
            System.out.println("2. Delete room type");
            System.out.println("3. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                doUpdateRoomTypeDetails(roomType);
            } else if (choice == 2) {
                doDeleteRoomType(roomType);
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid option selected. Please try again");
            }
        }
    }

    private void doUpdateRoomTypeDetails(RoomType roomType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doDeleteRoomType(RoomType roomType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewRoomDetails(Long option) throws RoomNotFoundException {
        Scanner sc = new Scanner(System.in);
        Room room = roomControllerRemote.retrieveRoomById(option);
        System.out.println("Print out room type details here");
        while (true) {
            System.out.println("Choose following options");
            System.out.println("1. Update room details");
            System.out.println("2. Delete room");
            System.out.println("3. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                doUpdateRoomDetails(room);
            } else if (choice == 2) {
                doDeleteRoom(room);
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid option selected. Please try again");
            }
        }
    }

    private void doUpdateRoomDetails(Room room) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doDeleteRoom(Room room) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewRoomRateDetails(Long option) throws RoomRateNotFoundException {
        Scanner sc = new Scanner(System.in);
        RoomRate roomRate = roomRateControllerRemote.retrieveRoomRateById(option);
        System.out.println("Print out room rate details here");
        while (true) {
            System.out.println("Choose following options");
            System.out.println("1. Update room details");
            System.out.println("2. Delete room");
            System.out.println("3. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                doUpdateRoomRateDetails(roomRate);
            } else if (choice == 2) {
                doDeleteRoomRate(roomRate);
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid option selected. Please try again");
            }
        }
    }

    private void doUpdateRoomRateDetails(RoomRate roomRate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doDeleteRoomRate(RoomRate roomRate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}