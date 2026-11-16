import javax.swing.*;
import java.awt.*;

public class SimulationGUI implements SimulationListener {

    private Simulation simulation;
    private JFrame frame;
    private DefaultListModel<String> bodyListModel;
    private JList<String> bodyList;

    private Timer simulationTimer;


    public SimulationGUI(Simulation simulation) {
        this.simulation = simulation;
        simulation.addSimulationListener(this);

        //making display
        frame = new JFrame("Gravity Simulator");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //list for bodies
        bodyListModel = new DefaultListModel<>(); // will store strings in GUI
        bodyList = new JList<>(bodyListModel); // creates visual list

        /* Simulation: List<CelestialBody> bodies stores actual data
        *  SimulationGUI: DefaultListModel<String> displays the names in GUI
        *
        * They are seperate lists so that the actual data isn't mixed with the names in GUI
        * Also JList expects a ListModel */

        frame.add(new JScrollPane(bodyList), BorderLayout.CENTER);
        // .add (adds) JScrollPane allows for scrolling, and centers scrollable list

        // making the buttons, first make jpanel (window inside jframe)
        JPanel buttonPanel = new JPanel();

        JButton addPlanet = new JButton("Add Planet");
        JButton addStar = new JButton("Add Star");
        JButton removeBody = new JButton("Remove Body");
        JButton startSimulation = new JButton("Start Simulation");

        buttonPanel.add(addPlanet);
        buttonPanel.add(addStar);
        buttonPanel.add(removeBody);
        buttonPanel.add(startSimulation);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        // Creating action listeners

        // Add a planet
        addPlanet.addActionListener(e -> {

            Double mass = askForPositiveNumber("Enter planet mass (kg):");
            if (mass == null) return;

            Double radius = askForPositiveNumber("Enter planet radius (m):");
            if (radius == null) return;

            Planet earth = new Planet(mass, radius,
                    new Vector3D(1.496e11, 0, 0),
                    new Vector3D(0, 29780, 0),
                    Atmosphere.NITROGEN_OXYGEN
            );
            simulation.addBody(earth);
        });

        // Add a star
        addStar.addActionListener(e -> {

            Double mass = askForPositiveNumber("Enter star mass (kg):");
            if (mass == null) return;

            Double radius = askForPositiveNumber("Enter star radius (m):");
            if (radius == null) return;

            Star sun = new Star(mass, radius,
                    new Vector3D(0, 0, 0),
                    new Vector3D(0, 0, 0),
                    1.0,
                    5778
            );
            simulation.addBody(sun);
        });

        // Remove selected body
        removeBody.addActionListener(e -> {
            int selectedIndex = bodyList.getSelectedIndex();

            if (selectedIndex != -1) {
                CelestialBody selectedBody = simulation.getBodies().get(selectedIndex);
                simulation.removeBody(selectedBody);
            }
        });

        // Creating simulation timer
        simulationTimer = new Timer(30, e -> {
            try {
                simulation.step(3600); //advances by 3600 sec (an hour)
            } catch (IllegalArgumentException ex) {

                simulationTimer.stop(); //if error, stop
                startSimulation.setText("Start Simulation");//changes button

                JOptionPane.showMessageDialog(//shows user why error occured
                        frame, ex.getMessage(), "Simulation Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Start and stop the simulation
        startSimulation.addActionListener(e -> {

            if (simulationTimer.isRunning()) {
                simulationTimer.stop();
                startSimulation.setText("Start Simulation");
            } else {
                simulationTimer.start();
                startSimulation.setText("Stop Simulation");
            }
        });
        onSimulationStep();
        frame.setVisible(true);
    }

    // Ask the user for a positive number
    private Double askForPositiveNumber(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(frame, message);

            // if the user clicks cancel, stop asking for input
            if (input == null) {
                return null;
            }

            try {
                double value = Double.parseDouble(input.trim()); //string to double

                if (value > 0 && Double.isFinite(value)) { //checks if input is finite and positive
                    return value;
                }

            } catch (NumberFormatException ex) {
                // happens if the user enters something that isn't a number
            }

            JOptionPane.showMessageDialog( //tells them to input a right number
                    frame,
                    "Please enter a valid positive number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Refresh simulation display
    @Override
    public void onSimulationStep() {

        bodyListModel.clear();

        for (CelestialBody body : simulation.getBodies()) {

            String type = "Body";

            if (body instanceof Planet) {
                type = "Planet";
            } else if (body instanceof Star) {
                type = "Star";
            }

            bodyListModel.addElement(
                    type + " | Mass: " + body.getMass()
            );
        }
    }
}
