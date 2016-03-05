package SubSystems;

import Utilities.Ports;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TestTalon{
	private CANTalon talon;
	private int absolutePosition;
	public TestTalon(){
		talon = new CANTalon(Ports.TEST_TALON);
		absolutePosition = talon.getPulseWidthPosition() & 0xFFF;
		talon.setEncPosition(absolutePosition);
		
    	
    	talon.setFeedbackDevice(FeedbackDevice.AnalogPot);
    	talon.reverseSensor(false);
    	talon.reverseOutput(true);
    	talon.configPotentiometerTurns(360);
    	talon.configNominalOutputVoltage(+0f, -0f);
    	talon.configPeakOutputVoltage(+12f, -12f);
    	talon.setAllowableClosedLoopErr(0); 
    	talon.changeControlMode(TalonControlMode.Position);
    	talon.set(talon.getPosition());
    	talon.setPID(4.0, 0.001, 240.0, 0.0, 0, 0.0, 0);
    	talon.setPID(3.0, 0.0, 240.0, 0.0, 0, 0.0, 1);    	
    	talon.setProfile(0);  
/*		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	talon.reverseSensor(false);
    	talon.configEncoderCodesPerRev(360);
    	talon.configNominalOutputVoltage(+0f, -0f);
    	talon.configPeakOutputVoltage(+12f, -12f);
    	talon.setAllowableClosedLoopErr(0); 
    	talon.changeControlMode(TalonControlMode.Position);
//    	turret_motor.setPID(1.4, 0.0, 140.0, 0.0, 0, 0.0, 0);
    	talon.setProfile(0);*/
	}
	
	public void update(){
		SmartDashboard.putNumber("TEST_GET", talon.get());
		SmartDashboard.putNumber("TEST", talon.getPosition());
	}
}