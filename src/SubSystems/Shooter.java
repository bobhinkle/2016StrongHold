package SubSystems;

import Sensors.SuperEncoder;
import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Shooter 
{
    private CANTalon motor1;
    private CANTalon motor2;
    private CANTalon motor3;
    private CANTalon motor4;
    private SuperEncoder enc;
    private Solenoid hood;
    private int absolutePosition;
    private static Shooter instance = null;
    public boolean fenderShot = false;
    public static Shooter getInstance()
    {
        if( instance == null )
            instance = new Shooter();
        return instance;
    }
    
    public Shooter(){
    	motor1 = new CANTalon(Ports.SHOOTER_MOTOR_3);
    	absolutePosition = motor1.getPulseWidthPosition() & 0xFFF;
    	motor1.setEncPosition(absolutePosition);
    	motor1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	motor1.reverseSensor(false);
//    	intake_arm_motor.reverseOutput(true);
    	motor1.configEncoderCodesPerRev(360);
    	motor1.configNominalOutputVoltage(+0f, -0f);
    	motor1.configPeakOutputVoltage(+0f, -12f);
    	motor1.setAllowableClosedLoopErr(0); 
    	motor1.changeControlMode(TalonControlMode.Speed);
    	motor1.set(motor1.getPosition());
//    	motor1.setPID(0.048, 0.0, 0.0, 0.048, 0, 0.0, 0);
//    	motor1.setPID(0.0, 0.0, 0.0, 0.048, 0, 0.0, 1);
    	motor1.setProfile(0);
        motor2 = new CANTalon(Ports.SHOOTER_MOTOR_1);
        motor2.changeControlMode(TalonControlMode.Follower);
        motor2.set(Ports.SHOOTER_MOTOR_3);
        motor3 = new CANTalon(Ports.SHOOTER_MOTOR_2);
        motor3.changeControlMode(TalonControlMode.Follower);
        motor3.set(Ports.SHOOTER_MOTOR_3);
        motor4 = new CANTalon(Ports.SHOOTER_MOTOR_4);
        motor4.changeControlMode(TalonControlMode.Follower);
        motor4.set(Ports.SHOOTER_MOTOR_3);
    }
    public void update(){
    	SmartDashboard.putNumber("SHOOTER_SPEED", motor1.getSpeed());
    	SmartDashboard.putNumber("SHOOTER_GOAL", motor1.getSetpoint());
    	SmartDashboard.putNumber("SHOOTER_POWER", motor1.getOutputVoltage());
    	SmartDashboard.putNumber("SHOOTER_CURRENT", motor1.getOutputCurrent());
    	SmartDashboard.putNumber("SHOOTER_ERROR", motor1.getSetpoint()-motor1.getSpeed());
    }
    public void set(double speed){
    	motor1.setProfile(0);
    	motor1.set(speed);
    }
    public void stop(){
    	motor1.setProfile(1);
    	motor1.set(0.0);
    }
    public void hoodExtend(){ hood.set(false); } 
    public void hoodRetract(){ hood.set(true); }
	
}