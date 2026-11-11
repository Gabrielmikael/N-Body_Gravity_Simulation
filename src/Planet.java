public class Planet extends CelestialBody{

    private Atmosphere atmosphere;

    public Planet(double mass, double radius, Vector3D position, Vector3D velocity, Atmosphere atmosphere) {
        super(mass, radius, position, velocity);
        setAtmosphere(atmosphere);
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        if (atmosphere == null) {
            throw new IllegalArgumentException("Atmosphere cannot be null");
        }
        this.atmosphere = atmosphere;
    }

}
