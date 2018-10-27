/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author sleep
 */
public class RoomTypeeExistException extends Exception {

    public RoomTypeeExistException() {
    }
    public RoomTypeeExistException(String msg)
    {
        super(msg);
    }
    
}
