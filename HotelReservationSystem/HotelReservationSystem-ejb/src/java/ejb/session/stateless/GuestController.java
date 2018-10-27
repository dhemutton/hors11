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
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author sleep
 */
@Stateless
@Local(GuestControllerLocal.class)
@Remote(GuestControllerRemote.class)
public class GuestController implements GuestControllerRemote, GuestControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public Guest createGuest(Guest guest) throws GuestExistException {
        try {
            em.persist(guest);
            em.flush();

            return guest;
        } catch (PersistenceException ex) {
             
                throw new GuestExistException("Room already exists");
            }
    }
    
    @Override
    public List<Guest> retrieveAllGuest() {
        Query query = em.createQuery("SELECT g FROM Guest g");
        return query.getResultList();
    }
    
    @Override
    public Guest retrieveGuestByEmail(String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email=:email");
        query.setParameter("email", email);
        Guest guest = (Guest)query.getSingleResult();
        if(guest!=null) {
            return guest;
        }
        else {
            throw new GuestNotFoundException("Guest does not exist");
        }
    }
    
    @Override
    public List<Guest> retrieveAllLoginGuest(String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.isLogin=true");
        return query.getResultList();
    }
    
    @Override
    public void updateGuest(Guest guest) {
        em.merge(guest);
    }
    
    @Override
    public void deleteGuest(Guest guest) {
        em.remove(guest);
    }
}
