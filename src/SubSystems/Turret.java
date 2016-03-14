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
    private double angle;
    private DigitalInput hallEffect;
    private double scale = 39.13; //39.13
    private Elevator elevator;
    private FSM fsm;
    private int checks = 0;
    private State visionState = Turret.State.OFF;
    private Vision vision;
    private Direction trackingDirection = Direction.LEFT;
    private long timePassed = 500;
    private long timeStarted = 0;
    public static enum Direction{
    	LEFT, RIGHT
    }
    public static Turret getInstance()
    {
        if( instance == null )
            instance = new Turret();
        return instance;
    }
    public static enum State{
		TRACKING, HOLDING, OFF,SPOTTED,SINGLE
	}
    public Turret(){
    	turret_motor = new CANTalon(Ports.TURRET);
    	absolutePosition = turret_motor.getPulseWidthPosition() & 0xFFF;
    	turret_motor.setEncPosition(absolutePosition);
    	turret_motor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	turret_motor.reverseSensor(false);
    	turret_motor.configEncoderCodesPerRev(360);
    	turret_motor.configNominalOutputVoltage(+0f, -0f);
    	turret_motor.configPeakOutputVoltage(+12f, -12f);
    	turret_motor.setAllowableClosedLoopErr(0); 
    	turret_motor.changeControlMode(TalonControlMode.Position);
    	turret_motor.setPID(1.52, 0.002, 140.0, 0.0, 0, 0.0, 0);
    	turret_motor.setProfile(0);
    	turret_motor.set(0.0);
    	hallEffect = new DigitalInput(Ports.TURRET_RESET);
    	elevator = Elevator.getInstance();
    	fsm = FSM.getInstance();
    	vision = Vision.getInstance();
    }
    public double getGoal(){
    	return turret_motor.getSetpoint();
    }
    public void setState(Turret.State newState){
		visionState = newState;
	}
    public void update(){
    	position = turret_motor.getPosition();
    	angle = position * scale;
    	SmartDashboard.putNumber("TURRET_ANGLE", angle);
    	SmartDashboard.putNumber("TURRET_DRAW", turret_motor.getOutputCurrent());
    	SmartDashboard.putNumber("TURRET_GOAL", turret_motor.getSetpoint()*scale);
    	SmartDashboard.putNumber("TURRET_ERROR", (position-turret_motor.getSetpoint())*scale);
    	SmartDashboard.putBoolean("TURRET_RESET", hallEffect.get());
    	
    	if(elevator.status() == Elevator.Direction.UP){
	    	switch(visionState){
		    	case OFF:
		    		SmartDashboard.putString("TUR_STATUS", "OFF");
		    		break;
		    	case TRACKING:	    	
		    		SmartDashboard.putString("TUR_STATUS", "TRACKING");
		    		if(vision.isTargetSeen()){		    			
		    			setState(Turret.State.SPOTTED);
		    		}else{
		    			SmartDashboard.putString("TUR_STATUS", "MOVING");
		    			double goalAngle = turret_motor.getPosition();
		    			switch(trackingDirection){
		    			case LEFT:
		    				SmartDashboard.putString("TUR_STATUS", "T_LEFT");
		    				if(angle > Constants.TURRET_TRACKING_ANGLE){
		    					trackingDirection = Turret.Direction.RIGHT;
		    					set(goalAngle - 0.001);
		    				}else{
		    					set(goalAngle + 0.001);
		    				}
		    				break;
		    			case RIGHT:
		    				SmartDashboard.putString("TUR_STATUS", "T_RIGHT");
		    				if(angle < -Constants.TURRET_TRACKING_ANGLE){
		    					trackingDirection = Turret.Direction.LEFT;
		    					set(goalAngle + 0.001);
		    				}else{
		    					set(goalAngle - 0.001);
		    				}
		    				break;
		    			}
		    		}
		    		break;
		    	case SPOTTED:
		    		SmartDashboard.putString("TUR_STATUS", "SPOTTED");
		    		if(vision.isTargetSeen()){		    			
		    			if(System.currentTimeMillis() > (timeStarted + timePassed)){
		    				set(Vision.getAngle() + angle);
		    				timeStarted = System.currentTimeMillis();
		    				System.out.println("Move");
		    			}else{
		    				System.out.println("Waiting");
		    			}
		    		}else{
		    			setState(Turret.State.TRACKING);
		    		}
		    		break;
		    	case SINGLE:
		    		if(System.currentTimeMillis() > (timeStarted + timePassed)){
			    		double vAngle = Vision.getAngle();
			    		double newAngle = vAngle + angle;
			    		if(vision.isTargetSeen() && vAngle >-5 && vAngle < 5 ){	 
			    				set(newAngle);
			    				
			    				System.out.println("Move");
			    		}
			    		timeStarted = System.currentTimeMillis();
		    		}else{
	    				System.out.println("Waiting");
	    			}
		    		break;
		    	case HOLDING:
		    		if(System.currentTimeMillis() > (timeStarted + timePassed)){
		    			
		    		}
		    		SmartDashboard.putString("TUR_STATUS", "HOLDING");
		    		break;
		    	default:
		    		SmartDashboard.putString("TUR_STATUS", "DEFAULT");
		    		break;
	    	}
    	}
    	vision.update();
    }
    public boolean safeToLower(){
    	if(checks < 0 && !hallEffect.get()){
    		return true;
    	}else{
    		if(!hallEffect.get() && Util.onTarget(0.0, turret_motor.getPosition()*scale, 1.0)){
    			checks--;
    		}else{
    			checks = Constants.TURRET_MIN_ZERO_CHECKS;
    		}
    		return false;
    	}
    }
    public boolean onTarget(){
    	return Util.onTarget(turret_motor.getSetpoint(), getAngle(), 1.5);
    }
    public void stop(){
    	turret_motor.setSetpoint(turret_motor.getPosition());
    }
    public void set(double angle){
    	if(elevator.status() == Elevator.Direction.UP){
    		if(angle <= Constants.TURRET_MIN_ANGLE){
    			angle = Constants.TURRET_MIN_ANGLE;
    		}else if(angle >= Constants.TURRET_MAX_ANGLE){
    			angle = Constants.TURRET_MAX_ANGLE;
    		}
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
    	return turret_motor.getPosition()*scale;
    }
}
