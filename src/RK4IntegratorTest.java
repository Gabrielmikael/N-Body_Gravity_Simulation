import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RK4IntegratorTest {

    private static class TestBody extends CelestialBody {
        public TestBody() {
            super(100, 5,
                    new Vector3D(0, 0, 0),
                    new Vector3D(1, 0, 0));
        }
    }

    @Test
    void testRK4Integration() {
        TestBody body = new TestBody();

        AccelerationFunction acc = bodies ->
                List.of(new Vector3D(2, 0, 0));

        RK4Integrator rk4 = new RK4Integrator();

        rk4.integrate(List.of(body), 1.0, acc);

        assertEquals(2.0, body.getPosition().getX(), 0.0001);
        assertEquals(3.0, body.getVelocity().getX(), 0.0001);
    }

    @Test
    void testPositionsRestoredAfterException() {
        TestBody body = new TestBody();

        AccelerationFunction acc = bodies -> {
            throw new RuntimeException("Calculation failed");
        };

        RK4Integrator rk4 = new RK4Integrator();

        assertThrows(RuntimeException.class, () ->
                rk4.integrate(List.of(body), 1.0, acc)
        );

        assertEquals(0.0, body.getPosition().getX(), 0.0001);
    }
}
