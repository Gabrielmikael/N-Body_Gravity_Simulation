import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    private final Calculator calculator = new Calculator();

    //body used for testing
    private static class TestBody extends CelestialBody {
        public TestBody(double mass, Vector3D position) {
            super(mass, 1, position, new Vector3D(0, 0, 0));
        }
    }

    @Test
    void testForceMagnitude() {
        CelestialBody body1 = new TestBody(1e10, new Vector3D(0, 0, 0));
        CelestialBody body2 = new TestBody(2e10, new Vector3D(10, 0, 0));

        Vector3D force = calculator.calculateForce(body1, body2);
        double expected = 6.67430e-11 * 1e10 * 2e10 / 100;

        assertEquals(expected, force.magnitude(), 1e-6);
    }

    @Test
    void testForceDirection() {
        CelestialBody body1 = new TestBody(1e10, new Vector3D(0, 0, 0));
        CelestialBody body2 = new TestBody(1e10, new Vector3D(10, 0, 0));

        Vector3D force = calculator.calculateForce(body1, body2);

        assertTrue(force.getX() > 0);
        assertEquals(0, force.getY(), 1e-10);
        assertEquals(0, force.getZ(), 1e-10);
    }

    @Test
    void testForceDecreasesWithDistance() {
        CelestialBody body1 = new TestBody(1e10, new Vector3D(0, 0, 0));
        CelestialBody closeBody = new TestBody(1e10, new Vector3D(10, 0, 0));
        CelestialBody farBody = new TestBody(1e10, new Vector3D(20, 0, 0));

        double closeForce = calculator.calculateForce(body1, closeBody).magnitude();
        double farForce = calculator.calculateForce(body1, farBody).magnitude();

        assertEquals(closeForce / 4, farForce, 1e-6);
    }

    @Test
    void testNullBodyThrowsException() {
        CelestialBody body = new TestBody(1e10, new Vector3D(0, 0, 0));

        assertThrows(IllegalArgumentException.class, () -> calculator.calculateForce(null, body));
    }

    @Test
    void testSamePositionThrowsException() {
        CelestialBody body1 = new TestBody(1e10, new Vector3D(0, 0, 0));
        CelestialBody body2 = new TestBody(1e10, new Vector3D(0, 0, 0));

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateForce(body1, body2)
        );
    }

    @Test
    void testCalculateAccelerationPointsTowardOtherBody() {
        Calculator calculator = new Calculator();

        Star sun = new Star(
                1.989e30, 6.957e8,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0, 5778
        );

        Planet earth = new Planet(
                5.972e24, 6.371e6,
                new Vector3D(1.496e11, 0, 0),
                new Vector3D(0, 0, 0),
                Atmosphere.NITROGEN_OXYGEN
        );

        Vector3D acceleration = calculator.calculateAcceleration(earth, sun);

        assertTrue(acceleration.getX() < 0);
        assertEquals(0, acceleration.getY());
        assertEquals(0, acceleration.getZ());
    }

    @Test
    void testCalculateAccelerationMagnitude() {
        Calculator calculator = new Calculator();

        Star sun = new Star(
                1.989e30, 6.957e8,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0, 5778
        );

        Planet earth = new Planet(
                5.972e24, 6.371e6,
                new Vector3D(1.496e11, 0, 0),
                new Vector3D(0, 0, 0),
                Atmosphere.NITROGEN_OXYGEN
        );

        double expected = Calculator.G * sun.getMass()
                / Math.pow(1.496e11, 2);

        Vector3D acceleration = calculator.calculateAcceleration(earth, sun);

        assertEquals(expected, acceleration.magnitude(), expected * 1e-10);
    }

    @Test
    void testCalculateAccelerationWithNullBody() {
        Calculator calculator = new Calculator();

        Star sun = new Star(
                1.989e30, 6.957e8,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0, 5778
        );

        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateAcceleration(null, sun));
    }
}