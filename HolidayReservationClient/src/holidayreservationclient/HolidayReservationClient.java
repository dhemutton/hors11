/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;

import java.util.Scanner;

/**
 *
 * @author matthealoo
 */
public class HolidayReservationClient {

    private static Boolean loggedIn = false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("*** Welcome to HoRS Partner Client  ***\n");
        Scanner sc = new Scanner(System.in);
        while (true) {
            while (!loggedIn) {
                System.out.println("1. Partner Login");
                System.out.println("3. Search Hotel Room");
                System.out.println("4. Exit");
                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
//                    doPartnerLogin();
                } else if (choice == 2) {
//                   doSearchRoom();
                } else if (choice == 3) {
                    
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }

            while (loggedIn) {

                System.out.println("1. Partner Search Hotel Room");
                System.out.println("2. View Partner Reservation Details");
                System.out.println("3. View All Partner Reservations");
                System.out.println("4. Logout");
                int choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) {
//                    doSearchRoom();
                } else if (choice == 2) {
//                    doViewMyReservation();
                } else if (choice == 3) {
//                    doViewAllMyReservation(guest.getGuestId());
                } else if (choice == 4) {
                    loggedIn = false;
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        }
    }
    
    private void doPartnerLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = sc.nextLine().trim();
        System.out.println("Please enter password");
            String password = sc.nextLine().trim();
 
       
    }
    
    
    
    
    
    
    
}


