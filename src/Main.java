import javafx.application.Platform;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;

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

        // Launch the Java GUI
        SwingUtilities.invokeLater(() -> {
            new SimulationGUI(simulation);
        });

        Platform.startup(() -> {
            ThreeDView view = new ThreeDView(simulation);
            view.start(new Stage());
        });
    }
}
