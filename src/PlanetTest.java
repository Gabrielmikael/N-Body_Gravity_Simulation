import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlanetTest {

    @Test
    void testPlanetConstructor() {
        Vector3D position = new Vector3D(1, 2, 3);
        Vector3D velocity = new Vector3D(4, 5, 6);

        Planet planet = new Planet(
                5.97e24,
                6.371e6,
                position,
                velocity,
                Atmosphere.NITROGEN_OXYGEN
        );

        assertEquals(5.97e24, planet.getMass());
        assertEquals(6.371e6, planet.getRadius());
        assertEquals(position, planet.getPosition());
        assertEquals(velocity, planet.getVelocity());
        assertEquals(Atmosphere.NITROGEN_OXYGEN, planet.getAtmosphere());
    }

    @Test
    void testSetAtmosphere() {
        Planet planet = new Planet(
                5.97e24,
                6.371e6,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                Atmosphere.HYDROGEN_HELIUM
        );

        planet.setAtmosphere(Atmosphere.CARBON_DIOXIDE);
        assertEquals(Atmosphere.CARBON_DIOXIDE, planet.getAtmosphere());
    }


    @Test
    void testNullAtmosphereThrowsException() {
        Planet planet = new Planet(
                5.97e24,
                6.371e6,
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, 0),
                Atmosphere.NONE
        );

        assertThrows(IllegalArgumentException.class,
                () -> planet.setAtmosphere(null));
    }
}