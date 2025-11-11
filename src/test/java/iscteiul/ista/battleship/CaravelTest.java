package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CaravelTest {

    @Test
    @DisplayName("Valida nome e tamanho da Caravela")
    void testMetadata() {
        Caravel c = new Caravel(Compass.NORTH, new Position(0, 0));
        assertEquals(2, c.getSize(), "Tamanho deve ser 2");
    }

    @Test
    @DisplayName("Valida que getSize() retorna sempre o valor correto")
    void testGetSizeMethod() {
        Caravel c1 = new Caravel(Compass.NORTH, new Position(0, 0));
        Caravel c2 = new Caravel(Compass.EAST, new Position(3, 3));

        assertEquals(2, c1.getSize(), "getSize() deve retornar 2 para qualquer Caravel");
        assertEquals(2, c2.getSize(), "getSize() deve retornar 2 para qualquer Caravel");
    }

    @Test
    @DisplayName("Valida posições para NORTH (cresce para baixo)")
    void testPositionsNorth() {
        IPosition start = new Position(5, 5);
        Caravel c = new Caravel(Compass.NORTH, start);
        List<IPosition> positions = c.getPositions();

        assertEquals(2, positions.size());
        // Posição inicial (5,5)
        assertEquals(5, positions.get(0).getRow());
        assertEquals(5, positions.get(0).getColumn());
        // Segunda posição (6,5) -> linha + 1
        assertEquals(6, positions.get(1).getRow());
        assertEquals(5, positions.get(1).getColumn());
    }

    @Test
    @DisplayName("Valida posições para SOUTH (igual a NORTH nesta implementação)")
    void testPositionsSouth() {
        IPosition start = new Position(5, 5);
        Caravel c = new Caravel(Compass.SOUTH, start);
        List<IPosition> positions = c.getPositions();

        assertEquals(2, positions.size());
        assertEquals(5, positions.get(0).getRow());
        assertEquals(6, positions.get(1).getRow());
        assertEquals(5, positions.get(0).getColumn());
        assertEquals(5, positions.get(1).getColumn());
    }

    @Test
    @DisplayName("Valida posições para EAST (cresce para a direita)")
    void testPositionsEast() {
        IPosition start = new Position(5, 5);
        Caravel c = new Caravel(Compass.EAST, start);
        List<IPosition> positions = c.getPositions();

        assertEquals(2, positions.size());
        // Posição inicial (5,5)
        assertEquals(5, positions.get(0).getRow());
        assertEquals(5, positions.get(0).getColumn());
        // Segunda posição (5,6) -> coluna + 1
        assertEquals(5, positions.get(1).getRow());
        assertEquals(6, positions.get(1).getColumn());
    }

    @Test
    @DisplayName("Valida posições para WEST (igual a EAST nesta implementação)")
    void testPositionsWest() {
        IPosition start = new Position(5, 5);
        Caravel c = new Caravel(Compass.WEST, start);
        List<IPosition> positions = c.getPositions();

        assertEquals(2, positions.size());
        assertEquals(5, positions.get(0).getRow());
        assertEquals(5, positions.get(1).getRow());
        assertEquals(5, positions.get(0).getColumn());
        assertEquals(6, positions.get(1).getColumn());
    }

    @Test
    @DisplayName("Lança AssertionError se bearing for nulo (validação feita em Ship)")
    void testNullCompass() {
        assertThrows(AssertionError.class, () -> {
            new Caravel(null, new Position(1, 1));
        }, "Deve lançar AssertionError se a bússola for null (verificado na superclasse Ship)");
    }

    @Test
    @DisplayName("Lança IllegalArgumentException se bearing for UNKNOWN")
    void testInvalidBearingUnknown() {
        IPosition start = new Position(0, 0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new Caravel(Compass.UNKNOWN, start);
        });
        assertTrue(ex.getMessage().contains("invalid bearing"),
                "Mensagem de erro deve indicar bearing inválido");
    }

}
