package io.github.nejc92.sy.game;

import io.github.nejc92.sy.BoardFileParser;

import java.util.List;
import java.util.stream.Collectors;

public class Board {

    private static final String BOARD_FILE_NAME = "src/java/resources/board_file.xml";

    private final List<List<Action>> boardPositions;

    protected static Board initialize() {
        List<List<Action>> boardPositions = getParsedPositions();
        return new Board(boardPositions);
    }

    private static List<List<Action>> getParsedPositions() {
        BoardFileParser parser = new BoardFileParser(BOARD_FILE_NAME);
        return parser.getParsedData();
    }

    private Board(List<List<Action>> boardPositions) {
        this.boardPositions = boardPositions;
    }

    protected List<Action> getPossibleActionsForPosition(int position) {
        int positionListIndex = position - 1;
        return boardPositions.get(positionListIndex);
    }

    protected List<Integer> getDestinationsForPosition(int position) {
        return getPossibleActionsForPosition(position).stream()
                .map(Action::getDestination)
                .collect(Collectors.toList());
    }

    protected List<Action> getPossibleTransportationActionsForPosition(
            Action.Transportation transportation, int position) {
        int positionListIndex = position - 1;
        return boardPositions.get(positionListIndex).stream()
                .filter(action -> action.getTransportation() == transportation)
                .collect(Collectors.toList());
    }

    protected List<Integer> getTransportationDestinationsForPosition(
            Action.Transportation transportation, int position) {
        return getPossibleTransportationActionsForPosition(transportation, position).stream()
                .map(Action::getDestination)
                .collect(Collectors.toList());
    }
}