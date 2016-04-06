package IO;import ControlSystem.FSM;
import ControlSystem.RoboSystem;
import SubSystems.DriveTrain.GEAR;
import SubSystems.Elevator;
import SubSystems.Turret;
import SubSystems.Vision;
import Utilities.Constants;
import Utilities.Util;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;


public class TeleController
{
    public static final double STICK_DEAD_BAND = 0.2;
    private Xbox codriver;
    private ThrustMasterWheel wheel;
    private Joystick driver;
    private FSM fsm;
    private RoboSystem robot;
    private static TeleController instance = null;
    public TeleController(){
        driver = new Joystick(0);
        codriver  = new Xbox(2);
        codriver.init();
        wheel = new ThrustMasterWheel(1);
        robot = RoboSystem.getInstance();
        fsm = FSM.getInstance();
        System.out.println("CONTROLS STARTED");
    }
    public static TeleController getInstance(){
        if(instance == null){
            instance = new TeleController();
        }
        return instance;
    }    
    public void coDriver(){
        if(codriver.aButton.isPressed() || codriver.aButton.isHeld()){
        	if(codriver.aButton.buttonHoldTime() > 10){
        		fsm.setGoalState(FSM.State.INTAKE);
        		robot.intake.intake_forward();
        		robot.logFile.writeToLog(System.currentTimeMillis() + " A  PRESSED OR HELD");
        	}else{
        		System.out.println("ButtonHold" + codriver.aButton.buttonHoldTime());
        	}
        }else if(codriver.aButton.isReleased()){
        	robot.intake.intake_stop();
        	robot.intake.setAngle(Constants.INTAKE_WAIT_FOR_GRAB);
        	robot.logFile.writeToLog(System.currentTimeMillis() + " A  RELEASED");
        }        
        //////////////////////////////////////////
        if(codriver.bButton.isPressed()){
        	fsm.setGoalState(FSM.State.STOW);
        	robot.logFile.writeToLog(System.currentTimeMillis() + " B  PRESSED");
        }
        ////////////////////////////////////////
        if(codriver.xButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_CLOSE);
        	robot.logFile.writeToLog(System.currentTimeMillis() + " X  PRESSED");
        }
        if(codriver.xButton.buttonHoldTime() > 250){
        	fsm.setGoalState(FSM.State.BATTER_SHOT);
        	robot.logFile.writeToLog(System.currentTimeMillis() + " X  HELD");
        }
        ///////////////////////////////////////
        if(codriver.yButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_FAR);
        	
