import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StarTest {

    private Star createStar() {
        return new Star(
                1.989e30,
                696340000,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                1.0,
                5778
        );
    }

    @Test
    void testConstructorAndGetters() {
        Star star = createStar();

        assertEquals(1.989e30, star.getMass());
        assertEquals(696340000, star.getRadius());
        assertEquals(1.0, star.getLuminosity());
        assertEquals(5778, star.getTemperature());
    }

    @Test
    void testSetLuminosity() {
        Star star = createStar();

        star.setLuminosity(2.5);

        assertEquals(2.5, star.getLuminosity());
    }

    @Test
    void testInvalidLuminosity() {
        Star star = createStar();

        assertThrows(IllegalArgumentException.class,
                () -> star.setLuminosity(-1));

        assertThrows(IllegalArgumentException.class,
                () -> star.setLuminosity(Double.NaN));

        assertThrows(IllegalArgumentException.class,
                () -> star.setLuminosity(Double.POSITIVE_INFINITY));
    }

    @Test
    void testSetTemperature() {
        Star star = createStar();
        star.setTemperature(6000);
        assertEquals(6000, star.getTemperature());
    }

    @Test
    void testInvalidTemperature() {
        Star star = createStar();
        assertThrows(IllegalArgumentException.class,
                () -> star.setTemperature(-1));
    }

    @Test
    void testZeroTemperatureAndLuminosity() {
        Star star = createStar();

        star.setTemperature(0);
        star.setLuminosity(0);

        assertEquals(0, star.getTemperature());
        assertEquals(0, star.getLuminosity());
    }
}