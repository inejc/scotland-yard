package io.github.nejc92.sy.game.board;

import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.utilities.BoardGraphGenerator;
import org.jgrapht.UndirectedGraph;

import java.util.List;
import java.util.stream.Collectors;

public class Board {

    private static final String BOARD_FILE_NAME = "src/java/resources/board_file.xml";

    private final UndirectedGraph<Integer, Connection> graph;

    public static Board initialize() {
        BoardGraphGenerator boardGraphGenerator = new BoardGraphGenerator(BOARD_FILE_NAME);
        UndirectedGraph<Integer, Connection> graph = boardGraphGenerator.generateGraph();
        return new Board(graph);
    }

    private Board(UndirectedGraph<Integer, Connection> graph) {
        this.graph = graph;
    }

    public List<Integer> getDestinationsForPosition(int position) {
       return graph.edgesOf(position).stream()
               .map(Connection::getVertex2).collect(Collectors.toList());
    }

    public List<Action> getActionsForPosition(int position) {
        return graph.edgesOf(position).stream()
                .map(connection -> new Action(connection.getTransportation(), connection.getVertex2()))
                .collect(Collectors.toList());
    }

    public List<Integer> getTransportationDestinationsForPosition (
            Connection.Transportation transportation, int position) {
        return graph.edgesOf(position).stream()
                .filter(connection -> connection.isTransportation(transportation))
                .map(Connection::getVertex2).collect(Collectors.toList());
    }

    public List<Action> getTransportationActionsForPosition(Connection.Transportation transportation, int position) {
        return graph.edgesOf(position).stream()
                .filter(connection -> connection.isTransportation(transportation))
                .map(connection -> new Action(connection.getTransportation(), connection.getVertex2()))
                .collect(Collectors.toList());
    }
}