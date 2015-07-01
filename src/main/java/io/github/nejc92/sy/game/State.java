package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State implements MctsDomainState<Action, Player> {

    private static final int NUMBER_OF_PLAYERS = 6;
    private static final int NUMBER_OF_ROUNDS = 24;
    private static final List<Integer> HIDER_SURFACES_ROUNDS = new ArrayList<>(Arrays.asList(3, 8, 13, 18, 24));

    private final PlayersOnBoard playersOnBoard;
    private int currentRound;
    private int currentPlayerIndex;
    private int previousPlayerIndex;
    private boolean searchInvokingPlayerIsHider;
    private boolean inSimulation;

    public static State initialize(Player[] players) {
        // todo: validate players
        PlayersOnBoard playersOnBoard = PlayersOnBoard.initialize(players);
        return new State(playersOnBoard);
    }

    private State(PlayersOnBoard playersOnBoard) {
        this.playersOnBoard = playersOnBoard;
        this.currentRound = 1;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = playersOnBoard.getNumberOfPlayers() - 1;
        this.inSimulation = false;
    }

    public void setCurrentPlayerAsSearchInvokingPlayer() {
        searchInvokingPlayerIsHider = playersOnBoard.playerIsHider(currentPlayerIndex);
    }

    public void setSimulationModeOn() {
        inSimulation = true;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public Player getCurrentAgent() {
        return playersOnBoard.getPlayerAtIndex(currentPlayerIndex);
    }

    @Override
    public Player getPreviousAgent() {
        return playersOnBoard.getPlayerAtIndex(previousPlayerIndex);
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        if (searchInvokingPlayerIsHider)
            return playersOnBoard.getAvailableActionsForPlayerFromActualPosition(currentPlayerIndex);
        else
            return playersOnBoard.getAvailableActionsForPlayerFromSeekersPov(currentPlayerIndex);
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        validateIsAvailableAction(action);
        playersOnBoard.movePlayerWithAction(currentPlayerIndex, action);
        // remove transportation card
        //getCurrentAgent().moveToBoardPosition(action.getDestination());
        //calculateNextRoundPlayersIndices();
        // check hider double move, if yes: currentPlayerIndex--
        // if not - recalculate hiders possible positions
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

    private void prepareForNextRound() {
        currentRound++;
        previousPlayerIndex = currentPlayerIndex;
        currentPlayerIndex = ++currentPlayerIndex % playersOnBoard.getNumberOfPlayers();
    }
}