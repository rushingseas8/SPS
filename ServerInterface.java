import java.io.*;
import java.util.*;

public class ServerInterface {
    private static final String OS = System.getProperty("os.name");

    private static String groupId = "e9960d3a-acd2-435b-b374-b573192cff7d";
    private static HashMap<String, String> sensors = new HashMap<String, String>();
    private static HashMap<String, Integer> sensorType = new HashMap<String, Integer>();
    private static HashMap<String, String> sensorValues = new HashMap<String, String>();
    private static HashMap<String, Integer[]> positions = new HashMap<String, Integer[]>();
    private static String positionID = genSensor();

    private static int[] sensorNums = {0, 0, 0};

    /**
     * The testing method. Doesn't have an explicit purpose yet.
     */
    public static void main(String[] args) {
        //while(true) {
        //A little bluej hack that clears everything in the console window.
        //System.out.print('\f');

        //Gets the raw data
        //String data = getRawData();

        //Prints out the data in a neat format
        //System.out.println(parse(data));

        //try {Thread.sleep(2000);}catch(InterruptedException i) {}
        //}

        //Prints out one of the variables' data, in a neat format
        //System.out.println(parse(getData(data, getValidVariableNames(data)[0])));

        System.out.println("Posting: " + post("tripSummaryUpload", "{\"test\":100,\"test2\":28}"));
        try{Thread.sleep(1000);}catch(Exception e){}
        System.out.println("Data: " + parse(getRawData()));
    }

