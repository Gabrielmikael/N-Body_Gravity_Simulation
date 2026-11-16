public class Vector3D {

    private double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3D add(Vector3D other) {
        return new Vector3D(
                this.x + other.x,
                this.y + other.y,
                this.z + other.z
        );
    }

    public Vector3D subtract(Vector3D other) {
        return new Vector3D(
                this.x - other.x,
                this.y - other.y,
                this.z - other.z
        );
    }

    // Scalar multiplication: multiply every component by one number
    public Vector3D multiply(double scalar) {
        return new Vector3D(
                this.x * scalar,
                this.y * scalar,
                this.z * scalar
        );
    }

    // Component-wise multiplication: multiply matching components
    public Vector3D componentMultiply(Vector3D other) {
        return new Vector3D(
                this.x * other.x,
                this.y * other.y,
                this.z * other.z
        );
    }
    public double magnitude() {
        return Math.sqrt(
                Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2)
        );
    }

    public Vector3D normalize() {
        double magnitude = this.magnitude();

        if(magnitude != 0.0) {
            return new Vector3D(
                    this.x / magnitude,
                    this.y / magnitude,
                    this. z / magnitude
            );
        }
        else{
            throw new ArithmeticException("Cannot divide by zero");
        }
    }

    // Dot product: returns a single number (double)
    public double dot(Vector3D other) {
        return this.x * other.x
                + this.y * other.y
                + this.z * other.z;
    }

    public Vector3D crossProduct(Vector3D other) {
        return new Vector3D(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public double distance(Vector3D other) {
        return this.subtract(other).magnitude();
    }

}
