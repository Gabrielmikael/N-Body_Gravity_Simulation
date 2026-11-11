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
}