package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static iscteiul.ista.battleship.IFleet.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.*;


public class BattleshipTest {

    @Nested
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



}
