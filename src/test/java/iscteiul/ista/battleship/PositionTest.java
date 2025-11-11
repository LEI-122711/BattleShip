package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Position")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PositionTest {

    private Position position;

    @BeforeEach
    void setUp() {
        position = new Position(2, 3);
    }

    @AfterEach
    void tearDown() {
        position = null;
    }

    @Test
    @DisplayName("Deve retornar a linha correta")
    void getRow() {
        assertEquals(2, position.getRow(), "A linha deve ser 2");
    }

    @Test
    @DisplayName("Deve retornar a coluna correta")
    void getColumn() {
        assertEquals(3, position.getColumn(), "A coluna deve ser 3");
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente para posições iguais")
    void testHashCode() {
        Position same = new Position(2, 3);
        assertEquals(position.hashCode(), same.hashCode(), "hashCodes devem ser iguais para posições iguais");
    }

    @Test
    @DisplayName("Deve comparar igualdade corretamente")
    void testEquals() {
        Position same = new Position(2, 3);
        Position differentRow = new Position(1, 3);
        Position differentColumn = new Position(2, 4);

        assertAll("Testes de igualdade",
                () -> assertTrue(position.equals(same), "Mesmas coordenadas devem ser iguais"),
                () -> assertFalse(position.equals(differentRow), "Linhas diferentes não devem ser iguais"),
                () -> assertFalse(position.equals(differentColumn), "Colunas diferentes não devem ser iguais"),
                () -> assertFalse(position.equals(null), "Não deve ser igual a null"),
                () -> assertFalse(position.equals("Texto"), "Não deve ser igual a objeto de outro tipo"),
                () -> assertTrue(position.equals(position), "Um objeto deve ser igual a si próprio")
        );
    }

    @Test
    @DisplayName("Deve identificar posições adjacentes corretamente")
    void isAdjacentTo() {
        Position adjacent = new Position(3, 3);   // abaixo
        Position diagonal = new Position(1, 2);   // diagonal
        Position farAway = new Position(5, 5);
        // 🔥 Casos adicionais para cobertura total
        Position farRow = new Position(10, 3);    // linha distante, coluna igual
        Position farColumn = new Position(2, 10); // coluna distante, linha igual

        assertAll("Testes de adjacência",
                () -> assertTrue(position.isAdjacentTo(adjacent), "Posição imediatamente abaixo é adjacente"),
                () -> assertTrue(position.isAdjacentTo(diagonal), "Posição diagonal é adjacente"),
                () -> assertFalse(position.isAdjacentTo(farAway), "Posição distante não é adjacente"),
                () -> assertFalse(position.isAdjacentTo(farRow), "Deve ser false se apenas a linha for distante"),
                () -> assertFalse(position.isAdjacentTo(farColumn), "Deve ser false se apenas a coluna for distante")
        );
    }

    @Test
    @DisplayName("Deve marcar posição como ocupada após chamar occupy()")
    void occupy() {
        assertFalse(position.isOccupied(), "Inicialmente não deve estar ocupada");
        position.occupy();
        assertTrue(position.isOccupied(), "Após ocupar deve estar marcada como ocupada");
    }

    @Test
    @DisplayName("Deve marcar posição como atingida após chamar shoot()")
    void shoot() {
        assertFalse(position.isHit(), "Inicialmente não deve estar atingida");
        position.shoot();
        assertTrue(position.isHit(), "Após shoot() deve estar marcada como atingida");
    }

    @Test
    @DisplayName("Deve retornar corretamente o estado de ocupação")
    void isOccupied() {
        assertFalse(position.isOccupied());
        position.occupy();
        assertTrue(position.isOccupied());
    }

    @Test
    @DisplayName("Deve retornar corretamente o estado de impacto")
    void isHit() {
        assertFalse(position.isHit());
        position.shoot();
        assertTrue(position.isHit());
    }

    @Test
    @DisplayName("toString() deve retornar string formatada corretamente")
    void testToString() {
        String expected = "Linha = 2 Coluna = 3";
        assertEquals(expected, position.toString());
    }
}
