import java.util.List;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.*;
/**
 * Write a description of class Parser here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Parser
{
    public static void parse () throws IOException
    {
        String s = ServerInterface.getRawData();
        //System.out.println(s);
        //HashMap<String, String>[] values = new HashMap<String, String>[3];
        ArrayList<HashMap<String, String>> values = new ArrayList<>(); 

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
        
        for (int i = 0; i < values.size(); i++) {
            System.out.println(values.get(i));
        }

        /*ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<User> list = mapper.readValue(s, new TypeReference<List<User>>() {});

        if (list.size() >= 2) {
        String[] sensorIDArray = new String[list.size()-1];
        for(int i = 0; i < sensorIDArray.length; i++)
        {
        String u = list.get(i+1).data.toString();
        sensorIDArray[i] = u.substring(u.length()-37, u.length()-1);
        }

        System.out.println(Arrays.toString(sensorIDArray));
        }

        for(User u: list)
        {
        System.out.println(u.data.toString());
        }
         */
        //System.out.println(list);
        //window = home.window.open; after semicolon
        //stove = home.smoke.alarm_on; after semicolon
        //position = vehicle.trip.arrived
        //System.out.println(ServerInterface.getData(s,"home.smoke.alarm_on"));
    }

    public static class User{
        public Map<String, Object> data;
    }
}

