package io.github.nejc92.sy.game;

import io.github.nejc92.sy.utilities.BoardFileParser;

import java.util.List;
import java.util.stream.Collectors;

public class Board {

    private static final String BOARD_FILE_NAME = "src/java/resources/board_file.xml";

    private final List<List<Action>> boardPositions;

    protected static Board initialize() {
        List<List<Action>> boardPositions = getParsedPositionsFromFile();
        return new Board(boardPositions);
    }

    private static List<List<Action>> getParsedPositionsFromFile() {
        BoardFileParser parser = new BoardFileParser(BOARD_FILE_NAME);
        return parser.getParsedData();
    }

    private Board(List<List<Action>> boardPositions) {
        this.boardPositions = boardPositions;
    }

    protected List<Integer> getDestinationsForPosition(int position) {
        return getActionsForPosition(position).stream()
                .map(Action::getDestination)
                .collect(Collectors.toList());
    }

    protected List<Action> generateBlackFareActionsForPosition(int position) {
        return getActionsForPosition(position).stream()
                .map(Action::generateBlackFareAction)
                .collect(Collectors.toList());
    }

    protected List<Action> getActionsForPosition(int position) {
        int positionListIndex = getListIndexFromPosition(position);
        return boardPositions.get(positionListIndex);
    }

    protected List<Integer> getTransportationDestinationsForPosition (
            Action.Transportation transportation, int position) {
        return getTransportationActionsForPosition(transportation, position).stream()
                .map(Action::getDestination)
                .collect(Collectors.toList());
    }

    protected List<Action> getTransportationActionsForPosition(Action.Transportation transportation, int position) {
        int positionListIndex = getListIndexFromPosition(position);
        return boardPositions.get(positionListIndex).stream()
                .filter(action -> action.getTransportation() == transportation)
                .collect(Collectors.toList());
    }

    private int getListIndexFromPosition(int position) {
        return position - 1;
    }
}