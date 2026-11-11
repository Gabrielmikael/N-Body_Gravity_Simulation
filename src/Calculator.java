public class Calculator {

    private static final double G = 6.67430e-11;

    public Vector3D calculateForce(CelestialBody body1, CelestialBody body2) {
        if (body1 == null || body2 == null) {
            throw new IllegalArgumentException("Objecs cannot be null.");
        }
        Vector3D direction = body2.getPosition().subtract(body1.getPosition()); // Vectors pointing from body1 toward body2

        double distance = direction.magnitude();
        if (distance == 0) {
            throw new IllegalArgumentException("Bodies cannot be in the same position.");
        }

        double forceMagnitude = G * body1.getMass() * body2.getMass() / (distance * distance); //Newtons Law
        return direction.normalize().multiply(forceMagnitude); // Converts the magnitude into a force vector

    }
}