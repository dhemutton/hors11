package exceptions;



public class PartnerExistException extends Exception
{
    public PartnerExistException()
    {
    }
    
    
    
    public PartnerExistException(String msg)
    {
        super(msg);
    }
}