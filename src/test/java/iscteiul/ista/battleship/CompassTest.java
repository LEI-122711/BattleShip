package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o enum Compass.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Testes da enumeração Compass")
class CompassTest {

    @Test
    @DisplayName("getDirection() devolve o caractere correto para cada direção")
    void getDirection() {
        assertAll("direções",
                () -> assertEquals('n', Compass.NORTH.getDirection()),
                () -> assertEquals('s', Compass.SOUTH.getDirection()),
                () -> assertEquals('e', Compass.EAST.getDirection()),
                () -> assertEquals('o', Compass.WEST.getDirection()),
                () -> assertEquals('u', Compass.UNKNOWN.getDirection())
        );
    }

    @Test
    @DisplayName("toString() devolve o caractere da direção")
    void testToString() {
        assertAll("toString",
                () -> assertEquals("n", Compass.NORTH.toString()),
                () -> assertEquals("s", Compass.SOUTH.toString()),
                () -> assertEquals("e", Compass.EAST.toString()),
                () -> assertEquals("o", Compass.WEST.toString()),
                () -> assertEquals("u", Compass.UNKNOWN.toString())
        );
    }

    @Test
    @DisplayName("charToCompass() converte corretamente caracteres válidos e inválidos")
    void charToCompass() {
        assertAll("charToCompass",
                () -> assertEquals(Compass.NORTH, Compass.charToCompass('n')),
                () -> assertEquals(Compass.SOUTH, Compass.charToCompass('s')),
                () -> assertEquals(Compass.EAST, Compass.charToCompass('e')),
                () -> assertEquals(Compass.WEST, Compass.charToCompass('o')),
                () -> assertEquals(Compass.UNKNOWN, Compass.charToCompass('x')),
                () -> assertEquals(Compass.UNKNOWN, Compass.charToCompass(' '))
        );
    }

    @Test
    @DisplayName("values() devolve todas as direções conhecidas")
    void values() {
        Compass[] values = Compass.values();
        assertNotNull(values);
        assertEquals(5, values.length);
        assertArrayEquals(
                new Compass[]{Compass.NORTH, Compass.SOUTH, Compass.EAST, Compass.WEST, Compass.UNKNOWN},
                values
        );
    }

    @Test
    @DisplayName("valueOf() devolve o valor enum correspondente ao nome")
    void valueOf() {
        assertAll("valueOf",
                () -> assertEquals(Compass.NORTH, Compass.valueOf("NORTH")),
                () -> assertEquals(Compass.SOUTH, Compass.valueOf("SOUTH")),
                () -> assertEquals(Compass.EAST, Compass.valueOf("EAST")),
                () -> assertEquals(Compass.WEST, Compass.valueOf("WEST")),
                () -> assertEquals(Compass.UNKNOWN, Compass.valueOf("UNKNOWN"))
        );
    }
}
