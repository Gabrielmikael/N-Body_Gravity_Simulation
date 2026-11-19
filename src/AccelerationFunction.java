import java.util.List;

public interface AccelerationFunction {
    List<Vector3D> compute(List<CelestialBody> bodies);

}
