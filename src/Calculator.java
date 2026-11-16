public class Calculator {

    public static final double G = 6.67430e-11;

    public Vector3D calculateForce(CelestialBody body1, CelestialBody body2) {
        if (body1 == null || body2 == null) {
            throw new IllegalArgumentException("Objetcs cannot be null.");
        }
        Vector3D direction = body2.getPosition().subtract(body1.getPosition()); // distance vectors

        double distance = direction.magnitude();
        if (distance == 0) {
            throw new IllegalArgumentException("Bodies cannot be in the same position.");
        }

        double forceMagnitude = G * body1.getMass() * body2.getMass() / (distance * distance); //Newtons Law
        return direction.normalize().multiply(forceMagnitude); // Converts the magnitude into a force vector

    }
    //now we need F = ma, rearrange to a = F/m, we should have both
    public Vector3D calculateAcceleration(CelestialBody body1, CelestialBody body2) {

        Vector3D force = calculateForce(body1, body2);

        return force.multiply(1.0 / body1.getMass()); // F/m
    }
}