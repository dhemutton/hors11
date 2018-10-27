/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import exceptions.RoomTypeeExistException;
import entity.RoomType;
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
@Local(RoomTypeControllerLocal.class)
@Remote(RoomTypeControllerRemote.class)
public class RoomTypeController implements RoomTypeControllerRemote, RoomTypeControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public RoomType createRoomType(RoomType roomType) throws RoomTypeeExistException {
        try {
            em.persist(roomType);
            em.flush();

            return roomType;
        } catch (PersistenceException ex) {
             
                throw new RoomTypeeExistException("Employee with same NRIC already exists");
            }
    }
    
    @Override
    public List<RoomType> retrieveAllRoomtype() {
        Query query  = em.createQuery("SELECT rt FROM RoomType rt");
        return query.getResultList();
    }
    
    @Override
    public void updateRoomType(RoomType roomType) {
        em.merge(roomType);
    }
    
    @Override
    public void deleteRoomType(RoomType roomType) {
        em.remove(roomType);
    }
}
