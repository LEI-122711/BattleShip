package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarrackTest {

    @Test
    @DisplayName("getSize devolve 3 (tamanho fixo da Carrack/Nau)")
    void getSize() {
        Carrack c = new Carrack(Compass.NORTH, new Position(0, 0));
        assertEquals(3, c.getSize());
    }

    @ParameterizedTest(name = "Carrack {0}: cria 3 posições VERTICAIS (mesma coluna, linhas consecutivas)")
    @EnumSource(value = Compass.class, names = {"NORTH", "SOUTH"})
    @DisplayName("Construtor cria 3 posições verticais para NORTH/SOUTH")
    void positionsForNorthSouth(Compass bearing) {
        Position origin = new Position(1, 2);
        Carrack c = new Carrack(bearing, origin);

        List<IPosition> pos = c.getPositions();
        assertEquals(3, pos.size(), "Carrack deve ter exatamente 3 posições");

        assertAll(
                () -> assertEquals(1, pos.get(0).getRow()),
                () -> assertEquals(2, pos.get(0).getColumn()),
                () -> assertEquals(2, pos.get(1).getRow()),
                () -> assertEquals(2, pos.get(1).getColumn()),
                () -> assertEquals(3, pos.get(2).getRow()),
                () -> assertEquals(2, pos.get(2).getColumn())
        );

        // 1ª posição é cópia lógica do origin (mesmas coords) mas instância diferente
        assertAll(
                () -> assertEquals(origin, pos.get(0)),
                () -> assertNotSame(origin, pos.get(0))
        );
    }

    @ParameterizedTest(name = "Carrack {0}: cria 3 posições HORIZONTAIS (mesma linha, colunas consecutivas)")
    @EnumSource(value = Compass.class, names = {"EAST", "WEST"})
    @DisplayName("Construtor cria 3 posições horizontais para EAST/WEST")
    void positionsForEastWest(Compass bearing) {
        Position origin = new Position(5, 6);
        Carrack c = new Carrack(bearing, origin);

        List<IPosition> pos = c.getPositions();
        assertEquals(3, pos.size(), "Carrack deve ter exatamente 3 posições");

        assertAll(
                () -> assertEquals(5, pos.get(0).getRow()),
                () -> assertEquals(6, pos.get(0).getColumn()),
                () -> assertEquals(5, pos.get(1).getRow()),
                () -> assertEquals(7, pos.get(1).getColumn()),
                () -> assertEquals(5, pos.get(2).getRow()),
                () -> assertEquals(8, pos.get(2).getColumn())
        );

        // 1ª posição é cópia lógica do origin (mesmas coords) mas instância diferente
        assertAll(
                () -> assertEquals(origin, pos.get(0)),
                () -> assertNotSame(origin, pos.get(0))
        );
    }

    @Test
    @DisplayName("Construtor lança IllegalArgumentException se bearing == UNKNOWN")
    void constructorThrowsIllegalArgumentIfBearingIsUnknown() {
        Position pos = new Position(1, 1);
        assertThrows(IllegalArgumentException.class, () -> new Carrack(Compass.UNKNOWN, pos));
    }

    @Test
    @DisplayName("Construtor com bearing == null lança AssertionError (Ship) ou NullPointerException (switch)")
    void constructorThrowsIfBearingIsNull() {
        Position pos = new Position(1, 1);
        Throwable t = assertThrows(Throwable.class, () -> new Carrack(null, pos));
        assertTrue(
                t instanceof AssertionError || t instanceof NullPointerException,
                () -> "Esperava AssertionError ou NullPointerException, mas foi: " + t.getClass()
        );
    }

    @Test
    @DisplayName("Alterar a posição original após o construtor não afeta posições internas (cópias independentes)")
    void originalModificationDoesNotAffectInternalPositions() {
        Position origin = new Position(3, 3);
        Carrack c = new Carrack(Compass.EAST, origin);

        origin.shoot(); // mexe só no original

        List<IPosition> pos = c.getPositions();
        assertAll(
                () -> assertFalse(pos.get(0).isHit()),
                () -> assertFalse(pos.get(1).isHit()),
                () -> assertFalse(pos.get(2).isHit())
        );
    }

    @Test
    @DisplayName("Alterar uma posição interna não afeta a posição original (independência nos dois sentidos)")
    void internalModificationDoesNotAffectOriginal() {
        Position origin = new Position(4, 4);
        Carrack c = new Carrack(Compass.SOUTH, origin);

        IPosition mid = c.getPositions().get(1);
        assertFalse(origin.isOccupied());
        mid.occupy(); // altera apenas a interna

        assertAll(
                () -> assertTrue(mid.isOccupied()),
                () -> assertFalse(origin.isOccupied())
        );
    }
}
