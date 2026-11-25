
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EulerIntegrationTest {

    private static class TestBody extends CelestialBody {
        public TestBody() {
            super(100, 5,
                    new Vector3D(0, 0, 0),
                    new Vector3D(1, 0, 0));
        }
    }

    @Test
    void testEulerIntegration() {
        TestBody body = new TestBody();

        AccelerationFunction acc = bodies ->
                List.of(new Vector3D(2, 0, 0));

        EulerIntegration euler = new EulerIntegration();

        euler.integrate(List.of(body), 1.0, acc);

        assertEquals(3.0, body.getPosition().getX(), 0.0001);
        assertEquals(3.0, body.getVelocity().getX(), 0.0001);
    }
}
