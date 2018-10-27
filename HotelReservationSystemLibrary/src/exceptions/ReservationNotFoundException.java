package exceptions;



public class ReservationNotFoundException extends Exception
{
    public ReservationNotFoundException()
    {
    }
    
    
    
    public ReservationNotFoundException(String msg)
    {
        super(msg);
    }
}