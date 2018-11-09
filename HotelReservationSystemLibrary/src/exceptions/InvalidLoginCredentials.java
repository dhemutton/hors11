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
public class InvalidLoginCredentials extends Exception {

    /**
     * Creates a new instance of <code>InvalidLoginCredentials</code> without
     * detail message.
     */
    public InvalidLoginCredentials() {
    }

    /**
     * Constructs an instance of <code>InvalidLoginCredentials</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidLoginCredentials(String msg) {
        super(msg);
    }
}
