package SubSystems;

import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {

private static Intake instance = null;
    private CANTalon intake_motor;
    private CANTalon intake_arm_motor;
    private int absolutePosition;
    private double position;
    private boolean whatForPositionDisable = false;
    private AnalogInput ballSensor;
    public static Intake getInstance()
    {
        if( instance == null )
            instance = new Intake();
        return instance;
    }
    
    public Intake(){
    	intake_motor = new CANTalon(Ports.INTAKE);
    	intake_motor.setProfile(0); 
    	//intake_motor.setVoltageRampRate(12);
    	intake_arm_motor = new CANTalon(Ports.INTAKE_ARM_MOTOR);
    	absolutePosition = intake_arm_motor.getPulseWidthPosition() & 0xFFF;
    	intake_arm_motor.setEncPosition(absolutePosition);
    	intake_arm_motor.setFeedbackDevice(FeedbackDevice.AnalogPot);
    	intake_arm_motor.reverseSensor(false);
    	intake_arm_motor.reverseOutput(false);
    	intake_arm_motor.configPotentiometerTurns(360);
    	intake_arm_motor.configNominalOutputVoltage(+0f, -0f);
    	intake_arm_motor.configPeakOutputVoltage(+12f, -12f);
    	intake_arm_motor.setAllowableClosedLoopErr(0); 
    	intake_arm_motor.changeControlMode(TalonControlMode.Position);
    	intake_arm_motor.set(intake_arm_motor.getPosition());
    	intake_arm_motor.setPID(2.5, 0.0, 240.0, 0.0, 0, 0.0, 0);
    	intake_arm_motor.setPID(5.0, 0.005, 150.0, 0.0, 0, 0.0, 1);    	
    	intake_arm_motor.setProfile(0);   
    	ballSensor = new AnalogInput(Ports.PRESSURE);
    }
    public double getAngle(){
    	return intake_arm_motor.get();
    }
    public void setPositionForDisable(double angle){
    	setAngle(angle);
    	whatForPositionDisable = true;
    }
    public void enablePID(){
    	intake_arm_motor.changeControlMode(TalonControlMode.Position);
    	intake_arm_motor.set(intake_arm_motor.get());
    }
    public void disablePID(){
    	intake_arm_motor.changeControlMode(TalonControlMode.Disabled);
    }
    public void checkForDisable(){
    	if(Util.onTarget(intake_arm_motor.getSetpoint(), intake_arm_motor.get(), 10)){
    		disablePID();
    		whatForPositionDisable = false;
    	}
    }
    public double ballSensorData(){
    	return ballSensor.getAverageVoltage() * Constants.PRESSURE_V2P;
    }
    public void update(){
    	position = intake_arm_motor.getPosition();
    	SmartDashboard.putNumber("INTAKE_ANGLE", position);
    	SmartDashboard.putNumber("INTAKE_DRAW", intake_arm_motor.getOutputCurrent());
    	SmartDashboard.putNumber("INTAKE_P", intake_arm_motor.getP());
    	SmartDashboard.putNumber("INTAKE_GOAL", intake_arm_motor.getSetpoint());
    	SmartDashboard.putNumber("INTAKE_POWER", intake_arm_motor.getOutputVoltage());
    	SmartDashboard.putNumber("INTAKE_ERROR", (intake_arm_motor.getPosition()-intake_arm_motor.getSetpoint()));
    	SmartDashboard.putNumber("BALL_PRES", ballSensorData());
    	if((intake_arm_motor.getPosition()-intake_arm_motor.getSetpoint()) > 0){
    		intake_arm_motor.setProfile(0);
    	}else{
    		intake_arm_motor.setProfile(1);
    	}
    	if(whatForPositionDisable){
    		checkForDisable();
    	}
    }
    
    public void intake_forward(){
    	intake_motor.set(1.0);
    }
    
    public void intake_reverse(){
    	intake_motor.set(-1.0);
    }
    public void intake_stop(){
    	intake_motor.set(0);
    }
    public void arm_stop(){
    	intake_arm_motor.setSetpoint(intake_arm_motor.getPosition());
    }
    
    public void manualMove(double angle){
    	double current = intake_arm_motor.getPosition();
    	double newpos  = current + angle;
    	SmartDashboard.putNumber("INTAKE_MAN", newpos);
    	if(newpos < Constants.INTAKE_ARM_MIN_ANGLE){
    		setAngle(Constants.INTAKE_ARM_MIN_ANGLE);    		
    	}else if(newpos > Constants.INTAKE_ARM_MAX_ANGLE){
    		setAngle(Constants.INTAKE_ARM_MAX_ANGLE);
    	}else{
    		setAngle(angle);
    	}
    }
    public void setAngle(double angle){
    	if(angle > intake_arm_motor.getPosition()){
    		intake_arm_motor.setProfile(0);
    	}else{
    		intake_arm_motor.setProfile(1);
    	}
    	if(angle > Constants.INTAKE_ARM_MAX_ANGLE){
    		intake_arm_motor.setSetpoint(Constants.INTAKE_ARM_MAX_ANGLE);
    	}else if(angle < Constants.INTAKE_ARM_MIN_ANGLE){
    		intake_arm_motor.setSetpoint(Constants.INTAKE_ARM_MIN_ANGLE);
    	}else{
    		intake_arm_motor.setSetpoint(angle);
    	}    	
    }
}
