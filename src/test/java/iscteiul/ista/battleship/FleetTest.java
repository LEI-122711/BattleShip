package iscteiul.ista.battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static iscteiul.ista.battleship.IFleet.BOARD_SIZE;
import static iscteiul.ista.battleship.IFleet.FLEET_SIZE;
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

        Fleet fleetToFill = new Fleet();
        Position[] positions = {
                new Position(0,0),
                new Position(0,2),
                new Position(0,4),
                new Position(2,0),
                new Position(2,2),
                new Position(2,4),
                new Position(4,0),
                new Position(4,2),
                new Position(4,4),
                new Position(6,0),
                new Position(6,2)
        };

        for (Position pos : positions) {
            Barge barge = new Barge(Compass.NORTH, pos);
            boolean added = fleetToFill.addShip(barge);
            System.out.println("Adicionada barca em " + pos.getRow() + "," + pos.getColumn() + ": " + added);
        }
        Barge barge = new Barge(Compass.SOUTH, new Position(6, 4));
        System.out.println(fleetToFill.getShips().size());
        assertFalse(fleetToFill.addShip(barge));


    }


    @Test
    void testIsInsideBoardBranches() {
        Fleet fleet = new Fleet();

        // 1️⃣ Fora à esquerda
        IShip left = new Frigate(Compass.NORTH, new Position(0, -1));
        assertFalse(fleet.addShip(left));

        // 2️⃣ Fora à direita
        IShip right = new Frigate(Compass.NORTH, new Position(0, BOARD_SIZE));
        assertFalse(fleet.addShip(right));

        // 3️⃣ Fora acima
        IShip top = new Frigate(Compass.NORTH, new Position(-1, 0));
        assertFalse(fleet.addShip(top));

        // 4️⃣ Fora abaixo
        IShip bottom = new Frigate(Compass.NORTH, new Position(BOARD_SIZE, 0));
        assertFalse(fleet.addShip(bottom));

        // 5️⃣ Dentro do tabuleiro (controle positivo)
        IShip valid = new Frigate(Compass.NORTH, new Position(5, 5));
        assertTrue(fleet.addShip(valid));
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
        barge.getPositions().get(0).shoot();

        List<IShip> floatingShips = fleet.getFloatingShips();
        // Barge ainda flutua
        assertTrue(floatingShips.contains(frigate));
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

        assertDoesNotThrow(() -> fleet.printStatus());
        assertThrows(AssertionError.class, () -> {
            fleet.printShipsByCategory(null);
        });
    }
}
