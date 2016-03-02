package SubSystems;

import SubSystems.DriveTrain.GEAR;
import Utilities.Constants;
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
	private ARM_STATUS ptoDirection = ARM_STATUS.DOWN;
	public static Hanger getInstance(){
		if(instance == null)
			instance = new Hanger();
		return instance;
	}
	public enum ARM_STATUS{
		EXTENDED, MOVING, DOWN , STOPPED, UP
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
	public void retractArm(){
		dt.directDrive(-Constants.HANGER_MAX_POWER,Constants.HANGER_MAX_POWER);
		Timer.delay(0.01);
		dt.directDrive(0.0, 0.0);
	}
	public void ptoUp(){
		dt.applyPower(Constants.HANGER_MAX_POWER, DriveTrain.SIDE.LEFT);
		dt.applyPower(-Constants.HANGER_MAX_POWER,DriveTrain.SIDE.RIGHT);
		ptoDirection = ARM_STATUS.UP;
		armStatus = ARM_STATUS.MOVING;
	}
	public void ptoDown(){
		dt.applyPower(-Constants.HANGER_MAX_POWER, DriveTrain.SIDE.LEFT);
		dt.applyPower(Constants.HANGER_MAX_POWER,DriveTrain.SIDE.RIGHT);
		ptoDirection = ARM_STATUS.DOWN;
		armStatus = ARM_STATUS.MOVING;
	}
	public void ptoStop(){
		if(dt.currentGear() == GEAR.PTO || armStatus == ARM_STATUS.MOVING){
			dt.applyPower(0, DriveTrain.SIDE.LEFT);
			dt.applyPower(0,DriveTrain.SIDE.RIGHT);
			armStatus = ARM_STATUS.STOPPED;
		}
	}
	public boolean armDeployed(){
		return armDeployed;
	}
	public void update(){
		if(dt.currentGear() == GEAR.PTO && armStatus == ARM_STATUS.MOVING){
			switch(ptoDirection){
			case UP:
				if(dt.left.currentDraw() > 10.0 || dt.right.currentDraw() > 10.0 ){
					dt.applyPower(0, DriveTrain.SIDE.LEFT);
					dt.applyPower(0,DriveTrain.SIDE.RIGHT);
					armStatus = ARM_STATUS.EXTENDED;
					armDeployed = true;
				}
				break;
			case DOWN:
				if(dt.left.currentDraw() > 10.0 || dt.right.currentDraw() > 10.0){
					dt.applyPower(0, DriveTrain.SIDE.LEFT);
					dt.applyPower(0,DriveTrain.SIDE.RIGHT);
					armStatus = ARM_STATUS.DOWN;
				}
				break;
			default:
				break;
			}
		}
	}
}