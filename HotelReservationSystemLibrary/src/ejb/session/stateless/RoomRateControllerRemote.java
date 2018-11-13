package ejb.session.stateless;

import entity.Booking;
import entity.RoomRate;
import entity.RoomType;
import exceptions.RoomRateExistException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeCannotHaveDuplicatePublishedOrNormalException;
import java.math.BigDecimal;
import java.util.List;

public interface RoomRateControllerRemote {

    public void persist(Object object);

    public RoomRate createRoomRate(RoomRate roomRate, Long roomTypeIds) throws RoomRateExistException, RoomTypeCannotHaveDuplicatePublishedOrNormalException;

    public RoomRate retrieveRoomRateByName(String roomRateName) throws RoomRateNotFoundException;

    public List<RoomRate> retrieveAllRoomRates();

    public List<RoomRate> retrieveAllRoomRatesByRoomType(RoomType roomType);

    public List<RoomRate> retrieveAllEnabledRoomRates();

    public List<RoomRate> retrieveAllDisabledRoomRates();

    public List<RoomRate> retrieveAllRoomRatesForPartners();

    public List<RoomRate> retrieveAllRoomRatesForNonPartners();

    public void updateRoomRate(RoomRate roomRate, Long roomTypeId);

    public void deleteRoomRate(Long roomRateId);

    public RoomRate retrieveRoomRateById(Long RoomRateId) throws RoomRateNotFoundException;

    public void mergeRoomRate(RoomRate roomRate);

    public BigDecimal calculateReservationCost(Booking booking, RoomType roomType);

}
