package IO;import SubSystems.Elevator;
import SubSystems.Turret;
import SubSystems.Vision;
import SubSystems.DriveTrain.GEAR;
import Utilities.Constants;
import Utilities.Util;
import ControlSystem.FSM;
import ControlSystem.RoboSystem;
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
    private boolean tracked = false;
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
        if(codriver.xButton.buttonHoldTime() > 200){
        	robot.turret.setState(Turret.State.TRACKING);
        }
        ///////////////////////////////////////
        if(codriver.yButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_FAR);
        }
        if(codriver.yButton.isHeld()){
        	robot.turret.setState(Turret.State.TRACKING);
        }
        /////////////////////////////////////////////
        if(codriver.rightTrigger.isPressed()){ 
        	robot.turret.stop();
        	robot.shooter.fire();
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
        	robot.turret.setState(Turret.State.HOLDING);
        	if(fsm.getPreviousState()==FSM.State.SHOOTER_CLOSE){
        		robot.shooter.set(Constants.SHOOTER_CLOSE_SHOT);
        	}else if(fsm.getPreviousState()==FSM.State.SHOOTER_FAR){
        		robot.shooter.set(Constants.SHOOTER_FAR_SHOT);
        	}else{
        		System.out.println("Shooting position not set");
        	}
        }
        if(codriver.leftTrigger.buttonHoldTime() > 200){
        	if(robot.elevator.status() == Elevator.Direction.UP){
        		robot.turret.setState(Turret.State.HOLDING);
        	}
        }else{
			tracked = false;
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
        	robot.turret.manualMove(-Util.turretSmoother(codriver.getButtonAxis(Xbox.RIGHT_STICK_X))*10);
        }else if(codriver.getButtonAxis(Xbox.RIGHT_STICK_X) < -0.25){
        	robot.turret.setState(Turret.State.HOLDING);
        	robot.turret.manualMove(Util.turretSmoother(codriver.getButtonAxis(Xbox.RIGHT_STICK_X))*10);
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
        }
        if(codriver.getPOV() == 0){
        	robot.shooter.preloader_forward();
        }
        if(codriver.getPOV() == 180){
        	robot.shooter.preloader_reverse();
        }
    }
    
    public void driver() {
    	if (driver.getRawButton(1)){robot.dt.setGear(GEAR.LOW);
    	robot.hanger.retractHanger();}
    	if(driver.getRawButton(2)){robot.dt.setGear(GEAR.NUETRAL); }
        if(driver.getRawButton(3)){robot.dt.setGear(GEAR.HIGH); 
        robot.hanger.retractHanger();}
        if(driver.getRawButton(4)){
        	if(robot.hanger.armDeployed()){
        		robot.hanger.retractArm(); 
        	}else{
        		robot.hanger.ptoUp();
        	}
        }
        if(driver.getRawButton(5)){
        	robot.hanger.extendHanger(); 
        	robot.dt.setGear(GEAR.PTO);}
        if(driver.getRawButton(6)){robot.dt.setGear(GEAR.PTO); }
        if(driver.getRawButton(7)){robot.dt.disablePTO(); }    
        robot.dt.cheesyDrive(wheel.getX(), -driver.getY(), wheel.getLeftBumper() || wheel.getRightBumper());
    }
    public void update(){
    	codriver.run();
    	coDriver();
    	driver();
    }
    
}
