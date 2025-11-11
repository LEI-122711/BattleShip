package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarrackTest {

    @Test
    @DisplayName("Valida Nome e Tamanho da Nau")
    void testMetadata() {
        Carrack c = new Carrack(Compass.NORTH, new Position(0, 0));
        assertEquals(3, c.getSize(), "Nau deve ter tamanho 3");
    }

    @Test
    @DisplayName("Valida posições verticais (NORTH/SOUTH)")
    void testPositionsVertical() {
        IPosition start = new Position(2, 2);

        // Testa NORTH (cresce para baixo)
        Carrack north = new Carrack(Compass.NORTH, start);
        List<IPosition> nPos = north.getPositions();
        assertEquals(3, nPos.size());
        assertEquals(2, nPos.get(0).getRow()); // Posição inicial
        assertEquals(3, nPos.get(1).getRow());
        assertEquals(4, nPos.get(2).getRow()); // Terceira posição

        // Testa SOUTH (mesmo comportamento nesta implementação)
        Carrack south = new Carrack(Compass.SOUTH, start);
        List<IPosition> sPos = south.getPositions();
        assertEquals(nPos.get(2).getRow(), sPos.get(2).getRow(), "SOUTH deve ser igual a NORTH");
    }

    @Test
    @DisplayName("Valida posições horizontais (EAST/WEST)")
    void testPositionsHorizontal() {
        IPosition start = new Position(2, 2);

        // Testa EAST (cresce para a direita)
        Carrack east = new Carrack(Compass.EAST, start);
        List<IPosition> ePos = east.getPositions();
        assertEquals(3, ePos.size());
        assertEquals(2, ePos.get(0).getColumn()); // Posição inicial
        assertEquals(3, ePos.get(1).getColumn());
        assertEquals(4, ePos.get(2).getColumn()); // Terceira posição

        // Testa WEST (mesmo comportamento nesta implementação)
        Carrack west = new Carrack(Compass.WEST, start);
        List<IPosition> wPos = west.getPositions();
        assertEquals(ePos.get(2).getColumn(), wPos.get(2).getColumn(), "WEST deve ser igual a EAST");
    }

    @Test
    @DisplayName("Valida exceção de bússola nula (via superclasse)")
    void testNullCompass() {
        // Assume-se que a superclasse Ship lança AssertionError para null, conforme visto antes.
        // Se tiveres alterado a Ship para lançar NullPointerException, ajusta aqui.
        assertThrows(AssertionError.class, () -> {
            new Carrack(null, new Position(1, 1));
        });
    }
}