/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import exceptions.RoomTypeExistException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;

/**
 *
 * @author sleep
 */
public interface RoomTypeControllerLocal {

    public void persist(Object object);

    public RoomType createRoomType(RoomType roomType) throws RoomTypeExistException;

    public RoomType retrieveRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException;

    public List<RoomType> retrieveAllRoomtype();

    public List<RoomType> retrieveAllEnabledRoomType();

    public void updateRoomType(RoomType roomType);

    public void deleteRoomType(RoomType roomType);

    public RoomType retrieveRoomTypeById(Long RoomTypeId) throws RoomTypeNotFoundException;

}
