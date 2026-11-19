import java.util.List;

public class VerletIntegrator implements Integrator {
    @Override
    public void integrate(List<CelestialBody> bodies, double dt, AccelerationFunction acc) {
        List<Vector3D> aOld = acc.compute(bodies);

        // 1. update positions
        for (int i = 0; i < bodies.size(); i++) {
            CelestialBody b = bodies.get(i);
            Vector3D move = b.getVelocity().multiply(dt)
                    .add(aOld.get(i).multiply(0.5 * dt * dt));
            b.setPosition(b.getPosition().add(move));
        }

        // 2. recompute acceleration at the new positions
        List<Vector3D> aNew = acc.compute(bodies);

        // 3. update velocities with the average acceleration
        for (int i = 0; i < bodies.size(); i++) {
            Vector3D avg = aOld.get(i).add(aNew.get(i)).multiply(0.5);
            bodies.get(i).updateVelocity(avg, dt);
        }
    }
}