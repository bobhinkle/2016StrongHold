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
    private double angle;
    private DigitalInput hallEffect;
    private double scale = 39.13;
    private Elevator elevator;
    private FSM fsm;
    private State visionState = Turret.State.OFF;
    private Vision vision;
    private Direction trackingDirection = Direction.LEFT;
    private long timePassed = 125;
    private long timeStarted = 0;
    private double lastAngle = 0;
    private int turretChecks = 15;
    private int checksCompleted = 0;
    private double trackingTurnSpeed = 10.0;
    public static enum Direction{
    	LEFT, RIGHT,FIRST
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
    	turret_motor.reverseSensor(true);
    	turret_motor.configEncoderCodesPerRev(360);
    	turret_motor.configNominalOutputVoltage(+0f, -0f);
    	turret_motor.configPeakOutputVoltage(+12f, -12f);
    	turret_motor.setAllowableClosedLoopErr(0); 
    	turret_motor.changeControlMode(TalonControlMode.Position);
    	turret_motor.setPID(5, 0.0, 50.0, 0.0, 0, 0.0, 0);
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
    	angle = getAngle();
    	checkForStop();    	
    	SmartDashboard.putNumber("TURRET_GOAL", turret_motor.getSetpoint()*scale);
    	SmartDashboard.putNumber("TURRET_ER", angle - turret_motor.getSetpoint());
    	SmartDashboard.putBoolean("TURRET_RESET", hallEffect.get());
    	SmartDashboard.putNumber("TURRET_ANGLE", angle);
    	SmartDashboard.putBoolean("TURRET_MOVING", onTarget());
    	
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
		    			switch(trackingDirection){
		    			case LEFT:
		    				SmartDashboard.putString("TUR_STATUS", "T_LEFT");
		    				if(angle + trackingTurnSpeed > Constants.TURRET_TRACKING_ANGLE){
		    					trackingDirection = Turret.Direction.RIGHT;
		    				}else{
		    					set(angle + trackingTurnSpeed);
		    				}
		    				break;
		    			case RIGHT:
		    				SmartDashboard.putString("TUR_STATUS", "T_RIGHT");
		    				if(angle - trackingTurnSpeed < -Constants.TURRET_TRACKING_ANGLE){
		    					trackingDirection = Turret.Direction.LEFT;
		    				}else{
		    					set(angle - trackingTurnSpeed);
		    				}
		    				break;
		    			case FIRST:
		    				set(vision.lastKnownAngle());
		    				if(vision.lastKnownAngle() > 0)
		    					trackingDirection = Turret.Direction.RIGHT;
		    				else
		    					trackingDirection = Turret.Direction.LEFT;
		    				break;
		    			}		    		
		    		}
		    		break;
		    	case SPOTTED:
		    		SmartDashboard.putString("TUR_STATUS", "SPOTTED");
		    		double visionAngle = Vision.getAngle();
		    		double turAngle = getAngle();
		    		if(vision.isTargetSeen()){		    
		    			if(System.currentTimeMillis() > (timeStarted + timePassed) && onTarget()){
		    				if((lastAngle!=visionAngle && Math.abs(visionAngle) > 1.5)){
		    					System.out.println(visionAngle + " " + lastAngle + " " + turAngle + " MOVING");
			    				set(visionAngle + angle);			    				
			    				lastAngle = visionAngle;
			    				timeStarted = System.currentTimeMillis();			    				
		    				}
		    				else{
		    					System.out.println(visionAngle + " " + lastAngle + " " + turAngle+ "no move 1" + onTarget());
		    				}
		    			}else{
		    				System.out.println(visionAngle + " " + lastAngle + " " + turAngle + "no move 2" + onTarget());
		    			}
		    		}else{
		    			timeStarted = System.currentTimeMillis();
		    			trackingDirection = Turret.Direction.FIRST;
		    			setState(Turret.State.TRACKING);
		    		}
		    		break;
		    	case SINGLE:
		    		if(System.currentTimeMillis() > (timeStarted + timePassed)){
			    		double vAngle = Vision.getAngle();
			    		double newAngle = vAngle + angle;
			    		if(vision.isTargetSeen() && Math.abs(vAngle) > 1.5){	 
			    				set(newAngle);			    				
			    		}
			    		timeStarted = System.currentTimeMillis();
			    		setState(Turret.State.OFF);
		    		}else{
	    				
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
    	return onTarget() && !hallEffect.get();
    }
    public boolean onTarget(){    	
    	return checksCompleted < 0;
    }
    public void stop(){
    	turret_motor.enableBrakeMode(true);
    }
    public void estop(){
    	turret_motor.setSetpoint(turret_motor.getPosition());
    }
    public void set(double angle){
    	turret_motor.enableBrakeMode(false);
    	if(elevator.status() == Elevator.Direction.UP){
    		if(angle <= Constants.TURRET_MIN_ANGLE){
    			angle = Constants.TURRET_MIN_ANGLE;
    		}else if(angle >= Constants.TURRET_MAX_ANGLE){
    			angle = Constants.TURRET_MAX_ANGLE;
    		}
    		turret_motor.setSetpoint(angle/scale);
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
    public void checkForStop(){
    	if(Util.onTarget(turret_motor.getSetpoint()* scale, getAngle(), 1.0)){
    		checksCompleted--;
    	}else{
    		checksCompleted = turretChecks;
    	}
    }
    public double getAngle(){
    	return turret_motor.getPosition()*scale;
    }
    public double getUnscaledAngle(){
    	return turret_motor.get();
    }
}
