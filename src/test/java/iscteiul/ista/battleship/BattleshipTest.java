package iscteiul.ista.battleship;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static iscteiul.ista.battleship.IFleet.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.*;


@TmsLink("TMS-S2-C2")
@DisplayName("Testes para todas as classes do projeto BattleShip")
public class BattleshipTest {

    @Nested
    @TmsLink("TMS-S5-C13")
    @DisplayName("Testes para classe Fleet")
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
        @DisplayName("Teste para adicionar Navio")
        void testAddShipSuccessfully() {
            assertTrue(fleet.addShip(frigate));
            assertTrue(fleet.addShip(galleon));
            assertFalse(fleet.addShip(galleonToColide));
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
            assertFalse(fleetToFill.addShip(barge));


        }


        @Test
        @DisplayName("Teste para o método que veridica limites do board")
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
        @DisplayName("Teste para adicionar colisões entre navios")
        void testAddShipCollision() {
            fleet.addShip(frigate);
            // Tenta adicionar outra fragata que colida
            IShip collidingShip = new Frigate(Compass.NORTH, new Position(0, 0));
            assertFalse(fleet.addShip(collidingShip));
            assertEquals(1, fleet.getShips().size());
        }

        @Test
        @DisplayName("Teste para o método que compara navios")
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
        @DisplayName("Teste para o método que obtem navios que ainda flutuam")
        void testGetFloatingShips() {
            fleet.addShip(frigate);
            fleet.addShip(barge);

            // Atira à fragata
            barge.getPositions().get(0).shoot();

            List<IShip> floatingShips = fleet.getFloatingShips();
            // Barge ainda flutua
            assertTrue(floatingShips.contains(frigate));
        }

        @DisplayName("Teste para obter os navios numa dada posição")
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
        @DisplayName("Teste para testar o método que imprime o status")
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

