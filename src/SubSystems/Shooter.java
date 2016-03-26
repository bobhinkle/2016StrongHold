package SubSystems;

import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter 
{
    private CANTalon motor1;
    private CANTalon motor2;
    private CANTalon motor3;
    private CANTalon motor4;
    private CANTalon preloader_motor;
    private Solenoid hood;
    private Solenoid topHood;
    private int absolutePosition;
    private static Shooter instance = null;
    private boolean firing = false;
    public boolean fenderShot = false;
    private Elevator elevator;
    private ShootingAction fireCommand;
    private Shot setShot = Shot.CLOSE;
    
    final String defaultSpeed = "Default";
    final String speed1 = "+250";
    final String speed2 = "+500";
    final String speed3 = "+750";
    final String speed4 = "+1000";
    final String speed5 = "-250";
    final String speed6 = "-500";
    final String speed7 = "-750";
    final String speed8 = "-1000";
    
    SendableChooser chooser;
    public static Shooter getInstance()
    {
        if( instance == null )
            instance = new Shooter();
        return instance;
    }
    public static enum Status{
    	CLOSE, FAR, STOPPED,AUTO
    }
    public static enum Shot{
    	CLOSE,FAR,AUTO,WALL
    }
    public static enum HoodStates{
    	DOWN, FAR_SHOT, CLOSE_SHOT,UP
    }
    public void setShot(Shot newShot){
    	setShot = newShot;
    }
    public Shooter(){
    	motor1 = new CANTalon(Ports.SHOOTER_MOTOR_3);
    	absolutePosition = motor1.getPulseWidthPosition() & 0xFFF;
    	motor1.setEncPosition(absolutePosition);
    	motor1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	motor1.reverseSensor(true);
    	motor1.configEncoderCodesPerRev(360);
    	motor1.configNominalOutputVoltage(+0f, -0f);
    	motor1.configPeakOutputVoltage(+12f, 0);
    	motor1.setAllowableClosedLoopErr(0); 
    	motor1.changeControlMode(TalonControlMode.Speed);
    	motor1.set(motor1.getPosition());
    	motor1.setPID(0.048, 0.0, 0.0, 0.048, 0, 0.0, 0);
    	motor1.setPID(0.0, 0.0, 0.0, 0.05, 0, 0.0, 1);
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
        preloader_motor = new CANTalon(Ports.PRELOAD);
        elevator = Elevator.getInstance();
        hood = new Solenoid(21,Ports.HOOD);
        topHood = new Solenoid(21,Ports.TOP_HOOD);
        
        chooser = new SendableChooser();
        chooser.addDefault("default", defaultSpeed);
        chooser.addObject("+250", speed1);
        chooser.addObject("+500", speed2);
        chooser.addObject("+750", speed3);
        chooser.addObject("+1000", speed4);
        chooser.addObject("-250", speed5);
        chooser.addObject("-500", speed6);
        chooser.addObject("-750", speed7);
        chooser.addObject("-1000", speed8);
        SmartDashboard.putData("Shooter modes", chooser);
    }
    public void update(){
    	SmartDashboard.putNumber("SHOOTER_SPEED", motor1.getSpeed());
    	SmartDashboard.putNumber("SHOOTER_GOAL", motor1.getSetpoint());
    	SmartDashboard.putNumber("SHOOTER_POWER", motor1.getOutputVoltage());
    	SmartDashboard.putNumber("SHOOTER_CURRENT", motor1.getOutputCurrent());
    	SmartDashboard.putNumber("SHOOTER_ERROR", motor1.getSetpoint()-motor1.getSpeed());
    	
    }
    public void setHoodState(HoodStates state){
    	switch(state){
    	case DOWN:
    		hood.set(false);
    		topHood.set(false);
    		System.out.println("DOWN");
    		break;
    	case FAR_SHOT:
    		hood.set(true);
    		topHood.set(false);
    		System.out.println("FAR");
    		break;
    	case CLOSE_SHOT:
    		hood.set(true);
    		topHood.set(true);
    		System.out.println("CLOSE");
    		break;
    	case UP:
    		hood.set(true);
    		topHood.set(false);
    		System.out.println("UP");
    		break;
    	}
    }
    public void setPresetSpeed(){
    	switch(setShot){
    	case CLOSE:
    		set(Constants.SHOOTER_CLOSE_SHOT);
    		break;
    	case FAR:
//    		set(Constants.SHOOTER_FAR_SHOT);
    		set(getchooseableSpeed());
    		break;
    	case AUTO:
    		set(Constants.SHOOTER_AUTON_SIDE_SHOT);
    		break;
    	default:
    		set(Constants.SHOOTER_CLOSE_SHOT);
    		break;
    	}
    }
    public double getchooseableSpeed(){
    	String speedSelected = (String) chooser.getSelected();
    	switch(speedSelected){
    	case "+250":
    		return Constants.SHOOTER_FAR_SHOT + 250;
    	case "+500":
    		return Constants.SHOOTER_FAR_SHOT + 500;
    	case "+750":
    		return Constants.SHOOTER_FAR_SHOT + 750;
    	case "+1000":
    		return Constants.SHOOTER_FAR_SHOT + 1000;
    	case "-250":
    		return Constants.SHOOTER_FAR_SHOT - 250;
    	case "-500":
    		return Constants.SHOOTER_FAR_SHOT - 500;
    	case "-750":
    		return Constants.SHOOTER_FAR_SHOT - 750;
    	case "-1000":
    		return Constants.SHOOTER_FAR_SHOT - 1000;
    	default:
    		return Constants.SHOOTER_FAR_SHOT;
    	}
    }
    public void set(double _speed){
    	motor1.setProfile(0);
    	motor1.set(_speed);
    }
    public void stop(){
    	motor1.setProfile(1);
    	motor1.set(0.0);
    }
    public void hoodExtend(){   	
    	hood.set(false); 
    } 
    public void hoodRetract(){ 
    	if((elevator.status() != Elevator.Direction.MOVING) || (elevator.status() != Elevator.Direction.STOP) )
    		hood.set(true); 
    }	
    public void preloader_forward(){
    	preloader_motor.set(-1.0);
    }
    public void preloader_reverse(){
    	preloader_motor.set(1.0);
    }
    public void preloader_stop(){
    	preloader_motor.set(0.0);
    }
    public boolean onTarget(){
    	return Util.onTarget(motor1.getSetpoint(), motor1.get(), Constants.SHOOTER_ERROR);
    }
    public void fire(){
    	if(!firing){
    		if(Util.onTarget(motor1.getSetpoint(), motor1.get(), Constants.SHOOTER_ERROR) ){
				System.out.println("FIRE CLOSE");
				fireCommand = new ShootingAction();
		    	fireCommand.start();
			}else{
				System.out.println("FIRE CLOSE ERROR");
			}
    	}else{
    		System.out.println("FIRE STUCK");
    	}
    }
    public void killFire(){
    	if(firing && fireCommand != null){
    		try{
    			fireCommand.kill();
    		}catch(Exception e){
    			System.out.println(e);
    		}
    	}
    }
    public class ShootingAction extends Thread{
    	private boolean keepRunning = true;
		@Override
		public void run() {
			firing = true;
			preloader_forward();
			while(Util.onTarget(Constants.SHOOTER_CLOSE_SHOT, motor1.get(), Constants.SHOOTER_ERROR+200) && keepRunning)
				Timer.delay(0.1);
			if(keepRunning){
				Timer.delay(2.0);
				preloader_stop();
			}
			firing = false;			
		}
    	public void kill(){
    		keepRunning = false;
    	}
    }
}