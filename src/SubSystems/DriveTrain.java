package SubSystems;

import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;


public class DriveTrain{
	private static DriveTrain instance = null;
	private CANTalon right_dt_1;
    private CANTalon right_dt_2;
    private CANTalon left_dt_1;
    private CANTalon left_dt_2;
	public DriveBase left;
	public DriveBase right;
	private Solenoid shifter;
	
	public enum SIDE{
		LEFT,RIGHT
	}
	public DriveTrain(){
		left = new DriveBase(Ports.LEFT_DT_1,Ports.LEFT_DT_2);
		right = new DriveBase(Ports.RIGHT_DT_1,Ports.RIGHT_DT_2);
		shifter = new Solenoid(1,Ports.DRIVE_SHIFT);
	}
	public static DriveTrain getInstance()
    {
        if( instance == null )
            instance = new DriveTrain();
        return instance;
    }
	private static class DriveBase{
		private CANTalon _1;
	    private CANTalon _2;
	    
	    public DriveBase(int Port1,int Port2){
	    	_1 = new CANTalon(Port1);
	    	_2 = new CANTalon(Port2);
	    }	    
	}
	public void applyPower(double power, DriveTrain.SIDE side){
    	switch(side){
		case LEFT:
			left._1.set(power);
			left._2.set(power);
			break;
		case RIGHT:
			right._1.set(power);
			right._2.set(power);
			break;
		default:
			break;
    	}
    }
	public void directArcadeDrive(double x, double y)
    {
        x = Util.limit(x, -1.0, 1.0);
        y = Util.limit(y, -1.0, 1.0);
        double left = y + x;
        double right = y - x;
        left = Util.limit(left, -1.0, 1.0);
        right = Util.limit(right, -1.0, 1.0);
        directDrive(left, right);
    }
	public void directDrive(double left, double right)
    {
        applyPower(left,DriveTrain.SIDE.LEFT);
        applyPower(-right,DriveTrain.SIDE.RIGHT);
    }
	public void highGear(){
		shifter.set(true);
	}
	public void lowGear(){
		shifter.set(false);
	}
}