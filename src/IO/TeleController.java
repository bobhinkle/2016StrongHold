package IO;import ControlSystem.FSM;
import ControlSystem.RoboSystem;

/** Handles the input from an xbox controller in order to calculate what the
 *  forebar angles and claw state should be. It is designed to keep the logic for
 *  deciding what to do apart from the logic for how to do it
 *
 * @author Robotics
 */ 
public class TeleController
{
    public static final double STICK_DEAD_BAND = 0.2;

    private Xbox codriver,driver;
    private FSM fsm;
    private RoboSystem robot;
    private static TeleController instance = null;
    public TeleController(){
        driver = new Xbox(0);
        driver.init();
        codriver  = new Xbox(1);
        codriver.init();
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
        if(codriver.aButton.isPressed()){
        	robot.intake.setAngle(160);
//        	robot.enablePTO();
        }
        //////////////////////////////////////////
        if(codriver.bButton.isPressed()){
        	robot.disablePTO();
        }
        ////////////////////////////////////////
        if(codriver.xButton.isPressed()){
        	robot.intake.setAngle(200);
 //       	robot.shooter.goTo(7500);
        }
        ///////////////////////////////////////
        if(codriver.yButton.isPressed()){
        	robot.intake.setAngle(270);
 //       	robot.shooter.goTo(10000);
        }
        /////////////////////////////////////////////

        if(codriver.rightTrigger.isPressed()){ 
        	robot.intake.preloader_forward();
        }
        //////////////////////////////////
        if(codriver.rightBumper.isPressed()) {
        	robot.intake.intake_forward();
        }
        ///////////////////////////////////////////////////////
        if(codriver.leftTrigger.isPressed()){
        	robot.intake.preloader_reverse();
        }
        //////////////////////////////////////////////////////////////////// 
        if(codriver.leftBumper.isPressed()){ 
        	robot.intake.intake_reverse();
        }
        //////////////////////////////////////////////////////
        if(codriver.backButton.isPressed()){  // stop all      
        	robot.intake.intake_stop();
        	robot.intake.arm_stop();
        	robot.shooter.stop();
        	robot.intake.preloader_stop();
        	robot.turret.stop();
        }
        ////////////////////////////////////////////////////////
        if(codriver.startButton.isPressed()){
        	
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) > 0.2) {
        	robot.elevator.setCurrent(0.5*codriver.getButtonAxis(Xbox.RIGHT_STICK_Y));
        }else if(codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) < -0.2){
        	robot.elevator.setCurrent(0.5*codriver.getButtonAxis(Xbox.RIGHT_STICK_Y));
        }else{
        	robot.elevator.setCurrent(0);
        }
        ///////////////////////////////////////////////
        if (codriver.getButtonAxis(Xbox.LEFT_STICK_Y) > 0.3) {
        	
        }else if( codriver.getButtonAxis(Xbox.LEFT_STICK_Y) < -0.3){
        	
        }else{
        	
        }
        ///////////////////////////////////////////////
        if(codriver.leftCenterClick.isPressed()){
        	robot.turret.set(15);
        }     
        ///////////////////////////////////////////////
        if(codriver.rightCenterClick.isPressed()) {
        	robot.turret.set(-15);
        }
        if(codriver.getPOV() == 0){
        	robot.shooter.set(4000);;
        }
        if(codriver.getPOV() == 180){
        	robot.shooter.set(18000*2/3/2);;
        }
    }
    
    public void driver() {
    	
    	if(driver.aButton.isPressed()){       
    		robot.enablePTO();
        }else if(driver.bButton.isPressed()){  
        	robot.disablePTO();
        }else if(driver.xButton.isPressed()){
        	
        }else if(driver.yButton.isPressed()){
        	
        }else{
        	
        }        
    	
        //////////////////////////////////
        if(driver.rightTrigger.isPressed()) {
        	robot.dt.highGear();
        }
		//////////////////////////////////
		if(driver.leftTrigger.isPressed()) {
			robot.dt.lowGear();
		}
        /////////////////////////////////////////////////////
        if(driver.leftBumper.isPressed()){
        	
        }
        ///////////////////////////////////////////////////////
        if(driver.rightBumper.isPressed()){
        	
        }
        
        //////////////////////////////////////////////////////
        if(driver.backButton.isHeld()){  // 
        }
        ////////////////////////////////////////////////////////
        if(driver.startButton.isPressed()){
        	
        }
        if(driver.leftCenterClick.isPressed()){
        	
        }
        if(driver.rightCenterClick.isPressed()){
        	
        }
        if(driver.getPOV() == 0){
        	
        }
        robot.dt.directArcadeDrive(driver.getButtonAxis(Xbox.LEFT_STICK_X), -driver.getButtonAxis(Xbox.RIGHT_STICK_Y));
        
    }
    public void update(){
    	codriver.run();
    	driver.run();
    	coDriver();
    	driver();
    }
    
}
