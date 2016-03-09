package ControlSystem;

import java.util.Timer;
import java.util.TimerTask;

import SubSystems.Elevator;
import SubSystems.Shooter;
import SubSystems.Turret;
import Utilities.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class FSM {
	
	public enum State{
    	DEFAULT, INIT, LOW_BAR, 
    	INTAKE, INTAKE_READY,STOW, ELEVATOR_WAITING,ELEVATOR_LOWER, STOW_READY,
    	SHOOTER_CLOSE, SHOOTER_FAR, SHOOTER_WAITING,SHOOTER_READY,PTO
    	
    }
	private RoboSystem robot;
	private static FSM instance = null;
	private volatile State currentState = State.INIT;
    private volatile State goalState = State.DEFAULT;
    private volatile State prevState = State.DEFAULT;
    private static final int K_READING_RATE = 200;
    // synchronized access object
    private final Timer mTimer = new Timer();
	public static FSM getInstance()
    {
        if( instance == null )
            instance = new FSM();
        return instance;
    }
	public void start() {
        synchronized (mTimer) {
            mTimer.schedule(new InitTask(), 0);
        }
    }
	private class InitTask extends TimerTask {
        @Override
        public void run() {
            while (true) {
                try {
                	SmartDashboard.putString("FSM", "STARTED");
                    robot = RoboSystem.getInstance();
                    break;
                } catch (Exception e) {
                    System.out.println("Gyro failed to initialize: " + e.getMessage());
                    synchronized (mTimer) {
                        mTimer.schedule(new InitTask(), 500);
                    }
                }
            }
            synchronized (mTimer) {
                mTimer.schedule(new UpdateTask(), 0, (int) (1000.0 / K_READING_RATE));
            }
        }
    }
    public FSM() {
    	
    }
    
    public void setGoalState(State goal) {
        if(currentState == goal){
            currentState = State.DEFAULT;
            goalState = goal;
        }else{
        	prevState = currentState;
            goalState = goal;
        }
    }
    
    public State getCurrentState() {
        return currentState;
    }
    private void stateComplete(State state){
        currentState = state;
    }
    public State getPreviousState(){
    	return prevState;
    }
    public void nextState(){
    	switch(getPreviousState()){
    	
		
		default:
			setGoalState(FSM.State.INIT);
			break;
		}
    }
    private class UpdateTask extends TimerTask {
	    public void run(){ 
	        switch(goalState){
	            case INIT:
	                SmartDashboard.putString("FSM_STATE", "INIT");
	                stateComplete(State.INIT);
	                break;
	            case LOW_BAR:
	            	robot.turret.set(0.0);
	            	robot.shooter.set(0);
	            	robot.shooter.preloader_stop();
	            	robot.intake.setAngle(Constants.INTAKE_LOW_BAR_ANGLE);
	            	robot.shooter.setHoodState(Shooter.HoodStates.DOWN);
	            	stateComplete(FSM.State.LOW_BAR);
	            	setGoalState(State.ELEVATOR_WAITING);
	            	break;
	            case INTAKE:
	            	robot.intake.setAngle(Constants.INTAKE_GRAB_BALL_ANGLE);
	            	robot.turret.set(0.0);
	            	robot.shooter.set(0.0);
	            	robot.shooter.preloader_stop();
	            	robot.shooter.setHoodState(Shooter.HoodStates.UP);
	            	stateComplete(FSM.State.INTAKE);
	            	setGoalState(State.INTAKE_READY);
	            	break;
	            case INTAKE_READY:
	            	stateComplete(FSM.State.INTAKE_READY);
	            	SmartDashboard.putString("FSM_STATE", "INTAKE_READY");
	            	break;
	            case STOW:
	            	robot.intake.setAngle(Constants.INTAKE_STOW_ANGLE);
	            	robot.turret.set(0.0);
	            	robot.shooter.set(0.0);
	            	robot.shooter.preloader_stop();
	            	robot.shooter.setHoodState(Shooter.HoodStates.FAR_SHOT);
	            	stateComplete(FSM.State.STOW);
	            	setGoalState(FSM.State.STOW_READY);
	            	break;
	            case ELEVATOR_WAITING:
	            	if(robot.turret.safeToLower()){
	            		robot.elevator.down();
	            		stateComplete(FSM.State.ELEVATOR_WAITING);
	            		setGoalState(State.STOW_READY);
	            	}
	            	SmartDashboard.putString("FSM_STATE", "STOW WAITING");
	            	break;
	            case STOW_READY:
	            	stateComplete(FSM.State.STOW_READY);
	            	SmartDashboard.putString("FSM_STATE", "STOW READY");
	            	break;
	            case SHOOTER_CLOSE:
	            	robot.intake.setAngle(Constants.INTAKE_GRAB_BALL_ANGLE);
//	            	if(robot.elevator.status() != Elevator.Direction.UP)
	            		robot.elevator.up();
	            	robot.intake.intake_stop();
	            	robot.shooter.setHoodState(Shooter.HoodStates.CLOSE_SHOT);
	            	robot.shooter.setPresetSpeed(Shooter.Status.CLOSE);
	            	robot.turret.setState(Turret.State.TRACKING);
	            	stateComplete(FSM.State.SHOOTER_CLOSE);
	            	setGoalState(State.SHOOTER_READY);
	            	break;
	            case SHOOTER_FAR:
	            	robot.intake.setAngle(Constants.INTAKE_GRAB_BALL_ANGLE);
//	            	if(robot.elevator.status() != Elevator.Direction.UP)
	            		robot.elevator.up();
	            	robot.intake.intake_stop();
	            	robot.shooter.setHoodState(Shooter.HoodStates.FAR_SHOT);
	            	robot.shooter.setPresetSpeed(Shooter.Status.FAR);
	            	robot.turret.setState(Turret.State.TRACKING);
	            	stateComplete(FSM.State.SHOOTER_FAR);
	            	setGoalState(State.SHOOTER_WAITING);
	            	break;
	            case SHOOTER_WAITING:
	            	if(robot.elevator.status() == Elevator.Direction.UP){
	            		setGoalState(State.SHOOTER_READY);
	            	}SmartDashboard.putString("FSM_STATE", "SHOOTER WAITING");
	            	break;
	            case SHOOTER_READY:
	            	stateComplete(FSM.State.SHOOTER_READY);
	            	SmartDashboard.putString("FSM_STATE", "SHOOTER READY");
	            	break;
	            case DEFAULT:
	            	SmartDashboard.putString("FSM_STATE", "WAITING");
	            	break;
			default:
				break;
	        }
	        try{
		        robot.intake.update();
			}catch(Exception e){
				        	
			 }
	        try{
		        robot.shooter.update();
			}catch(Exception e){
				        	
			}
	        try{
		        robot.turret.update();
			}catch(Exception e){
				        	
			 }
	        try{
		        robot.elevator.update();
			}catch(Exception e){
				        	
			}
		    try{
		    	robot.hanger.update();
	        }catch(Exception e){
	        	
	        }
	    }
    }
}