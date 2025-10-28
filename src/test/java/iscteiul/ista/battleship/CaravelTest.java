package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CaravelTest {

    @Test
    @DisplayName("getSize devolve 2 (tamanho fixo da Caravel)")
    void getSize() {
        Caravel c = new Caravel(Compass.NORTH, new Position(0, 0));
        assertEquals(2, c.getSize());
    }

    @ParameterizedTest(name = "Caravel {0}: cria 2 posições VERTICAIS (mesma coluna, linhas consecutivas)")
    @EnumSource(value = Compass.class, names = {"NORTH", "SOUTH"})
    @DisplayName("Construtor cria 2 posições verticais para NORTH/SOUTH")
    void positionsForNorthSouth(Compass bearing) {
        Position origin = new Position(1, 2);
        Caravel c = new Caravel(bearing, origin);

        List<IPosition> pos = c.getPositions();
        assertEquals(2, pos.size());

        assertAll(
                () -> assertEquals(1, pos.get(0).getRow()),
                () -> assertEquals(2, pos.get(0).getColumn()),
                () -> assertEquals(2, pos.get(1).getRow()),
                () -> assertEquals(2, pos.get(1).getColumn())
        );

        // a primeira posição é cópia lógica do origin (mesmas coords) mas instância diferente
        assertAll(
                () -> assertEquals(origin, pos.get(0)),
                () -> assertNotSame(origin, pos.get(0))
        );
    }

    @ParameterizedTest(name = "Caravel {0}: cria 2 posições HORIZONTAIS (mesma linha, colunas consecutivas)")
    @EnumSource(value = Compass.class, names = {"EAST", "WEST"})
    @DisplayName("Construtor cria 2 posições horizontais para EAST/WEST")
    void positionsForEastWest(Compass bearing) {
        Position origin = new Position(5, 6);
        Caravel c = new Caravel(bearing, origin);

        List<IPosition> pos = c.getPositions();
        assertEquals(2, pos.size());

        assertAll(
                () -> assertEquals(5, pos.get(0).getRow()),
                () -> assertEquals(6, pos.get(0).getColumn()),
                () -> assertEquals(5, pos.get(1).getRow()),
                () -> assertEquals(7, pos.get(1).getColumn())
        );

        assertAll(
                () -> assertEquals(origin, pos.get(0)),
                () -> assertNotSame(origin, pos.get(0))
        );
    }

    @Test
    @DisplayName("Construtor lança IllegalArgumentException se bearing == UNKNOWN")
    void constructorThrowsIllegalArgumentIfBearingIsUnknown() {
        Position pos = new Position(1, 1);
        assertThrows(IllegalArgumentException.class, () -> new Caravel(Compass.UNKNOWN, pos));
    }

    @Test
    @DisplayName("Construtor com bearing == null lança AssertionError (Ship) ou NullPointerException (Caravel)")
    void constructorThrowsIfBearingIsNull() {
        Position pos = new Position(1, 1);
        Throwable t = assertThrows(Throwable.class, () -> new Caravel(null, pos));
        assertTrue(
                t instanceof AssertionError || t instanceof NullPointerException,
                () -> "Esperava AssertionError ou NullPointerException, mas foi: " + t.getClass()
        );
    }

    @Test
    @DisplayName("Alterar a posição original após o construtor não afeta posições internas (cópias independentes)")
    void originalModificationDoesNotAffectInternalPositions() {
        Position origin = new Position(3, 3);
        Caravel c = new Caravel(Compass.EAST, origin);

        origin.shoot(); // altera só o original

        List<IPosition> pos = c.getPositions();
        assertAll(
                () -> assertFalse(pos.get(0).isHit()),
                () -> assertFalse(pos.get(1).isHit())
        );
    }

    @Test
    @DisplayName("Alterar posição interna não afeta a posição original (independência nos dois sentidos)")
    void internalModificationDoesNotAffectOriginal() {
        Position origin = new Position(4, 4);
        Caravel c = new Caravel(Compass.SOUTH, origin);

        IPosition stored = c.getPositions().get(1);
        stored.occupy(); // altera apenas a interna

        assertAll(
                () -> assertTrue(stored.isOccupied()),
                () -> assertFalse(origin.isOccupied())
        );
    }
}
