package IO;

import edu.wpi.first.wpilibj.*;

public class ThrustMasterWheel extends Joystick
{
    public ThrustMasterWheel(int usb)   { super(usb); }

    public double  getXAxis()         { return getRawAxis(1); }
    public boolean getXButton()       { return getRawButton(1); }
    public boolean getAButton()       { return getRawButton(2); }
    public boolean getBButton()       { return getRawButton(3); }
    public boolean getYButton()       { return getRawButton(4); }
    public boolean getLeftBumper()    { return getRawButton(5); }
    public boolean getRightBumper()   { return getRawButton(6); }
    public boolean getUpLeftButton()  { return getRawButton(7); }
    public boolean getUpRightButton() { return getRawButton(8); }
    public boolean getSelectButton()  { return getRawButton(9); }
    public boolean getStartButton()   { return getRawButton(10); }
}
