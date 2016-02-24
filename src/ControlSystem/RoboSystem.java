
package ControlSystem;

import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Hanger;
import SubSystems.Intake;
import SubSystems.Shooter;
import SubSystems.Turret;
import edu.wpi.first.wpilibj.CANTalon;


public class RoboSystem{
	public DriveTrain dt;	
	public CANTalon hangerDrive;
	public Shooter shooter;
	public Intake intake;
	public Turret turret;
	public Elevator elevator;
	public Hanger hanger;
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
    	hanger = Hanger.getInstance();
    }
}
