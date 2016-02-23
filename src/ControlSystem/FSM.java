package ControlSystem;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class FSM {
	
	public enum State{
    	DEFAULT, INIT, PRE_TOTE,
    	
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
            goalState = goal;
        }
    }
    
    public State getCurrentState() {
        return currentState;
    }
    private void stateComplete(State state){
        prevState = state;
    }
    public State previousState(){
    	return prevState;
    }
    public void nextState(){
    	switch(previousState()){
    	
		
		default:
			setGoalState(FSM.State.PRE_TOTE);
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
	            
	            case DEFAULT:
	            	SmartDashboard.putString("FSM_STATE", "WAITING");
	            	break;
			default:
				break;
	        }
	        robot.intake.update();
	        robot.shooter.update();
	        robot.turret.update();
//	        robot.elevator.update();
	    }
    }
}