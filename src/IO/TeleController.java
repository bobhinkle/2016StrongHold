package IO;import ControlSystem.FSM;
import ControlSystem.RoboSystem;
import SubSystems.DriveTrain.GEAR;
import SubSystems.Elevator;
import SubSystems.Turret;
import SubSystems.Vision;
import Utilities.Constants;
import Utilities.Util;
import edu.wpi.first.wpilibj.Joystick;


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
        	}else{
        		System.out.println("ButtonHold" + codriver.aButton.buttonHoldTime());
        	}
        }else if(codriver.aButton.isReleased()){
        	robot.intake.intake_stop();
        	robot.intake.setAngle(Constants.INTAKE_WAIT_FOR_GRAB);
        }        
        //////////////////////////////////////////
        if(codriver.bButton.isPressed()){
        	fsm.setGoalState(FSM.State.STOW);
        }
        ////////////////////////////////////////
        if(codriver.xButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_CLOSE);
        }
        if(codriver.xButton.buttonHoldTime() > 500){
        	fsm.setGoalState(FSM.State.WALL_SHOT);
        }
        ///////////////////////////////////////
        if(codriver.yButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_FAR);
        }
        /////////////////////////////////////////////
        if(codriver.rightTrigger.isPressed()){ 
        	robot.turret.stop();
        	robot.shooter.fire();
        }
        if(codriver.rightTrigger.isHeld()){
        	if(robot.turret.visionError() < 5 && robot.vision.isTargetSeen()){
        		robot.turret.stop();
            	robot.shooter.fire();
        	}
        }
        //////////////////////////////////////////////////////////////////// 
        if(codriver.leftBumper.isPressed()){ 
        	robot.intake.intake_reverse();
        	if(robot.elevator.status() == Elevator.Direction.UP){
        		robot.shooter.preloader_reverse();
        	}
        }
        //////////////////////////////////
        if(codriver.rightBumper.isPressed()) {
        	robot.intake.intake_forward();
        }
        ///////////////////////////////////////////////////////
        if(codriver.leftTrigger.isPressed()){
        	robot.shooter.setPresetSpeed();        	
        }
        if(codriver.leftTrigger.buttonHoldTime() > 500){
        	if(robot.elevator.status() == Elevator.Direction.UP){
        		if(robot.vision.isTargetSeen() && Vision.getAngle() < 2 && Vision.getAngle() > -2 && robot.shooter.onTarget()){
        			robot.turret.setState(Turret.State.OFF);
        			robot.shooter.fire();
        		}
        	}
        }
        //////////////////////////////////////////////////////
        if(codriver.backButton.isPressed()){  // stop all      
        	robot.intake.intake_stop();
        	robot.shooter.stop();
        	robot.shooter.preloader_stop();
        	robot.hanger.ptoStop();
        	robot.shooter.killFire();
        }
        ////////////////////////////////////////////////////////
        if(codriver.startButton.isPressed()){
        	fsm.setGoalState(FSM.State.LOW_BAR);
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) > 0.75) {
        	if(fsm.getCurrentState() == FSM.State.STOW || fsm.getCurrentState() == FSM.State.STOW_READY){
        		robot.shooter.hoodRetract();
        		robot.elevator.up();
        	}else{
        		robot.elevator.up();
        	}
        }else if(codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) < -0.75){
        	robot.turret.set(0.0);
        	fsm.setGoalState(FSM.State.ELEVATOR_WAITING);
        }else{
        	
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Xbox.RIGHT_STICK_X) > 0.25) {
        	robot.turret.setState(Turret.State.HOLDING);
        	robot.turret.manualMove(-Util.turretSmoother(codriver.getButtonAxis(Xbox.RIGHT_STICK_X))*7);
        }else if(codriver.getButtonAxis(Xbox.RIGHT_STICK_X) < -0.25){
        	robot.turret.setState(Turret.State.HOLDING);
        	robot.turret.manualMove(Util.turretSmoother(codriver.getButtonAxis(Xbox.RIGHT_STICK_X))*7);
        }else{
        	
        }
        ///////////////////////////////////////////////
        if (codriver.getButtonAxis(Xbox.LEFT_STICK_Y) > 0.3) {
        	robot.intake.manualMove(-codriver.getButtonAxis(Xbox.LEFT_STICK_Y)*2);
        }else if( codriver.getButtonAxis(Xbox.LEFT_STICK_Y) < -0.3){
        	robot.intake.manualMove(-codriver.getButtonAxis(Xbox.LEFT_STICK_Y)*2);
        }else{
        	
        }
		///////////////////////////////////////////////
		if (codriver.getButtonAxis(Xbox.LEFT_STICK_X) > 0.3) {
		
		}else if( codriver.getButtonAxis(Xbox.LEFT_STICK_X) < -0.3){
		
		}else{
		
		}
        ///////////////////////////////////////////////
        if(codriver.leftCenterClick.isPressed()){
        	robot.elevator.down();
        }     
        ///////////////////////////////////////////////
        if(codriver.rightCenterClick.isPressed()) {
        	robot.turret.set(0.0);
        	robot.turret.setState(Turret.State.OFF);
        }
        if(codriver.getPOV() == 0){
        	robot.shooter.preloader_forward();
        }
        if(codriver.getPOV() == 180){
        	robot.shooter.preloader_reverse();
        }
    }
    
    public void driver() {
    	if (driver.getRawButton(1)){
    		robot.dt.setGear(GEAR.LOW);
    	}
    	if(driver.getRawButton(2)){robot.dt.setGear(GEAR.HIGH); }
        if(driver.getRawButton(3)){robot.dt.setGear(GEAR.HIGH); }
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
