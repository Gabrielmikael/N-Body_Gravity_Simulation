import java.util.ArrayList;
import java.util.List;

// Fourth-order Runge-Kutta integration: samples the acceleration 4 times per step and blends the samples,
// which is far more accurate than Euler for the same time step
public class RK4Integrator implements Integrator {
    @Override
    public void integrate(List<CelestialBody> bodies, double dt, AccelerationFunction acc) {
        int n = bodies.size(); // number of bodies

        // Remember where every body started, because we move the bodies around temporarily while sampling
        List<Vector3D> x0 = new ArrayList<>(); // starting positions
        List<Vector3D> v0 = new ArrayList<>(); // starting velocities
        for (CelestialBody body : bodies) {
            x0.add(body.getPosition());
            v0.add(body.getVelocity());
        }

        List<Vector3D> k1v, k2v, k3v, k4v; // accelerations sampled at the 4 points
        List<Vector3D> k2x, k3x, k4x;      // velocities sampled at the 4 points (k1x is just v0)

        try {
            // First starting with stage 1
            k1v = acc.compute(bodies); //=a(x_mid)

            // Stage 2: halfway through the step, moving with the starting velocity
            k2x = velocityAt(v0, k1v, dt / 2); //v0 +dt/2 * k1v
            setPositions(bodies, x0, v0, dt / 2); //gets midpoint, x_mid = x0 + dt/2*v0
            k2v = acc.compute(bodies); //=a(x_mid)

            // stage 3: halfway through the step again, but using the velocity from sample 2
            k3x = velocityAt(v0, k2v, dt / 2);
            setPositions(bodies, x0, k2x, dt / 2);
            k3v = acc.compute(bodies);


            // stage 4: same, just use the velocity from stage 3
            k4x = velocityAt(v0, k3v, dt);
            setPositions(bodies, x0, k3x, dt);
            k4v = acc.compute(bodies);

        } finally {
            // Always put the bodies back where they started (also if compute() threw an error, so nothing is left half-moved)
            for (int i = 0; i < n; i++) {
                bodies.get(i).setPosition(x0.get(i));
            }
        }

        // Combine the 4 samples with weights 1, 2, 2, 1 (divided by 6) and apply the real step
        for (int i = 0; i < n; i++) {
            CelestialBody body = bodies.get(i);

            Vector3D velocityBlend = v0.get(i) // sum of velocity samples (weighted)
                    .add(k2x.get(i).multiply(2))
                    .add(k3x.get(i).multiply(2))
                    .add(k4x.get(i));  // (k1x + 2*k2x + 2*k3x + k4x)

            Vector3D accelerationBlend = k1v.get(i) // sum of acceleration samples (weighted)
                    .add(k2v.get(i).multiply(2))
                    .add(k3v.get(i).multiply(2))
                    .add(k4v.get(i)); // (k1v + 2*k2v + 2*k3v + k4v)

            body.setPosition(x0.get(i).add(velocityBlend.multiply(dt / 6.0)));
            // x_new = x0 + dt/6(k1x + 2*k2x + 2*k3x + k4x)
            body.setVelocity(v0.get(i).add(accelerationBlend.multiply(dt / 6.0)));
            // v_new = v0 + dt/6(k1v + 2*k2v + 2*k3v + k4v)
        }
    }

    // Moves every body to start + velocity * time, so acceleration can be sampled there
    private void setPositions(List<CelestialBody> bodies, List<Vector3D> start,
                              List<Vector3D> velocity, double time) {
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).setPosition(start.get(i).add(velocity.get(i).multiply(time)));
        }
    }

    // Returns start velocity + acceleration * time for every body
    private List<Vector3D> velocityAt(List<Vector3D> start, List<Vector3D> acceleration, double time) {
        List<Vector3D> result = new ArrayList<>();
        for (int i = 0; i < start.size(); i++) {
            result.add(start.get(i).add(acceleration.get(i).multiply(time)));
        }
        return result;
    }
}