public abstract class CelestialBody {

    private double mass;
    private double radius;

    private Vector3D position;
    private Vector3D velocity;

    public CelestialBody(double mass, double radius, Vector3D position, Vector3D velocity) {
        setMass(mass);
        setRadius(radius);
        setPosition(position);
        setVelocity(velocity);
    }

    public double getMass(){
        return mass;
    }

    public double getRadius(){
        return radius;
    }

    public Vector3D getPosition(){
        return position;
    }

    public Vector3D getVelocity(){
        return velocity;
    }

    // Setters
    public void setPosition(Vector3D position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null.");
        }
        this.position = position; //can be neg
    }

    public void setVelocity(Vector3D velocity) {
        if (velocity == null) {
            throw new IllegalArgumentException("Velocity cannot be null.");
        }
        this.velocity = velocity; //can be neg
    }
    public void setMass(double mass) {
        if (mass <= 0 || !Double.isFinite(mass)) { // had to add this after testing: !Double.isFinite(mass)
            throw new IllegalArgumentException("Mass must be positive and finite.");
        }

        this.mass = mass;
    }
    public void setRadius(double radius){
        if(radius > 0){
            this.radius = radius;
        }
        else{
            throw new IllegalArgumentException("Radius must be greater than 0");
        }
    }


    public void updatePosition(double dt) {
        if (dt < 0 || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Invalid time step");
        }

        this.position = this.position.add(this.velocity.multiply(dt));
    } // x_new = x_old + velocity * (change in)time


    public void updateVelocity(Vector3D acceleration, double dt) {
        if (acceleration == null || dt < 0 || !Double.isFinite(dt)) {
            throw new IllegalArgumentException("Invalid acceleration or time step");
        }

        this.velocity = this.velocity.add(acceleration.multiply(dt));
    } //v_new = v_old + a * (change in) time




}