    public static void pushSensors() {
        clearAllData();
        //Push sensor positions first
        //post(groupId, positionID, formatPositions());
        Object[] keys = sensors.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            int state = -1;
            String currKey = (String)(keys[i]);
            String currId = sensors.get(currKey);
            state = sensorType.get(currId);
            System.out.println(currId);
            String data = "";
            String s = "\"sid\":\""+currId + "\",";
            if (state == 0) {
                String[] vals = sensorValues.get(currId).split(",");
                data += "{\"tripSummaryUpload\":{" + s + "\"x\":" + vals[0] +",\"y\":" + vals[1] + ",\"z\":" + vals[2] + ",";
            }
            else if (state == 1)
                data += "{\"homeSmokeAlarmOn\":{" + s + "\"on\":" + sensorValues.get(currId) + ",";
            else if (state == 2)
                data += "{\"homeWindowOpen\":{" + s + "\"open\":" + sensorValues.get(currId) + ",";
            data += "\"type\":" + Integer.toString(state) + ",";
            Integer[] tpos = positions.get(currId);
            data += "\"posx\":" + tpos[0]+",";
            data += "\"posy\":" + tpos[1]+",";
            data += "\"posz\":" + tpos[2];
            data += "}}";

            //System.out.println(sensorValues.get(currKey));
            System.out.println(data);
            post(groupId, currId, data);
        }
    }

    public static String formatPositions() {
        String res = "{\"homeFireAlarmOn\":{";
        Object[] keys = sensorValues.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String currKey = (String)(keys[i]);
            res += "\"" + currKey + "\":{";
            Integer[] point = positions.get(currKey);
            res += "\"x\":" + Integer.toString(point[0]) + ",";
            res += "\"y\":" + Integer.toString(point[1]) + ",";
            res += "\"z\":" + Integer.toString(point[2]) + "},";
        }
        res = res.substring(0, res.length()-1) + "}}";
        return res;
    }

    public static void updatePositionSensor(String sensorName, int x, int y, int z) {
        String id = sensors.get(sensorName);
        /*String inp = "{\"tripSummaryUpload\":{\"x\":" + Integer.toString(x) +",\"y\":"
        + Integer.toString(y) + ",\"z\":" + Integer.toString(z) + "}}";*/
        String inp = Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z);
        sensorValues.put(id, inp);
    }

    public static void updateStoveSensor(String sensorName, boolean on) {
        String id = sensors.get(sensorName);
        //String inp = "{\"homeSmokeAlarmOn\":{\"on\":" + Boolean.toString(on) + "}}";
        String inp = Boolean.toString(on);
        sensorValues.put(id, inp);
    }

    public static void updateWindowSensor(String sensorName, boolean open) {
        String id = sensors.get(sensorName);
        //String inp = "{\"homeWindowOpen\":{\"open\":" + Boolean.toString(open) + "}}";
        String inp = Boolean.toString(open);
        sensorValues.put(id, inp);
    }

    /*
    0 = Position sensor
    1 = stove
    2 = window
     */
    public static String addSensor(int type, int x, int y, int z) {
        if (type > 2 || type < 0) {
            System.out.println("ERROR: Sensor must be between 0 and 2");
            return null;
        }
        String name = "";
        if (type == 0) {
            name += "positionSensor";
        }
        else if (type == 1) {
            name += "stoveSensor";
        }
        else if (type == 2) {
            name += "windowSensor";
        }
        name += "" + sensorNums[type];
        sensorNums[type] += 1;

        String id = genSensor();
        sensorType.put(id, type);

        Integer[] p = {x, y, z};

        positions.put(id, p);
        sensors.put(name, id);

        return name;
    }

    public static void addSensor(int type) {
        if (type > 2 || type < 0) {
            System.out.println("ERROR: Sensor must be between 0 and 2");
            return;
        }
        String name = "";
        if (type == 0) {
            name += "positionSensor";
        }
        else if (type == 1) {
            name += "stoveSensor";
        }
        else if (type == 2) {
            name += "windowSensor";
        }
        name += "" + sensorNums[type];
        sensorNums[type] += 1;

        String id = genSensor();

        Integer[] p = {0,0,0};

        positions.put(id, p);
        sensors.put(name, id);
    }

    public static String genSensor() {
        String s = executeCommand("curl -X POST --header Content-Type: application/json --header Accept: application/json" + 
                " http://a6.cfapps.io/groups/" + groupId + "/sensors");
        //System.out.println(s);
        return getValue(s, "sensorId");
    }

    public static String getValue(String rawData, String name) {
        int indexOfData = rawData.indexOf(name) + name.length() + 3;
        int endInd = rawData.indexOf("\"", indexOfData);
        return rawData.substring(indexOfData, endInd);
    }

    public static String post(String groupID, String sensorID, String data) {
        //System.out.println("curl -X POST --header Content-Type: application/json --header Accept: application/json -d "
        //    + data + " http://a6.cfapps.io/groups/" + groupID + "/sensors/" + sensorID + "/data");
        return executeCommand("curl -X POST --header Content-Type: application/json --header Accept: application/json -d "
            + data + " http://a6.cfapps.io/groups/" + groupID + "/sensors/" + sensorID + "/data");
    }

    /**
     * This method will clear all of the server's data.
     */
    public static void clearAllData() {
        executeCommand("curl -X DELETE --header Accept: */* http://glassbeard.cfapps.io/data");
    }

    /**
     * Posts data to the default group/sensor group.
     * 
     * @param varName The name of the variable we are modifying. This needs to
     *  be one of the variables provided: tripSummaryUpload, vehicleHealthUpload,
     *  homeWindowOpen, homeWindowClosed, homeGarageOpen, homeGarageClosed,
     *  homeSmokeAlarmOn, homeSmokeAlarmOff, homeSmokeAlarmBatteryLow,
     *  homeFireAlarmOn, homeFireAlarmOff, homeWaterSensorAlarmOn,
     *  or homeWaterSensorAlarmOff. Do not add extra quotes.
     * @param data The data that will be associated with "varName". This
     *  can be any valid JSON, and if it is invalid, this command will not
     *  be guaranteed to have predictable behavior. 
     */
    public static String post(String varName, String data) {
        return post("e9960d3a-acd2-435b-b374-b573192cff7d", "23393514-dbc2-4424-9e28-a1ccc588ece5", varName, data);
    }

    /**
     * Posts the given data, "data" to "varName", under the group "groupID" and the sensor "sensorID".
     * 
     * @param groupID The groupID to which we are posting.
     * @param sensorID The sensorID to which we are posting.
     * @param varName The name of the variable we are modifying. This needs to
     *  be one of the variables provided: tripSummaryUpload, vehicleHealthUpload,
     *  homeWindowOpen, homeWindowClosed, homeGarageOpen, homeGarageClosed,
     *  homeSmokeAlarmOn, homeSmokeAlarmOff, homeSmokeAlarmBatteryLow,
     *  homeFireAlarmOn, homeFireAlarmOff, homeWaterSensorAlarmOn,
     *  or homeWaterSensorAlarmOff. Do not add extra quotes.
     * @param data The data that will be associated with "varName". This
     *  can be any valid JSON, and if it is invalid, this command will not
     *  be guaranteed to have predictable behavior. 
     */
    public static String post(String groupID, String sensorID, String varName, String data) {
        data = data.replaceAll("\n", "");
        data = data.replaceAll(" ", "");
        String postData = "{\"" + varName + "\":" + data + "}";
        return executeCommand("curl -X POST --header Content-Type: application/json --header Accept: application/json -d "
            + postData + " http://a6.cfapps.io/groups/" + groupID + "/sensors/" + sensorID + "/data");
    }

    /**
     * Parses the raw data given into somewhat neat looking JSON parsing.
     * 
     * @param data The raw data to be parsed.
     * @return A (hopefully!) neatly formatted String built using JSON
     *  parsing, based on "data". If "data" is null, this will return null.
     */
    public static String parse(String data) {
        if(data == null)
            return null;

        //The string we will return after adding spacing and indenting.
        String parsedData = ""; 

        //The amount of tabs we need to indent, based on the level of braces we're in.
        int indent = 0; 

        //Now, we go through the data character by character to add spacing as needed.
        for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == '{' || data.charAt(i) == '[') { 
                //The brace, a newline, and then indent the next line.
                parsedData += data.charAt(i) + "\n" + indent(++indent); 
            } else if(data.charAt(i) == '}' || data.charAt(i) == ']') { 
                //A newline, then the indentation, and then the brace.
                parsedData += "\n" + indent(--indent) + data.charAt(i);
            } else if(data.charAt(i) == ',') {
                //The comma, then a newline and the indentation.
                parsedData += data.charAt(i) + "\n" + indent(indent);
            } else if(data.charAt(i) == ':') {
                //If there's a brace after the colon, then indent.
                if(data.charAt(i+1) == '{') 
                    parsedData += data.charAt(i) + "\n" + indent(indent); 

                //If there's no brace after, this is data; just add a space.
                else 
                    parsedData += data.charAt(i) + " "; 
            } else {
                //Otherwise, no special character, and so just add that character.
                parsedData+=data.charAt(i); 
            }   
        }

        return parsedData;
    }

    /**
     * This is a private helper method that simply gives back a String
     * containing 'indentNumber' tabs.
     * 
     * @param indentNumber The number of tabs in the return String.
     * @return an indentation String that consists of 'indentNumber' tabs.
     */
    private static String indent(int indentNumber) {
        String indent = "";
        for(int i = 0; i < indentNumber; i++)
            indent += "\t";
        return indent;
    }

    /**
     * Returns a String array containing all of the possible variables
     * that the user can call upon.
     * 
     * @param rawData The data through which we should scan for variables.
     * @return An array containing all of the variable names in the data.
     *  Any of these can be called into "getData(rawData, varName)" to get
     *  the associated data with that variable.
     */
    public static String[] getValidVariableNames(String rawData) {
        int count = 0;
        String s = "";
        for(int i = rawData.indexOf("\"insight\":", 0); i != -1; i = rawData.indexOf("\"insight\":", i + 1)) {
            s+=rawData.substring(rawData.indexOf(":", i) + 1, rawData.indexOf(",", i)).replace('"', ' ').trim() + "\n";
            count++;
        }

        return s.split("\n");
    }

    /**
     * Gets the JSON value of "data" for a given variable, dataName. 
     * 
     * This method MAY NOT work if "rawData" is processed, hence the 
     * reason for the naming of the variable. You may get the right
     * answer, but with the spacing all janky. It is HIGHLY RECOMMENDED
     * that you only use this method with raw data.
     * 
     * Example usage: getData(getData(), "vehicle.trip.arrived") will 
     * return the data value: '{"tripSummaryUpload":{"foo":1,"bar":2}}'.
     * 
     * @param rawData The data through which we should scan. Typically,
     *  this is going to be found by the "getData()" method. 
     * @param dataName The String of the varable, or information, we 
     *  are searching for.
     *  
     * @return The raw data that is associated with "dataName".
     */
    public static String getData(String rawData, String dataName) {
        //First of all, if we don't have the data, don't try to parse through anything.
        if(rawData == null || dataName == null || !rawData.contains(dataName)) return null;

        //Gets the index of the first opening curly brace after the first index of "data"
        //after the first index of the data we're looking for, 'dataName'.
        //int indexOfData = rawData.indexOf("{", rawData.indexOf("\"data\"", rawData.indexOf(dataName) + 1));

        //This guy gets the actual "data" value that was originally passed to the variable.
        int indexOfData = rawData.indexOf("{", rawData.indexOf("{", rawData.indexOf("\"data\"", rawData.indexOf(dataName) + 1)) + 1);

        //This next block will make sure we parse out the right number of curly braces.
        //If the data contains, say, 5 levels of data, then just getting the first index
        //of the first brace will possibly cut off some data.
        //Until numBraces == 0 again, we keep moving through the data, and the variable
        //'lastIndex' will contain the last index of the data we need.
        int numBraces = 0, lastIndex = -1;
        for(int i = indexOfData; i < rawData.length(); i++) {
            lastIndex = i;
            if(rawData.charAt(i) == '{') numBraces++;
            if(rawData.charAt(i) == '}') numBraces--;
            if(numBraces == 0) break;
        }

        //This one includes the curly braces.
        //return rawData.substring(indexOfData, lastIndex + 1);

        //This return values does NOT include the edge curly braces.
        return rawData.substring(indexOfData + 1, lastIndex).trim();
    }

    /**
     * Gets the raw JSON data from the server. This will return an unformatted
     * String that contains all of the server's data.
     * 
     * @return A String containing all of the data downloaded from the server,
     *  or a String starting with "ERROR:" that describes any error that occurred
     *  while trying to read the data from the server.
     */
    public static String getRawData() {
        return executeCommand("curl -X get --header Accept: application/json http://glassbeard.cfapps.io/data");
    }

    /**
     * This method will execute "command" in the respective terminal window.
     * 
     * @param command The curl command that will get the data from the web server.
     * @return The output from the command that was executed, or a String starting 
     *  with "ERROR" if there is an error during the execution of the command.
     *  This may not always return a String containing "ERROR" if something went
     *  wrong, however; but if you see "ERROR", then there was 100% something wrong.
     */
    public static String executeCommand(String command) {
        //This app is built with Mac in mind, since we only have macs to test on.
        if(OS.contains("Mac") || OS.contains("Linux")) {
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(command);
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace(System.out);
                return "ERROR: Could not execute the curl command properly.";
            }

            try {
                StringBuffer output = new StringBuffer();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                return output.toString();
            } catch (IOException e) {
                e.printStackTrace(System.out);
                return "ERROR: Could not read the output of the curl command.";
            }
        } else {
            return "ERROR: Your operating system (" + OS + ") is not supported.";
        }
    }
}