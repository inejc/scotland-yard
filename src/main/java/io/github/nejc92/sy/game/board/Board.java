package io.github.nejc92.sy.game.board;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.utilities.BoardGraphGenerator;
import io.github.nejc92.sy.utilities.DistancesFileParser;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;

import java.util.List;
import java.util.stream.Collectors;

public class Board {

    private static final String BOARD_FILE_NAME = "src/main/resources/board_file.xml";
    private static final String DISTANCES_FILE_NAME = "src/main/resources/distances_file.xml";

    private final UndirectedGraph<Integer, Connection> graph;
    private final UndirectedGraph<Integer, Connection> seekersSubGraph;
    private final List<List<Integer>> distances;

    public static Board initialize() {
        BoardGraphGenerator boardGraphGenerator = new BoardGraphGenerator(BOARD_FILE_NAME);
        UndirectedGraph<Integer, Connection> graph = boardGraphGenerator.generateGraph();
        UndirectedGraph<Integer, Connection> seekersSubGraph = boardGraphGenerator.generateSeekersGraph(graph);
        DistancesFileParser distancesFileParser = new DistancesFileParser(DISTANCES_FILE_NAME);
        List<List<Integer>> distances = distancesFileParser.getParsedData();
        return new Board(graph, seekersSubGraph, distances);
    }

    private Board(UndirectedGraph<Integer, Connection> graph, UndirectedGraph<Integer, Connection> seekersSubGraph,
                  List<List<Integer>> distances) {
        this.graph = graph;
        this.seekersSubGraph = seekersSubGraph;
        this.distances = distances;
    }

    public List<Integer> getDestinationsForPosition(int position) {
       return graph.edgesOf(position).stream()
               .filter(connection -> connection.getVertex1() == position)
               .map(Connection::getVertex2).collect(Collectors.toList());
    }

    public List<Action> getActionsForPosition(int position) {
        return graph.edgesOf(position).stream()
                .filter(connection -> connection.getVertex1() == position)
                .map(connection -> new Action(connection.getTransportation(), connection.getVertex2()))
                .collect(Collectors.toList());
    }

    public List<Integer> getTransportationDestinationsForPosition (
            Connection.Transportation transportation, int position) {
        return graph.edgesOf(position).stream()
                .filter(connection -> connection.isTransportation(transportation)
                        && connection.getVertex1() == position)
                .map(Connection::getVertex2).collect(Collectors.toList());
    }

    public List<Action> getTransportationActionsForPosition(Connection.Transportation transportation, int position) {
        return graph.edgesOf(position).stream()
                .filter(connection -> connection.isTransportation(transportation)
                        && connection.getVertex1() == position)
                .map(connection -> new Action(connection.getTransportation(), connection.getVertex2()))
                .collect(Collectors.toList());
    }

    public int shortestDistanceBetween(int position1, int position2) {
//        List path = DijkstraShortestPath.findPathBetween(seekersSubGraph, position1, position2);
//        return path.size();
        if (position1 == position2)
            return 0;
        int index1, index2;
        if (position1 < position2) {
            index1 = position1 - 1;
            index2 = (position2 - position1) - 1;
        }
        else {
            index1 = position2 - 1;
            index2 = (position1 - position2) - 1;
        }
        return distances.get(index1).get(index2);
    }
}