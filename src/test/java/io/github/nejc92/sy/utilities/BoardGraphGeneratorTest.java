package io.github.nejc92.sy.utilities;

import io.github.nejc92.sy.game.board.Connection;
import org.jgrapht.UndirectedGraph;
import org.junit.Test;
import org.junit.Before;

import java.util.*;

import static org.junit.Assert.*;

public class BoardGraphGeneratorTest {

    private static final String BOARD_FILE_NAME = "src/main/resources/board_file.xml";

    private static final Set<Connection> POSITION_1_CONNECTIONS = new HashSet<>(
            Arrays.asList(new Connection(1, 8, Connection.Transportation.TAXI),
                    new Connection(1, 9, Connection.Transportation.TAXI),
                    new Connection(1, 58, Connection.Transportation.BUS),
                    new Connection(1, 46, Connection.Transportation.BUS),
                    new Connection(1, 46, Connection.Transportation.UNDERGROUND),
                    new Connection(8, 1, Connection.Transportation.TAXI),
                    new Connection(9, 1, Connection.Transportation.TAXI),
                    new Connection(58, 1, Connection.Transportation.BUS),
                    new Connection(46, 1, Connection.Transportation.BUS),
                    new Connection(46, 1, Connection.Transportation.UNDERGROUND)));

    private static final Set<Connection> POSITION_194_CONNECTIONS = new HashSet<>(
            Arrays.asList(new Connection(194, 192, Connection.Transportation.TAXI),
                    new Connection(194, 193, Connection.Transportation.TAXI),
                    new Connection(194, 195, Connection.Transportation.TAXI),
                    new Connection(194, 157, Connection.Transportation.BLACK_FARE),
                    new Connection(192, 194, Connection.Transportation.TAXI),
                    new Connection(193, 194, Connection.Transportation.TAXI),
                    new Connection(195, 194, Connection.Transportation.TAXI),
                    new Connection(157, 194, Connection.Transportation.BLACK_FARE)));

    private static final Set<Connection> POSITION_194_CONNECTIONS_SEEKERS = new HashSet<>(
            Arrays.asList(new Connection(194, 192, Connection.Transportation.TAXI),
                    new Connection(194, 193, Connection.Transportation.TAXI),
                    new Connection(194, 195, Connection.Transportation.TAXI),
                    new Connection(192, 194, Connection.Transportation.TAXI),
                    new Connection(193, 194, Connection.Transportation.TAXI),
                    new Connection(195, 194, Connection.Transportation.TAXI)));

    private BoardGraphGenerator boardGraphGenerator;

    @Before
    public void setUp() {
        boardGraphGenerator = new BoardGraphGenerator(BOARD_FILE_NAME);
    }

    @Test
    public void testGenerateGraph() {
        UndirectedGraph<Integer, Connection> graph = boardGraphGenerator.generateGraph();
        assertEquals(POSITION_1_CONNECTIONS, graph.edgesOf(1));
        assertEquals(POSITION_194_CONNECTIONS, graph.edgesOf(194));
    }

    @Test
    public void testGenerateSeekersGraph() {
        UndirectedGraph<Integer, Connection> graph = boardGraphGenerator.generateGraph();
        UndirectedGraph<Integer, Connection> seekersGraph = boardGraphGenerator.generateSeekersGraph(graph);
        assertEquals(POSITION_1_CONNECTIONS, seekersGraph.edgesOf(1));
        assertEquals(POSITION_194_CONNECTIONS_SEEKERS, seekersGraph.edgesOf(194));
    }
}