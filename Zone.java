import javafx.geometry.Point3D;
import java.io.*;
/**
 * The Zone object stores the name and position of the object the user wants to save,
 * using a String for the name and a Point3D object for the position.
 * 
 * @author GLASSBEARD
 * @version 0.0
 * @date 10-9-2015
 */

public class Zone {
    /**
     * 0: Danger Zone:    implies that if user is too close to zone, send notification to app
     *                      e.g. user(mainly geared towards children for this instance) 
     *                      is too close to an open window, for longer than short period of time
     *                      output would be to notify homeowner/family member
     * 1: Attention Zone: implies dangerous situation that needs attention, 
     *                      user must be in close proximity to zone
     *                      e.g. stove is on, user is away from stove for extended period of time
     *                      output should be to turn stove off automatically
     * 2: Hazard Zone:    implies user is disabled near a risk-likely zone
     *                      e.g. user is at bottom of the staircase for extended period of time
     *                      output would be to notify homeowner/family member
     */
    private String name;
    private String sensorID;
    private int type;
    private boolean state;
    private boolean isInZone;//Precondition: user is not in 2 zones at one instance of time
    private long initialTime;
    private Point3D pos;

    public Zone(String nm, String id, int t, Point3D p) {
        name = nm;
        sensorID = id;
        type = t;
        pos = p;
        isInZone = false;
        state = false;
        initialTime = -1;
    }
    
    public String getSensorID() {
        return sensorID;
    }
    
    public String getName() {
        return name;
    }
    
    public int getType() {
        return type;
    }
    
    public Point3D getPosition() {
        return pos;
    }
    
    public void setState(boolean b) {
        state = b;
    }
    
    public boolean getState() {
        return state;
    }
    
    public void setIsInZone(boolean b) {
        isInZone = b;
    }
    
    public boolean getIsInZone() {
        return isInZone;
    }
    
    public void setInitialTime(long time) {
        initialTime = time;
    }
    
    public long getInitialTime() {
        return initialTime;
    }
    
    public double getDistance(Point3D other) {
        return pos.distance(other);
    }
    
    public String toString() {
        String s = "";
        s += pos + " " + state;
        return s;
    }
}