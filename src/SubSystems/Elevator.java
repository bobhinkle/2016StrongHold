package SubSystems;

import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {
	private static Elevator instance = null;
    private CANTalon elevator_motor;
    public static Elevator getInstance()
    {
        if( instance == null )
            instance = new Elevator();
        return instance;
    }
    
    public Elevator(){
    	elevator_motor = new CANTalon(Ports.INTAKE_ARM_MOTOR);
    	elevator_motor.configNominalOutputVoltage(+0f, -0f);
    	elevator_motor.configPeakOutputVoltage(+12f, -12f);
    	elevator_motor.changeControlMode(TalonControlMode.Position);
//    	intake_arm_motor.setPID(4.0, 0.001, 240.0, 0.0, 0, 0.0, 0);
//    	intake_arm_motor.setPID(3.0, 0.0, 240.0, 0.0, 0, 0.0, 1);
    	elevator_motor.setProfile(0);
    	
    }
    
    public void update(){
    	SmartDashboard.putNumber("ELE_DRAW", elevator_motor.getOutputCurrent());
    	SmartDashboard.putNumber("ELE_GOAL", elevator_motor.getSetpoint());
    	SmartDashboard.putNumber("ELE_POWER", elevator_motor.getOutputVoltage());
    	SmartDashboard.putNumber("ELE_P", elevator_motor.getP());
    }
    
    public void setCurrent(double current){
    	elevator_motor.set(current);
    }
}
