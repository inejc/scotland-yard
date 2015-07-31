package io.github.nejc92.sy.game;

import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.utilities.BoardFileParser;
import io.github.nejc92.sy.utilities.DistancesFileParser;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

    private static final String BOARD_FILE_NAME = "src/main/resources/board_file.xml";
    private static final String HIDERS_DISTANCES_FILE_NAME = "src/main/resources/hiders_distances_file.xml";
    private static final String SEEKERS_DISTANCES_FILE_NAME = "src/main/resources/seekers_distances_file.xml";

    private final List<List<Action>> positionsActions;
    private final List<List<Integer>> hidersDistances;
    private final List<List<Integer>> seekersDistances;

    public static Board initialize() {
        BoardFileParser boardFileParser = new BoardFileParser(BOARD_FILE_NAME);
        List<List<Action>> positionsActions = boardFileParser.getParsedData();
        DistancesFileParser distancesFileParser = new DistancesFileParser(HIDERS_DISTANCES_FILE_NAME);
        List<List<Integer>> hidersDistances = distancesFileParser.getParsedData();
        distancesFileParser = new DistancesFileParser(SEEKERS_DISTANCES_FILE_NAME);
        List<List<Integer>> seekersDistances = distancesFileParser.getParsedData();
        return new Board(positionsActions, hidersDistances, seekersDistances);
    }

    private Board(List<List<Action>> positionsActions, List<List<Integer>> hidersDistances,
                  List<List<Integer>> seekersDistances) {
        this.positionsActions = positionsActions;
        this.hidersDistances = hidersDistances;
        this.seekersDistances = seekersDistances;
    }

    public List<Integer> getDestinationsForPosition(int position) {
        return getActionsForPosition(position).stream()
                .map(Action::getDestination)
                .collect(Collectors.toList());
    }

    public List<Action> getActionsForPosition(int position) {
        int positionListIndex = getListIndexFromPosition(position);
        return positionsActions.get(positionListIndex);
    }

    public List<Integer> getTransportationDestinationsForPosition (
            Action.Transportation transportation, int position) {
        return getTransportationActionsForPosition(transportation, position).stream()
                .map(Action::getDestination)
                .collect(Collectors.toList());
    }

    public List<Action> getTransportationActionsForPosition(Action.Transportation transportation, int position) {
        int positionListIndex = getListIndexFromPosition(position);
        return positionsActions.get(positionListIndex).stream()
                .filter(action -> action.getTransportation() == transportation)
                .collect(Collectors.toList());
    }

    private int getListIndexFromPosition(int position) {
        return position - 1;
    }

    public int shortestDistanceBetween(int position1, int position2, Player.Type type) {
        if (position1 == position2)
            return 0;
        else
            return shortestDistanceBetweenDifferent(position1, position2, type);
    }

    public int shortestDistanceBetweenDifferent(int position1, int position2, Player.Type type) {
        int index1, index2;
        if (position1 < position2) {
            index1 = position1 - 1;
            index2 = (position2 - position1) - 1;
        }
        else {
            index1 = position2 - 1;
            index2 = (position1 - position2) - 1;
        }
        if (type == Player.Type.HIDER)
            return hidersDistances.get(index1).get(index2);
        else
            return seekersDistances.get(index1).get(index2);
    }
}