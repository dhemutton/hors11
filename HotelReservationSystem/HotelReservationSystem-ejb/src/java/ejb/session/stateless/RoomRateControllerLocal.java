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
import exceptions.RoomTypeNotFoundException;
import java.util.List;

/**
 *
 * @author sleep
 */
public interface RoomRateControllerLocal {

    public void persist(Object object);

    public RoomRate createRoomRate(RoomRate roomRate, Long roomTypeIds) throws RoomRateExistException;

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


}
