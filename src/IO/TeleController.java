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

    private Xbox codriver,driver,driver2;
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
        	
        }
        //////////////////////////////////////////
        if(codriver.bButton.isPressed()){
        	
        }
        ////////////////////////////////////////
        if(codriver.xButton.isPressed()){
        	
        }
        ///////////////////////////////////////
        if(codriver.yButton.isPressed()){
        	
        }
        /////////////////////////////////////////////

        if(codriver.rightTrigger.isPressed()){ 
        	
        }
        //////////////////////////////////
        if(codriver.rightBumper.isPressed()) {
        	
        }
        ///////////////////////////////////////////////////////
        if(codriver.leftTrigger.isPressed()){
        	
        }
        //////////////////////////////////////////////////////////////////// 
        if(codriver.leftBumper.isPressed()){ 
        	
        }
        //////////////////////////////////////////////////////
        if(codriver.backButton.isPressed()){  // stop all      
        	
        }
        ////////////////////////////////////////////////////////
        if(codriver.startButton.isPressed()){
        	
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) > 0.15) {
        	
        }else if(codriver.getButtonAxis(Xbox.RIGHT_STICK_Y) < -0.2){
        	
        }else{
        	
        }
        ///////////////////////////////////////////////
        if (codriver.getButtonAxis(Xbox.LEFT_STICK_Y) > 0.3) {
            
        }else if( codriver.getButtonAxis(Xbox.LEFT_STICK_Y) < -0.3){
        	
        }else{
        	
        }
        ///////////////////////////////////////////////
        if(codriver.leftCenterClick.isPressed()){
        	
        }     
        ///////////////////////////////////////////////
        if(codriver.rightCenterClick.isPressed()) {
        	
        }
        if(codriver.getPOV() == 0){
        	
        }
        if(codriver.getPOV() ==180){
        	
        }
    }
    
    public void driver() {
    	
    	if(driver.aButton.isPressed()){       
    		
        }else if(driver.bButton.isPressed()){  
        	
        }else if(driver.xButton.isPressed()){
        	
        }else if(driver.yButton.isPressed()){
        	
        }else{
        	
        }        
    	
        //////////////////////////////////
        if(driver.rightTrigger.isPressed()) {
        	
        }
        /////////////////////////////////////////////////////
        if(driver.leftBumper.isPressed()){
        	robot.intakeReverse();
        }
        ///////////////////////////////////////////////////////
        if(driver.rightBumper.isPressed()){
        	robot.intakeForward();
        }
        
        //////////////////////////////////////////////////////
        if(driver.backButton.isHeld()){  // 
        	robot.intakeStop();
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
