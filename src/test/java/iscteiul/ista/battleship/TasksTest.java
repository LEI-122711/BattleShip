package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Scanner;

@DisplayName("Testes unitários da classe Tasks")
public class TasksTest {

    @Test
    @DisplayName("readPosition() - Deve ler corretamente uma posição (linha, coluna)")
    void testReadPosition() {
        Scanner scanner = new Scanner("3 5");
        Position pos = Tasks.readPosition(scanner);
        assertEquals(3, pos.getRow(), "A linha deve ser 3");
        assertEquals(5, pos.getColumn(), "A coluna deve ser 5");
    }

    @Test
    @DisplayName("readShip() - Deve criar corretamente um navio a partir do input")
    void testReadShip() {
        Scanner scanner = new Scanner("fragata 2 3 n");
        Ship ship = Tasks.readShip(scanner);

        assertNotNull(ship, "O navio não deve ser nulo");
        assertEquals("Fragata", ship.getCategory(), "A categoria deve ser Fragata");
        assertEquals(Compass.NORTH, ship.getBearing(), "A direção deve ser Norte");
        assertEquals(2, ship.getPosition().getRow());
        assertEquals(3, ship.getPosition().getColumn());
    }

    @Test
    @DisplayName("readShip() - Input inválido retorna null")
    void testReadShipInvalid() {
        Scanner scanner = new Scanner("unknown 1 1 n");
        Ship ship = Tasks.readShip(scanner);
        assertNull(ship, "Navio desconhecido deve resultar em null");
    }

    @Test
    @DisplayName("buildFleet() - Deve criar frota completa com 10 navios")
    void testBuildFleetWithTenShips() {
        String input = String.join("\n",
                "barca 0 0 s",
                "barca 2 0 o",
                "barca 4 0 n",
                "barca 6 0 e",
                "barca 8 0 e",
                "barca 0 2 o",
                "barca 2 2 n",
                "barca 4 2 s",
                "barca 6 2 e",
                "barca 8 2 e"
        );

        Scanner scanner = new Scanner(input);
        Fleet fleet = Tasks.buildFleet(scanner);
        assertThrows(AssertionError.class, () -> {
            Tasks.buildFleet(null);
        });
        assertNotNull(fleet, "A frota não deve ser nula");
        assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size(), "A frota deve conter exatamente 10 navios");
    }

    @Test
    @DisplayName("buildFleet() - Navios duplicados não devem ser adicionados")
    void testBuildFleetWithDuplicates() {
        String input = String.join("\n",
                "barca 0 0 s",
                "barca 0 0 s", // duplicado
                "barca 2 0 n",
                "barca 4 0 e",
                "barca 6 0 o",
                "barca 8 0 n",
                "naoexiste 0 0 s",// nao existe
                "barca 0 2 e",
                "barca 2 2 s",
                "barca 4 2 n",
                "barca 6 2 o",
                "barca 8 2 e",
                "barca 0 4 s"
        );

        Scanner scanner = new Scanner(input);
        Fleet fleet = Tasks.buildFleet(scanner);

        assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size(), "A frota deve ignorar duplicados e ainda ter 10 navios");
    }

    @Test
    @DisplayName("firingRound() - Deve registrar acertos, tiros inválidos e repetidos")
    void testFiringRound() {
        Fleet fleet = new Fleet();
        fleet.addShip(Ship.buildShip("fragata", Compass.NORTH, new Position(0, 0)));
        Game game = new Game(fleet);

        Scanner scanner = new Scanner("0 0 1 1 0 0"); // inclui repetição
        Tasks.firingRound(scanner, game);

        assertTrue(game.getHits() >= 1, "Deve haver pelo menos 1 acerto");
        assertTrue(game.getInvalidShots() >= 0, "Número de tiros inválidos deve ser >= 0");
        assertTrue(game.getRepeatedShots() >= 1, "Deve haver pelo menos 1 tiro repetido");
    }

    @Test
    @DisplayName("firingRound() - Deve lidar com tiros fora do mapa")
    void testFiringRoundOutOfBounds() {
        Fleet fleet = new Fleet();
        fleet.addShip(Ship.buildShip("fragata", Compass.NORTH, new Position(0, 0)));
        Game game = new Game(fleet);

        Scanner scanner = new Scanner("-1 0 0 -1 100 100");
        Tasks.firingRound(scanner, game);

        assertEquals(0, game.getHits(), "Nenhum acerto deve ocorrer");
        assertEquals(3, game.getInvalidShots(), "Todos os tiros devem ser inválidos");
    }
}
