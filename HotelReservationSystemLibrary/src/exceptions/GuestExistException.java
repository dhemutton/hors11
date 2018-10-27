package exceptions;



public class GuestExistException extends Exception
{
    public GuestExistException()
    {
    }
    
    
    
    public GuestExistException(String msg)
    {
        super(msg);
    }
}