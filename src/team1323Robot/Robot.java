
package team1323Robot;


import ControlSystem.FSM;
import ControlSystem.RoboSystem;
import IO.TeleController;
import SubSystems.DistanceController;
import SubSystems.DriveTrain.GEAR;
import Utilities.Constants;
import SubSystems.Elevator;
import SubSystems.Shooter;
import SubSystems.TurnController;
import SubSystems.Vision;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
    RoboSystem robot;
    private TeleController controllers;
    final String defaultAuto = "Default";
    final String customAuto = "Straight No Shoot";
    SendableChooser chooser;
    private FSM fsm;
    private turnThread turnTh;
    private distanceThread distTh;
    public TurnController turn; 
    public DistanceController dist;
    private double distanceGoal;
    public Robot() {
        robot = RoboSystem.getInstance();
        controllers = TeleController.getInstance();
    }
    
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto modes", chooser);
        fsm = FSM.getInstance();
        fsm.start();
        turnTh = new turnThread();
        turn = TurnController.getInstance();
        dist = DistanceController.getInstance();
        distTh = new distanceThread(false);
    }

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the if-else structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomous() {
    	
    	String autoSelected = (String) chooser.getSelected();
//		String autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		robot.dt.setGear(GEAR.HIGH);
		fsm.setGoalState(FSM.State.LOW_BAR);
		Timer.delay(0.5);
		robot.nav.resetRobotPosition(0, 0, 0, true);
    	switch(autoSelected) {
    	case customAuto:
    		//Distance - Heading - Max Speed - Timout - Tolerance 
    		driveDistanceHoldingHeading(125, 0, 0.5, 4.0, 4.0, false, 0);
    		while(!dist.onTarget()){
    			Timer.delay(0.1);
    		}    		
    		robot.shooter.setHoodState(Shooter.HoodStates.FAR_SHOT);
    		fsm.setGoalState(FSM.State.SHOOTER_FAR);
    		while(robot.elevator.status() != Elevator.Direction.UP){
    			Timer.delay(0.1);
    		}
    		
    		if(Vision.isTargetSeen()){ //
    			robot.turret.set(robot.turret.getAngle() - Vision.getAngle());
    		}else{
    			robot.turret.set(-30.0);
    		}
    		robot.shooter.set(Constants.SHOOTER_FAR_SHOT);
    		double visionAngle = 0;
    		while(!robot.shooter.onTarget() && isAutonomous()){
    			visionAngle = Vision.getAngle();
    			if(Vision.isTargetSeen() && visionAngle < -20 && visionAngle > -40){ //
        			robot.turret.set(robot.turret.getAngle() - Vision.getAngle());
        		}else{
        			robot.turret.set(-35.0);
        		}
    			Timer.delay(0.1);
    		}
    		robot.shooter.fire();
    		Timer.delay(4);
    		robot.shooter.stop();
    		robot.turret.set(0);
            break;
    	case defaultAuto:
    	default:
    		
            break;
    	}
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	robot.Init();
        while (isOperatorControl() && isEnabled()) {
        	controllers.update();  
            Timer.delay(0.01);		// wait for a motor update time
        }
    }
    public void disabledInit(){
  //  	robot.vision.gripProcess.destroy();
    }
    /**
     * Runs during test mode
     */
    public void test() {
    }
    public void turnToHeading(double heading, double timeout){
        robot.nav.resetRobotPosition(0, 0, 0,false);
        if(turn == null){
            turn = TurnController.getInstance();
        }
        turn.reset();
        turn.setGoal(heading,timeout);
        turnTh = new turnThread();
        turnTh.start();
    }
    private class turnThread extends Thread{
        
        public void run(){
            try {
                while(!turn.onTarget() && isAutonomous()){
                    turn.run();
                    Timer.delay(0.01);
                }
                System.out.println("done");
                robot.dt.directDrive(0, 0);
            }catch(Exception e){
                System.out.println("crash" + e.toString());
            }            
        }
    }
    public double driveDistanceHoldingHeading(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed,double breakTime){                
        dist.resetDistance();
        double startDistance = 0;
        double distanceChange = distance + startDistance;
        System.out.println("DD: " + startDistance + " " + distanceChange + " " + distance);
        dist.reset();
        dist.setGoal(distanceChange, maxSpeed,heading,timeout,tol,holdSpeed);
        distTh.run();
        return robot.nav.getDistance() - startDistance;
    }
    public void driveDistanceHoldingHeadingThreaded(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed){                
        dist.reset();
        dist.setGoal(distance, maxSpeed,heading,timeout,tol,holdSpeed);
        turn.setGoal(0, 0.1);
        distTh = new distanceThread(true);
        distTh.start();
    }
    private class distanceThread extends Thread{
        private boolean keeprunning = false;
        public distanceThread(boolean keeper){
            keeprunning = keeper;
        }
        public void run(){
                if(!keeprunning){
                    while(!dist.onTarget() && isAutonomous()){
                        dist.run();
                        Timer.delay(0.01);
                    }
                }else{
                    while(isAutonomous()){
                        dist.run();
                        dist.setSetpoint(distanceGoal);
                        Timer.delay(0.01);
                    }
                }
                System.out.println("done");
                robot.dt.directDrive(0, 0);
                       
        }
    }
}
