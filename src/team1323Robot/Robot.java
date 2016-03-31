
package team1323Robot;


import ControlSystem.FSM;
import ControlSystem.RoboSystem;
import IO.TeleController;
import SubSystems.DistanceController;
import SubSystems.DriveTrain.GEAR;
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
    final String testRun    = "right side";
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
        chooser.addDefault("TestRun", defaultAuto);
        chooser.addObject("LBSHOTBACKUNDER", customAuto);
        chooser.addObject("LOWBARNOSHOT", testRun);
        SmartDashboard.putData("Auto modes", chooser);
        fsm = FSM.getInstance();
        fsm.start();
        turnTh = new turnThread();
        turn = TurnController.getInstance();
        dist = DistanceController.getInstance();
        distTh = new distanceThread(false);        
    }

    public void autonomous() {
    	robot.vision.setAutonomousTracking(true);
    	String autoSelected = (String) chooser.getSelected();
//		String autoSelected = testRun;
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
    		driveDistanceHoldingHeading(152, 0, 0.57, 4, 2.0, false, 0,0);
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
    		robot.shooter.setShot(Shooter.Shot.AUTO);
    		robot.shooter.setPresetSpeed();
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
    		driveDistanceHoldingHeading(20, 0, 0.55, 4, 2.0, false, 0,0);
    		while(!dist.onTarget() && isAutonomous()){
    			System.out.println("WAITING1");
    			Timer.delay(0.1);
    		}    	    	
            break;
    	case testRun:    		
    		robot.turret.setState(Turret.State.OFF);
    		driveDistanceHoldingHeading(100, 0, 0.57, 2, 2.0, true, 0,0);
    		fsm.setGoalState(FSM.State.AUTO_SHOT);  
    		driveDistanceHoldingHeading(200, 0, 0.8, 2, 2.0, false, 0,-51);
    		Timer.delay(2.0);
    		while(robot.elevator.status() != Elevator.Direction.UP && isAutonomous()){
    			System.out.println("Step2");
    			Timer.delay(0.1);
    		}     		
    		robot.shooter.setShot(Shooter.Shot.AUTO);
    		robot.shooter.setPresetSpeed();
    		robot.turret.set(-51.0);
    		Timer.delay(1.0);
    		if(Math.abs(Vision.getAngle()) < 5 ){
	     		robot.turret.setState(Turret.State.SINGLE);    		
	    		Timer.delay(1.0);
    		}
    		while(!robot.shooter.onTarget() && isAutonomous()){
    			Timer.delay(0.1);
    		}
    		if(robot.vision.isTargetSeen() && Vision.getAngle() < 5  && Vision.getAngle() > -5 && isAutonomous()){
    			robot.turret.setState(Turret.State.OFF);
    			robot.turret.stop();
    			robot.shooter.fire();
    			Timer.delay(2.0);
    		}else{
    			Timer.delay(0.5);
    			robot.turret.setState(Turret.State.OFF);
    			robot.turret.stop();
    			robot.shooter.fire();
    			Timer.delay(2.0);
    		}

    		fsm.setGoalState(FSM.State.LOW_BAR);
    		driveDistanceHoldingHeading(200, 0, 0.68, 2, 2.0, false, 0,0); //120
    		if(robot.elevator.status() == Elevator.Direction.DOWN){
	    		driveDistanceHoldingHeading(200, 0, 0.55, 2, 2.0, false, 0,0); //0
	    		turnToHeading(90,1.2);
	    		while(isAutonomous()){
	    			Timer.delay(0.1);
	    		}
    		}
            break;
    	case defaultAuto:
    	default:
    		driveDistanceHoldingHeading(160, 0, 0.52, 4, 2.0, false, 0,0);
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
    	robot.vision.setAutonomousTracking(false);
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
    public double driveDistanceHoldingHeading(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed,double breakTime,double turretAngle){                
        dist.resetDistance();
        double startDistance = 0;
        double distanceChange = distance + startDistance;
        System.out.println("DD: " + startDistance + " " + distanceChange + " " + distance);
        dist.reset();
        dist.setGoal(distanceChange, maxSpeed,heading,timeout,tol,holdSpeed);
        distTh.setTurretAngle(turretAngle);
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
        private double turretAngle = 0.0;
        private boolean setTurret = true;
        public distanceThread(boolean keeper){
            keeprunning = keeper;
        }
        public void setTurretAngle(double angle){
        	turretAngle = angle;
        }
        public void run(){
                if(!keeprunning){
                    while(!dist.onTarget() && isAutonomous()){
                        dist.run();
                        if(robot.elevator.status() == Elevator.Direction.UP && setTurret){
                        	robot.turret.set(turretAngle);
                        	setTurret = false;
                        }
                        Timer.delay(0.01);
                    }
                }else{
                    while(isAutonomous()){
                        dist.run();
                        dist.resetZero();
                        dist.setSetpoint(distanceGoal);
                        Timer.delay(0.01);
                    }
                }
                System.out.println("done");
                robot.dt.directDrive(0, 0);
                       
        }
    }
}
