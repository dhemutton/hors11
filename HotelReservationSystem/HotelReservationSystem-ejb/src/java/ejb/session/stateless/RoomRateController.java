package ejb.session.stateless;

import entity.Booking;
import entity.RoomRate;
import entity.RoomType;
import static enums.BookingTypeEnum.WALKIN;
import enums.RateTypeEnum;
import static enums.RateTypeEnum.NORMAL;
import static enums.RateTypeEnum.PEAK;
import static enums.RateTypeEnum.PROMO;
import static enums.RateTypeEnum.PUBLISHED;
import exceptions.RoomRateExistException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeCannotHaveDuplicatePublishedOrNormalException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
            throw new RoomRateExistException("Room Rate already exists or input details are too lengthy.");
        }
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        if (roomRate.getRateType().equals(PEAK) || roomRate.getRateType().equals(PROMO)) {
            if (date.before(roomRate.getStartDate())) {
                roomRate.setIsValid(Boolean.FALSE);
            } else {
                roomRate.setIsValid(Boolean.TRUE);
            }
        } else {
            roomRate.setIsValid(Boolean.TRUE);
        }
        roomRate.setIsEnabled(Boolean.TRUE);
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
    public RoomRate retrieveRoomRateById(Long RoomRateId) throws RoomRateNotFoundException {
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
        RoomRate rr = (RoomRate) query.getSingleResult();

        try {
            return rr;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateNotFoundException("Room Rate Name " + roomRateName + " does not exist!");
        }
    }

    @Override
    public void updateRoomRate(RoomRate roomRate, Long roomTypeId) {
        RoomType roomType = em.find(RoomType.class, roomTypeId);

        RoomType oldRoomType = em.find(RoomType.class, roomRate.getRoomType().getRoomTypeId());
        oldRoomType.getRoomRates().size();
        oldRoomType.getRoomRates().remove(roomRate); //remove room rate from list of room rates attached to a room type
        em.merge(oldRoomType);
        em.flush();

        roomRate.setRoomType(roomType); //set new roomtype for room rate
        em.merge(roomRate);
        em.flush();

        roomType.getRoomRates().size();
        roomType.getRoomRates().add(roomRate);
        em.merge(roomType);
        em.flush();

    }

    @Override
    public void deleteRoomRate(Long roomRateId) {
        RoomRate roomRate = em.find(RoomRate.class,
                roomRateId);
        roomRate.setIsEnabled(Boolean.FALSE);
        RoomType roomType = em.find(RoomType.class, roomRate.getRoomType().getRoomTypeId());
        roomType.getRoomRates().remove(roomRate);
        em.merge(roomType);
        em.remove(roomRate);
        em.flush();
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
        System.out.println("Number of room rates: " + roomRates.size());
        Date checkDate = startDate;
        BigDecimal promoRate = new BigDecimal(0);
        BigDecimal peakRate = new BigDecimal(0);
        BigDecimal normalRate = new BigDecimal(0);
        BigDecimal publishedRate = new BigDecimal(0);
        for (RoomRate roomRate : roomRates) {
            if (roomRate.getRateType().equals(PUBLISHED)) {
                publishedRate = roomRate.getRatePerNight();
            }
            if (roomRate.getRateType().equals(PROMO)) {
                promoRate = roomRate.getRatePerNight();
            }
            if (roomRate.getRateType().equals(PEAK)) {
                peakRate = roomRate.getRatePerNight();
            }
            if (roomRate.getRateType().equals(NORMAL)) {
                normalRate = roomRate.getRatePerNight();
            }
        }
        while (checkDate.before(endDate)) {
            System.out.println("Checking reservation on " + checkDate);
            boolean promo = false;
            boolean peak = false;
            if (booking.getBookingType() == WALKIN) {
                total = total.add(publishedRate);
            } else {
                for (RoomRate roomRate : roomRates) {
                    if (roomRate.getRateType().equals(PROMO)) {
                        if (roomRate.getStartDate().before(checkDate) && roomRate.getEndDate().after(checkDate) || roomRate.getStartDate().equals(checkDate) || roomRate.getEndDate().equals(checkDate)) { //checking isValid
                            promo = true;
                        }
                    }
                    if (roomRate.getRateType().equals(PEAK)) {
                        if (roomRate.getStartDate().before(checkDate) && roomRate.getEndDate().after(checkDate) || roomRate.getStartDate().equals(checkDate) || roomRate.getEndDate().equals(checkDate)) { //checking isValid
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
                } else if (peak == false && promo == true) {
                    total = total.add(promoRate);
                    System.out.println("Promotion rate was added");
                } else {
                    total = total.add(normalRate);
                    System.out.println("Normal rate was added");
                }
            }

            Date temp = checkDate;
            checkDate.setDate(temp.getDate() + 1);
        }
        return total;
    }
    
    @Override
    public List<RoomRate> validateRoomRatePeriod(RateTypeEnum rateType, Date startDate, Date endDate) {
        List<RoomRate> finalList = new ArrayList<>();
        Query query1 = em.createQuery("SELECT DISTINCT r FROM RoomRate r WHERE r.rateType=:rateType AND r.startDate > :startDate AND r.endDate < :endDate"); //list1
        Query query2 = em.createQuery("SELECT DISTINCT r FROM RoomRate r WHERE r.rateType=:rateType AND r.startDate = :startDate AND r.endDate = :endDate"); //list1
        Query query3 = em.createQuery("SELECT DISTINCT r FROM RoomRate r WHERE r.rateType=:rateType AND r.startDate = :startDate AND r.endDate < :endDate"); //list1
        Query query4 = em.createQuery("SELECT DISTINCT r FROM RoomRate r WHERE r.rateType=:rateType AND r.startDate > :startDate AND r.endDate = :endDate"); //list1
        Query query5 = em.createQuery("SELECT DISTINCT r FROM RoomRate r WHERE r.rateType=:rateType AND r.startDate < :startDate AND r.endDate > :endDate"); //list3
        Query query6 = em.createQuery("SELECT DISTINCT r FROM RoomRate r WHERE r.rateType=:rateType AND r.startDate > :startDate AND r.startDate < :endDate"); //list3
        Query query7 = em.createQuery("SELECT DISTINCT r FROM RoomRate r WHERE r.rateType=:rateType AND :startDate > r.startDate AND :startDate < r.endDate"); //list3

        // Query query4 = em.createQuery("SELECT DISTINCT b FROM Booking b WHERE b.startDate BETWEEN (:startDate AND :endDate) AND b.endDate BETWEEN (:startDate AND :endDate)");
        query1.setParameter("startDate", startDate);
        query1.setParameter("endDate", endDate);
        query1.setParameter("rateType", rateType);
        query2.setParameter("startDate", startDate);
        query2.setParameter("endDate", endDate);
        query2.setParameter("rateType", rateType);
        query3.setParameter("startDate", startDate);
        query3.setParameter("endDate", endDate);
        query3.setParameter("rateType", rateType);
        query4.setParameter("startDate", startDate);
        query4.setParameter("endDate", endDate);
        query4.setParameter("rateType", rateType);
        query5.setParameter("startDate", startDate);
        query5.setParameter("endDate", endDate);
        query5.setParameter("rateType", rateType);
        query6.setParameter("startDate", startDate);
        query6.setParameter("endDate", endDate);
        query6.setParameter("rateType", rateType);
        query7.setParameter("startDate", startDate);
        query7.setParameter("rateType", rateType);


        finalList.addAll(query1.getResultList());
        List<RoomRate> list2 = query2.getResultList();
        for (RoomRate roomRate : list2) {
            if (!finalList.contains(roomRate)) {
                finalList.add(roomRate);
            }
        }
        List<RoomRate> list3 = query3.getResultList();
        for (RoomRate roomRate : list3) {
            if (!finalList.contains(roomRate)) {
                finalList.add(roomRate);
            }
        }

        List<RoomRate> list4 = query4.getResultList();
        for (RoomRate roomRate : list4) {
            if (!finalList.contains(roomRate)) {
                finalList.add(roomRate);
            }
        }

        List<RoomRate> list5 = query5.getResultList();
        for (RoomRate roomRate : list5) {
            if (!finalList.contains(roomRate)) {
                finalList.add(roomRate);
            }
        }
        
        List<RoomRate> list6 = query6.getResultList();
        for (RoomRate roomRate : list6) {
            if (!finalList.contains(roomRate)) {
                finalList.add(roomRate);
            }
        }

        List<RoomRate> list7 = query7.getResultList();
        for (RoomRate roomRate : list7) {
            if (!finalList.contains(roomRate)) {
                finalList.add(roomRate);
            }
        }

        return finalList;
    }
}
