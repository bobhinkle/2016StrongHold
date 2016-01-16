
package ControlSystem;

import SubSystems.DriveTrain;
import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;


public class RoboSystem{
	public DriveTrain dt;
	public CANTalon intake;
    private static RoboSystem instance = null;
    
    
    public static RoboSystem getInstance()
    {
        if( instance == null )
            instance = new RoboSystem();
        return instance;
    }
    
    public RoboSystem(){
    	dt = DriveTrain.getInstance();
    	intake = new CANTalon(Ports.INTAKE);
    }
    
    public void intakeForward(){
    	intake.set(0.8);
    }
    public void intakeReverse(){
    	intake.set(-0.8);
    }
    public void intakeStop(){
    	intake.set(0.0);
    }
}
