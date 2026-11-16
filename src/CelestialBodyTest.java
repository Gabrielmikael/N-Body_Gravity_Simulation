import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CelestialBodyTest {

    private static class TestBody extends CelestialBody {
        public TestBody(double mass, double radius, Vector3D position, Vector3D velocity) {
            super(mass, radius, position, velocity);
        }
    }

    private TestBody createBody() {
        return new TestBody(100, 5,
                new Vector3D(0, 0, 0),
                new Vector3D(2, 0, 0)
        );
    }

    @Test
    void testConstructorAndGetters() {
        TestBody body = createBody();

        assertEquals(100, body.getMass());
        assertEquals(5, body.getRadius());
        assertEquals(0, body.getPosition().getX());
        assertEquals(2, body.getVelocity().getX());
    }

    @Test
    void testInvalidMass() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestBody(-10, 5,
                        new Vector3D(0, 0, 0),
                        new Vector3D(0, 0, 0)));
    }

    @Test
    void testInvalidRadius() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestBody(100, -5,
                        new Vector3D(0, 0, 0),
                        new Vector3D(0, 0, 0)));
    }

    @Test
    void testNullPositionAndVelocity() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestBody(100, 5, null,
                        new Vector3D(0, 0, 0)));
    }

    @Test
    void testSetMassAndRadius() {
        TestBody body = createBody();
        body.setMass(200);
        body.setRadius(10);

        assertEquals(200, body.getMass());
        assertEquals(10, body.getRadius());
    }

    @Test
    void testInvalidSetMassAndRadius() {
        TestBody body = createBody();

        assertThrows(IllegalArgumentException.class, () -> body.setMass(-1));
        assertThrows(IllegalArgumentException.class, () -> body.setRadius(0));
    }

    @Test
    void testSetPositionAndVelocity() {
        TestBody body = createBody();

        Vector3D position = new Vector3D(5, 6, 7);
        Vector3D velocity = new Vector3D(1, 2, 3);

        body.setPosition(position);
        body.setVelocity(velocity);

        assertEquals(position, body.getPosition());
        assertEquals(velocity, body.getVelocity());
    }

    @Test
    void testNullPositionAndVelocitySetters() {
        TestBody body = createBody();

        assertThrows(IllegalArgumentException.class, () -> body.setPosition(null));
        assertThrows(IllegalArgumentException.class, () -> body.setVelocity(null));
    }

    @Test
    void testUpdatePosition() {
        TestBody body = createBody();
        body.updatePosition(2);

        assertEquals(4, body.getPosition().getX());
        assertEquals(0, body.getPosition().getY());
        assertEquals(0, body.getPosition().getZ());
    }

    @Test
    void testUpdateVelocity() {
        TestBody body = createBody();
        body.updateVelocity(new Vector3D(0, 3, 0), 2);

        assertEquals(2, body.getVelocity().getX());
        assertEquals(6, body.getVelocity().getY());
        assertEquals(0, body.getVelocity().getZ());
    }

    @Test
    void testInvalidTimeStep() {
        TestBody body = createBody();
        assertThrows(IllegalArgumentException.class, () -> body.updatePosition(-1));
        assertThrows(IllegalArgumentException.class, () -> body.updateVelocity(new Vector3D(0, 0, 0), -1));
    }
}