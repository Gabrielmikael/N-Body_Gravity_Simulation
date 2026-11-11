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
}