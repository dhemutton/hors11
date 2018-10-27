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
public class RoomExistException extends Exception {
    public RoomExistException() {
    }
    public RoomExistException(String msg)
    {
        super(msg);
    }
}
