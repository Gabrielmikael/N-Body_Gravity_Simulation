import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<SimulationListener> listeners = new ArrayList<>();

    public void addSimulationListener(SimulationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        listeners.add(listener);
    }

     void notifyListeners() {
        for (SimulationListener listener : listeners) {
            listener.onSimulationStep();
        }
    }

    public void removeSimulationListener(SimulationListener listener) {
        listeners.remove(listener);
    }
}