    @Nested
    @TmsLink("TMS-S3-C8")
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
            testShip = new iscteiul.ista.battleship.ShipTest.TestShip("caravela", Compass.EAST, startPos, 3);
        }

        @Test
        @DisplayName("Testar o construtor")
        void testConstructor() {
            assertThrows(AssertionError.class, () -> {
                new iscteiul.ista.battleship.ShipTest.TestShip("fragata", null, startPos, 3);
            });
            assertThrows(AssertionError.class, () -> {
                new iscteiul.ista.battleship.ShipTest.TestShip("fragata", Compass.EAST, null, 3);
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
            IShip other = new iscteiul.ista.battleship.ShipTest.TestShip("barca", Compass.SOUTH, new Position(1, 3), 2);
            assertTrue(testShip.tooCloseTo(other));

            IShip farShip = new iscteiul.ista.battleship.ShipTest.TestShip("barca", Compass.SOUTH, new Position(10, 10), 2);
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
            Ship southShip = new iscteiul.ista.battleship.ShipTest.TestShip("fragata", Compass.SOUTH, new Position(5, 5), 3);
            // Positions: (5,5), (6,5), (7,5)
            assertEquals(5, southShip.getTopMostPos());
            assertEquals(7, southShip.getBottomMostPos());
            assertEquals(5, southShip.getLeftMostPos());
            assertEquals(5, southShip.getRightMostPos());
        }

        @Test
        @DisplayName("Testar as posições quando o novio está orientado a North")
        void testTopBottomLeftRightPositions_North() {
            Ship northShip = new iscteiul.ista.battleship.ShipTest.TestShip("fragata", Compass.NORTH, new Position(5, 5), 3);
            // Positions: (5,5), (4,5), (3,5)
            assertEquals(3, northShip.getTopMostPos());
            assertEquals(5, northShip.getBottomMostPos());
            assertEquals(5, northShip.getLeftMostPos());
            assertEquals(5, northShip.getRightMostPos());
        }
        @Test
        @DisplayName("Testar as posições quando o novio está orientado a West")
        void testTopBottomLeftRightPositions_West() {
            Ship westShip = new iscteiul.ista.battleship.ShipTest.TestShip("fragata", Compass.WEST, new Position(5, 5), 3);
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

    @Nested
    @TmsLink("TMS-S4-C10")
    class CompassTest {

        @Test
        @DisplayName("getDirection() devolve o caractere correto para cada direção")
        void getDirection() {
            assertAll("direções",
                    () -> assertEquals('n', Compass.NORTH.getDirection()),
                    () -> assertEquals('s', Compass.SOUTH.getDirection()),
                    () -> assertEquals('e', Compass.EAST.getDirection()),
                    () -> assertEquals('o', Compass.WEST.getDirection()),
                    () -> assertEquals('u', Compass.UNKNOWN.getDirection())
            );
        }

        @Test
        @DisplayName("toString() devolve o caractere da direção")
        void testToString() {
            assertAll("toString",
                    () -> assertEquals("n", Compass.NORTH.toString()),
                    () -> assertEquals("s", Compass.SOUTH.toString()),
                    () -> assertEquals("e", Compass.EAST.toString()),
                    () -> assertEquals("o", Compass.WEST.toString()),
                    () -> assertEquals("u", Compass.UNKNOWN.toString())
            );
        }

        @Test
        @DisplayName("charToCompass() converte corretamente caracteres válidos e inválidos")
        void charToCompass() {
            assertAll("charToCompass",
                    () -> assertEquals(Compass.NORTH, Compass.charToCompass('n')),
                    () -> assertEquals(Compass.SOUTH, Compass.charToCompass('s')),
                    () -> assertEquals(Compass.EAST, Compass.charToCompass('e')),
                    () -> assertEquals(Compass.WEST, Compass.charToCompass('o')),
                    () -> assertEquals(Compass.UNKNOWN, Compass.charToCompass('x')),
                    () -> assertEquals(Compass.UNKNOWN, Compass.charToCompass(' '))
            );
        }

        @Test
        @DisplayName("values() devolve todas as direções conhecidas")
        void values() {
            Compass[] values = Compass.values();
            assertNotNull(values);
            assertEquals(5, values.length);
            assertArrayEquals(
                    new Compass[]{Compass.NORTH, Compass.SOUTH, Compass.EAST, Compass.WEST, Compass.UNKNOWN},
                    values
            );
        }

        @Test
        @DisplayName("valueOf() devolve o valor enum correspondente ao nome")
        void valueOf() {
            assertAll("valueOf",
                    () -> assertEquals(Compass.NORTH, Compass.valueOf("NORTH")),
                    () -> assertEquals(Compass.SOUTH, Compass.valueOf("SOUTH")),
                    () -> assertEquals(Compass.EAST, Compass.valueOf("EAST")),
                    () -> assertEquals(Compass.WEST, Compass.valueOf("WEST")),
                    () -> assertEquals(Compass.UNKNOWN, Compass.valueOf("UNKNOWN"))
            );
        }
    }

    @Nested
    @TmsLink("TMS-S4-C9")
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

    @Nested
    @TmsLink("TMS-S3-C3")
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

    @Nested
    @TmsLink("TMS-S3-C4")
    class CaravelTest {

        @Test
        @DisplayName("Valida nome e tamanho da Caravela")
        void testMetadata() {
            Caravel c = new Caravel(Compass.NORTH, new Position(0, 0));
            assertEquals(2, c.getSize(), "Tamanho deve ser 2");
        }

        @Test
        @DisplayName("Valida que getSize() retorna sempre o valor correto")
        void testGetSizeMethod() {
            Caravel c1 = new Caravel(Compass.NORTH, new Position(0, 0));
            Caravel c2 = new Caravel(Compass.EAST, new Position(3, 3));

            assertEquals(2, c1.getSize(), "getSize() deve retornar 2 para qualquer Caravel");
            assertEquals(2, c2.getSize(), "getSize() deve retornar 2 para qualquer Caravel");
        }

        @Test
        @DisplayName("Valida posições para NORTH (cresce para baixo)")
        void testPositionsNorth() {
            IPosition start = new Position(5, 5);
            Caravel c = new Caravel(Compass.NORTH, start);
            List<IPosition> positions = c.getPositions();

            assertEquals(2, positions.size());
            // Posição inicial (5,5)
            assertEquals(5, positions.get(0).getRow());
            assertEquals(5, positions.get(0).getColumn());
            // Segunda posição (6,5) -> linha + 1
            assertEquals(6, positions.get(1).getRow());
            assertEquals(5, positions.get(1).getColumn());
        }

        @Test
        @DisplayName("Valida posições para SOUTH (igual a NORTH nesta implementação)")
        void testPositionsSouth() {
            IPosition start = new Position(5, 5);
            Caravel c = new Caravel(Compass.SOUTH, start);
            List<IPosition> positions = c.getPositions();

            assertEquals(2, positions.size());
            assertEquals(5, positions.get(0).getRow());
            assertEquals(6, positions.get(1).getRow());
            assertEquals(5, positions.get(0).getColumn());
            assertEquals(5, positions.get(1).getColumn());
        }

        @Test
        @DisplayName("Valida posições para EAST (cresce para a direita)")
        void testPositionsEast() {
            IPosition start = new Position(5, 5);
            Caravel c = new Caravel(Compass.EAST, start);
            List<IPosition> positions = c.getPositions();

            assertEquals(2, positions.size());
            // Posição inicial (5,5)
            assertEquals(5, positions.get(0).getRow());
            assertEquals(5, positions.get(0).getColumn());
            // Segunda posição (5,6) -> coluna + 1
            assertEquals(5, positions.get(1).getRow());
            assertEquals(6, positions.get(1).getColumn());
        }

        @Test
        @DisplayName("Valida posições para WEST (igual a EAST nesta implementação)")
        void testPositionsWest() {
            IPosition start = new Position(5, 5);
            Caravel c = new Caravel(Compass.WEST, start);
            List<IPosition> positions = c.getPositions();

            assertEquals(2, positions.size());
            assertEquals(5, positions.get(0).getRow());
            assertEquals(5, positions.get(1).getRow());
            assertEquals(5, positions.get(0).getColumn());
            assertEquals(6, positions.get(1).getColumn());
        }

        @Test
        @DisplayName("Lança AssertionError se bearing for nulo (validação feita em Ship)")
        void testNullCompass() {
            assertThrows(AssertionError.class, () -> {
                new Caravel(null, new Position(1, 1));
            }, "Deve lançar AssertionError se a bússola for null (verificado na superclasse Ship)");
        }

        @Test
        @DisplayName("Lança IllegalArgumentException se bearing for UNKNOWN")
        void testInvalidBearingUnknown() {
            IPosition start = new Position(0, 0);
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                new Caravel(Compass.UNKNOWN, start);
            });
            assertTrue(ex.getMessage().contains("invalid bearing"),
                    "Mensagem de erro deve indicar bearing inválido");
        }

    }

    @Nested
    @TmsLink("TMS-S3-C5")
    class CarrackTest {

        @Test
        @DisplayName("Valida Nome e Tamanho da Nau")
        void testMetadata() {
            Carrack c = new Carrack(Compass.NORTH, new Position(0, 0));
            assertEquals(3, c.getSize(), "Nau deve ter tamanho 3");
        }

        @Test
        @DisplayName("Valida posições verticais (NORTH/SOUTH)")
        void testPositionsVertical() {
            IPosition start = new Position(2, 2);

            // Testa NORTH (cresce para baixo)
            Carrack north = new Carrack(Compass.NORTH, start);
            List<IPosition> nPos = north.getPositions();
            assertEquals(3, nPos.size());
            assertEquals(2, nPos.get(0).getRow()); // Posição inicial
            assertEquals(3, nPos.get(1).getRow());
            assertEquals(4, nPos.get(2).getRow()); // Terceira posição

            // Testa SOUTH (mesmo comportamento nesta implementação)
            Carrack south = new Carrack(Compass.SOUTH, start);
            List<IPosition> sPos = south.getPositions();
            assertEquals(nPos.get(2).getRow(), sPos.get(2).getRow(), "SOUTH deve ser igual a NORTH");
        }

        @Test
        @DisplayName("Valida posições horizontais (EAST/WEST)")
        void testPositionsHorizontal() {
            IPosition start = new Position(2, 2);

            // Testa EAST (cresce para a direita)
            Carrack east = new Carrack(Compass.EAST, start);
            List<IPosition> ePos = east.getPositions();
            assertEquals(3, ePos.size());
            assertEquals(2, ePos.get(0).getColumn()); // Posição inicial
            assertEquals(3, ePos.get(1).getColumn());
            assertEquals(4, ePos.get(2).getColumn()); // Terceira posição

            // Testa WEST (mesmo comportamento nesta implementação)
            Carrack west = new Carrack(Compass.WEST, start);
            List<IPosition> wPos = west.getPositions();
            assertEquals(ePos.get(2).getColumn(), wPos.get(2).getColumn(), "WEST deve ser igual a EAST");
        }

        @Test
        @DisplayName("Valida exceção de bússola nula (via superclasse)")
        void testNullCompass() {
            // Assume-se que a superclasse Ship lança AssertionError para null, conforme visto antes.
            // Se tiveres alterado a Ship para lançar NullPointerException, ajusta aqui.
            assertThrows(AssertionError.class, () -> {
                new Carrack(null, new Position(1, 1));
            });
        }
    }

    @Nested
    @TmsLink("TMS-S3-C6")
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

    @Nested
    @TmsLink("TMS-S3-C7")
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

    @Nested
    @TmsLink("TMS-S5-C12")
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
            assertDoesNotThrow(() -> game.printBoard(positions, '#'), "Não deve lançar exceções ao imprimir tabuleiro");
        }
    }

    @Nested
    @TmsLink("TMS-S5-C11")
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


}
