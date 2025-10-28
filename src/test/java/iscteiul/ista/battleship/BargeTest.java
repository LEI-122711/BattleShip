package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BargeTest {

    @Test
    @DisplayName("getSize devolve 1 (tamanho fixo da Barge)")
    void getSize() {
        Barge b = new Barge(Compass.NORTH, new Position(0, 0));
        assertEquals(1, b.getSize());
    }

    @ParameterizedTest(name = "Barge({0}, pos) -> 1 posição copiada (igual por coordenadas, instância diferente)")
    @EnumSource(value = Compass.class, names = {"NORTH", "SOUTH", "EAST", "WEST"})
    @DisplayName("Para qualquer bearing, Barge tem exatamente 1 posição copiada da original")
    void initialPositionIsCopiedForAllBearings(Compass bearing) {
        Position original = new Position(2, 3);
        Barge b = new Barge(bearing, original);

        List<IPosition> positions = b.getPositions();
        assertEquals(1, positions.size());

        IPosition stored = positions.get(0);
        assertAll(
                () -> assertEquals(original, stored, "Mesmas coordenadas"),
                () -> assertNotSame(original, stored, "Deve ser cópia (instância diferente)"),
                () -> assertEquals(2, stored.getRow()),
                () -> assertEquals(3, stored.getColumn())
        );
    }

    @Test
    @DisplayName("Alterar a posição original após o construtor não afeta a posição interna (cópia independente)")
    void originalModificationDoesNotAffectBargePosition() {
        Position original = new Position(4, 5);
        Barge b = new Barge(Compass.EAST, original);

        original.occupy(); // altera original depois de criar a barca

        IPosition stored = b.getPositions().get(0);
        assertAll(
                () -> assertTrue(original.isOccupied(), "Original foi ocupada"),
                () -> assertFalse(stored.isOccupied(), "Cópia interna não deve mudar")
        );
    }

    @Test
    @DisplayName("Alterar a posição interna da barca não afeta a posição original (independência nos dois sentidos)")
    void bargeInternalModificationDoesNotAffectOriginal() {
        Position original = new Position(7, 8);
        Barge b = new Barge(Compass.WEST, original);

        IPosition stored = b.getPositions().get(0);
        stored.shoot(); // muda apenas a posição interna

        assertAll(
                () -> assertTrue(stored.isHit(), "Interna deve refletir alteração"),
                () -> assertFalse(original.isHit(), "Original não deve ser afetada")
        );
    }
}
