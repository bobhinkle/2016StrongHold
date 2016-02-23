package SubSystems;

import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret {
	private static Turret instance = null;
    private CANTalon turret_motor;
    private int absolutePosition;
    private double position;
    private DigitalInput hallEffect;
    private double scale = 39.13; //39.13
    public static Turret getInstance()
    {
        if( instance == null )
            instance = new Turret();
        return instance;
    }
    
    public Turret(){
    	turret_motor = new CANTalon(Ports.TURRET);
    	absolutePosition = turret_motor.getPulseWidthPosition() & 0xFFF;
    	turret_motor.setEncPosition(absolutePosition);
    	turret_motor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	turret_motor.reverseSensor(false);
//    	intake_arm_motor.reverseOutput(true);
    	turret_motor.configEncoderCodesPerRev(360);
    	turret_motor.configNominalOutputVoltage(+0f, -0f);
    	turret_motor.configPeakOutputVoltage(+12f, -12f);
    	turret_motor.setAllowableClosedLoopErr(0); 
    	turret_motor.changeControlMode(TalonControlMode.Position);
//    	turret_motor.setPID(0.01, 0.0, 0.0, 0.0, 0, 0.0, 0);
    	turret_motor.setProfile(0);
    	turret_motor.set(turret_motor.getPosition());
    	hallEffect = new DigitalInput(Ports.TURRET_RESET);
    }
    
    public void update(){
    	position = turret_motor.getPosition();
    	SmartDashboard.putNumber("TURRET_ANGLE", position*scale);
    	SmartDashboard.putNumber("TURRET_DRAW", turret_motor.getOutputCurrent());
    	SmartDashboard.putNumber("TURRET_GOAL", turret_motor.getSetpoint()*scale);
    	SmartDashboard.putNumber("TURRET_POWER", turret_motor.getOutputVoltage());
    	SmartDashboard.putNumber("TURRET_P", turret_motor.getP());
    	SmartDashboard.putNumber("TURRET_ERROR", (turret_motor.getPosition()-turret_motor.getSetpoint())*scale);
    	SmartDashboard.putBoolean("TURRET_RESET", hallEffect.get());
    }
    
    public void stop(){
    	turret_motor.setSetpoint(turret_motor.getPosition());
//    	turret_motor.set(0);
    }
    public void set(double angle){
    	turret_motor.set(angle/scale);
    }
    
}
