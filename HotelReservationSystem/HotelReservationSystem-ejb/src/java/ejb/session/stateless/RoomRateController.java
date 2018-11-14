/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.RoomRate;
import entity.RoomType;
import static enums.RateTypeEnum.NORMAL;
import static enums.RateTypeEnum.PEAK;
import static enums.RateTypeEnum.PROMO;
import static enums.RateTypeEnum.PUBLISHED;
import exceptions.RoomRateExistException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeCannotHaveDuplicatePublishedOrNormalException;
import java.math.BigDecimal;
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
    public RoomRate createRoomRate(RoomRate roomRate, Long roomTypeId) throws RoomRateExistException, RoomTypeCannotHaveDuplicatePublishedOrNormalException {

        RoomType roomType = em.find(RoomType.class, roomTypeId);

        for (RoomRate roomRateExisting : roomType.getRoomRates()) {
            if (roomRateExisting.getRateType().equals(PUBLISHED) && roomRate.getRateType().equals(PUBLISHED) || roomRateExisting.getRateType().equals(NORMAL) && roomRate.getRateType().equals(NORMAL)) {
                throw new RoomTypeCannotHaveDuplicatePublishedOrNormalException("Room rate already has a published or a normal rate. Unable to create duplicates.");
            }
        }

        try {
            em.persist(roomRate);
            em.flush();
        } catch (PersistenceException ex) {
            throw new RoomRateExistException("Room Rate already exists");
        }
        Date date = new Date();

        if (date.before(roomRate.getStartDate())) {
            roomRate.setIsValid(Boolean.FALSE);
        } else {
            roomRate.setIsValid(Boolean.TRUE);
        }

        roomRate.setRoomType(roomType);
        roomType.getRoomRates().add(roomRate);

        return roomRate;
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
    public RoomRate
            retrieveRoomRateById(Long RoomRateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class,
                RoomRateId);

        if (roomRate != null) {
            return roomRate;
        } else {
            throw new RoomRateNotFoundException("Room rate ID " + RoomRateId + " does not exist");
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
        RoomType roomType = em.find(RoomType.class,
                roomTypeId);
        RoomType oldRoomType = roomRate.getRoomType();
        oldRoomType.getRoomRates().remove(roomRate); //remove room rate from list of room rates attached to a room type

        roomRate.setRoomType(roomType); //set new roomtype for room rate
        em.merge(roomRate);

        roomType.getRoomRates().add(roomRate);

    }

    @Override
    public void deleteRoomRate(Long roomRateId) {
        RoomRate roomRate = em.find(RoomRate.class,
                roomRateId);
        roomRate.setIsEnabled(Boolean.FALSE);
        em.remove(roomRate);
    }

    @Override
    public void mergeRoomRate(RoomRate roomRate) {

        em.merge(roomRate);
    }

    @Override
    public BigDecimal calculateReservationCost(Booking booking, RoomType roomType) {
        BigDecimal total = new BigDecimal(0);
        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType=:roomType AND rr.isEnabled = true");
        query.setParameter("roomType", roomType);
        List<RoomRate> roomRates = query.getResultList();
        System.out.println("Number of room rates: "+roomRates.size());
        Date checkDate = startDate;
        BigDecimal promoRate = new BigDecimal(0);
        BigDecimal peakRate = new BigDecimal(0);
        BigDecimal normalRate = new BigDecimal(0);
        for (RoomRate roomRate : roomRates) {
            if (roomRate.getRateType().equals(PROMO)) {
                promoRate = roomRate.getRatePerNight();
                System.out.println("Promo rate: "+promoRate);
            }
            if (roomRate.getRateType().equals(PEAK)) {
                peakRate = roomRate.getRatePerNight();
                System.out.println("Peak rate: "+peakRate);
            }
            if (roomRate.getRateType().equals(NORMAL)) {
                normalRate = roomRate.getRatePerNight();
                System.out.println("Normal rate: "+normalRate);
            }
        }
        while (checkDate.before(endDate)||checkDate.equals(endDate)) {
            System.out.println("Checking reservation on "+checkDate);
            boolean promo = false;
            boolean peak = false;
            for (RoomRate roomRate : roomRates) {
                if (roomRate.getRateType().equals(PROMO)) {
                    if (roomRate.getStartDate().before(checkDate) && roomRate.getEndDate().after(checkDate)) { //checking isValid
                        promo = true;
                    }
                }
                if (roomRate.getRateType().equals(PEAK)) {
                    if (roomRate.getStartDate().before(checkDate) && roomRate.getEndDate().after(checkDate)) { //checking isValid
                        peak = true;
                    }
                }
            }
            if (peak == true && promo == true) {
                total = total.add(promoRate);
                System.out.println("Promotion rate was added");
            } else if (peak == true && promo == false) {
                total = total.add(peakRate);
                System.out.println("Peak rate was added");
            } else if(peak==false&&promo==true) {
                    total = total.add(promoRate);
                    System.out.println("Promotion rate was added");
                } else {
                total = total.add(normalRate);
                System.out.println("Normal rate was added");
            }
            Date temp = checkDate;
            checkDate.setDate(temp.getDate() + 1);
        }
        System.out.println("Total cost: "+total);
        return total;
    }
}
