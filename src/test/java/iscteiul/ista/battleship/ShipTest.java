package iscteiul.ista.battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

/*
Ficaram por verificar 2 ramos :')
 */

@DisplayName("Testes para a classe Ship")
class ShipTest {

    private Ship testShip;
    private IPosition startPos;

    // Dummy Ship subclass to test abstract Ship
    static class TestShip extends Ship {
        private int size;

        public TestShip(String category, Compass bearing, IPosition pos, int size) {
            super(category, bearing, pos);
            this.size = size;
            // Fill positions list for testing
            for (int i = 0; i < size; i++) {
                int row = pos.getRow();
                int col = pos.getColumn();
                if (bearing == Compass.NORTH ) {
                    row -= i;
                } else if (bearing == Compass.SOUTH) {
                    row += i;
                } else if (bearing == Compass.EAST) {
                    col += i;
                } else if (bearing == Compass.WEST) {
                    col -= i;
                }
                getPositions().add(new Position(row, col));
            }
        }

        @Override
        public Integer getSize() {
            return size;
        }
    }

    @BeforeEach
    void setUp() {
        startPos = new Position(2, 3);
        testShip = new TestShip("caravela", Compass.EAST, startPos, 3);
    }

    @Test
    @DisplayName("Testar o construtor")
    void testConstructor() {
        assertThrows(AssertionError.class, () -> {
            new TestShip("fragata", null, startPos, 3);
        });
        assertThrows(AssertionError.class, () -> {
            new TestShip("fragata", Compass.EAST, null, 3);
        });

    }

    @Test
    @DisplayName("Testar os getters")
    void testGetters() {
        assertEquals("caravela", testShip.getCategory());
        assertEquals(Compass.EAST, testShip.getBearing());
        assertEquals(startPos, testShip.getPosition());
        assertEquals(3, testShip.getSize());
    }

    @Test
    @DisplayName("Testar o método Occupies")
    void testOccupies() {
        assertThrows(AssertionError.class, () -> {
            testShip.occupies(null);
        });
        IPosition occupied = new Position(2, 4);
        assertTrue(testShip.occupies(occupied));
        IPosition notOccupied = new Position(5, 5);
        assertFalse(testShip.occupies(notOccupied));
    }


    @DisplayName("Testar se ainda flutua")
    @Test
    void testStillFloating() {
        assertTrue(testShip.stillFloating());
        for (IPosition pos : testShip.getPositions()) {
            pos.shoot();
        }
        assertFalse(testShip.stillFloating());
    }

    @DisplayName("Testar o testShoot")
    @Test
    void testShoot() {
        assertThrows(AssertionError.class, () -> {
            testShip.shoot(null);
        });
        IPosition target = new Position(2, 3);
        testShip.shoot(target);
        assertTrue(testShip.getPositions().get(0).isHit());
    }

    @Test
    @DisplayName("Testar o método que verifica se as posições são demasiado próximas ou não")
    void testTooCloseToPosition() {
        IPosition adjacent = new Position(3, 3);
        assertTrue(testShip.tooCloseTo(adjacent));
        IPosition far = new Position(10, 10);
        assertFalse(testShip.tooCloseTo(far));
    }

    @Test
    @DisplayName("Testar o método que verifica se os navios estão demasiado próximos ou não")
    void testTooCloseToOtherShip() {
        assertThrows(AssertionError.class, () -> {
            testShip.tooCloseTo((IShip) null);
        });
        IShip other = new TestShip("barca", Compass.SOUTH, new Position(1, 3), 2);
        assertTrue(testShip.tooCloseTo(other));

        IShip farShip = new TestShip("barca", Compass.SOUTH, new Position(10, 10), 2);
        assertFalse(testShip.tooCloseTo(farShip));
    }

    @Test
    @DisplayName("Testar as posições quando o novio está orientado a East")
    void testTopBottomLeftRightPositions_East() {
        assertEquals(2, testShip.getTopMostPos());
        assertEquals(2, testShip.getBottomMostPos());
        assertEquals(3, testShip.getLeftMostPos());
        assertEquals(5, testShip.getRightMostPos());
    }

    @Test
    @DisplayName("Testar as posições quando o novio está orientado a South")
    void testTopBottomLeftRightPositions_South() {
        Ship southShip = new TestShip("fragata", Compass.SOUTH, new Position(5, 5), 3);
        // Positions: (5,5), (6,5), (7,5)
        assertEquals(5, southShip.getTopMostPos());
        assertEquals(7, southShip.getBottomMostPos());
        assertEquals(5, southShip.getLeftMostPos());
        assertEquals(5, southShip.getRightMostPos());
    }

    @Test
    @DisplayName("Testar as posições quando o novio está orientado a North")
    void testTopBottomLeftRightPositions_North() {
        Ship northShip = new TestShip("fragata", Compass.NORTH, new Position(5, 5), 3);
        // Positions: (5,5), (4,5), (3,5)
        assertEquals(3, northShip.getTopMostPos());
        assertEquals(5, northShip.getBottomMostPos());
        assertEquals(5, northShip.getLeftMostPos());
        assertEquals(5, northShip.getRightMostPos());
    }
    @Test
    @DisplayName("Testar as posições quando o novio está orientado a West")
    void testTopBottomLeftRightPositions_West() {
        Ship westShip = new TestShip("fragata", Compass.WEST, new Position(5, 5), 3);
        // Positions: (5,5), (5,4), (5,3)
        assertEquals(5, westShip.getTopMostPos());
        assertEquals(5, westShip.getBottomMostPos());
        assertEquals(3, westShip.getLeftMostPos());
        assertEquals(5, westShip.getRightMostPos());
    }

    @Test
    @DisplayName("Testar os construtores de vários tipos")
    void testBuildShipAllKinds() {
        assertTrue(Ship.buildShip("barca", Compass.NORTH, new Position(1, 1)) instanceof Barge);
        assertTrue(Ship.buildShip("caravela", Compass.EAST, new Position(1, 1)) instanceof Caravel);
        assertTrue(Ship.buildShip("nau", Compass.SOUTH, new Position(1, 1)) instanceof Carrack);
        assertTrue(Ship.buildShip("fragata", Compass.WEST, new Position(1, 1)) instanceof Frigate);
        assertTrue(Ship.buildShip("galeao", Compass.NORTH, new Position(1, 1)) instanceof Galleon);
        assertNull(Ship.buildShip("inexistente", Compass.SOUTH, new Position(1, 1)));
    }

    @Test
    @DisplayName("Testar os prints")
    void testToStringContainsInfo() {
        String str = testShip.toString();
        assertTrue(str.contains(testShip.getCategory()));
        assertTrue(str.contains(Compass.EAST.toString()));
        assertTrue(str.contains(startPos.toString()));
    }
}
