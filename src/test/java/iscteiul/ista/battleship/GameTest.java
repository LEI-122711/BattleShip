package iscteiul.ista.battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testes à classe Game")
class GameTest {

    private Game game;
    private Fleet fleet;
    private IShip barge;
    private IPosition pos;

    @BeforeEach
    @DisplayName("Configuração inicial do jogo e frota")
    void setUp() {
        fleet = new Fleet();
        pos = new Position(0, 0);
        barge = new Barge(Compass.EAST, pos);
        fleet.addShip(barge);
        game = new Game(fleet);
    }

    @Test
    @DisplayName("Testar disparo válido que acerta num navio")
    void fire_HitShip_ShouldIncreaseHitCount() {
        IShip hitShip = game.fire(pos);

        assertAll("Validação do disparo com acerto",
                () -> assertNotNull(fleet.shipAt(pos), "O navio deveria existir na posição"),
                () -> assertEquals(1, game.getHits(), "Deveria haver um acerto"),
                () -> assertEquals(1, game.getSunkShips(), "O navio de tamanho 1 deveria ficar afundado"),
                () -> assertEquals(barge, hitShip, "O navio retornado deveria ser a barca atingida")
        );
    }

    @Test
    @DisplayName("Testar disparo que acerta num navio mas não o afunda")
    void fire_HitButNotSunk_ShouldNotIncreaseSinkCount() {
        // Caravela de 2 posições: (0,0) e (0,1)
        IShip caravel = new Caravel(Compass.EAST, new Position(0, 0));
        fleet = new Fleet();
        fleet.addShip(caravel);
        game = new Game(fleet);

        // Dispara só na primeira posição
        IPosition firstHit = new Position(0, 0);
        IShip result = game.fire(firstHit);

        assertAll("Navio atingido mas ainda a flutuar",
                () -> assertNull(result, "Não deve devolver o navio, pois ainda não afundou"),
                () -> assertEquals(1, game.getHits(), "Deve contar um acerto"),
                () -> assertEquals(0, game.getSunkShips(), "Não deve contar navio afundado")
        );
    }

    @Test
    @DisplayName("Testar disparo fora dos limites do tabuleiro")
    void fire_InvalidShot_ShouldIncreaseInvalidCount() {
        IPosition invalid = new Position(20, 20);
        IShip result = game.fire(invalid);

        assertAll("Disparo inválido",
                () -> assertNull(result, "Não deve devolver nenhum navio"),
                () -> assertEquals(1, game.getInvalidShots(), "Deveria contar como tiro inválido"),
                () -> assertEquals(0, game.getHits(), "Não deve haver acertos")
        );
    }

    @Test
    @DisplayName("Testar disparo repetido na mesma posição")
    void fire_RepeatedShot_ShouldIncreaseRepeatedCount() {
        game.fire(pos); // primeiro tiro válido
        game.fire(pos); // tiro repetido

        assertAll("Disparo repetido",
                () -> assertEquals(1, game.getRepeatedShots(), "Deveria contar um tiro repetido"),
                () -> assertEquals(1, game.getHits(), "Apenas o primeiro tiro conta como acerto")
        );
    }

    @Test
    @DisplayName("Testar obtenção da lista de tiros disparados")
    void getShots_ShouldReturnAllFiredShots() {
        IPosition pos1 = new Position(0, 0);
        IPosition pos2 = new Position(0, 1);
        game.fire(pos1);
        game.fire(pos2);

        List<IPosition> shots = game.getShots();

        assertAll("Lista de tiros",
                () -> assertEquals(2, shots.size(), "Deveria conter dois tiros"),
                () -> assertTrue(shots.contains(pos1), "Deveria conter a primeira posição"),
                () -> assertTrue(shots.contains(pos2), "Deveria conter a segunda posição")
        );
    }

    @Test
    @DisplayName("Testar número de navios restantes após afundar um")
    void getRemainingShips_ShouldDecreaseAfterSink() {
        game.fire(pos); // afunda a barca

        int remaining = game.getRemainingShips();

        assertEquals(0, remaining, "Não deveria haver navios restantes após afundar a barca");
    }

    @Test
    @DisplayName("Testar impressão do tabuleiro com tiros válidos")
    void printValidShots_ShouldPrintWithoutErrors() {
        game.fire(pos);
        assertDoesNotThrow(() -> game.printValidShots(), "Não deve lançar exceções ao imprimir tiros válidos");
    }

    @Test
    @DisplayName("Testar limites da função validShot()")
    void validShot_ShouldCoverAllBranches() throws Exception {
        // Acesso via reflexão se validShot for privado
        var method = Game.class.getDeclaredMethod("validShot", IPosition.class);
        method.setAccessible(true);

        int boardSize = Fleet.BOARD_SIZE;

        // Casos de teste
        IPosition negativeRow = new Position(-1, 0);
        IPosition negativeCol = new Position(0, -1);
        IPosition tooLargeRow = new Position(boardSize + 1, 0);
        IPosition tooLargeCol = new Position(0, boardSize + 1);
        IPosition valid = new Position(boardSize, boardSize);

        // Invocações via reflexão
        boolean negRow = (boolean) method.invoke(game, negativeRow);
        boolean negCol = (boolean) method.invoke(game, negativeCol);
        boolean bigRow = (boolean) method.invoke(game, tooLargeRow);
        boolean bigCol = (boolean) method.invoke(game, tooLargeCol);
        boolean validCase = (boolean) method.invoke(game, valid);

        assertAll("Cobertura de ramos do validShot",
                () -> assertFalse(negRow, "Deve ser falso se a linha for negativa"),
                () -> assertFalse(negCol, "Deve ser falso se a coluna for negativa"),
                () -> assertFalse(bigRow, "Deve ser falso se a linha exceder o limite"),
                () -> assertFalse(bigCol, "Deve ser falso se a coluna exceder o limite"),
                () -> assertTrue(validCase, "Deve ser verdadeiro se estiver dentro dos limites")
        );
    }


    @Test
    @DisplayName("Testar impressão da frota no tabuleiro")
    void printFleet_ShouldPrintWithoutErrors() {
        assertDoesNotThrow(() -> game.printFleet(), "Não deve lançar exceções ao imprimir frota");
    }

    @Test
    @DisplayName("Testar impressão genérica do tabuleiro")
    void printBoard_ShouldPrintWithoutErrors() {
        List<IPosition> positions = List.of(new Position(1, 1), new Position(2, 2));
        assertDoesNotThrow(() -> game.printBoard(positions, Character.valueOf('#')), "Não deve lançar exceções ao imprimir tabuleiro");
    }
}
