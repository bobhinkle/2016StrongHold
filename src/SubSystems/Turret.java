package SubSystems;

import ControlSystem.FSM;
import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
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
    private Elevator elevator;
    private FSM fsm;
    private int checks = 0;
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
    	turret_motor.reverseSensor(true);
    	turret_motor.configEncoderCodesPerRev(360);
    	turret_motor.configNominalOutputVoltage(+0f, -0f);
    	turret_motor.configPeakOutputVoltage(+12f, -12f);
    	turret_motor.setAllowableClosedLoopErr(0); 
    	turret_motor.changeControlMode(TalonControlMode.Position);
    	turret_motor.setPID(1.4, 0.001, 140.0, 0.0, 0, 0.0, 0);
    	turret_motor.setProfile(0);
    	turret_motor.set(0.0);
    	hallEffect = new DigitalInput(Ports.TURRET_RESET);
    	elevator = Elevator.getInstance();
    	fsm = FSM.getInstance();
    }
    public double getGoal(){
    	return turret_motor.getSetpoint();
    }
    public void update(){
    	position = turret_motor.getPosition();
    	SmartDashboard.putNumber("TURRET_ANGLE", position*scale);
    	SmartDashboard.putNumber("TURRET_DRAW", turret_motor.getOutputCurrent());
    	SmartDashboard.putNumber("TURRET_GOAL", turret_motor.getSetpoint()*scale);
    	SmartDashboard.putNumber("TURRET_POWER", turret_motor.getOutputVoltage());
//    	SmartDashboard.putNumber("TURRET_P", turret_motor.getP());
    	SmartDashboard.putNumber("TURRET_ERROR", (turret_motor.getPosition()-turret_motor.getSetpoint())*scale);
    	SmartDashboard.putBoolean("TURRET_RESET", hallEffect.get());
    }
    public boolean safeToLower(){
    	if(checks < 0 && !hallEffect.get()){
    		return true;
    	}else{
    		if(!hallEffect.get() && Util.onTarget(0.0, turret_motor.getPosition()*scale, 1.5)){
    			checks--;
    		}else{
    			checks = Constants.TURRET_MIN_ZERO_CHECKS;
    		}
    		return false;
    	}
    }
    
    public void stop(){
    	turret_motor.setSetpoint(turret_motor.getPosition());
    }
    public void set(double angle){
    	if(elevator.status() == Elevator.Direction.UP){
    		turret_motor.set(angle/scale);
    	}
    }
    public void manualMove(double angle){
    	if(elevator.status() == Elevator.Direction.UP){
    		double current = turret_motor.get() * scale;
    		double newpos = current + angle;
    		if(fsm.getCurrentState() == FSM.State.SHOOTER_CLOSE || fsm.getPreviousState() == FSM.State.SHOOTER_CLOSE){
    			if(Constants.TURRET_CLOSE_SHOT_MIN_ANGLE > newpos){
        			turret_motor.setSetpoint(Constants.TURRET_CLOSE_SHOT_MIN_ANGLE/scale);
        		}else if(newpos > Constants.TURRET_CLOSE_SHOT_MAX_ANGLE){
        			turret_motor.setSetpoint(Constants.TURRET_CLOSE_SHOT_MAX_ANGLE/scale);
        		}else{
        			turret_motor.setSetpoint(newpos/scale);
        		}
    		}else{
    			if(Constants.TURRET_MIN_ANGLE > newpos){
        			turret_motor.setSetpoint(Constants.TURRET_MIN_ANGLE/scale);
        		}else if(newpos > Constants.TURRET_MAX_ANGLE){
        			turret_motor.setSetpoint(Constants.TURRET_MAX_ANGLE/scale);
        		}else{
        			turret_motor.setSetpoint(newpos/scale);
        		}
    		}    		
    	}
    }
    public void zeroCheck(){
    	if(!hallEffect.get()){
    		turret_motor.setEncPosition(0);
    	}
    }
    public double getAngle(){
    	return turret_motor.get()*scale;
    }
}
