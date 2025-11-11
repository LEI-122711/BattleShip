package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Assumindo que 'Position' e 'Compass' estão disponíveis
// import iscteiul.ista.battleship.Compass;
// import iscteiul.ista.battleship.IPosition;
// import iscteiul.ista.battleship.Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Frigate")
class FrigateTest {

    @Test
    @DisplayName("Tamanho (Size) deve ser 4")
    void getSize() {
        // Preenche o teste que já existia
        IPosition pos = new Position(0, 0); // Posição inicial irrelevante
        Frigate frigate = new Frigate(Compass.NORTH, pos);
        assertEquals(4, frigate.getSize(), "O tamanho da Fragata deve ser sempre 4");
    }

    @Test
    @DisplayName("Construtor deve falhar com 'bearing' nulo")
    void constructor_ShouldThrowAssertionError_WhenBearingIsNull() {
        // Testar a validação de 'null' que vem da superclasse 'Ship'
        IPosition pos = new Position(0, 0);
        assertThrows(AssertionError.class, () -> {
            new Frigate(null, pos);
        }, "A superclasse 'Ship' deve lançar AssertionError se 'bearing' for nulo");
    }

    @Test
    @DisplayName("Construtor deve falhar com 'position' nula")
    void constructor_ShouldThrowAssertionError_WhenPositionIsNull() {
        // Testar a validação de 'null' que vem da superclasse 'Ship'
        assertThrows(AssertionError.class, () -> {
            new Frigate(Compass.NORTH, null);
        }, "A superclasse 'Ship' deve lançar AssertionError se 'pos' for nula");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Norte (Vertical)")
    void checkPositions_BearingNorth_IsVertical() {
        IPosition startPos = new Position(5, 5);
        Frigate frigate = new Frigate(Compass.NORTH, startPos);

        List<IPosition> expected = new ArrayList<>();
        expected.add(new Position(5, 5)); // row
        expected.add(new Position(6, 5)); // row + 1
        expected.add(new Position(7, 5)); // row + 2
        expected.add(new Position(8, 5)); // row + 3

        assertEquals(4, frigate.getPositions().size(), "Deve ter 4 posições");
        assertTrue(frigate.getPositions().containsAll(expected),
                "Posições 'NORTH' (vertical) não estão corretas");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Sul (Vertical)")
    void checkPositions_BearingSouth_IsVertical() {
        // A lógica da classe 'Frigate' trata NORTH e SOUTH da mesma forma
        IPosition startPos = new Position(5, 5);
        Frigate frigate = new Frigate(Compass.SOUTH, startPos);

        List<IPosition> expected = new ArrayList<>();
        expected.add(new Position(5, 5)); // row
        expected.add(new Position(6, 5)); // row + 1
        expected.add(new Position(7, 5)); // row + 2
        expected.add(new Position(8, 5)); // row + 3

        assertEquals(4, frigate.getPositions().size(), "Deve ter 4 posições");
        assertTrue(frigate.getPositions().containsAll(expected),
                "Posições 'SOUTH' (vertical) não estão corretas");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Este (Horizontal)")
    void checkPositions_BearingEast_IsHorizontal() {
        IPosition startPos = new Position(5, 5);
        Frigate frigate = new Frigate(Compass.EAST, startPos);

        List<IPosition> expected = new ArrayList<>();
        expected.add(new Position(5, 5)); // col
        expected.add(new Position(5, 6)); // col + 1
        expected.add(new Position(5, 7)); // col + 2
        expected.add(new Position(5, 8)); // col + 3

        assertEquals(4, frigate.getPositions().size(), "Deve ter 4 posições");
        assertTrue(frigate.getPositions().containsAll(expected),
                "Posições 'EAST' (horizontal) não estão corretas");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Oeste (Horizontal)")
    void checkPositions_BearingWest_IsHorizontal() {
        // A lógica da classe 'Frigate' trata EAST e WEST da mesma forma
        IPosition startPos = new Position(5, 5);
        Frigate frigate = new Frigate(Compass.WEST, startPos);

        List<IPosition> expected = new ArrayList<>();
        expected.add(new Position(5, 5)); // col
        expected.add(new Position(5, 6)); // col + 1
        expected.add(new Position(5, 7)); // col + 2
        expected.add(new Position(5, 8)); // col + 3

        assertEquals(4, frigate.getPositions().size(), "Deve ter 4 posições");
        assertTrue(frigate.getPositions().containsAll(expected),
                "Posições 'WEST' (horizontal) não estão corretas");
    }

    @Test
    @DisplayName("Construtor deve falhar com 'bearing' não suportado")
    void constructor_ShouldThrowIllegalArgumentException_ForUnsupportedBearing() {
        IPosition pos = new Position(0, 0);

        // NOTA: Tal como no Galleon, este teste só funciona
        // se o seu 'enum Compass' tiver mais valores além dos 4 principais
        // (ex: NORTHEAST).

        // Se o seu enum só tiver os 4 valores, o 'default:' na classe Frigate
        // é código morto e deve ser removido para 100% de cobertura.

        Compass unsupportedBearing = Compass.UNKNOWN; // <-- Ajuste conforme necessário

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Frigate(unsupportedBearing, pos);
        }, "Deve lançar IllegalArgumentException para 'bearings' não suportados");

        assertEquals("ERROR! invalid bearing for thr frigate", exception.getMessage());
    }
}