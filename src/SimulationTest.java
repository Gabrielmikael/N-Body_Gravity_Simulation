import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test
    void testListenerNotification() {
        Simulation simulation = new Simulation();

        int[] counter = {0};
        SimulationListener listener = () -> counter[0]++;

        simulation.addSimulationListener(listener);
        simulation.notifyListeners();

        assertEquals(1, counter[0]);
    }

    @Test
    void testSimulationStep() {
        Simulation simulation = new Simulation();

        Star sun = new Star(
                1.989e30, 6.957e8,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0, 5778
        );

        Planet earth = new Planet(
                5.972e24, 6.371e6,
                new Vector3D(1.496e11, 0, 0),
                new Vector3D(0, 0, 0),
                Atmosphere.NITROGEN_OXYGEN
        );

        simulation.addBody(sun);
        simulation.addBody(earth);

        simulation.step(1);

        // Earth should move toward the Sun
        assertTrue(earth.getVelocity().getX() < 0);
        assertTrue(earth.getPosition().getX() < 1.496e11);

        // The Sun should move toward Earth
        assertTrue(sun.getVelocity().getX() > 0);
        assertTrue(sun.getPosition().getX() > 0);
    }

    @Test
    void testStepWithInvalidTimeStep() {
        Simulation simulation = new Simulation();

        assertThrows(IllegalArgumentException.class, () -> simulation.step(0));
        assertThrows(IllegalArgumentException.class, () -> simulation.step(-1));
    }

    @Test
    void testStepWithOneBody() {
        Simulation simulation = new Simulation();

        Star sun = new Star(
                1.989e30, 6.957e8,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0, 5778
        );

        simulation.addBody(sun);

        assertDoesNotThrow(() -> simulation.step(1));
    }

    @Test
    void testStepWithBodiesAtSamePosition() {
        Simulation simulation = new Simulation();

        Star sun = new Star(
                1.989e30, 6.957e8,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0, 5778
        );

        Planet earth = new Planet(
                5.972e24, 6.371e6,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                Atmosphere.NITROGEN_OXYGEN
        );

        simulation.addBody(sun);
        simulation.addBody(earth);

        assertThrows(IllegalArgumentException.class,
                () -> simulation.step(1));
    }
}