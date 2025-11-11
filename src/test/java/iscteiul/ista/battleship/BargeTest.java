package iscteiul.ista.battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BargeTest {

    private Barge barge;
    private IPosition startPos;

    @BeforeEach
    void setUp() {
        // Inicializa uma posição e uma barca antes de cada teste
        startPos = new Position(3, 3);
        barge = new Barge(Compass.NORTH, startPos);
    }

    @Test
    @DisplayName("Verificar metadados da Barca (Tamanho)")
    void testBargeMetadata() {
        assertEquals(1, barge.getSize(), "O tamanho da Barca deve ser sempre 1");
    }

    @Test
    @DisplayName("Verificar inicialização de posição única")
    void testBargePosition() {
        // Verifica se a orientação foi guardada corretamente
        assertEquals(Compass.NORTH, barge.getBearing(), "A orientação inicial deve ser preservada");

        // Verifica as posições ocupadas
        List<IPosition> positions = barge.getPositions();
        assertNotNull(positions, "A lista de posições não deve ser nula");
        assertEquals(1, positions.size(), "A Barca deve ocupar exatamente 1 posição");

        // Verifica se a posição ocupada é exatamente a que foi passada no construtor
        IPosition occupiedPos = positions.get(0);
        assertEquals(startPos.getRow(), occupiedPos.getRow(), "A linha da posição ocupada deve ser igual à inicial");
        assertEquals(startPos.getColumn(), occupiedPos.getColumn(), "A coluna da posição ocupada deve ser igual à inicial");
    }

    @Test
    @DisplayName("Verificar independência da orientação para tamanho 1")
    void testOrientationIndependence() {
        // Como a Barca tem tamanho 1, a posição ocupada deve ser a mesma independentemente da orientação
        IPosition pos = new Position(5, 5);

        Barge northBarge = new Barge(Compass.NORTH, pos);
        Barge eastBarge = new Barge(Compass.EAST, pos);
        Barge southBarge = new Barge(Compass.SOUTH, pos);
        Barge westBarge = new Barge(Compass.WEST, pos);

        assertAll("Verificar todas as orientações",
                () -> assertEquals(northBarge.getPositions().get(0).getRow(), eastBarge.getPositions().get(0).getRow()),
                () -> assertEquals(northBarge.getPositions().get(0).getColumn(), eastBarge.getPositions().get(0).getColumn()),
                () -> assertEquals(northBarge.getPositions().get(0).getRow(), southBarge.getPositions().get(0).getRow()),
                () -> assertEquals(northBarge.getPositions().get(0).getRow(), westBarge.getPositions().get(0).getRow())
        );
    }
}