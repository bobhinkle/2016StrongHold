
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
import SubSystems.Turret;
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
    final String dontRun    = "right side";
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
        chooser.addObject("rightTest", dontRun);
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
		robot.nav.resetPitch();
		robot.nav.resetRobotPosition(0, 0, 0, true);
    	switch(autoSelected) {
    	case customAuto:
    		//Distance - Heading - Max Speed - Timout - Tolerance 
    		System.out.println("Step1");
    		driveDistanceHoldingHeading(152, 0, 0.57, 4, 2.0, false, 0);
    		while(!dist.onTarget() && isAutonomous()){
    			System.out.println("WAITING1");
    			Timer.delay(0.1);
    		}    		    	
    		System.out.println("DONE");    		
    		fsm.setGoalState(FSM.State.AUTO_SHOT);
    		while(robot.elevator.status() != Elevator.Direction.UP && isAutonomous()){
    			System.out.println("Step2");
    			Timer.delay(0.1);
    		}    		
    		robot.shooter.preloader_forward();
    		
    		robot.turret.set(-45.0);
    		Timer.delay(0.25);
    		robot.shooter.preloader_stop();
    		System.out.println("Step3");
    		Timer.delay(0.25);
    		System.out.println("Step4");
    		robot.turret.setState(Turret.State.SINGLE);
    		Timer.delay(0.5);
    		System.out.println("Step5");
    		robot.turret.setState(Turret.State.HOLDING);
    		robot.turret.stop();
    		robot.shooter.setGoal(Constants.SHOOTER_AUTON_SIDE_SHOT);
    		robot.shooter.setPresetSpeed(Shooter.Status.AUTO);
    		while(!robot.shooter.onTarget() && isAutonomous() ){  
    			System.out.println("Step6");
    			Timer.delay(0.1);
    		}
    		System.out.println("Step7");
    		robot.shooter.fire();
    		Timer.delay(3.0);
    		robot.shooter.stop();
    		robot.turret.set(0);
       		robot.elevator.down();
    		robot.dt.setGear(GEAR.LOW);
    		fsm.setGoalState(FSM.State.LOW_BAR);
    		Timer.delay(0.5);
    		driveDistanceHoldingHeading(-166, 0, 0.55, 4, 2.0, false, 0);
    		while(!dist.onTarget() && isAutonomous()){
    			System.out.println("WAITING1");
    			Timer.delay(0.1);
    		}    	    	
            break;
    	case dontRun:
    		
    		robot.shooter.setHoodState(Shooter.HoodStates.CLOSE_SHOT);
    		fsm.setGoalState(FSM.State.AUTO_SHOT);
    		while(robot.elevator.status() != Elevator.Direction.UP && isAutonomous()){
    			System.out.println("Step2");
    			Timer.delay(0.1);
    		}    		
    		robot.turret.set(45.0);
    		System.out.println("Step3");
    		Timer.delay(1.0);
    		System.out.println("Step4");
    		robot.turret.setState(Turret.State.SINGLE);
    		Timer.delay(1.0);
    		System.out.println("Step5");
    		robot.turret.setState(Turret.State.HOLDING);
    		robot.turret.stop();
    		robot.shooter.setGoal(Constants.SHOOTER_AUTON_SIDE_SHOT);
    		robot.shooter.setPresetSpeed(Shooter.Status.AUTO);
    		while(!robot.shooter.onTarget() && isAutonomous() ){  
    			System.out.println("Step6");
    			Timer.delay(0.1);
    		}
    		System.out.println("Step7");
    		robot.shooter.fire();
    		Timer.delay(4);
    		robot.shooter.stop();
    		robot.turret.set(0);
            break;
    	case defaultAuto:
    	default:
    		driveDistanceHoldingHeading(160, 0, 0.52, 4, 2.0, false, 0);
    		while(!dist.onTarget()){
    			System.out.println("WAITING1");
    			Timer.delay(0.1);
    		}    		    	
    		System.out.println("DONE");
    		robot.shooter.setHoodState(Shooter.HoodStates.CLOSE_SHOT);
    		fsm.setGoalState(FSM.State.SHOOTER_CLOSE);
    		while(robot.elevator.status() != Elevator.Direction.UP && isAutonomous()){
    			System.out.println("Step2");
    			Timer.delay(0.1);
    		}  
            break;
    	}
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	robot.Init();
    	robot.turret.setState(Turret.State.HOLDING);
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
