import javafx.geometry.Point3D;
import java.util.*;
import java.io.*;
import java.lang.Thread;

/**
 * SPS_Main is the main class for the SMART Positional Systems Application.
 * The main method continually checks the users position and determines if the user is in danger.
 * 
 * @author GLASSBEARD
 * @version 0.0
 * @date 10-9-2015
 */
public class SPS_Main {
    /**
     * zoneArray stores an ArrayList of Zone objects which represent the possible danger zones
     */
    private static ArrayList<Zone> zoneArray = new ArrayList<Zone>();
    private static Point3D userPos = null;
    public static boolean running;
    private static ArrayList<HashMap<String, String>> values;
    
    private static long lastSent = -1;

    /** 
     * main method runs while loop to get user position and call check method with parameters user position and zoneArray
     */
    public static void main() throws IOException{ 
        setRunning(true);
        initialize();

        new Thread() {
            public void run() {
                //userPos = new Point3D(0,0,0);
                Grapher.main();
                while (running) {
                    try{parse();} catch(Exception e) {}
                    for(HashMap<String,String> h: values)
                    {
                        int t = Integer.parseInt(h.get("type"));
                        if(t == 0) //position of user
                        {
                            int x, y, z;
                            x = Integer.parseInt(h.get("x"));
                            y = Integer.parseInt(h.get("y"));
                            z = Integer.parseInt(h.get("z"));
                            userPos = new Point3D(x,y,z);
                        }
                        else if(t == 1) {
                            for(Zone z: zoneArray)
                            {
                                if(z.getType() == 1)
                                {
                                    z.setState(Boolean.parseBoolean(h.get("on")));
                                }
                            }
                        }
                        else {
                            for(Zone z: zoneArray)
                            {
                                if(z.getType() == 0)
                                {
                                    z.setState(Boolean.parseBoolean(h.get("open")));
                                }
                            }
                        }
                    }

                    Zone unsafeZone = check(userPos, zoneArray);
                    Grapher.graph(userPos, unsafeZone);
                    if (unsafeZone != null) {
                        if(System.currentTimeMillis() - lastSent > 30000) {
                            lastSent = System.currentTimeMillis();
                            Texter.send("6467501926", "You're unsafe at location " + userPos.toString() + "!");
                        }
                        //System.out.println("User is unsafe!  Zone in question is " + unsafeZone.toString() + " at location " + userPos.toString());
                    }
                    
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * takes parameters user position and ArrayList of Zone objects
     * calls danger method with user position and each zone in ArrayList
     * returns Zone that puts user in danger, null otherwise
     */

    public static Zone check (Point3D pos, ArrayList<Zone> zones) {
        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            System.out.println(z.toString());
            if (danger(pos, z))
                return z;
        }
        return null;
    }

    /**
     * takes parameters user position and single Zone object
     * based on type of Zone object, previous position relative to zone, and state of Zone
     * danger method returns true if user is in danger, false otherwise
     */
    private static boolean danger (Point3D pos, Zone z) {
        if (z.getType() == 0) /*Danger Zone e.g. window*/ {
            if(z.getDistance(pos) <= 5/*arbitrary value*/) {
                if (z.getState()) {
                    if (!z.getIsInZone()) {
                        z.setIsInZone(true);
                        z.setInitialTime(System.currentTimeMillis());
                        return false;
                    }
                    else
                        return System.currentTimeMillis() - z.getInitialTime() >= 2000;/*10 sec in milliseconds*/
                }
            }
            else
                z.setIsInZone(false);
        }
        else if (z.getType() == 1) /*Attention Zone e.g. stove*/ {
            if(z.getDistance(pos) >= 5/*arbitrary value*/) {
                if(z.getState()){
                    if(z.getIsInZone()){
                        z.setIsInZone(false);
                        z.setInitialTime(System.currentTimeMillis());
                        return false;
                    }
                    else
                        return System.currentTimeMillis() - z.getInitialTime() >= 2000;/*5 sec in milliseconds*/
                }
            }
            else
            {
                System.out.println("User is at Attention Zone, no risk is posed;");
                z.setIsInZone(true);
            }
        }
        else if (z.getType() == 2) /*Hazard Zone e.g. staircase*/{
            System.out.println(z.getDistance(pos));
            if (z.getDistance(pos) <= 5/*arbitrary value*/) {
                if (!z.getIsInZone()) {
                    z.setIsInZone(true);
                    z.setInitialTime(System.currentTimeMillis());
                    return false;
                }
                else
                    return System.currentTimeMillis() - z.getInitialTime() >= 5000;/*10 sec in milliseconds*/
            }
            else
            {
                z.setIsInZone(false);
                return false;
            }
        }
        return false;
    }

    private static void initialize() throws IOException{
        parse();
        ArrayList<Zone> zoneArray = new ArrayList<Zone>();
        for(HashMap<String,String> h: values) {
            int t = Integer.parseInt(h.get("type"));
            String sid = h.get("sid");
            int x, y, z;
            x = Integer.parseInt(h.get("posx"));
            y = Integer.parseInt(h.get("posy"));
            z = Integer.parseInt(h.get("posz"));
            String nm;
            Point3D p;
            p = new Point3D(x,y,z);
            int zt = -1;
            if(t == 0) {// position sensor, representation for staircase initialization and person's position over time
                //continue;
                zt = 2;
                nm = "Staircase";
            }
            else if(t == 1) {
                zt = 1;
                nm = "Stove";
            }
            else {
                zt = 0;
                nm = "Window";
            }
            Zone zone = new Zone(nm, sid, zt, p);
            zoneArray.add(zone);
        }
        SPS_Main.setZoneArray(zoneArray);
    }

    public static void parse () throws IOException
    {
        String s = ServerInterface.getRawData();
        //System.out.println(s);
        //HashMap<String, String>[] values = new HashMap<String, String>[3];
        values = new ArrayList<>();

        while (s.indexOf("\"sid") != -1) {
            HashMap<String, String> pairs = new HashMap<String, String>();
            int ind1 = s.indexOf("\"sid");
            int end1 = s.indexOf("}", ind1);
            String use = s.substring(ind1, end1).replaceAll("\"", "");
            String[] vals = use.split(",");
            for (int i = 0; i < vals.length; i++) {
                String[] p = vals[i].split(":");
                pairs.put(p[0], p[1]);
            }
            values.add(pairs);
            s = s.substring(end1);
        }
    }

    public static void setUserPosition (Point3D p) {
        userPos = p;
    }

    public static Point3D getUserPosition() {
        return userPos;
    }

    public static void setZoneArray (ArrayList<Zone> z) {
        zoneArray = z;
    }

    public static ArrayList<Zone> getZoneArray () {
        return zoneArray;
    }

    public static void setRunning (boolean b) {
        running = b;
    }
}
