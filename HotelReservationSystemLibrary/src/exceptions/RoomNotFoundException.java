package exceptions;



public class RoomNotFoundException extends Exception
{
    public RoomNotFoundException()
    {
    }
    
    
    
    public RoomNotFoundException(String msg)
    {
        super(msg);
    }
}