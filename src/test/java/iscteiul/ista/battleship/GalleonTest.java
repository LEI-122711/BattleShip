package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Galleon")
class GalleonTest {

    // ... (os outros testes que passaram estão ótimos) ...
    @Test
    @DisplayName("Tamanho (Size) deve ser 5")
    void getSize() {
        IPosition pos = new Position(0, 0);
        Galleon galleon = new Galleon(Compass.NORTH, pos);
        assertEquals(5, galleon.getSize(), "O tamanho do Galeão deve ser sempre 5");
    }

    @Test
    @DisplayName("Construtor deve falhar com 'bearing' nulo")
    void constructor_ShouldThrowException_WhenBearingIsNull() {
        IPosition pos = new Position(0, 0);

        // Basta chamar o método. Se o erro certo for lançado, o teste passa.
        // Se não for lançado, ou for lançado um erro diferente, o teste falha.
        assertThrows(AssertionError.class, () -> {
            new Galleon(null, pos);
        }, "O construtor da superclasse 'Ship' deve lançar AssertionError se 'bearing' for nulo");
    }

    @Test
    @DisplayName("Construtor deve falhar com 'position' nula")
    void constructor_ShouldThrowException_WhenPositionIsNull() {
        // Igual para o teste da posição nula
        assertThrows(AssertionError.class, () -> {
            new Galleon(Compass.NORTH, null);
        }, "O construtor da superclasse 'Ship' deve lançar AssertionError se 'pos' for nula");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Norte")
    void checkPositions_BearingNorth() {
        IPosition startPos = new Position(5, 5);
        Galleon galleon = new Galleon(Compass.NORTH, startPos);

        List<IPosition> expectedPositions = new ArrayList<>();
        expectedPositions.add(new Position(5, 5));
        expectedPositions.add(new Position(5, 6));
        expectedPositions.add(new Position(5, 7));
        expectedPositions.add(new Position(6, 6));
        expectedPositions.add(new Position(7, 6));

        assertEquals(5, galleon.getPositions().size(), "Deve ter 5 posições");
        assertTrue(galleon.getPositions().containsAll(expectedPositions),
                "As posições para 'NORTH' não estão corretas");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Sul")
    void checkPositions_BearingSouth() {
        IPosition startPos = new Position(5, 5);
        Galleon galleon = new Galleon(Compass.SOUTH, startPos);

        List<IPosition> expectedPositions = new ArrayList<>();
        expectedPositions.add(new Position(5, 5));
        expectedPositions.add(new Position(6, 5));
        // A sua lógica original tem j=2 -> 2-3 = -1
        expectedPositions.add(new Position(7, 4)); // (row + 2, col - 1)
        expectedPositions.add(new Position(7, 5)); // (row + 2, col)
        expectedPositions.add(new Position(7, 6)); // (row + 2, col + 1)

        assertEquals(5, galleon.getPositions().size(), "Deve ter 5 posições");
        assertTrue(galleon.getPositions().containsAll(expectedPositions),
                "As posições para 'SOUTH' não estão corretas");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Este")
    void checkPositions_BearingEast() {
        IPosition startPos = new Position(5, 5);
        Galleon galleon = new Galleon(Compass.EAST, startPos);

        List<IPosition> expectedPositions = new ArrayList<>();
        expectedPositions.add(new Position(5, 5));
        // A sua lógica original tem i=1 -> 1-3 = -2
        expectedPositions.add(new Position(6, 3)); // (row + 1, col - 2)
        expectedPositions.add(new Position(6, 4)); // (row + 1, col - 1)
        expectedPositions.add(new Position(6, 5)); // (row + 1, col)
        expectedPositions.add(new Position(7, 5));

        assertEquals(5, galleon.getPositions().size(), "Deve ter 5 posições");
        assertTrue(galleon.getPositions().containsAll(expectedPositions),
                "As posições para 'EAST' não estão corretas");
    }

    @Test
    @DisplayName("Posições devem estar corretas para Oeste")
    void checkPositions_BearingWest() {
        IPosition startPos = new Position(5, 5);
        Galleon galleon = new Galleon(Compass.WEST, startPos);

        List<IPosition> expectedPositions = new ArrayList<>();
        expectedPositions.add(new Position(5, 5));
        // A sua lógica original tem i=1 -> 1-1 = 0
        expectedPositions.add(new Position(6, 5)); // (row + 1, col)
        expectedPositions.add(new Position(6, 6)); // (row + 1, col + 1)
        expectedPositions.add(new Position(6, 7)); // (row + 1, col + 2)
        expectedPositions.add(new Position(7, 5));

        assertEquals(5, galleon.getPositions().size(), "Deve ter 5 posições");
        assertTrue(galleon.getPositions().containsAll(expectedPositions),
                "As posições para 'WEST' não estão corretas");
    }

    @Test
    @DisplayName("Construtor deve falhar com 'bearing' não suportado")
    void constructor_ShouldThrowIllegalArgumentException_ForUnsupportedBearing() {
        IPosition pos = new Position(0, 0);


        Compass unsupportedBearing = Compass.UNKNOWN;

        // Verifica se a exceção correta é lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Galleon(unsupportedBearing, pos);
        }, "Deve lançar IllegalArgumentException para 'bearings' não suportados");

        // Opcional: verificar a mensagem de erro exata
        assertEquals("ERROR! invalid bearing for the galleon", exception.getMessage());
    }
}