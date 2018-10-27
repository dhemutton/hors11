/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sleep
 */
@Stateless
public class RoomTypeController implements RoomTypeControllerRemote, RoomTypeControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public Long createRoomType(RoomType roomType) {
        em.persist(roomType);
        em.flush();
        return roomType.getRoomTypeId();
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomType rt");
        return query.getResultList();
    }
    
    @Override
    public RoomType retrieveRoomTypeById(Long id) throws RoomTypeNotFoundException {
        RoomType roomType = em.find(RoomType.class, id);
        
        if (roomType != null) {
            return roomType;
        } else {
            throw new RoomTypeNotFoundException("Room type ID " + id + " does not exist");
        }
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
