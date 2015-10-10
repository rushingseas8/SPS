import javafx.geometry.Point3D;

/**
 * Write a description of class User here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class User
{
    
    private String sensorID;
    private Point3D pos;
    
    public User (String s, Point3D p) {
        sensorID = s;
        pos = p;
    }

    public void setPosition (Point3D p) {
        pos = p;
    }
    
    public Point3D getPosition() {
        return pos;
    }
}
