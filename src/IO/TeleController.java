package IO;import ControlSystem.FSM;
import ControlSystem.RoboSystem;
import SubSystems.DriveTrain.GEAR;
import SubSystems.Elevator;
import SubSystems.Lights;
import SubSystems.Turret;
import SubSystems.Vision;
import Utilities.Constants;
import Utilities.Util;


public class TeleController
{
    public static final double STICK_DEAD_BAND = 0.2;
    public  Controller codriver;
    public Controller wheel;
    private Controller driver;
    private FSM fsm;
    private RoboSystem robot;
    private static TeleController instance = null;
    public TeleController(){
        driver = new Controller(0);
        driver.start();
        codriver  = new Controller(2);
        codriver.start();
        wheel = new Controller(1);
        wheel.start();
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
 //       		robot.logFile.writeToLog(System.currentTimeMillis() + " A  PRESSED OR HELD");
        	}else{
        		System.out.println("ButtonHold" + codriver.aButton.buttonHoldTime());
        	}
        }else if(codriver.aButton.isReleased()){
        	robot.intake.intake_stop();
        	robot.intake.setAngle(Constants.INTAKE_WAIT_FOR_GRAB);
 //       	robot.logFile.writeToLog(System.currentTimeMillis() + " A  RELEASED");
        }        
        //////////////////////////////////////////
        if(codriver.bButton.isPressed()){
        	fsm.setGoalState(FSM.State.STOW);
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " B  PRESSED");
        }
        ////////////////////////////////////////
        if(codriver.xButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_CLOSE);
        	robot.shooter.set(Constants.SHOOTER_LOAD_UP); 
        	robot.turret.setShot(Turret.Shot.CLOSE);
   //     	robot.logFile.writeToLog(System.currentTimeMillis() + " X  PRESSED");
        }
        if(codriver.xButton.buttonHoldTime() > 250){
        	fsm.setGoalState(FSM.State.BATTER_SHOT);
        	robot.shooter.set(Constants.SHOOTER_LOAD_UP); 
        	robot.turret.setShot(Turret.Shot.CLOSE);
   //     	robot.logFile.writeToLog(System.currentTimeMillis() + " X  HELD");
        }
        ///////////////////////////////////////
        if(codriver.yButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_FAR);
        	robot.shooter.set(Constants.SHOOTER_LOAD_UP); 
        	robot.turret.setShot(Turret.Shot.FAR);
  //      	robot.logFile.writeToLog(System.currentTimeMillis() + " Y  PRESSED");
        }
        /////////////////////////////////////////////
       if(codriver.rightTrigger.isHeld() && codriver.rightTrigger.buttonHoldTime() > 250 && !robot.shooter.isFiring()){
    	   if(robot.shooter.shooterOn()){
    		   if(robot.shooter.getSpeed() > Constants.SHOOTER_CLOSE_SHOT - 200){
		    	   if(!robot.vision.isTargetSeen() || Math.abs(Vision.getAngle()) < 2.0){
		    		 robot.turret.stop();
		           	 robot.shooter.fire();
		           	 robot.logFile.writeToLog("FORCE FIRE2");
		           	if(robot.turnTh != null)
                		robot.turnTh.kill();
		    	   }   
    		   }
    		}else{
    			robot.shooter.setPresetSpeed(0.0);
    		}
        }
        //////////////////////////////////////////////////////////////////// 
        if(codriver.leftBumper.isPressed()){ 
        	robot.intake.intake_reverse();
        	if(robot.elevator.status() == Elevator.Direction.UP){
        		robot.shooter.preloader_reverse();
        	}
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " LB  PRESSED");
        }
        //////////////////////////////////
        if(codriver.rightBumper.isPressed()) {
        	robot.intake.intake_forward();
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " RB  PRESSED");
        }
        ///////////////////////////////////////////////////////
        if(codriver.leftTrigger.isPressed() && !robot.shooter.isFiring()){
        	if(robot.shooter.shooterOn()){	        	 
	        	if(robot.vision.isTargetSeen()){	        		
	        		robot.turret.setState(Turret.State.SPOTTED);
	        	}else{
	        		robot.turret.setState(Turret.State.TRACKING);
	        	}			
        	}else{
        		robot.shooter.setPresetSpeed(0.0);   
        	}
        	if(!robot.turnRunning)
        		robot.turnToHeading(robot.nav.getRawHeading(), 20,true);
        }
        if(codriver.leftTrigger.buttonHoldTime() > 250){
 //       	robot.logFile.writeToLog(System.currentTimeMillis() + " LT  HELD && RT HELD");
        	robot.shooter.setPresetSpeed(robot.shooter.visionAdd()); 
        	if(robot.shooter.shooterOn()){            		
        		robot.visionFiring();
        	}else{
        		robot.shooter.setPresetSpeed(robot.shooter.visionAdd()); 
        	}
        }
        //////////////////////////////////////////////////////
        if(codriver.backButton.isPressed() || codriver.backButton.isHeld()){  // stop all      
        	robot.intake.intake_stop();
        	robot.shooter.stop();
        	robot.shooter.preloader_stop();
        	robot.shooter.killFire();
        	robot.shooter.killPreloaderIntake();
        	robot.shooter.killBallSucker();
        	robot.shooter.clearFire();
        	robot.lights.setState(Lights.MODE.WAITING);
        	if(robot.turnTh != null)
        		robot.turnTh.kill();
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " BACK PRESSED");
        }
        ////////////////////////////////////////////////////////
        if(codriver.startButton.isPressed()){
        	fsm.setGoalState(FSM.State.LOW_BAR);
        	robot.logFile.writeToLog(System.currentTimeMillis() + " START PRESSED");
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Controller.RIGHT_STICK_Y) > 0.75) {
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSY UP");
        	if(fsm.getCurrentState() == FSM.State.STOW || fsm.getCurrentState() == FSM.State.STOW_READY){
        		robot.shooter.hoodRetract();
        		robot.elevator.up();
        		
        	}else{
        		robot.elevator.up();
        	}
        }else if(codriver.getButtonAxis(Controller.RIGHT_STICK_Y) < -0.75){
        	robot.turret.set(0.0);
        	fsm.setGoalState(FSM.State.ELEVATOR_WAITING);
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSY DOWN");
        }else{
        	
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Controller.RIGHT_STICK_X) > 0.25) {
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSX UP");        	
        	robot.turret.manualMove(-Util.turretSmoother(codriver.getButtonAxis(Controller.RIGHT_STICK_X))*3);
        }else if(codriver.getButtonAxis(Controller.RIGHT_STICK_X) < -0.25){
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSX DOWN");
        	robot.turret.manualMove(Util.turretSmoother(codriver.getButtonAxis(Controller.RIGHT_STICK_X))*3);
        }else{
        	
        }
        ///////////////////////////////////////////////
        if (codriver.getButtonAxis(Controller.LEFT_STICK_Y) > 0.3) {
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " LSY UP");
        	robot.intake.manualMove(-codriver.getButtonAxis(Controller.LEFT_STICK_Y)*2);
        }else if( codriver.getButtonAxis(Controller.LEFT_STICK_Y) < -0.3){
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " LSY DOWN");
        	robot.intake.manualMove(-codriver.getButtonAxis(Controller.LEFT_STICK_Y)*2);
        }else{
        	
        }
		///////////////////////////////////////////////
		if (codriver.getButtonAxis(Controller.LEFT_STICK_X) > 0.3) {
			robot.turret.manualMove(-Util.turretSmoother(codriver.getButtonAxis(Controller.LEFT_STICK_X))*10);
///			robot.logFile.writeToLog(System.currentTimeMillis() + " LSX UP");
		}else if( codriver.getButtonAxis(Controller.LEFT_STICK_X) < -0.3){
			robot.turret.manualMove(Util.turretSmoother(codriver.getButtonAxis(Controller.LEFT_STICK_X))*10);
//			robot.logFile.writeToLog(System.currentTimeMillis() + " LSX DOWN");
		}else{
		
		}
        ///////////////////////////////////////////////
        if(codriver.leftCenterClick.isPressed() || codriver.rightCenterClick.isHeld()){
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " LCC PRESSED");
        	robot.turret.set(0.0);
        	robot.turret.setState(Turret.State.OFF);    	
        }     
        ///////////////////////////////////////////////
        if(codriver.rightCenterClick.isPressed() || codriver.rightCenterClick.isHeld()) {
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " RCC PRESSED");
        	robot.turret.set(0.0);
        	robot.turret.setState(Turret.State.OFF);        	
        }
        if(codriver.getPOV() == 0){
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " GP UP PRESSED");
        	robot.turret.set(0.0);
        	robot.turret.setState(Turret.State.OFF);   
        }
        if(codriver.getPOV() == 90){
        	robot.turret.setState(Turret.State.OFF);  
        	robot.turret.set(-45);
        }
        if(codriver.getPOV() == 270){
        	robot.turret.setState(Turret.State.OFF);  
        	robot.turret.set(45);
        }
        if(codriver.getPOV() == 180){
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " GP DOWN PRESSED");
        	robot.shooter.preloader_reverse();
        }
    }
    
    public void driver() {
    	if (driver.aButton.isPressed()){
//    		robot.logFile.writeToLog(System.currentTimeMillis() + " DRIVER (1) PRESSED");
    		robot.dt.setGear(GEAR.HIGH);
    	}
    	if(driver.bButton.isPressed()){
//    		robot.logFile.writeToLog(System.currentTimeMillis() + " DRIVER (2) PRESSED");
    		robot.dt.setGear(GEAR.LOW); 
    		}
        if(driver.xButton.isPressed()){
        	robot.intake.intake_stop();
        	robot.shooter.stop();
        	robot.shooter.preloader_stop();
        	robot.shooter.killFire();
        	robot.shooter.killPreloaderIntake();
        	robot.shooter.killBallSucker();
        	robot.shooter.clearFire();
        	if(robot.turnTh != null)
        		robot.turnTh.kill();
        }
        if(driver.getPOV() == 0 || wheel.bButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_CLOSE);
        	robot.shooter.set(Constants.SHOOTER_LOAD_UP); 
        	robot.turret.setShot(Turret.Shot.CLOSE);
        }
        if(driver.getPOV() == 180 || wheel.yButton.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_FAR);
        	robot.shooter.set(Constants.SHOOTER_LOAD_UP); 
        	robot.turret.setShot(Turret.Shot.FAR);
        }
        if(driver.leftBumper.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_CLOSE);
        	robot.shooter.set(Constants.SHOOTER_LOAD_UP); 
        	robot.turret.setShot(Turret.Shot.CLOSE);
        }
        if(driver.rightBumper.isPressed()){
        	fsm.setGoalState(FSM.State.SHOOTER_FAR);
        	robot.shooter.set(Constants.SHOOTER_LOAD_UP); 
        	robot.turret.setShot(Turret.Shot.FAR);
        }
		///////////////////////////////////////////////////////
		if(driver.yButton.isPressed() && !robot.shooter.isFiring()){
			if(robot.shooter.shooterOn()){	        	 
				if(robot.vision.isTargetSeen()){
					robot.turret.setState(Turret.State.SPOTTED);
		}else{
			robot.turret.setState(Turret.State.TRACKING);
		}			
		}else{
			robot.shooter.setPresetSpeed(robot.shooter.visionAdd());   
		}
		if(!robot.turnRunning)
			robot.turnToHeading(robot.nav.getRawHeading(), 20,true);
		}
		if(driver.yButton.buttonHoldTime() > 250){
		//       	robot.logFile.writeToLog(System.currentTimeMillis() + " LT  HELD && RT HELD");
			robot.shooter.setPresetSpeed(robot.shooter.visionAdd()); 
			if(robot.shooter.shooterOn()){		
				robot.visionFiring();
		}else{
				robot.shooter.setPresetSpeed(robot.shooter.visionAdd()); 
			}
		}
//        robot.hanger.retractHanger();}
        
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
        robot.dt.cheesyDrive(wheel.getX(), -driver.getY(), wheel.leftBumper.isPressed() || wheel.leftBumper.isHeld());
    }
    public void update(){
    	coDriver();
    	driver();
    }
    
}
