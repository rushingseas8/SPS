import javafx.geometry.Point3D;
import java.util.*;
import java.io.*;

/**
 * Tests the fuction of the SPS_Main class
 * 
 * @author GLASSBEARD
 * @version 0.0
 * @date 10-9-2015
 */
public class SPS_Runner {
    private static ArrayList<HashMap<String, String>> values;

    /**
     * Constructor for objects of class SPS_Tester
     */
    public static void main() throws IOException{
        boolean run = true;
        initialize();
        SPS_Main.main();
        Grapher.main();
        while(run)
        {
            parse();
            for(HashMap<String,String> h: values)
            {
                int t = Integer.parseInt(h.get("type"));
                if(t == 0) //position of user
                {
                    int x, y, z;
                    x = Integer.parseInt(h.get("posx"));
                    y = Integer.parseInt(h.get("posy"));
                    z = Integer.parseInt(h.get("posz"));
                    SPS_Main.setUserPosition(new Point3D(x,y,z));
                }
                else if(t == 1) {
                    ArrayList<Zone> a = SPS_Main.getZoneArray();
                    for(Zone z: a)
                    {
                        if(z.getType() == 1)
                        {
                            z.setState(Boolean.parseBoolean(h.get("state")));
                        }
                    }
                }
                else {
                    ArrayList<Zone> a = SPS_Main.getZoneArray();
                    for(Zone z: a)
                    {
                        if(z.getType() == 0)
                        {
                            z.setState(Boolean.parseBoolean(h.get("state")));
                        }
                    }
                }
            }
            Thread clock = new Thread();
            try {
                clock.sleep(4000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
}
