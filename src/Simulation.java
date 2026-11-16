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

    // Calculates the total acceleration acting on each body
    private List<Vector3D> calculateAccelerations(List<CelestialBody> currentBodies) {

        List<Vector3D> accelerations = new ArrayList<>();
        Calculator calculator = new Calculator();

        for (int i = 0; i < currentBodies.size(); i++) {
            CelestialBody current = currentBodies.get(i);
            Vector3D totalAcc = new Vector3D(0, 0, 0); // a=0
            for (int j = 0; j < currentBodies.size(); j++) {  // Add the acceleration caused by every other body
                if (i == j) {
                    continue; // Skip the current body
                }

                CelestialBody other = currentBodies.get(j);
                Vector3D acceleration = calculator.calculateAcceleration(current, other);
                totalAcc = totalAcc.add(acceleration); // net acc
            }
            // Save total acceleration for the object
            accelerations.add(totalAcc);
        }
        return accelerations;
    }

    public void step(double dt) {
        if (dt <= 0 || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Invalid time step.");
        }

        List<CelestialBody> currentBodies = getBodies();
        List<Vector3D> accelerations = calculateAccelerations(currentBodies);

        // Update velocities
        for (int i = 0; i < currentBodies.size(); i++) {
            currentBodies.get(i).updateVelocity(accelerations.get(i), dt);
        }

        // Update positions
        for (CelestialBody body : currentBodies) {
            body.updatePosition(dt);
        }

        notifyListeners();
    }

}

