package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.players.Seeker;

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
    private int currentPlayerIndex;
    private int previousPlayerIndex;
    private List<Integer> hidersPossiblePosition;
    private int hidersPositionFromCurrentPlayersPov;

    public static State initialize(Player[] players, List<Integer> startingPositions) {
        Board board = Board.initialize();
        List<Integer> hidersPossibleLocations = new ArrayList<>(startingPositions);
        hidersPossibleLocations.removeAll(getSeekersPositions(players));
        State state = new State(board, players, hidersPossibleLocations);
        state.setHidersPositionFromCurrentPlayersPov();
        return state;
    }

    private State(Board board, Player[] players, List<Integer> hidersPossiblePosition) {
        this.board = board;
        this.currentRound = 1;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = players.length - 1;
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
        return hidersPossiblePosition.get(0);
    }

    private void reCalculateHidersPossibleLocations(Action.Transportation transportation) {
        List<Integer> newHidersPossibleLocations = new ArrayList<>();
        if (HIDER_SURFACES_ROUNDS.contains(currentRound))
            newHidersPossibleLocations.add(getPreviousAgent().getBoardPosition());
        else{
            for (int position : hidersPossiblePosition) {
                if (transportation == Action.Transportation.BLACK_FARE)
                    newHidersPossibleLocations.addAll(board.getDestinationsForPosition(position));
                else
                    newHidersPossibleLocations.addAll(board.getTransportationDestinationsForPosition(transportation, position));
            }
            newHidersPossibleLocations.removeAll(getSeekersPositions(players));
        }
        hidersPossiblePosition = newHidersPossibleLocations;
    }

    private static List<Integer> getSeekersPositions(Player[] players) {
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
        if (currentPlayer.isHider()) {
            return getAvailableActionsForHider((Hider)currentPlayer);
        }
        else {
            return getAvailableActionsForSeeker((Seeker)currentPlayer);
        }
    }

    private List<Action> getAvailableActionsForHider(Hider hider) {
        List<Action> availableActions = new ArrayList<>(board.getActionsForPosition(hidersPositionFromCurrentPlayersPov));
        availableActions = removeImpossibleActions(availableActions);
        if (hider.willUseBlackfareTicket(this))
            // remove blackfare actions if exist
            availableActions.addAll(getBlackfareActionsForHider());
        return availableActions;
    }

    private List<Action> getBlackfareActionsForHider() {
        return board.generateBlackFareActionsForPosition(hidersPositionFromCurrentPlayersPov);
    }

    private List<Action> getAvailableActionsForSeeker(Seeker seeker) {
        List<Action> availableActions = new ArrayList<>(board.getActionsForPosition(seeker.getBoardPosition()));
        return removeImpossibleActions(availableActions);
    }

    private List<Action> removeImpossibleActions(List<Action> actions) {
        // no tickets (blackfare too)
        // occupied positions
        return actions;
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
        previousPlayerIndex = currentPlayerIndex;
        currentPlayerIndex = ++currentPlayerIndex % players.length;
    }
}