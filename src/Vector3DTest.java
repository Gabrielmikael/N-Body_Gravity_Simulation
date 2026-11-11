import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Vector3DTest {

    @Test
    void testGetters() {
        Vector3D vector = new Vector3D(1, 2, 3);

        assertEquals(1, vector.getX());
        assertEquals(2, vector.getY());
        assertEquals(3, vector.getZ());
    }

    @Test
    void testAdd() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);

        Vector3D result = v1.add(v2);

        assertEquals(5, result.getX());
        assertEquals(7, result.getY());
        assertEquals(9, result.getZ());
    }

    @Test
    void testSubtract() {
        Vector3D v1 = new Vector3D(5, 7, 9);
        Vector3D v2 = new Vector3D(1, 2, 3);

        Vector3D result = v1.subtract(v2);

        assertEquals(4, result.getX());
        assertEquals(5, result.getY());
        assertEquals(6, result.getZ());
    }

    @Test
    void testScalarMultiply() {
        Vector3D vector = new Vector3D(1, 2, 3);
        Vector3D result = vector.multiply(2);

        assertEquals(2, result.getX());
        assertEquals(4, result.getY());
        assertEquals(6, result.getZ());
    }

    @Test
    void testMagnitude() {
        Vector3D vector = new Vector3D(3, 4, 0);
        assertEquals(5, vector.magnitude(), 1e-10);
    }

    @Test
    void testNormalize() {
        Vector3D vector = new Vector3D(3, 0, 4);
        Vector3D result = vector.normalize();

        assertEquals(0.6, result.getX(), 1e-10);
        assertEquals(0, result.getY(), 1e-10);
        assertEquals(0.8, result.getZ(), 1e-10);
        assertEquals(1, result.magnitude(), 1e-10);
    }

    @Test
    void testNormalizeZeroVector() {
        Vector3D zero = new Vector3D(0, 0, 0);
        assertThrows(ArithmeticException.class, () -> zero.normalize());
    }

    @Test
    void testDotProduct() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);

        assertEquals(32, v1.dot(v2));
    }

    @Test
    void testComponentMultiply() {
        Vector3D v1 = new Vector3D(2, 3, 4);
        Vector3D v2 = new Vector3D(5, 6, 7);

        Vector3D result = v1.componentMultiply(v2);

        assertEquals(10, result.getX());
        assertEquals(18, result.getY());
        assertEquals(28, result.getZ());
    }

    @Test
    void testCrossProduct() {
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(0, 1, 0);

        Vector3D result = v1.crossProduct(v2);

        assertEquals(0, result.getX());
        assertEquals(0, result.getY());
        assertEquals(1, result.getZ());
    }

    @Test
    void testDistanceTo() {
        Vector3D v1 = new Vector3D(0, 0, 0);
        Vector3D v2 = new Vector3D(3, 4, 0);

        assertEquals(5, v1.distance(v2), 1e-10);
    }
}