        	robot.logFile.writeToLog(System.currentTimeMillis() + " Y  PRESSED");
        }
        /////////////////////////////////////////////
        if(codriver.rightTrigger.isHeld() && codriver.rightTrigger.buttonHoldTime() < 250 && !robot.shooter.isFiring()){ 
        	robot.turret.stop();
        	robot.shooter.fire();
        	robot.logFile.writeToLog(System.currentTimeMillis() + " RT  PRESSED TARGET SEEN:" + robot.vision.isTargetSeen() + " VISION ANGLE:" + Vision.getAngle() + " SHOOTER SPEED :" + robot.shooter.onTarget() + " T_ANGLE:" + robot.turret.getAngle());
        }else if(codriver.rightTrigger.isHeld() && codriver.rightTrigger.buttonHoldTime() > 500 && !robot.shooter.isFiring()){
        	robot.turret.stop();
        	robot.shooter.noCheckFire();
        }
        //////////////////////////////////////////////////////////////////// 
        if(codriver.leftBumper.isPressed()){ 
        	robot.intake.intake_reverse();
        	if(robot.elevator.status() == Elevator.Direction.UP){
        		robot.shooter.preloader_reverse();
        	}
        	robot.logFile.writeToLog(System.currentTimeMillis() + " LB  PRESSED");
        }
        //////////////////////////////////
        if(codriver.rightBumper.isPressed()) {
        	robot.intake.intake_forward();
        	robot.logFile.writeToLog(System.currentTimeMillis() + " RB  PRESSED");
        }
        ///////////////////////////////////////////////////////
        if(codriver.leftTrigger.isPressed()){
        	robot.shooter.setPresetSpeed();     
        	robot.logFile.writeToLog(System.currentTimeMillis() + " LT  PRESSED");
        }
        if(codriver.leftTrigger.buttonHoldTime() > 250 && !robot.shooter.isFiring()){
        	robot.logFile.writeToLog(System.currentTimeMillis() + " LT  HELD && RT HELD");
        	if(robot.elevator.status() == Elevator.Direction.UP){
        		if(robot.vision.isTargetSeen() && Math.abs(Vision.getAngle()) <= 1.15 && robot.shooter.onTarget() && robot.turret.onTarget()){
        			robot.turret.setState(Turret.State.OFF);
        			robot.turret.stop();
        			Timer.delay(0.25);        			
        			robot.shooter.fire();
        			robot.logFile.writeToLog(System.currentTimeMillis() + " LT HELD FIRE- TARGET SEEN:" + robot.vision.isTargetSeen() + " VISION ANGLE:" + Vision.getAngle() + " SHOOTER SPEED :" + robot.shooter.onTarget() + " T_ANGLE:" + robot.turret.getAngle());
        		}else if(robot.vision.isTargetSeen() && Math.abs(Vision.getAngle()) > 1.15){
        			robot.turret.setState(Turret.State.SPOTTED);
        			System.out.println("RESCAN");
        		}
        		else{
        			System.out.println("NOTHING");
        			robot.logFile.writeToLog(System.currentTimeMillis() + " LT HELD NO FIRE- TARGET SEEN:" + robot.vision.isTargetSeen() + " VISION ANGLE:" + Vision.getAngle() + " SHOOTER SPEED :" + robot.shooter.onTarget() + " T_ANGLE:" + robot.turret.getAngle());
        		}
        	}
        }else{
        	
        }
        //////////////////////////////////////////////////////
        if(codriver.backButton.isPressed()){  // stop all      
        	robot.intake.intake_stop();
        	robot.shooter.stop();
        	robot.shooter.preloader_stop();
        	robot.shooter.killFire();
        	robot.shooter.killPreloaderIntake();
        	robot.shooter.killBallSucker();
        	robot.logFile.writeToLog(System.currentTimeMillis() + " BACK PRESSED");
        }
        ////////////////////////////////////////////////////////
        if(codriver.startButton.isPressed()){
        	fsm.setGoalState(FSM.State.LOW_BAR);
        	robot.logFile.writeToLog(System.currentTimeMillis() + " START PRESSED");
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) > 0.75) {
        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSY UP");
        	if(fsm.getCurrentState() == FSM.State.STOW || fsm.getCurrentState() == FSM.State.STOW_READY){
        		robot.shooter.hoodRetract();
        		robot.elevator.up();
        		
        	}else{
        		robot.elevator.up();
        	}
        }else if(codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) < -0.75){
        	robot.turret.set(0.0);
        	fsm.setGoalState(FSM.State.ELEVATOR_WAITING);
        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSY DOWN");
        }else{
        	
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Xbox.RIGHT_STICK_X) > 0.25) {
        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSX UP");        	
        	robot.turret.manualMove(-Util.turretSmoother(codriver.getButtonAxis(Xbox.RIGHT_STICK_X))*7);
        }else if(codriver.getButtonAxis(Xbox.RIGHT_STICK_X) < -0.25){
        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSX DOWN");
        	robot.turret.manualMove(Util.turretSmoother(codriver.getButtonAxis(Xbox.RIGHT_STICK_X))*7);
        }else{
        	
        }
        ///////////////////////////////////////////////
        if (codriver.getButtonAxis(Xbox.LEFT_STICK_Y) > 0.3) {
        	robot.logFile.writeToLog(System.currentTimeMillis() + " LSY UP");
        	robot.intake.manualMove(-codriver.getButtonAxis(Xbox.LEFT_STICK_Y)*2);
        }else if( codriver.getButtonAxis(Xbox.LEFT_STICK_Y) < -0.3){
        	robot.logFile.writeToLog(System.currentTimeMillis() + " LSY DOWN");
        	robot.intake.manualMove(-codriver.getButtonAxis(Xbox.LEFT_STICK_Y)*2);
        }else{
        	
        }
		///////////////////////////////////////////////
		if (codriver.getButtonAxis(Xbox.LEFT_STICK_X) > 0.3) {
			robot.logFile.writeToLog(System.currentTimeMillis() + " LSX UP");
		}else if( codriver.getButtonAxis(Xbox.LEFT_STICK_X) < -0.3){
			robot.logFile.writeToLog(System.currentTimeMillis() + " LSX DOWN");
		}else{
		
		}
        ///////////////////////////////////////////////
        if(codriver.leftCenterClick.isPressed()){
        	robot.logFile.writeToLog(System.currentTimeMillis() + " LCC PRESSED");
        	robot.elevator.down();        	
        }     
        ///////////////////////////////////////////////
        if(codriver.rightCenterClick.isPressed() || codriver.rightCenterClick.isHeld()) {
        	robot.logFile.writeToLog(System.currentTimeMillis() + " RCC PRESSED");
        	robot.turret.set(0.0);
        	robot.turret.setState(Turret.State.OFF);        	
        }
        if(codriver.getPOV() == 0){
        	robot.logFile.writeToLog(System.currentTimeMillis() + " GP UP PRESSED");
        	robot.shooter.preloader_forward();
        }
        if(codriver.getPOV() == 180){
        	robot.logFile.writeToLog(System.currentTimeMillis() + " GP DOWN PRESSED");
        	robot.shooter.preloader_reverse();
        }
    }
    
    public void driver() {
    	if (driver.getRawButton(1)){
    		robot.logFile.writeToLog(System.currentTimeMillis() + " DRIVER (1) PRESSED");
    		robot.dt.setGear(GEAR.HIGH);
    	}
    	if(driver.getRawButton(2)){
    		robot.logFile.writeToLog(System.currentTimeMillis() + " DRIVER (2) PRESSED");
    		robot.dt.setGear(GEAR.LOW); 
    		}
        if(driver.getRawButton(3)){
        	robot.logFile.writeToLog(System.currentTimeMillis() + " DRIVER (3) PRESSED");
        	robot.dt.setGear(GEAR.HIGH); 
        	}
//        robot.hanger.retractHanger();}
//        if(driver.getRawButton(4)){
//        	if(robot.hanger.armDeployed()){
//        		robot.hanger.retractArm(); 
//        	}else{
//        		robot.hanger.ptoUp();
//        	}
//        }
//        if(driver.getRawButton(5)){
////        	robot.hanger.extendHanger(); 
//        	robot.dt.setGear(GEAR.PTO);}
//        if(driver.getRawButton(6)){robot.dt.setGear(GEAR.PTO); }
//        if(driver.getRawButton(7)){robot.dt.disablePTO(); }    
        robot.dt.cheesyDrive(wheel.getX(), -driver.getY(), wheel.getLeftBumper() || wheel.getRightBumper());
    }
    public void update(){
    	codriver.run();
    	coDriver();
    	driver();
    }
    
}
