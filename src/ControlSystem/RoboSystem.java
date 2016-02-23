
package ControlSystem;

import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Intake;
import SubSystems.Shooter;
import SubSystems.Turret;
import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;


public class RoboSystem{
	public DriveTrain dt;
	public Solenoid pto;
	public Solenoid hanger;
	public CANTalon hangerDrive;
	public Shooter shooter;
	public Intake intake;
	public Turret turret;
	public Elevator elevator;
    private static RoboSystem instance = null;
    
    
    public static RoboSystem getInstance()
    {
        if( instance == null )
            instance = new RoboSystem();
        return instance;
    }
    
    public RoboSystem(){
    	dt = DriveTrain.getInstance();
    	intake = Intake.getInstance();
    	shooter = Shooter.getInstance();
    	turret = Turret.getInstance();
    	elevator = Elevator.getInstance();
    	pto = new Solenoid(1,Ports.PTO);
    }
    
    public void enablePTO(){
    	pto.set(true);
    }
    public void disablePTO(){
    	pto.set(false);
    }
    
    
}
