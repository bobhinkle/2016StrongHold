package SubSystems;

import IO.Controller.Vibration;
import IO.TeleController;
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
    private State visionState = Turret.State.OFF;
    private Vision vision;
    private Direction trackingDirection = Direction.LEFT;
    private int turretChecks = 15; //25
    private int checksCompleted = 0;
    private Shot shot = Shot.FAR;
    private TeleController controller;
    private Lights lights;
    public static enum Direction{
    	LEFT, RIGHT,FIRST 
    }
    public static enum Shot{
    	CLOSE,FAR
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
    	turret_motor.setPID(5, 0.001, 50.0, 0.0, 0, 0.0, 0); 
    	turret_motor.setPID(5.5, 0.0015, 40.0, 0.0, 0, 0.0, 1);
    	turret_motor.setProfile(0);
    	turret_motor.set(0.0);
    	hallEffect = new DigitalInput(Ports.TURRET_RESET);
    	elevator = Elevator.getInstance();
    	vision = Vision.getInstance();
    }
    public double getGoal(){
    	return turret_motor.getSetpoint();
    }
    public void setState(Turret.State newState){
		visionState = newState;
	}
    
    public void update(){
    	vision.update();
    	angle = getAngle();
    	if(Math.abs(angle - (turret_motor.getSetpoint()*scale)) < 2){
    		turret_motor.setProfile(1);
    	}else{
    		turret_motor.setProfile(0);
    	}
    	checkForStop();    	
    	SmartDashboard.putNumber("TURRET_GOAL", turret_motor.getSetpoint()*scale);
    	SmartDashboard.putNumber("TURRET_ER", Math.abs(angle - (turret_motor.getSetpoint()*scale)));
    	SmartDashboard.putBoolean("TURRET_RESET", hallEffect.get());
    	SmartDashboard.putNumber("TURRET_ANGLE", angle);
    	SmartDashboard.putBoolean("TURRET_MOVING", !onTarget());   	
    	if(lights == null){
    		lights = Lights.getInstance();
    	}
    	switch(visionState){
	    	case OFF:
	    		SmartDashboard.putString("TUR_STATUS", "OFF");
	    		break;
	    	case TRACKING:	    	
	    		boolean trackSet = false;
	    		SmartDashboard.putString("TUR_STATUS", "TRACKING");
	    		if(vision.isTargetSeen()){		    		
	    			trackSet = false;
	    			setState(Turret.State.SPOTTED);
	    		}else{	
	    			SmartDashboard.putString("TUR_STATUS", "MOVING");
	    			switch(trackingDirection){
	    			case LEFT:
	    				SmartDashboard.putString("TUR_STATUS", "T_LEFT");
	    				if(onTarget()){
	    					trackingDirection = Turret.Direction.RIGHT;
	    					trackSet = false;
	    				}else{
	    					if(!trackSet){
	    						set(15);
	    						trackSet = true;
	    					}
	    					
	    				}
	    				break;
	    			case RIGHT:
	    				SmartDashboard.putString("TUR_STATUS", "T_RIGHT");
	    				if(onTarget()){
	    					trackingDirection = Turret.Direction.LEFT;
	    					trackSet = false;
	    				}else{
	    					if(!trackSet){
	    						set(-15);
	    						trackSet = true;
	    					}
	    				}
	    				break;
	    			case FIRST:
	    				set(0.0);	
	    				trackSet = true;
	    				trackingDirection = Turret.Direction.LEFT;
	    				break;
	    			}		    		
	    		}
	    		break;
	    	case SPOTTED:
	    		SmartDashboard.putString("TUR_STATUS", "SPOTTED");
	    		double visionAngle = Vision.getAngle();
	    		if(vision.isTargetSeen()){		    
	    			if(onTarget()){
	    				if(Math.abs(visionAngle) >= 0.5){
//	    					System.out.println(visionAngle + " " + lastAngle + " " + angle + " MOVING");
		    				set(visionAngle + angle);			    				
	    				}
	    				else{		    
	    					if(controller == null){
	    						controller = TeleController.getInstance();
	    					}
	    					lights.setState(Lights.MODE.ON_TARGET);
	    					controller.codriver.rumble(Vibration.SINGLE);
//	    					System.out.println(visionAngle + " " + lastAngle + " " + angle+ "no move 1" + onTarget());
	    				}
	    			}else{
//	    				System.out.println(visionAngle + " " + lastAngle + " " + angle + "no move 2" + onTarget());
	    			}
	    		}else{
	    			lights.setState(Lights.MODE.OFF_TARGET);
	    			trackingDirection = Turret.Direction.FIRST;
	    			setState(Turret.State.TRACKING);
	    		}
	    		break;
	    	case SINGLE:
	    		if(onTarget()){
		    		double vAngle = Vision.getAngle();
		    		double newAngle = vAngle + angle;
		    		if(vision.isTargetSeen() && Math.abs(vAngle) > 0.5){	 
		    				set(newAngle);			    				
		    		}
		    		setState(Turret.State.OFF);
	    		}else{
    				
    			}
	    		break;
	    	case HOLDING:
	    		
	    		SmartDashboard.putString("TUR_STATUS", "HOLDING");
	    		break;
	    	default:
	    		SmartDashboard.putString("TUR_STATUS", "DEFAULT");
	    		break;
    	}
    	
    	
    }
    public void setShot(Shot where){
    	shot = where;
    }
    public boolean safeToLower(){
    	return onTarget() && !hallEffect.get();
    }
    public boolean onTarget(){  
    	if(shot == Shot.FAR)
    		return checksCompleted < 0;
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
    	setState(Turret.State.OFF);
    	double current = getAngle();
		double newpos = current + angle;
		set(newpos);	
    }
    public void zeroCheck(){
    	if(!hallEffect.get()){
    		turret_motor.setEncPosition(0);
    	}
    }
    public void checkForStop(){
    	if(Util.onTarget(turret_motor.getSetpoint() * scale, getAngle(), 0.6)){
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
