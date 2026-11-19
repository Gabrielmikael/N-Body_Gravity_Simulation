import java.util.List;

public class EulerIntegration implements Integrator {
    @Override
    public void integrate(List<CelestialBody> bodies, double dt, AccelerationFunction acc) {
        List<Vector3D> accelerations = acc.compute(bodies);

        // Update velocities
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).updateVelocity(accelerations.get(i), dt);
        }

        // Update positions
        for (CelestialBody body : bodies) {
            body.updatePosition(dt);
        }
    }
}