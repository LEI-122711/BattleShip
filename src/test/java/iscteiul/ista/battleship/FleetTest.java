package iscteiul.ista.battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
Ficaram alguns branch para testar -> confirmar com o stor
 */


class FleetTest {

    private Fleet fleet;
    private IShip frigate;
    private IShip galleon;
    private IShip barge;
    private IShip galleonToColide;

    @BeforeEach
    void setUp() {
        fleet = new Fleet();
        frigate = new Frigate(Compass.SOUTH, new Position(0, 0));
        galleon = new Galleon(Compass.EAST, new Position(7, 7));
        galleonToColide = new Galleon(Compass.EAST, new Position(2, 2));
        barge = new Barge(Compass.SOUTH, new Position(4, 5));
    }

    @Test
    void testAddShipSuccessfully() {
        assertTrue(fleet.addShip(frigate));
        assertTrue(fleet.addShip(galleon));
        assertFalse(fleet.addShip(galleonToColide));
        System.out.println(fleet.getShips());
        assertEquals(2, fleet.getShips().size());
    }

    @Test
    void testAddShipOutsideBoard() {
        // Posicao fora do tabuleiro (assumindo BOARD_SIZE = 10)
        IShip invalidShip = new Frigate(Compass.NORTH, new Position(10, 10));
        assertFalse(fleet.addShip(invalidShip));
        assertEquals(0, fleet.getShips().size());
    }

    @Test
    void testAddShipCollision() {
        fleet.addShip(frigate);
        // Tenta adicionar outra fragata que colida
        IShip collidingShip = new Frigate(Compass.NORTH, new Position(0, 0));
        assertFalse(fleet.addShip(collidingShip));
        assertEquals(1, fleet.getShips().size());
    }

    @Test
    void testGetShipsLike() {
        fleet.addShip(frigate);
        fleet.addShip(galleon);
        fleet.addShip(barge);

        List<IShip> fragates = fleet.getShipsLike("Fragata");
        assertEquals(1, fragates.size());
        assertEquals(frigate, fragates.get(0));

        List<IShip> galleons = fleet.getShipsLike("Galeao");
        assertEquals(1, galleons.size());
        assertEquals(galleon, galleons.get(0));
    }

    @Test
    void testGetFloatingShips() {
        fleet.addShip(frigate);
        fleet.addShip(barge);

        // Atira à fragata
        frigate.getPositions().get(0).shoot();

        List<IShip> floatingShips = fleet.getFloatingShips();
        // Barge ainda flutua
        assertTrue(floatingShips.contains(barge));
    }

    @Test
    void testShipAt() {
        fleet.addShip(frigate);
        fleet.addShip(galleon);

        // Posição ocupada pela fragata
        IPosition pos = frigate.getPositions().get(0);
        assertEquals(frigate, fleet.shipAt(pos));

        // Posição não ocupada
        IPosition emptyPos = new Position(9, 9);
        assertNull(fleet.shipAt(emptyPos));
    }

    @Test
    void testPrintStatus() {
        fleet.addShip(frigate);
        fleet.addShip(galleon);
        fleet.addShip(barge);

        // Apenas testamos se não lança exceção
        assertDoesNotThrow(() -> fleet.printStatus());
    }
}
