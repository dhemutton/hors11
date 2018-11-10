/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author matthealoo
 */
public class RoomTypeCannotHaveDuplicatePublishedOrNormalException extends Exception {

    /**
     * Creates a new instance of
     * <code>RoomTypeCannotHaveDuplicatePublishedOrNormalException</code>
     * without detail message.
     */
    public RoomTypeCannotHaveDuplicatePublishedOrNormalException() {
    }

    /**
     * Constructs an instance of
     * <code>RoomTypeCannotHaveDuplicatePublishedOrNormalException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeCannotHaveDuplicatePublishedOrNormalException(String msg) {
        super(msg);
    }
}
