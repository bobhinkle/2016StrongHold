package SubSystems;

import java.util.HashMap;
import java.util.Map;

import IO.TeleController;
import IO.Controller;
import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
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
    Map<Integer, Double> map;
    Map<Integer,String> adjustments;  
    Map<Integer,Double> visionBump;
    private DigitalInput ballSensor;
    SendableChooser chooser;
    private BallReadingAction ballIntake;
    private boolean intakingBall = false;
    private BallSuckForLowBar bSucker;
    private BallBlower bBlower;
    private boolean ballInShooter = false;
    private boolean unJamming = false;
    private boolean suckingInBall = false;
    private Vision vision;
    private TeleController controller;
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
    	DOWN, FAR_SHOT, CLOSE_SHOT
    }
    public void setShot(Shot newShot){
    	setShot = newShot;
    }
    public Shooter(){
    	motor1 = new CANTalon(Ports.SHOOTER_MOTOR_3);
    	absolutePosition = motor1.getPulseWidthPosition() & 0xFFF;
    	motor1.setEncPosition(absolutePosition);
    	motor1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    	motor1.reverseSensor(false);
    	motor1.configEncoderCodesPerRev(360);
    	motor1.configNominalOutputVoltage(+0f, -0f);
    	motor1.configPeakOutputVoltage(+12f, 0);
    	motor1.setAllowableClosedLoopErr(0); 
    	motor1.changeControlMode(TalonControlMode.Speed);
    	motor1.set(motor1.getPosition());
    	motor1.setPID(0.1, 0.0, 0.4, 0.050, 0, 0.0, 0);
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
        adjustmentsMap();
        chooser = new SendableChooser();
        chooser.addDefault("default", adjustments.get(0));
        chooser.addObject("+100", adjustments.get(1));
        chooser.addObject("+200", adjustments.get(2));
        chooser.addObject("-100", adjustments.get(3));
        chooser.addObject("-200", adjustments.get(4));
        SmartDashboard.putData("Shooter modes", chooser);
        ballSensor = new DigitalInput(Ports.BALL_SENSOR);
        loadMap();
        vision = Vision.getInstance();
    }
    public double ballSensorData(){
    	return preloader_motor.getOutputCurrent();
    }
    public void startPreloaderIntake(){
    	if(!intakingBall && !ballHeld() && !suckingInBall){
    		ballIntake = new BallReadingAction();
    		ballIntake.start();
    		System.out.println("PSTARTED");
    	}
    }
    public void clearFire(){
    	firing = false;
    }
    public boolean ballInShooter(){
    	return ballInShooter;
    }
    public void killPreloaderIntake(){
    	if(intakingBall){
    		ballIntake.kill();
    	}
    }
    public void startBallSucker(){
    	if(ballHeld()){
    		bSucker = new BallSuckForLowBar();
    		bSucker.start();
    	}
    }    
    public void getBallOutOfShooter(){
    	if(!unJamming){
	    	bBlower = new BallBlower();
	    	bBlower.start();
    	}
    }
    public void killBallSucker(){
    	if(unJamming){
    		bBlower.kill();
    	}
    }
    public boolean suckingBall(){
    	return suckingInBall;
    }
    public boolean ballHeld(){
    	return !ballSensor.get() ;
    }
    public boolean ballDetected(){
    	return ballSensorData() > Constants.BALL_DRAW;
    }
    public double getSpeed(){
    	return motor1.getSpeed();
    }
    public void update(){
    	SmartDashboard.putNumber("SHOOTER_SPEED", motor1.getSpeed());
    	SmartDashboard.putNumber("SHOOTER_GOAL", motor1.getSetpoint());
    	SmartDashboard.putNumber("SHOOTER_POWER", motor1.getOutputVoltage());
    	SmartDashboard.putNumber("SHOOTER_CURRENT", motor1.getOutputCurrent());
    	SmartDashboard.putNumber("SHOOTER_ERROR", motor1.getSetpoint()-motor1.getSpeed());
    	SmartDashboard.putNumber("PRESHOOTER_DRAW", preloader_motor.getOutputCurrent());
    	SmartDashboard.putBoolean("FIRING", firing); 
    	SmartDashboard.putNumber("SHOOTER_VBUMP", visionAdd());
    }
    public boolean shooterOn(){
    	return motor1.getSetpoint() > 2000;
    }
    public void loadMap(){
    	map =  new HashMap<>();
    	map.put(10, 3000.0);
    	map.put(15, 3500.0);
    	map.put(20, 3750.0);
    	map.put(30, 4000.0);
    	map.put(45, 4250.0);
    	map.put(50, 4500.0);
    	visionBump = new HashMap<>();
    	visionBump.put(0, 0.0);
    	visionBump.put(300, 0.0);
    	visionBump.put(305, 0.0);
    	visionBump.put(400,100.0);
    	visionBump.put(425,150.0);
    	visionBump.put(450,200.0);
    }
    public void adjustmentsMap(){
    	adjustments = new HashMap<>();
    	adjustments.put(0, "Default");
    	adjustments.put(1, "+100");
    	adjustments.put(2, "+200");
    	adjustments.put(3, "-100");
    	adjustments.put(4, "-200");
    }
    public double visionAdd(){
    	if(visionBump.get((int)Math.floor(vision.getHieght()/25)*25) == null){
    		return 0.0;
    	}else{
    		return visionBump.get((int)Math.floor(vision.getHieght()/25)*25);
    	}
    }
    public double getSpeedByHardness(double hardness){
    	int hardnessRounded = (int)Math.round(hardness);
    	return map.get(hardnessRounded);
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
    	default:
    		
    		break;
    	}
    }
    public void setPresetSpeed(double offset){
    	switch(setShot){
    	case CLOSE:
    		set(Constants.SHOOTER_CLOSE_SHOT + offset);
    		break;
    	case FAR:
//    		set(Constants.SHOOTER_FAR_SHOT);
    		set(getChooseableSpeed()+ offset);
    		break;
    	case AUTO:
    		set(Constants.SHOOTER_AUTON_SIDE_SHOT+ offset);
    		break;
    	default:
    		set(Constants.SHOOTER_CLOSE_SHOT+ offset);
    		break;
    	}
    }
    public double getChooseableSpeed(){
    	String speedSelected = (String) chooser.getSelected();
    	switch(speedSelected){
    	case "+250":
    		//return Constants.SHOOTER_FAR_SHOT + 250;
    		return  2000;
    	case "+500":
    		//return Constants.SHOOTER_FAR_SHOT + 500;
    		return  2500;
    	case "+750":
    		//return Constants.SHOOTER_FAR_SHOT + 750;
    		return  3000;
    	case "+1000":
    		//return Constants.SHOOTER_FAR_SHOT + 1000;
    		return  3250;
    	case "-250":
    		//return Constants.SHOOTER_FAR_SHOT - 250;
    		return  3500;
    	case "-500":
    		//return Constants.SHOOTER_FAR_SHOT - 500;
    		return  3750;
    	case "-750":
    		//return Constants.SHOOTER_FAR_SHOT - 750;
    		return  4000;
    	case "-1000":
    		//return Constants.SHOOTER_FAR_SHOT - 1000;
    		return  4500;
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
    public void intakeing_preloader_forward(){
    	preloader_motor.set(-0.5);
    }
    public void preloader_forward(){
    	preloader_motor.set(-1.0);
    }
    public void preloader_reverse(){
    	preloader_motor.set(1.0);
    }
    public void preloader_ballDetect(){
    	preloader_motor.set(-0.4);
    }
    public void preloader_stop(){
    	preloader_motor.set(0.0);
    }
    public boolean onTarget(){
    	return Util.onTarget(motor1.getSetpoint(), motor1.get(), Constants.SHOOTER_ERROR);
    }
    public boolean onTarget(double error){
    	return Util.onTarget(motor1.getSetpoint(), motor1.get(), error);
    }
    public boolean firing(){
    	return firing;
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
    public void noCheckFire(){
    	if(!firing){
    		System.out.println("FIRE CLOSE");
			fireCommand = new ShootingAction();
	    	fireCommand.start();
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
    public boolean isFiring(){
    	return firing;
    }
    public Shot shot(){
    	return setShot;
    }
    public class ShootingAction extends Thread{
    	private boolean keepRunning = true;
    	private long endTime = 0;
		@Override
		public void run() {
			if(suckingInBall){
				ballIntake.kill();
			}
			endTime = System.currentTimeMillis()+2000;
			firing = true;
			preloader_forward();
			while(motor1.get() > motor1.getSetpoint() - Constants.SHOOTER_FIRED_SPEED && keepRunning && (System.currentTimeMillis() < endTime)){
				preloader_forward();
				Timer.delay(0.1);
			}
			if(keepRunning){
				Timer.delay(2.0);
				preloader_stop();
			}
			set(0.0);
			firing = false;	
		}
    	public void kill(){
    		keepRunning = false;
    		firing = false;	
    	}
    }
    public class BallReadingAction extends Thread{
    	private boolean keepRunning = true;
    	private long endTime = 0;
		@Override
		public void run() {
			if(controller == null){
				controller = TeleController.getInstance();
			}
			endTime = System.currentTimeMillis()+10000;
			suckingInBall = true;
			preloader_ballDetect();			
			while(!ballDetected() && keepRunning && (System.currentTimeMillis() < endTime) && !ballHeld())
				Timer.delay(0.01);
			controller.codriver.rumble(Controller.Vibration.SINGLE);
			preloader_forward();
			while(!ballHeld() && keepRunning && (System.currentTimeMillis() < endTime)){
				Timer.delay(0.01);
			}
			
			preloader_stop();
			suckingInBall = false;
/*			ballPSI = ballSensorData();
			Timer.delay(0.1);
			preloader_reverse();
			Timer.delay(0.1);
			preloader_stop();		*/
		}
    	public void kill(){
    		keepRunning = false;
    		suckingInBall = false;
    	}
    }
    public class BallSuckForLowBar extends Thread{
    	private boolean keepRunning = true;
    	public void kill(){
    		keepRunning = false;
    	}
    	@Override
		public void run() {
    		suckingInBall = true;
    		intakeing_preloader_forward();
    		while(ballHeld() && keepRunning)
				Timer.delay(0.01);
    		Timer.delay(0.35);
    		preloader_stop();
    		ballInShooter = true;
    		suckingInBall = false;
    	}
    }
    public class BallBlower extends Thread{
    	private boolean keepRunning = true;
    	public void kill(){
    		keepRunning = false;
    	}
    	@Override
    	public void run(){
    		unJamming = true;
    		preloader_reverse();
    		while(ballHeld() && keepRunning){
        		Timer.delay(0.01);
    		}    
    		Timer.delay(0.3);
    		preloader_stop();
    		unJamming = false;
    		ballInShooter = false;
    	}
    }
}
