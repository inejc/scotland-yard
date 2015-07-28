package io.github.nejc92.sy.game;

import io.github.nejc92.sy.game.board.Board;
import io.github.nejc92.sy.game.board.Connection;
import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayersOnBoard {

    private static final int NUMBER_OF_PLAYERS = 6;
    private static final int HIDERS_INDEX = 0;
    private static final int SKIP_HIDER = 1;
    private static final List<Integer> POSSIBLE_STARTING_POSITIONS = new ArrayList<>(
            Arrays.asList(13, 26, 34, 50, 53, 62, 91, 94, 103, 112, 117, 132, 138, 141, 155, 174, 197, 198));

    private final Board board;
    private final Player[] players;
    private int[] playersActualPositions;
    private List<Integer> hidersPossiblePositions;
    private int hidersMostProbablePosition;

    protected static PlayersOnBoard initialize(Player[] players) {
        validatePlayers(players);
        Board board = Board.initialize();
        int[] playersPositions = generateRandomPlayersPositions(players.length);
        List<Integer> hidersPossibleLocations = calculateInitialHidersPossibleLocations(playersPositions);
        Collections.shuffle(hidersPossibleLocations);
        int hidersMostProbablePosition = hidersPossibleLocations.get(0);
        return new PlayersOnBoard(board, players, playersPositions, hidersPossibleLocations,
                hidersMostProbablePosition);
    }

    protected static PlayersOnBoard initializeTest(Player[] players, int[] playersPositions,
                                                   int hidersMostProbablePosition) {
        validatePlayers(players);
        Board board = Board.initialize();
        List<Integer> hidersPossibleLocations = calculateInitialHidersPossibleLocations(playersPositions);
        return new PlayersOnBoard(board, players, playersPositions, hidersPossibleLocations,
                hidersMostProbablePosition);
    }

    private static void validatePlayers(Player[] players) {
        if (!numberOfPlayersValid(players))
            throw new IllegalArgumentException("Error: invalid players passed as function parameter");
        if (!playerTypesValid(players))
            throw new IllegalArgumentException("Error: invalid players passed as function parameter");
    }

    private static boolean numberOfPlayersValid(Player[] players) {
        return players.length == NUMBER_OF_PLAYERS;
    }

    private static boolean playerTypesValid(Player[] players) {
        return players[HIDERS_INDEX].isHider()
                && Arrays.stream(players).skip(SKIP_HIDER).allMatch(Player::isSeeker);
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
                .skip(SKIP_HIDER).boxed().collect(Collectors.toList());
    }

    private PlayersOnBoard(Board board, Player[] players, int[] playersPositions,
                           List<Integer> hidersPossiblePositions, int hidersMostProbablePosition) {
        this.board = board;
        this.players = players;
        this.playersActualPositions = playersPositions;
        this.hidersPossiblePositions = hidersPossiblePositions;
        this.hidersMostProbablePosition = hidersMostProbablePosition;
    }


    protected int[] getPlayersActualPositions() {
        return playersActualPositions;
    }

    protected int getNumberOfPlayers() {
        return players.length;
    }

    protected boolean playerIsHider(int playerIndex) {
        return getPlayerAtIndex(playerIndex).isHider();
    }

    protected boolean playerIsHuman(int playerIndex) {
        return getPlayerAtIndex(playerIndex).isHuman();
    }

    protected Player getPlayerAtIndex(int playerIndex) {
        return players[playerIndex];
    }

    protected boolean seekerOnHidersMostProbablePosition() {
        return seekerOnPosition(hidersMostProbablePosition);
    }

    protected boolean seekerOnHidersActualPosition() {
        return seekerOnPosition(playersActualPositions[HIDERS_INDEX]);
    }

    protected boolean seekerOnPosition(int position) {
        return Arrays.stream(playersActualPositions)
                .skip(SKIP_HIDER)
                .anyMatch(hidersPosition -> hidersPosition == position);
    }

    protected List<Action> getAvailableActionsFromSeekersPov(int playerIndex) {
        if (playerIsHider(playerIndex))
            return getAvailableActionsForHiderFromSeekersPov(playerIndex);
        else
            return getAvailableActionsForActualPosition(playerIndex);
    }

    protected List<Action> getAvailableActionsForHiderFromSeekersPov(int playerIndex) {
        List<Action> possibleActions = board.getActionsForPosition(hidersMostProbablePosition);
        return getAvailableActionsFromPossibleActionsForPlayer(possibleActions, playerIndex);
    }

    protected List<Action> getAvailableActionsForActualPosition(int playerIndex) {
        List<Action> possibleActions = board.getActionsForPosition(playersActualPositions[playerIndex]);
        return getAvailableActionsFromPossibleActionsForPlayer(possibleActions, playerIndex);
    }

    private List<Action> getAvailableActionsFromPossibleActionsForPlayer(
            List<Action> possibleActions, int playerIndex) {
        List<Action> availableActions = new ArrayList<>(possibleActions);
        availableActions = removeActionsWithOccupiedDestinations(availableActions);
        availableActions = removeActionsBecauseOfNoPlayersTickets(availableActions, playerIndex);
        // remove black fair tickets for hider too
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
                .skip(SKIP_HIDER)
                .allMatch(position -> position != destinationPosition);
    }

    private List<Action> removeActionsBecauseOfNoPlayersTickets(List<Action> actions, int playerIndex) {
        Player player = players[playerIndex];
        if (!player.hasTaxiTickets())
            actions = removeTransportationActions(Connection.Transportation.TAXI, actions);
        if (!player.hasBusTickets())
            actions = removeTransportationActions(Connection.Transportation.BUS, actions);
        if (!player.hasUndergroundTickets())
            actions = removeTransportationActions(Connection.Transportation.UNDERGROUND, actions);
        return actions;
    }

    private List<Action> fixHidersBlackFareActions(Hider hider, List<Action> actions) {
        if (!hider.hasBlackFareTicket())
            actions = removeTransportationActions(Connection.Transportation.BLACK_FARE, actions);
        return actions;
    }

    private List<Action> removeTransportationActions(Connection.Transportation transportation, List<Action> actions) {
        return actions.stream()
                .filter(action -> !action.isTransportationAction(transportation))
                .collect(Collectors.toList());
    }

    protected void movePlayerFromActualPosition(int playerIndex, Action action) {
        removeTransportationCard(playerIndex, action);
        playersActualPositions[playerIndex] = action.getDestination();
    }

    protected void movePlayerFromSeekersPov(int playerIndex, Action action) {
        removeTransportationCard(playerIndex, action);
        if (playerIsHider(playerIndex))
            hidersMostProbablePosition = action.getDestination();
        else
            playersActualPositions[playerIndex] = action.getDestination();
    }

    private void removeTransportationCard(int playerIndex, Action action) {
        players[playerIndex].removeTicket(action.getTransportation());
        if (!playerIsHider(playerIndex)) {
            Hider hider = (Hider)getPlayerAtIndex(HIDERS_INDEX);
            hider.addTicket(action.getTransportation());
        }
    }

    protected void setHidersActualAsMostProbablePosition() {
        hidersPossiblePositions = new ArrayList<>();
        hidersPossiblePositions.add(playersActualPositions[HIDERS_INDEX]);
        hidersMostProbablePosition = playersActualPositions[HIDERS_INDEX];
    }

    protected void recalculateHidersMostProbablePosition(Connection.Transportation transportation) {
        hidersPossiblePositions = recalculateHidersPossiblePositions(transportation);
        hidersMostProbablePosition = getMostProbableHidersPosition();
    }

    protected void removeCurrentSeekersPositionFromPossibleHidersPositions(int playerIndex) {
        hidersPossiblePositions.remove(new Integer(playersActualPositions[playerIndex]));
        hidersMostProbablePosition = getMostProbableHidersPosition();
    }

    private List<Integer> recalculateHidersPossiblePositions(Connection.Transportation transportation) {
        List<Integer> newHidersPossiblePositions = new ArrayList<>();
        for (int position : hidersPossiblePositions) {
            if (transportation == Connection.Transportation.BLACK_FARE)
                newHidersPossiblePositions.addAll(board.getDestinationsForPosition(position));
            else {
                newHidersPossiblePositions.addAll(
                        board.getTransportationDestinationsForPosition(transportation, position));
            }
        }
        newHidersPossiblePositions.removeAll(getSeekersPositions(playersActualPositions));
        return new ArrayList<>(new LinkedHashSet<>(newHidersPossiblePositions));
    }

    private int getMostProbableHidersPosition() {
        List<Integer> seekersPositions = getSeekersPositions(playersActualPositions);
        double[] probabilities = new double[hidersPossiblePositions.size()];
        for (int i = 0; i < hidersPossiblePositions.size(); i++) {
            int position = hidersPossiblePositions.get(i);
            int minDistance = seekersPositions.stream().min(((position1, position2) -> Integer.compare(
                    board.shortestDistanceBetween(position1, position),
                    board.shortestDistanceBetween(position2, position)))).get();
            switch (minDistance) {
                case 1:
                    probabilities[i] = 0.196;
                    break;
                case 2:
                    probabilities[i] = 0.671;
                    break;
                case 3:
                    probabilities[i] = 0.540;
                    break;
                case 4:
                    probabilities[i] = 0.384;
                    break;
                default:
                    probabilities[i] = 0.196;
            }
        }
        // todo: fix whole method
        boolean notAccepted = true;
        int chosen = 0;
        double max_weight = Arrays.stream(probabilities).max().getAsDouble();
        while (notAccepted){
            chosen = (int)(probabilities.length * Math.random());
            if(Math.random() < probabilities[chosen] / max_weight) {
                notAccepted = false;
            }
        }
        return hidersPossiblePositions.get(chosen);
    }
}