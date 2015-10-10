import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * A GUI that lets the user play with individual sensor data, in accordance
 * with the GLASSBEARD project. 
 */
public class SensorGUI {
    private static JFrame frame;
    private static JPanel inputBar;

    private static JMenuBar menu;
    private static JMenu help;
    private static JMenuItem helpItem;

    private static JTextField typeField = new JTextField("Sensor type");
    private static JTextField xField = new JTextField("X position");
    private static JTextField yField = new JTextField("Y position");
    private static JTextField zField = new JTextField("Z position");
    private static JButton create = new JButton("Create new sensor");
    private static JButton clear = new JButton("Clear");

    private static JSplitPane split;

    private static JTable names;
    private static DefaultTableModel namesModel;
    private static String[] namesCols = {"Sensor type/pos"};
    private static String[][] namesData;

    private static JTable sensorData;
    private static DefaultTableModel sensorModel;
    private static String[] sensorCols = {"Value 1", "Value 2", "Value 3"};
    private static Object[][] sensorDataData;

    public static void main(String[] args) {
        frame = new JFrame("Sensor GUI");
        frame.setSize(640, 480);

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        menu = new JMenuBar();
        help = new JMenu("Help");
        helpItem = new JMenuItem("Help");
        helpItem.addActionListener(a -> {
                JOptionPane.showMessageDialog(frame, 
                    "Type is the sensor type: 0=position,1=stove,2=window.\n" +
                    "X,Y,Z represent the position of the sensor added.\n" + 
                    "You have to first modify the values of the sensor to update\n" +
                    "their cloud values. Type 0 needs x,y,z values, 1 and 2\n" +
                    "need boolean values representing their states.");
            });
        help.add(helpItem);
        menu.add(help);
        frame.setJMenuBar(menu);

        inputBar = new JPanel();
        inputBar.setLayout(new FlowLayout(FlowLayout.CENTER));

        typeField.setColumns(8);
        xField.setColumns(8);
        yField.setColumns(8);
        zField.setColumns(8);

        inputBar.add(typeField);
        inputBar.add(xField);
        inputBar.add(yField);
        inputBar.add(zField);
        inputBar.add(create);
        inputBar.add(clear);

        create.addActionListener(a -> {
                try {
                    int t = Integer.parseInt(typeField.getText());
                    int x = Integer.parseInt(xField.getText());
                    int y = Integer.parseInt(yField.getText());
                    int z = Integer.parseInt(zField.getText());

                    String s = typeField.getText() + ","
                        + xField.getText() + "," + yField.getText() + "," + zField.getText();

                    //Actually creates the sensor object.
                    String name = ServerInterface.addSensor(t,x,y,z);

                    namesModel.addRow(new String[]{s + "," + name});
                    sensorModel.addRow(new Object[]{null, null, null});
                } catch (Exception e) {
                    //This means there was an error in parsing. Ignore.
                } finally {
                    typeField.setText("");
                    xField.setText("");
                    yField.setText("");
                    zField.setText("");
                }
            });

        clear.addActionListener(a -> {
                ServerInterface.clearAllData();
                namesModel.setRowCount(0);
                sensorModel.setRowCount(0);
            });

        namesData = new String[0][0];
        namesModel = new DefaultTableModel(namesData, namesCols) {
            //This makes it so that the user cannot change the values of T,X,Y,Z
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        names = new JTable(namesModel);

        sensorDataData = new Object[0][0];
        sensorModel = new DefaultTableModel(sensorDataData, sensorCols);
        sensorData = new JTable(sensorModel);
        sensorModel.addTableModelListener(new SensorModelListener());

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
            new JScrollPane(names), new JScrollPane(sensorData));
        split.setContinuousLayout(true);

        frame.add(inputBar, "North");
        frame.add(split, "Center");

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        split.setDividerLocation(.3);
    }

    private static class SensorModelListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            //If this is an insertion, let that proceed.
            if(e.getType() == TableModelEvent.INSERT) return;

            //We don't worry about multiple edits.
            if(e.getFirstRow() != e.getLastRow()) return; 

            if(e.getFirstRow() >= namesModel.getRowCount()) return; 

            String sensorName = (String)(namesModel.getValueAt(e.getFirstRow(), 0));
            int type = Integer.parseInt(sensorName.substring(0, sensorName.indexOf(",")));
            sensorName = sensorName.substring(sensorName.lastIndexOf(",") + 1);

            if(type == 0) {
                try {
                    //If the three elements are valid and are integers, we're good!
                    int x = Integer.parseInt((String)(sensorModel.getValueAt(e.getFirstRow(), 0)));
                    int y = Integer.parseInt((String)(sensorModel.getValueAt(e.getFirstRow(), 1)));
                    int z = Integer.parseInt((String)(sensorModel.getValueAt(e.getFirstRow(), 2)));

                    ServerInterface.updatePositionSensor(sensorName, x, y ,z);
                    ServerInterface.pushSensors();
                } catch (Exception i) {
                    //Ignore
                }
            } else if(type == 1) {
                try {
                    //Only need one valid element
                    boolean b = Boolean.parseBoolean((String)(sensorModel.getValueAt(e.getFirstRow(), 0)));

                    ServerInterface.updateStoveSensor(sensorName, b);
                    ServerInterface.pushSensors();
                } catch (Exception i) {
                    //Ignore
                }
            } else if(type == 2) {
                try {
                    //Only need one valid element
                    boolean b = Boolean.parseBoolean((String)(sensorModel.getValueAt(e.getFirstRow(), 0)));

                    ServerInterface.updateWindowSensor(sensorName, b);
                    ServerInterface.pushSensors();
                } catch (Exception i) {
                    //Ignore
                }
            } 
        }
    }
}