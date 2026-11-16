
public class Main {

    public static void main(String[] args) {

        Simulation simulation = new Simulation();

        // Create the Sun
        Star sun = new Star(
                1.989e30, 6.957e8,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0, 5778
        );

        // Create Earth
        Planet earth = new Planet(
                5.972e24, 6.371e6,
                new Vector3D(1.496e11, 0, 0),
                new Vector3D(0, 29780, 0),
                Atmosphere.NITROGEN_OXYGEN
        );

        // Create Mars
        Planet mars = new Planet(
                6.417e23, 3.3895e6,
                new Vector3D(2.279e11, 0, 0),
                new Vector3D(0, 24070, 0),
                Atmosphere.CARBON_DIOXIDE
        );

        simulation.addBody(sun);
        simulation.addBody(earth);
        simulation.addBody(mars);

        // Run the simulation for ten steps
        double dt = 3600; // One hour per step

        for (int i = 1; i <= 10; i++) {
            simulation.step(dt);

            System.out.println("Step " + i);

            for (CelestialBody body : simulation.getBodies()) {
                Vector3D position = body.getPosition();

                System.out.println(
                        "Position: (" +
                                position.getX() + ", " +
                                position.getY() + ", " +
                                position.getZ() + ")"
                );
            }

            System.out.println();
        }
        // Launch the GUI using the same simulation
        javax.swing.SwingUtilities.invokeLater(() -> {
            new SimulationGUI(simulation);
        });
    }
}
