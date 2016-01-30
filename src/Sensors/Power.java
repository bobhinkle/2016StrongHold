package Sensors;

import Utilities.Constants;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class Power{

	private static Power instance = null;
	private PowerDistributionPanel pdp;
	
	public Power(){
		pdp = new PowerDistributionPanel();
	}
	
	public static Power getInstance(){
		if( instance == null )
            instance = new Power();
        return instance;
	}
	
	public double getPowerDraw(int port){
		return pdp.getCurrent(port);
	}
	
	public boolean checkStall(int port){
		return pdp.getCurrent(port) > Constants.POWER_STALL;
	}
}