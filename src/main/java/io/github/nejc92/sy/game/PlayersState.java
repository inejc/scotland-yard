package io.github.nejc92.sy.game;

import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayersState {

    private static final List<Integer> POSSIBLE_STARTING_POSITIONS = new ArrayList<>(
            Arrays.asList(13, 26, 34, 50, 53, 62, 91, 94, 103, 112, 117, 132, 138, 141, 155, 174, 197, 198));

    private final Board board;
    private final Player[] players;
    private int[] playersActualPositions;
    private List<Integer> hidersPossiblePositions;
    private int hidersMostProbablePosition;

    protected static PlayersState initialize(Player[] players) {
        Board board = Board.initialize();
        int[] playersPositions = generateRandomPlayersPositions(players.length);
        List<Integer> hidersPossibleLocations = calculateInitialHidersPossibleLocations(playersPositions);
        return new PlayersState(board, players, playersPositions, hidersPossibleLocations);
    }

    private static int[] generateRandomPlayersPositions(int numberOfPlayers) {
        Collections.shuffle(POSSIBLE_STARTING_POSITIONS);
        return IntStream.range(0, numberOfPlayers)
                .map(POSSIBLE_STARTING_POSITIONS::get).toArray();
    }

    private static List<Integer> calculateInitialHidersPossibleLocations(int[] playersPositions) {
        List<Integer> hidersPossibleLocations = new ArrayList<>(POSSIBLE_STARTING_POSITIONS);
        hidersPossibleLocations.removeAll(getSeekersPositions(playersPositions));
        return hidersPossibleLocations;
    }

    private static List<Integer> getSeekersPositions(int[] playersPositions) {
        return Arrays.stream(playersPositions)
                .skip(0).boxed().collect(Collectors.toList());
    }

    private PlayersState(Board board, Player[] players, int[] playersPositions,
                         List<Integer> hidersPossiblePositions) {
        this.board = board;
        this.players = players;
        this.playersActualPositions = playersPositions;
        this.hidersPossiblePositions = hidersPossiblePositions;
    }

    protected int getNumberOfPlayers() {
        return players.length;
    }

    protected boolean playerIsHider(int playerIndex) {
        return getPlayerAtIndex(playerIndex).isHider();
    }

    protected Player getPlayerAtIndex(int playerIndex) {
        return players[playerIndex];
    }

    protected List<Action> getAvailableActionsForPlayerFromSeekersPov(int playerIndex) {
        if (playerIsHider(playerIndex))
            return getAvailableActionsForHiderFromSeekersPov(playerIndex);
        else
            return getAvailableActionsForPlayerFromActualPosition(playerIndex);
    }

    protected List<Action> getAvailableActionsForHiderFromSeekersPov(int playerIndex) {
        List<Action> possibleActions = board.getActionsForPosition(hidersMostProbablePosition);
        return getAvailableActionsFromPossibleActionsForPlayer(possibleActions, playerIndex);
    }

    protected List<Action> getAvailableActionsForPlayerFromActualPosition(int playerIndex) {
        List<Action> possibleActions = board.getActionsForPosition(playersActualPositions[playerIndex]);
        return getAvailableActionsFromPossibleActionsForPlayer(possibleActions, playerIndex);
    }

    private List<Action> getAvailableActionsFromPossibleActionsForPlayer(
            List<Action> possibleActions, int playerIndex) {
        List<Action> availableActions = new ArrayList<>(possibleActions);
        availableActions = removeActionsWithOccupiedDestinations(availableActions);
        availableActions = removeActionsBecauseOfNoPlayersTickets(availableActions, playerIndex);
        if (playerIsHider(playerIndex))
            availableActions = fixHidersBlackFareActions((Hider) players[playerIndex], availableActions);
        return availableActions;
    }

    private List<Action> removeActionsWithOccupiedDestinations(List<Action> actions) {
        return actions.stream()
                .filter(this::actionsDestinationNotOccupied).collect(Collectors.toList());
    }

    private boolean actionsDestinationNotOccupied(Action action) {
        int destinationPosition = action.getDestination();
        return Arrays.stream(playersActualPositions)
                .skip(0)
                .allMatch(position -> position != destinationPosition);
    }

    private List<Action> removeActionsBecauseOfNoPlayersTickets(List<Action> actions, int playerIndex) {
        Player player = players[playerIndex];
        if (!player.hasTaxiTickets())
            actions = removeTransportationActions(Action.Transportation.TAXI, actions);
        if (!player.hasBusTickets())
            actions = removeTransportationActions(Action.Transportation.BUS, actions);
        if (!player.hasUndergroundTickets())
            actions = removeTransportationActions(Action.Transportation.UNDERGROUND, actions);
        return actions;
    }

    private List<Action> removeTransportationActions(Action.Transportation transportation, List<Action> actions) {
        return actions.stream()
                .filter(action -> !action.isTransportationAction(transportation))
                .collect(Collectors.toList());
    }

    private List<Action> fixHidersBlackFareActions(Hider hider, List<Action> actions) {
        if (!hider.hasBlackFareTicket())
            actions = removeTransportationActions(Action.Transportation.BLACK_FARE, actions);
        return actions;
    }

    protected void movePlayerWithAction(int playerIndex, Action action) {
    }

//
//    private int getMostProbableHidersPosition() {
//        return hidersPossiblePosition.get(0);
//    }
//
//    private void reCalculateHidersPossibleLocations(Action.Transportation transportation) {
//        List<Integer> newHidersPossibleLocations = new ArrayList<>();
//        if (HIDER_SURFACES_ROUNDS.contains(currentRound))
//            newHidersPossibleLocations.add(getPreviousAgent().getBoardPosition());
//        else{
//            for (int position : hidersPossiblePosition) {
//                if (transportation == Action.Transportation.BLACK_FARE)
//                    newHidersPossibleLocations.addAll(board.getDestinationsForPosition(position));
//                else
//                    newHidersPossibleLocations.addAll(board.getTransportationDestinationsForPosition(transportation, position));
//            }
//            newHidersPossibleLocations.removeAll(getSeekersPositions(players));
//        }
//        hidersPossiblePosition = newHidersPossibleLocations;
//    }
}