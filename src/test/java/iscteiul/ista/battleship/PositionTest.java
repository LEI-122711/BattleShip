package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    @DisplayName("getRow devolve a linha correta")
    void getRow() {
        Position p = new Position(2, 3);
        assertEquals(2, p.getRow());
    }

    @Test
    @DisplayName("getColumn devolve a coluna correta")
    void getColumn() {
        Position p = new Position(2, 3);
        assertEquals(3, p.getColumn());
    }

    @Test
    @DisplayName("hashCode é consistente e respeita a relação com equals")
    void testHashCode() {
        Position p1 = new Position(1, 1);
        Position p2 = new Position(1, 1); // equals a p1 (mesma row/col)

        assertAll(
                // iguais -> hashCodes iguais enquanto o estado for o mesmo
                () -> assertEquals(p1.hashCode(), p2.hashCode()),
                // consistência (sem alterar estado)
                () -> assertEquals(p1.hashCode(), p1.hashCode())
        );

        // mudar estado altera hash (implementação inclui isOccupied/isHit)
        p1.occupy();
        assertNotEquals(p1.hashCode(), p2.hashCode(), "Alterar isOccupied deve mudar o hash");

        // igualar o estado volta a alinhar os hashCodes
        p2.occupy();
        assertEquals(p1.hashCode(), p2.hashCode());

        // alterar isHit muda novamente
        p1.shoot();
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("equals é reflexivo, simétrico, transitivo e ignora estados (occupied/hit)")
    void testEquals() {
        Position a = new Position(4, 5);
        Position b = new Position(4, 5);
        Position c = new Position(4, 5);
        Position diffRow = new Position(5, 5);
        Position diffCol = new Position(4, 6);

        assertAll(
                // básicos
                () -> assertEquals(a, a, "Reflexivo"),
                () -> assertEquals(a, b, "Igualdade por row/col"),
                () -> assertEquals(b, a, "Simétrico"),
                () -> assertEquals(a, c, "Transitivo (a=b, a=c)"),
                () -> assertNotEquals(a, diffRow),
                () -> assertNotEquals(a, diffCol),
                () -> assertNotEquals(a, null),
                () -> assertNotEquals(a, new Object())
        );

        // equals não depende de isOccupied/isHit
        a.occupy();
        a.shoot();
        assertEquals(a, b, "Estados distintos não afetam equals (só row/col contam)");
    }

    @Test
    @DisplayName("isAdjacentTo é true para vizinhos (inclui diagonais e a própria célula) e false caso contrário")
    void isAdjacentTo() {
        Position center = new Position(2, 2);

        assertAll(
                // a própria célula
                () -> assertTrue(center.isAdjacentTo(new Position(2, 2))),
                // vizinhos ortogonais
                () -> assertTrue(center.isAdjacentTo(new Position(1, 2))),
                () -> assertTrue(center.isAdjacentTo(new Position(3, 2))),
                () -> assertTrue(center.isAdjacentTo(new Position(2, 1))),
                () -> assertTrue(center.isAdjacentTo(new Position(2, 3))),
                // vizinhos diagonais
                () -> assertTrue(center.isAdjacentTo(new Position(1, 1))),
                () -> assertTrue(center.isAdjacentTo(new Position(1, 3))),
                () -> assertTrue(center.isAdjacentTo(new Position(3, 1))),
                () -> assertTrue(center.isAdjacentTo(new Position(3, 3))),
                // não adjacentes
                () -> assertFalse(center.isAdjacentTo(new Position(4, 4))),
                () -> assertFalse(center.isAdjacentTo(new Position(0, 0))),
                () -> assertFalse(center.isAdjacentTo(new Position(2, 4))),
                () -> assertFalse(center.isAdjacentTo(new Position(4, 2)))
        );
    }

    @Test
    @DisplayName("occupy altera o estado para ocupado")
    void occupy() {
        Position p = new Position(0, 0);
        assertFalse(p.isOccupied(), "Estado inicial deve ser não-ocupado");
        p.occupy();
        assertTrue(p.isOccupied(), "Depois de occupy() deve estar ocupado");
    }

    @Test
    @DisplayName("shoot altera o estado para atingido")
    void shoot() {
        Position p = new Position(0, 1);
        assertFalse(p.isHit(), "Estado inicial deve ser não-atingido");
        p.shoot();
        assertTrue(p.isHit(), "Depois de shoot() deve estar atingido");
    }

    @Test
    @DisplayName("isOccupied reflete corretamente o estado")
    void isOccupied() {
        Position p = new Position(6, 7);
        assertFalse(p.isOccupied());
        p.occupy();
        assertTrue(p.isOccupied());
    }

    @Test
    @DisplayName("isHit reflete corretamente o estado")
    void isHit() {
        Position p = new Position(8, 9);
        assertFalse(p.isHit());
        p.shoot();
        assertTrue(p.isHit());
    }

    @Test
    @DisplayName("toString segue o formato 'Linha = X Coluna = Y'")
    void testToString() {
        Position p = new Position(10, 11);
        assertEquals("Linha = 10 Coluna = 11", p.toString());
    }
}
