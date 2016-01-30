
package ControlSystem;

import SubSystems.DriveTrain;
import SubSystems.Shooter;
import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;


public class RoboSystem{
	public DriveTrain dt;
	public CANTalon intake;
	public Solenoid intake_arm;
	public Solenoid pto;
	public Solenoid hanger;
	public CANTalon hangerDrive;
	public Shooter shooter;
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
    	intake_arm = new Solenoid(1,Ports.INTAKE_ARM);
    	pto = new Solenoid(1,Ports.PTO);
    	hanger = new Solenoid(1,Ports.HANGER);
    	hangerDrive = new CANTalon(Ports.HANGER_DRIVE);
    	shooter = Shooter.getInstance();
    	shooter.start();
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
    public void extendIntake(){
    	intake_arm.set(true);
    }
    public void retractIntake(){
    	intake_arm.set(false);
    }
    public void enablePTO(){
    	pto.set(true);
    }
    public void disablePTO(){
    	pto.set(false);
    }
    public void hangerOut(){
    	hanger.set(true);
    }
    public void hangerIn(){
    	hanger.set(false);
    }
    
    
}
