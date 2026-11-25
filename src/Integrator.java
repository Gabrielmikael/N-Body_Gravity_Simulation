import java.util.List;

public interface Integrator {
    void integrate(List<CelestialBody> bodies, double dt, AccelerationFunction acc);
} // use the list of celestial bodies, the section of time dt, total acc
