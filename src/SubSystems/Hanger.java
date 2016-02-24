package SubSystems;

import SubSystems.DriveTrain.GEAR;
import Utilities.Ports;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;


public class Hanger{
	private static Hanger instance = null;
	private DriveTrain dt;
	private Solenoid hangerArm;
	private Solenoid latch;
	private boolean armDeployed = false;
	private ARM_STATUS armStatus = ARM_STATUS.DOWN;
	public static Hanger getInstance(){
		if(instance == null)
			instance = new Hanger();
		return instance;
	}
	public enum ARM_STATUS{
		EXTENDED, MOVING, DOWN
	}
	
	public Hanger(){
		hangerArm = new Solenoid(Ports.HANGER);
		latch = new Solenoid(Ports.LACTCH);
		dt = DriveTrain.getInstance();
	}
	
	public void extendHanger(){
		hangerArm.set(true);
	}
	public void retractHanger(){
		hangerArm.set(false);
	}
	public void extendLatch(){
		latch.set(true);
	}
	public void retractLatch(){
		latch.set(false);
	}
	public void checkArm(){
		if(dt.left.currentDraw() > 10.0 || dt.right.currentDraw() > 10.0){
			armStatus = ARM_STATUS.EXTENDED;
		}
	}
	public void update(){
		
	}
	public void retractArm(){
		dt.directDrive(-0.1, 0.1);
		Timer.delay(0.01);
		dt.directDrive(0.0, 0.0);
	}
	public void deployArm(){
		if(!armDeployed){
			extendHanger();
			retractLatch();
			dt.setGear(GEAR.NUETRAL);
			dt.enablePTO();
			armDeployed = true;
		}else{
			checkArm();
			if(armStatus != ARM_STATUS.EXTENDED){
				dt.directDrive(0.1, -0.1);
				Timer.delay(0.01);
				dt.directDrive(0.0, 0.0);
			}
		}
	}
}