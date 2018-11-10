/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import exceptions.RoomRateExistException;
import exceptions.RoomRateNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author sleep
 */
@Stateless
@Local(RoomRateControllerLocal.class)
@Remote(RoomRateControllerRemote.class)
public class RoomRateController implements RoomRateControllerRemote, RoomRateControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public RoomRate createRoomRate(RoomRate roomRate, Long roomTypeId) throws RoomRateExistException {
        try {
            em.persist(roomRate);
            em.flush();

            RoomType roomType = em.find(RoomType.class, roomTypeId);
            Date date = new Date();
            if (date.before(roomRate.getStartDate())) {
                roomRate.setIsValid(Boolean.FALSE);
            } else {
                roomRate.setIsValid(Boolean.TRUE);
            }
            roomRate.setRoomType(roomType);

            roomType.getRoomRates().size();
            roomType.getRoomRates().add(roomRate);
            roomType.setIsEnabled(Boolean.TRUE);

            return roomRate;
        } catch (PersistenceException ex) {
            throw new RoomRateExistException("Room Rate already exists");
        }
    }

    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");
        return query.getResultList();
    }

    @Override
    public List<RoomRate> retrieveAllRoomRatesByRoomType(RoomType roomType) {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType=:roomType");
        query.setParameter("roomType", roomType);
        return query.getResultList();
    }

    @Override
    public List<RoomRate> retrieveAllEnabledRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.isEnabled=:true");
        return query.getResultList();
    }

    @Override
    public List<RoomRate> retrieveAllDisabledRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.isEnabled=false");
        return query.getResultList();
    }

    @Override
    public List<RoomRate> retrieveAllRoomRatesForPartners() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.forPartner=true");
        return query.getResultList();
    }

    @Override
    public List<RoomRate> retrieveAllRoomRatesForNonPartners() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.forPartner=false");
        return query.getResultList();
    }

    @Override
    public RoomRate retrieveRoomRateById(Long RoomRateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, RoomRateId);

        if (roomRate != null) {
            return roomRate;
        } else {
            throw new RoomRateNotFoundException("Employee ID " + RoomRateId + " does not exist");
        }
    }

    @Override
    public RoomRate retrieveRoomRateByName(String roomRateName) throws RoomRateNotFoundException {
        Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.name = :arg");
        query.setParameter("arg", roomRateName);

        try {
            return (RoomRate) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateNotFoundException("Room Rate Name " + roomRateName + " does not exist!");
        }
    }

    @Override
    public void updateRoomRate(RoomRate roomRate, Long roomTypeId) {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        RoomType oldRoomType = roomRate.getRoomType();
        oldRoomType.getRoomRates().remove(roomRate); //remove room rate from list of room rates attached to a room type

        roomRate.setRoomType(roomType); //set new roomtype for room rate
        em.merge(roomRate);

        roomType.getRoomRates().add(roomRate);

    }

    @Override
    public void deleteRoomRate(Long roomRateId) {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        roomRate.setIsEnabled(Boolean.FALSE);
        em.remove(roomRate);
    }

    @Override
    public void mergeRoomRate(RoomRate roomRate) {

        em.merge(roomRate);
    }
}
