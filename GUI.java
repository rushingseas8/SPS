import javax.swing.*;
import java.awt.*;

/**
 * A GUI class that allows developers to play with data on the cloud.
 * There exists support for adding, removing, viewing, and retrieving
 * all of the data, either individually or as a group.c
 */
public class GUI {
    private static final int DEFAULT_WIDTH = 960, DEFAULT_HEIGHT = 630;

    /**
     * The variable names for submitting data to the server.
     */
    private static String[] variableNames = new String[] {
            "tripSummaryUpload", "vehicleHealthUpload",
            "homeWindowOpen", "homeWindowClosed",
            "homeGarageOpen", "homeGarageClosed",
            "homeSmokeAlarmOn", "homeSmokeAlarmOff",
            "homeSmokeAlarmBatteryLow", "homeFireAlarmOn",
            "homeFireAlarmOff", "homeWaterSensorAlarmOn",
            "homeWaterSensorAlarmOff"
        };

    /**
     * So, the problem is that the actual insight names to retrieve
     * the data are different from the variable names used to post
     * the data. These are used for getting data back from sensors.
     */
    private static String[] insightNames = new String[] {
            "vehicle.trip.arrived", "vehicle.health.arrived", 
            "home.window.open", "home.window.closed",
            "home.garage.open", "home.garage.closed",
            "home.smoke.alarm_on", "home.smoke.alarm_off",
            "home.smoke.alarm_battery_low", "home.fire.alarm_on",
            "home.fire.alarm_off", "home.water.sensor.alarm_on",
            "home.water.sensor.alarm_off"
        };

    private static JFrame frame;
    private static JTabbedPane tabber;

    private static JPanel addTopBar;
    private static JLabel addGroupIDLabel = new JLabel("Group ID: ");
    private static JTextField addGroupIDField = new JTextField("sample_id");
    private static JLabel addSensorIDLabel = new JLabel("Sensor ID: ");
    private static JTextField addSensorIDField = new JTextField("sample_id");
    private static JLabel addVarLabel = new JLabel("Variable name: ");
    private static JComboBox<String> addVariableCombo = new JComboBox<String>(variableNames);
    private static JLabel addDataLabel = new JLabel("Data: ");
    //private static JTextField addDataField = new JTextField("sample_data");
    private static JTextArea addDataArea = new JTextArea();
    private static JButton addDataButton = new JButton("POST DATA");

    private static JPanel getTopBar;
    private static JLabel getVariableLabel = new JLabel("Variable name:");
    private static JComboBox<String> getVariableCombo = new JComboBox<String>(variableNames);
    private static JButton getDataButton = new JButton("GET DATA");

    private static JPanel clearTopBar;
    private static JButton clearButton = new JButton("CLEAR ALL DATA");

    private static JPanel checkTopBar;
    private static JButton checkButton = new JButton("LIST ALL DATA");

    private static JPanel panel1;
    private static JPanel panel2;
    private static JPanel panel3;
    private static JPanel panel4;

    private static JScrollPane scroll1;
    private static JScrollPane scroll2;
    private static JScrollPane scroll3;
    private static JScrollPane scroll4;

    private static JTextArea text1;
    private static JTextArea text2;
    private static JTextArea text3;
    private static JTextArea text4;

    public static void main(String[] args) {
        frame = new JFrame("Developer Editor");
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        //Panel 1 - to post/add data.
        panel1 = new JPanel();
        addTopBar = new JPanel();

        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.CENTER));

        //top.add(addGroupIDLabel);
        //top.add(addGroupIDField);

        //top.add(addSensorIDLabel);
        //top.add(addSensorIDField);

        top.add(addVarLabel);
        top.add(addVariableCombo);

        addDataArea.setBorder(BorderFactory.createTitledBorder("data:"));
        addDataArea.setRows(4);

        addTopBar.setLayout(new BorderLayout());
        addTopBar.add(top, "North");
        addTopBar.add(addDataArea, "Center");

        text1 = new JTextArea();
        text1.setBorder(BorderFactory.createTitledBorder("output:"));

        scroll1 = new JScrollPane(text1);
        scroll1.setBorder(null);

        top.add(addDataButton);
        
        //Add data button will call the "post" method in ServerInterfacer.
        addDataButton.addActionListener(a -> {
                text1.append(ServerInterface.post(
                        variableNames[addVariableCombo.getSelectedIndex()], 
                        addDataArea.getText())); 
            });

        panel1.setLayout(new BorderLayout());
        panel1.add(addTopBar, "North");
        panel1.add(scroll1, "Center");
        panel1.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        //Panel 2 - to get data.
        panel2 = new JPanel();
        getTopBar = new JPanel();
        getTopBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        getTopBar.add(getVariableLabel);
        getTopBar.add(getVariableCombo);
        getTopBar.add(getDataButton);

        //Get data button will call the "getData" method, which calls the "getRawData" method.
        getDataButton.addActionListener(a -> {
                text2.setText(ServerInterface.parse(
                        ServerInterface.getData(ServerInterface.getRawData(), 
                            insightNames[getVariableCombo.getSelectedIndex()])));
            });

        text2 = new JTextArea();  
        text2.setBorder(BorderFactory.createTitledBorder("output:"));

        scroll2 = new JScrollPane(text2);
        scroll2.setBorder(null);

        panel2.setLayout(new BorderLayout());
        panel2.add(getTopBar, "North");
        panel2.add(scroll2, "Center");
        panel2.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        //Panel 3 - to clear data.
        panel3 = new JPanel();
        clearTopBar = new JPanel();
        clearTopBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        clearTopBar.add(clearButton);

        //This clears all data. Be careful!
        clearButton.addActionListener(a -> {
                ServerInterface.clearAllData();
                text1.setText("");
                text2.setText("");
                text3.setText("Cleared all data.");
                text4.setText("");
            });

        text3 = new JTextArea();
        text3.setBorder(BorderFactory.createTitledBorder("output:"));

        scroll3 = new JScrollPane(text3);
        scroll3.setBorder(null);

        panel3.setLayout(new BorderLayout());
        panel3.add(clearTopBar, "North");
        panel3.add(scroll3, "Center");
        panel3.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        //Panel 4 - to check all data.
        panel4 = new JPanel();
        checkTopBar = new JPanel();
        checkTopBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        checkTopBar.add(checkButton);

        text4 = new JTextArea();
        text4.setBorder(BorderFactory.createTitledBorder("output:"));

        scroll4 = new JScrollPane(text4);
        scroll4.setBorder(null);

        checkButton.addActionListener(a -> {
                text4.setText(ServerInterface.parse(ServerInterface.getRawData()));
            });

        panel4.setLayout(new BorderLayout());
        panel4.add(checkTopBar, "North");
        panel4.add(scroll4, "Center");
        panel4.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        //Set up the tabbed pane.
        tabber = new JTabbedPane();
        tabber.addTab("Send data", panel1);
        tabber.addTab("Get data", panel2);
        tabber.addTab("Clear data", panel3);
        tabber.addTab("Check data", panel4);

        frame.add(tabber);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}