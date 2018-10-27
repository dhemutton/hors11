package exceptions;



public class GuestNotFoundException extends Exception
{
    public GuestNotFoundException()
    {
    }
    
    
    
    public GuestNotFoundException(String msg)
    {
        super(msg);
    }
}