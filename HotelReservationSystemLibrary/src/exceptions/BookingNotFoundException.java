package exceptions;



public class BookingNotFoundException extends Exception
{
    public BookingNotFoundException()
    {
    }
    
    
    
    public BookingNotFoundException(String msg)
    {
        super(msg);
    }
}