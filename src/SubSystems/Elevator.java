package SubSystems;

import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {
	private static Elevator instance = null;
    private CANTalon elevator_motor;
    private Direction status = Direction.STOP;
    private Direction direction = Direction.UP;
    public static enum Direction{
    	UP,DOWN,MOVING,STOP
    }
    public static Elevator getInstance()
    {
        if( instance == null )
            instance = new Elevator();
        return instance;
    }
    
    public Elevator(){
    	elevator_motor = new CANTalon(Ports.ELEVATOR);
    	elevator_motor.configNominalOutputVoltage(+0f, -0f);
    	elevator_motor.configPeakOutputVoltage(+12f, -12f);
    	elevator_motor.changeControlMode(TalonControlMode.Voltage);
//    	intake_arm_motor.setPID(4.0, 0.001, 240.0, 0.0, 0, 0.0, 0);
//    	intake_arm_motor.setPID(3.0, 0.0, 240.0, 0.0, 0, 0.0, 1);
    	elevator_motor.setProfile(0);
    	
    }
    public Direction status(){
    	return status;
    }
    public void up(){
//    	if(status != Direction.UP){
	    	status = Direction.MOVING;
	    	direction = Direction.UP;
	    	setVoltage(-12.0);
//    	}
    }
    public void down(){
//    	if(status != Direction.DOWN){
	    	status = Direction.MOVING;
	    	direction = Direction.DOWN;
	    	setVoltage(12.0);
//    	}
    }
    private void check(){
    	switch(direction){
    	case UP:
    		if(elevator_motor.getOutputCurrent() > 20){
        		setVoltage(-1.0);
        		status = Direction.UP;
        	}
    		break;
    	case DOWN:
    		if(elevator_motor.getOutputCurrent() > 20){
        		setVoltage(0.5);
        		status = Direction.DOWN;
        	}
        	break;
		default:
			break;
    	}  
    	currentStatus();
    }
    public void currentStatus(){
    	switch(status){
    	case UP:
    		SmartDashboard.putString("ELE_STATE", "UP");
    		break;
    	case DOWN:
    		SmartDashboard.putString("ELE_STATE", "DOWN");
    		break;
    	case MOVING:
    		SmartDashboard.putString("ELE_STATE", "MOVING");
    		break;
    	case STOP:
    		SmartDashboard.putString("ELE_STATE", "STOPPED");
    		break;
    	}
    	
    }
    public void stop(){
    	setVoltage(0.0);
    	status = Direction.STOP;
    }
    public synchronized void update(){
    	try{	    	
	    	check();
	    	SmartDashboard.putNumber("ELE_DRAW", elevator_motor.getOutputCurrent());
	    	SmartDashboard.putNumber("ELE_GOAL", elevator_motor.getSetpoint());
	    	SmartDashboard.putNumber("ELE_POWER", elevator_motor.getOutputVoltage());
    	}catch(Error e){
    		System.out.print(e);
    	}
    	
    }
    
    public void setVoltage(double current){
    	elevator_motor.set(current);
    }
}
