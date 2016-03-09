
package ControlSystem;

import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Hanger;
import SubSystems.Intake;
import SubSystems.Navigation;
import SubSystems.Shooter;
import SubSystems.TestTalon;
import SubSystems.Turret;
import SubSystems.Vision;
import Utilities.Constants;
import edu.wpi.first.wpilibj.CANTalon;


public class RoboSystem{
	public DriveTrain dt;	
	public CANTalon hangerDrive;
	public Shooter shooter;
	public Intake intake;
	public Turret turret;
	public Elevator elevator;
	public Hanger hanger;	
	public TestTalon testTalon;
	public Navigation nav;
	public Vision vision;
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
    	nav = Navigation.getInstance();
    	vision = Vision.getInstance();
//    	testTalon = new TestTalon();
    }
    public void Init(){
    	intake.setAngle(Constants.INTAKE_OFFSET-intake.getAngle());
    	turret.set(turret.getAngle());
    	shooter.stop();
    }
}
