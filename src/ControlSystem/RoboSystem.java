
package ControlSystem;


public class RoboSystem{
	
    private static RoboSystem instance = null;
    
    public static RoboSystem getInstance()
    {
        if( instance == null )
            instance = new RoboSystem();
        return instance;
    }
    
    public RoboSystem(){
    	
    }
    
}
