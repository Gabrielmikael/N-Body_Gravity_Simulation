import java.util.List;

public interface Integrator {
    void integrate(List<CelestialBody> bodies, double dt, AccelerationFunction acc);
}
