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
import java.util.List;

/**
 *
 * @author sleep
 */
public interface RoomRateControllerRemote {
    
    public void persist(Object object);

    public RoomRate createRoom(RoomRate roomRate) throws RoomRateExistException;

    public List<RoomRate> retrieveAllRoomRates();

    public List<RoomRate> retrieveAllRoomRatesByRoomType(RoomType roomType);

    public List<RoomRate> retrieveAllEnabledRoomRates();

    public List<RoomRate> retrieveAllDisabledRoomRates();

    public List<RoomRate> retrieveAllRoomRatesForPartners();

    public List<RoomRate> retrieveAllRoomRatesForNonPartners();

    public void updateRoomRate(RoomRate roomRate);

    public void deleteRoomRate(RoomRate roomRate);
    
    public RoomRate retrieveRoomRateById(Long RoomRateId) throws RoomRateNotFoundException;
    
}
