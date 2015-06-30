package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class State implements MctsDomainState<Action, Player> {

    private static final int NUMBER_OF_ROUNDS = 24;
    private static final List<Integer> HIDER_SURFACES_ROUNDS = new ArrayList<>(Arrays.asList(3, 8, 13, 18, 24));

    private final Board board;
    private int currentRound;
    private final Player[] players;
    private final int numberOfPlayers;
    private int currentPlayerIndex;
    private int previousPlayerIndex;
    private List<Integer> hidersPossiblePosition;
    private int hidersPositionFromCurrentPlayersPov;

    public static State initialize(Player[] players, List<Integer> startingPositions) {
        Board board = Board.initialize();
        State state = new State(board, players, startingPositions);
        state.setHidersPositionFromCurrentPlayersPov();
        return state;
    }

    private State(Board board, Player[] players, List<Integer> hidersPossiblePosition) {
        this.board = board;
        this.currentRound = 1;
        this.players = players;
        this.numberOfPlayers = players.length;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = numberOfPlayers - 1;
        this.hidersPossiblePosition = hidersPossiblePosition;
    }

    private void setHidersPositionFromCurrentPlayersPov() {
        Player currentPlayer = getCurrentAgent();
        if (currentPlayer.isHider())
            hidersPositionFromCurrentPlayersPov = currentPlayer.getBoardPosition();
        else
            hidersPositionFromCurrentPlayersPov = getMostProbableHidersPosition();
    }

    public int getMostProbableHidersPosition() {
        return 0;
    }

    private void reCalculateHidersPossibleLocations(Action.Transportation transportation) {
        List<Integer> newHidersPossibleLocations = new ArrayList<>();
        if (HIDER_SURFACES_ROUNDS.contains(currentRound))
            newHidersPossibleLocations.add(getPreviousAgent().getBoardPosition());
        else{
            for (int position : hidersPossiblePosition) {
                if (transportation == Action.Transportation.BLACKFARE)
                    newHidersPossibleLocations.addAll(board.getDestinationsForPosition(position));
                else
                    newHidersPossibleLocations.addAll(board.getTransportationDestinationsForPosition(transportation, position));
            }
            newHidersPossibleLocations.removeAll(getSeekersPositions());
        }
        hidersPossiblePosition = newHidersPossibleLocations;
    }

    private List<Integer> getSeekersPositions() {
        return Arrays.stream(players).skip(0).map(Player::getBoardPosition).collect(Collectors.toList());
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public Player getCurrentAgent() {
        return players[currentPlayerIndex];
    }

    @Override
    public Player getPreviousAgent() {
        return players[previousPlayerIndex];
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        Player currentPlayer = getCurrentAgent();
        int currentPlayersPosition;
        if (currentPlayer.isHider())
            currentPlayersPosition = hidersPositionFromCurrentPlayersPov;
        else
            currentPlayersPosition = currentPlayer.getBoardPosition();
        List<Action> allPossibleActions = board.getPossibleActionsForPosition(currentPlayersPosition);
        List<Action> availableActions = new ArrayList<>(allPossibleActions);
        // remove impossible and no tickets
        return availableActions;
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        validateIsAvailableAction(action);
        // before hider
        // remove transportation card
        getCurrentAgent().moveToBoardPosition(action.getDestination());
        selectNextPlayer();
        // check hider double move, if yes: currentPlayerIndex--
        // if not - after hider
        return this;
    }

    private void validateIsAvailableAction(Action action) {
        if (!isAvailableAction(action)) {
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
        }
    }

    private boolean isAvailableAction(Action action) {
        return getAvailableActionsForCurrentAgent().contains(action);
    }

    private void selectNextPlayer() {
        currentPlayerIndex = ++currentPlayerIndex % numberOfPlayers;
        previousPlayerIndex = ++previousPlayerIndex % numberOfPlayers;
    }
}