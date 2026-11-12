import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private List<SimulationListener> listeners = new ArrayList<>();
    private List<CelestialBody> bodies = new ArrayList<>();

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

    public void addBody(CelestialBody body) {
        if (body == null) {
            throw new IllegalArgumentException("Body cannot be null");
        }
        bodies.add(body);
        notifyListeners();
    }

    public void removeBody(CelestialBody body) {
        bodies.remove(body);
        notifyListeners();
    }

    public List<CelestialBody> getBodies() {
        return new ArrayList<>(bodies);
    }
}
