import javax.swing.*;
import java.awt.*;

public class SimulationGUI implements SimulationListener {

    private Simulation simulation;
    private JFrame frame;
    private DefaultListModel<String> bodyListModel;
    private JList<String> bodyList;

    private Timer simulationTimer;
    private double speed = 1.0; //double so we can double speeds like 0.5x and 2x
    private double stepAccumulator = 0.0; //stores leftover fractional steps (ex: 0.5x only runs a step every 2nd tick)


    public SimulationGUI(Simulation simulation) {
        this.simulation = simulation;
        simulation.addSimulationListener(this);

        //making display
        frame = new JFrame("Gravity Simulator");
        frame.setSize(1000, 400);
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

        // Speed selector
        JComboBox<String> speedSelector = //jcombobox = drop down menu
                new JComboBox<>(new String[]{"0.25x", "0.5x", "1x", "2x", "5x", "10x", "50x"}); //make string array list

        speedSelector.setSelectedItem("1x"); //default selection is now the String "1x"
        speedSelector.setPreferredSize(new Dimension(70, 25));

        speedSelector.addActionListener(e -> {
            String selected = (String) speedSelector.getSelectedItem(); //get the chosen text option
            speed = Double.parseDouble(selected.replace("x", ""));
            //parseDouble makes string to double, then there is an x at the end of the string so remove that using replace()
        });

        // Integration method selector
        JComboBox<String> methodSelector = new JComboBox<>(
                new String[]{"RK4", "Verlet", "Euler"} //3 strategies
        );
        methodSelector.setSelectedItem("RK4"); // Making RK4 defualt
        simulation.setIntegrator(new RK4Integrator()); // make the simulation match the dropdown's starting choice

        //make Actionlistener
        methodSelector.addActionListener(e -> {
            String method = (String) methodSelector.getSelectedItem(); // the chosen name
            if (method.equals("RK4")) {
                simulation.setIntegrator(new RK4Integrator()); // most accurate per step
            } else if (method.equals("Verlet")) {
                simulation.setIntegrator(new VerletIntegrator()); // good at keeping orbits stable over long runs
            } else {
                simulation.setIntegrator(new EulerIntegration()); // simplest and least accurate
            }
        });

        buttonPanel.add(addPlanet);
        buttonPanel.add(addStar);
        buttonPanel.add(removeBody);
        buttonPanel.add(startSimulation);
        buttonPanel.add(new JLabel("Speed:"));
        buttonPanel.add(speedSelector);
        buttonPanel.add(new JLabel("Method:"));
        buttonPanel.add(methodSelector);

        frame.add(buttonPanel, BorderLayout.SOUTH); //never forget

        // Creating more action listeners
        // Add a planet
        addPlanet.addActionListener(e -> {

            Double mass = askForPositiveNumber("Enter planet mass (kg):");
            if (mass == null) return;

            Double radius = askForPositiveNumber("Enter planet radius (m):");
            if (radius == null) return;

            //This is an issue I ran into, I would input the positoin, but the planet would be so far away it wouldn't show up
            //so the message gives an example (3e11 is past Mars, so it doesn't land on Earth or Mars)
            Vector3D position = askForPosition();
            if (position == null) return; // cancel = nothing

            if (isPositionTaken(position)) return; // if body is already there shows stop message

            Vector3D velocity = orbitalVelocity(position); // calculated for them so the new planet orbits instead of sitting still

            Planet planet = new Planet(mass, radius, position, velocity, Atmosphere.NITROGEN_OXYGEN);

            simulation.addBody(planet); // works while the simulation is running
        });

        // Add a star
        addStar.addActionListener(e -> {
            Double mass = askForPositiveNumber("Enter star mass (kg):");
            if (mass == null) return;

            Double radius = askForPositiveNumber("Enter star radius (m):");
            if (radius == null) return;

            Vector3D position = askForPosition();
            if (position == null) return;

            if (isPositionTaken(position)) return;

            Vector3D velocity = orbitalVelocity(position); // without this a new star would just sit there for minutes

            Star star = new Star(mass, radius, position, velocity, 1.0, 5778); // 1.0 = luminosity, 5778 = temperature (K)

            simulation.addBody(star);
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
                stepAccumulator += speed; // add the speed each tick (1x adds 1.0, 2x adds 2.0, 0.5x adds 0.5)

                while (stepAccumulator >= 1.0) { // run one step for every whole number in the accumulator
                    simulation.step(3600); // always a fixed 3600 sec (an hour) step, so orbits stay accurate at any speed
                    stepAccumulator -= 1.0; // use up one step from the accumulator
                }
            } catch (IllegalArgumentException ex) {

                simulationTimer.stop(); //if error, stop
                stepAccumulator = 0.0; // clear leftover steps so the next start is clean
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

    // Same as above but zero and negative numbers are allowed (positions can be 0 or negative)
    private Double askForNumber(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(frame, message);

            if (input == null) { // cancel
                return null;
            }

            try {
                double value = Double.parseDouble(input.trim()); //string to double

                if (Double.isFinite(value)) { //only checks that it is finite, no positive check
                    return value;
                }

            } catch (NumberFormatException ex) {
                // happens if the user enters something that isn't a number
            }

            JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Asks for x, y and z one at a time and puts them in a Vector3D
    private Vector3D askForPosition() {
        Double x = askForNumber("Enter x position (m), example: 3e11");
        if (x == null) return null; // cancel

        Double y = askForNumber("Enter y position (m):");
        if (y == null) return null;

        Double z = askForNumber("Enter z position (m):");
        if (z == null) return null;

        return new Vector3D(x, y, z);
    }

    // It loops through simulation.getBodies() and keeps the heaviest one.
    private Vector3D orbitalVelocity(Vector3D position) {
        CelestialBody heaviest = null; // heaviest star/object

        for (CelestialBody body : simulation.getBodies()) {
            if (heaviest == null || body.getMass() > heaviest.getMass()) {
                heaviest = body;
            }
        }

        if (heaviest == null) { // no bodies at all (user removed everything), nothing to orbit
            return new Vector3D(0, 0, 0);
        }

        Vector3D toBody = position.subtract(heaviest.getPosition()); // line from the heavy body to the new body

        // z axis cross the line = a direction sideways (perpendicular) to the line, this is the way the planet moves
        Vector3D sideways = new Vector3D(0, 0, 1).crossProduct(toBody);

        if (sideways.magnitude() == 0) { // exactly on top of the heavy body (or straight above it), no sideways direction exists
            return new Vector3D(0, 0, 0);
        }

        double orbitSpeed = Math.sqrt(Calculator.G * heaviest.getMass() / toBody.magnitude()); //circular orbit speed, v = sqrt(G * M / r)

        return sideways.normalize().multiply(orbitSpeed); // normalize = length 1, then multiply by the speed
    }

    // true if another body is already exactly at this position (the simulation would crash on the next step)
    private boolean isPositionTaken(Vector3D position) {
        for (CelestialBody body : simulation.getBodies()) {
            if (position.distance(body.getPosition()) == 0) { // distance 0 means the same spot
                JOptionPane.showMessageDialog( //tells them to pick a different spot
                        frame,
                        "Another body is already at that position. Choose a different position.",
                        "Position Taken",
                        JOptionPane.ERROR_MESSAGE
                );
                return true;
            }
        }
        return false; // the spot is free
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