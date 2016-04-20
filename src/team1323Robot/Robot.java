
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
import Utilities.Constants;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
    RoboSystem robot;
    private TeleController controllers;
    final String defaultAuto = "low bar";
    final String rough_rock_ramps    = "rough terrain";
    final String cdf        = "cdf";
    final String pc		   = "Portcullis";
    
    final String defaultPosition = "1";
    final String pos2            = "2";
    final String pos3			 = "3";
    final String pos4  			 = "4";
    final String pos5			 = "5";
    
    SendableChooser defenseType,position;
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
    public static enum AUTO{
    	CDF, LOWBAR,PC,ROUGH_ROCK_RAMPS
    }
    public static enum AUTO_TARGET_SELECT{
    	LEFT,RIGHT,BIGGEST
    }
    public void robotInit() {
        defenseType = new SendableChooser();
        defenseType.addDefault("Low Bar", defaultAuto);
        defenseType.addObject("Rough/Rock Wall", rough_rock_ramps);
        defenseType.addObject("CDF", cdf);
        defenseType.addObject("Portcullis", pc);
        SmartDashboard.putData("Auto Select1", defenseType);
        
        position = new SendableChooser();
        position.addDefault("1", defaultPosition);
        position.addObject("2", pos2);
        position.addObject("3", pos3);
        position.addObject("4", pos4);
        position.addObject("5", pos5);
        SmartDashboard.putData("Position Mode1", position);
        
        fsm = FSM.getInstance();
        fsm.start();
        turnTh = new turnThread();
        turn = TurnController.getInstance();
        dist = DistanceController.getInstance();
        distTh = new distanceThread(false);        
    }

    public void executeAuto(AUTO autoSelect, int position){
    	boolean keepGoing = false;
    	double overallDistance = 0.0;
    	double turretAngle    = 0.0;
    	AUTO_TARGET_SELECT ats = null;
    	Shooter.Shot shot = Shooter.Shot.FAR;
    	switch(position){
    	case 1:
    		overallDistance = 152;
			turretAngle = -45;
			ats = AUTO_TARGET_SELECT.BIGGEST;
			shot = Shooter.Shot.FAR;
    		break;
		case 2:
			overallDistance = 272;
			turretAngle = -40;
			ats = AUTO_TARGET_SELECT.BIGGEST;
			shot = Shooter.Shot.FAR;
			break;
		case 3:
			overallDistance = 125;
			turretAngle = -35;
			ats = AUTO_TARGET_SELECT.BIGGEST;
			shot = Shooter.Shot.FAR;
			break;
		case 4:
			overallDistance = 140;
			turretAngle = 5;
			ats = AUTO_TARGET_SELECT.BIGGEST;
			shot = Shooter.Shot.FAR;
			break;
		case 5:
			overallDistance = 235;
			turretAngle = 20;
			ats = AUTO_TARGET_SELECT.BIGGEST;
			shot = Shooter.Shot.CLOSE;
			break;
		}
    	switch(ats){
    	case LEFT:
    		robot.vision.setBias(Vision.BIAS.LEFT);
    		break;
    	case RIGHT:
    		robot.vision.setBias(Vision.BIAS.RIGHT);
    		break;
    	case BIGGEST:
    		robot.vision.setBias(Vision.BIAS.BIGGEST);
    		break;
    	}
    	
    	switch(autoSelect){
    	case CDF:
    		fsm.setGoalState(FSM.State.CDF_CROSS);
    		driveDistanceHoldingHeading(40, 0, 0.8, 1.5, 2.0, false, 0,turretAngle);
    		while(!dist.onTarget() && isAutonomous()){
    			System.out.println("WAITING1");
    			Timer.delay(0.1);
    		}  
    		robot.intake.setAngle(Constants.INTAKE_LOW_BAR_ANGLE);
    		while(isAutonomous() && robot.intake.getAngle() > Constants.INTAKE_LOW_BAR_ANGLE + 5){
    			Timer.delay(0.01);
    		}
    		robot.turret.set(turretAngle);
    		driveDistanceHoldingHeading(overallDistance, 1.5, 0.8, 5, 5.0, false, 0,turretAngle);
    		Timer.delay(1.0);
    		robot.turret.setState(Turret.State.SPOTTED);
    		keepGoing = true;
    		while(isAutonomous() && keepGoing){
    			keepGoing = !autoFireCommand(shot,1.25);
    		}
    		break;
    	case LOWBAR:
    		//Distance - Heading - Max Speed - Timout - Tolerance 
    		System.out.println("Step1");
    		driveDistanceHoldingHeading(overallDistance, 0, 0.60, 4, 2.0, true, 0,turretAngle);
    		fsm.setGoalState(FSM.State.AUTO_SHOT);
    		driveDistanceHoldingHeading(50, 0, 0.65, 4, 2.0, false, 0,turretAngle);   
    		robot.turret.setState(Turret.State.SPOTTED);
    		System.out.println("DONE");   		
    		robot.shooter.preloader_forward();    		
 //   		robot.turret.set(-45.0);
    		Timer.delay(0.25);
    		robot.shooter.preloader_stop();
    		System.out.println("Step3");
    		Timer.delay(0.25);
    		keepGoing = true;
    		while(isAutonomous() && keepGoing){
    			keepGoing = !autoFireCommand(shot,1.15);
    		}
    		Timer.delay(2);
    		robot.shooter.stop();
    		robot.turret.set(0);       		
       		Timer.delay(0.5);
    		robot.dt.setGear(GEAR.LOW);
    		fsm.setGoalState(FSM.State.LOW_BAR);
    		Timer.delay(0.5);
    		driveDistanceHoldingHeading(-100, 0, 0.55, 4, 2.0, false, 0,0);    		 	    	
    		break;
    	case ROUGH_ROCK_RAMPS:
    		fsm.setGoalState(FSM.State.CDF_CROSS);
    		driveDistanceHoldingHeading(overallDistance, 0, 0.8, 7, 4.0, false, 0,turretAngle);
    		robot.intake.setAngle(Constants.INTAKE_LOW_BAR_ANGLE);
    		robot.turret.set(turretAngle);
    		Timer.delay(2.0);
    		robot.turret.setState(Turret.State.SPOTTED);  
    		keepGoing = true;
    		while(isAutonomous() && keepGoing){
    			keepGoing = !autoFireCommand(shot,0.8);
    		}
    		break;
    	case PC:
    		
    		break;
    	default:
    		
    		break;
    	}
    }
    public void autonomous() {
    	robot.vision.setAutonomousTracking(true);
    	String autoSelected = (String) defenseType.getSelected();
    	String positionSelected = (String) position.getSelected();
    	int pos = Integer.parseInt(positionSelected);
		System.out.println("Auto selected: " + autoSelected);
		robot.nav.resetPitch();
		robot.nav.resetRobotPosition(0, 0, 0, true);
		switch(autoSelected){
			case defaultAuto:
				executeAuto(AUTO.LOWBAR,pos);
				break;
			case rough_rock_ramps:
				executeAuto(AUTO.ROUGH_ROCK_RAMPS,pos);
				break;
			case cdf: 
				executeAuto(AUTO.CDF,pos);
				break;
			case pc:
				executeAuto(AUTO.PC,pos);
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
    	robot.vision.setBias(Vision.BIAS.BIGGEST);
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
    public boolean autoFireCommand(Shooter.Shot shot, double error){
    	if(Math.abs(Vision.getAngle()) < error){ 
			robot.turret.setState(Turret.State.OFF);
			robot.turret.stop();
			if(shot == Shooter.Shot.CLOSE){
				robot.shooter.setShot(Shooter.Shot.CLOSE);
			}else{
				robot.shooter.setShot(Shooter.Shot.FAR);
			}    				
    		robot.shooter.setPresetSpeed(0.0);
			Timer.delay(2);
    		System.out.println("AUTO 1");
    		if(robot.shooter.onTarget()){
    			System.out.println("AUTO 2");
    			robot.shooter.fire();
    		}else{
    			System.out.println("AUTO 3");
    			Timer.delay(.5);
    			if(robot.shooter.onTarget(500)){
    				System.out.println("AUTO 4");
    				robot.shooter.fire();
    			}    	    			
    		}        			
			return true;
		}
		else{
			System.out.println("AUTO 5");
			robot.turret.setState(Turret.State.SPOTTED);    
			return true;
		}
    }
    public void turnToHeading(double heading, double timeout){
        robot.nav.resetRobotPosition(0, 0, 0,false);
        if(turn == null){
            turn = TurnController.getInstance();
        }
        turn.reset();
        turn.setGoal(heading,timeout,false);
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
        turn.setGoal(0, 0.1,false);
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

