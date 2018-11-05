/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import exceptions.GuestExistException;
import exceptions.GuestNotFoundException;
import java.util.List;

/**
 *
 * @author sleep
 */
public interface GuestControllerLocal {

    public void updateGuestLogin(Guest guest, boolean loggedIn);

    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException;

    public void persist(Object object);

    public Guest createGuest(Guest guest) throws GuestExistException;

    public List<Guest> retrieveAllGuest();

    public Guest retrieveGuestByEmail(String email) throws GuestNotFoundException;

    public List<Guest> retrieveAllLoginGuest(String email) throws GuestNotFoundException;

    public void updateGuest(Guest guest);

    public void deleteGuest(Guest guest);

}
