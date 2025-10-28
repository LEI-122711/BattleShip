package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompassTest {

    @Test
    @DisplayName("getDirection devolve o char correto para cada direção")
    void getDirection() {
        assertAll(
                () -> assertEquals('n', Compass.NORTH.getDirection()),
                () -> assertEquals('s', Compass.SOUTH.getDirection()),
                () -> assertEquals('e', Compass.EAST.getDirection()),
                () -> assertEquals('o', Compass.WEST.getDirection()),
                () -> assertEquals('u', Compass.UNKNOWN.getDirection())
        );
    }

    @Test
    @DisplayName("toString devolve a string correta para cada direção")
    void testToString() {
        assertAll(
                () -> assertEquals("n", Compass.NORTH.toString()),
                () -> assertEquals("s", Compass.SOUTH.toString()),
                () -> assertEquals("e", Compass.EAST.toString()),
                () -> assertEquals("o", Compass.WEST.toString()),
                () -> assertEquals("u", Compass.UNKNOWN.toString())
        );
    }

    @Test
    @DisplayName("charToCompass mapeia corretamente; desconhecidos e maiúsculas vão para UNKNOWN")
    void charToCompass() {
        assertAll(
                () -> assertEquals(Compass.NORTH, Compass.charToCompass('n')),
                () -> assertEquals(Compass.SOUTH, Compass.charToCompass('s')),
                () -> assertEquals(Compass.EAST,  Compass.charToCompass('e')),
                () -> assertEquals(Compass.WEST,  Compass.charToCompass('o')),
                () -> assertEquals(Compass.UNKNOWN, Compass.charToCompass('x')), // desconhecido
                () -> assertEquals(Compass.UNKNOWN, Compass.charToCompass('N'))  // sensível a maiúsculas
        );
    }

    @Test
    @DisplayName("values contém as 5 direções na ordem declarada")
    void values() {
        Compass[] expected = {
                Compass.NORTH, Compass.SOUTH, Compass.EAST, Compass.WEST, Compass.UNKNOWN
        };
        assertAll(
                () -> assertEquals(5, Compass.values().length),
                () -> assertArrayEquals(expected, Compass.values())
        );
    }

    @Test
    @DisplayName("valueOf aceita todos os nomes válidos do enum")
    void valueOf_valid() {
        assertAll(
                () -> assertEquals(Compass.NORTH,   Compass.valueOf("NORTH")),
                () -> assertEquals(Compass.SOUTH,   Compass.valueOf("SOUTH")),
                () -> assertEquals(Compass.EAST,    Compass.valueOf("EAST")),
                () -> assertEquals(Compass.WEST,    Compass.valueOf("WEST")),
                () -> assertEquals(Compass.UNKNOWN, Compass.valueOf("UNKNOWN"))
        );
    }

    @Test
    @DisplayName("valueOf lança exceção em nomes inválidos")
    void valueOf_invalid() {
        assertThrows(IllegalArgumentException.class, () -> Compass.valueOf("INVALID"));
    }
}
