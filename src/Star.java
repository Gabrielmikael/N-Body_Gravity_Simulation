public class Star extends CelestialBody {

    private double luminosity;
    private int temperature;


    public Star(double mass, double radius, Vector3D position, Vector3D velocity, double luminosity, int temperature) {
        super(mass, radius, position, velocity);

        setLuminosity(luminosity);
        setTemperature(temperature);
    }

    public double getLuminosity() {
        return luminosity;
    }

    public int getTemperature() {
         return temperature;
    }

    public void setTemperature(int temperature) {
        if (temperature < 0 || temperature > 100000000) {
            throw new IllegalArgumentException("Temperature must be between 0 and 100000000");
        }
        else{
            this.temperature = temperature; //temp measure in Kelvin, nothing about 0
        }
    }

    public void setLuminosity(double luminosity) {
        if (luminosity < 0 || !Double.isFinite(luminosity)) {
            throw new IllegalArgumentException("Luminosity cannot be negative and has to be finite.");
        }

        this.luminosity = luminosity; //luminosity in solar luminosities instead of Watts that way the parameter can be smaller.
    } //prob have to talk about units later if user can input stuff inside


}
