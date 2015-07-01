package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class State implements MctsDomainState<Action, Player> {

    private static final int NUMBER_OF_PLAYERS = 6;
    private static final int NUMBER_OF_ROUNDS = 24;
    private static final List<Integer> HIDER_SURFACES_ROUNDS = new ArrayList<>(Arrays.asList(3, 8, 13, 18, 24));

    private final PlayersState playersState;
    private int currentRound;
    private int currentPlayerIndex;
    private int previousPlayerIndex;
    private boolean searchInvokingPlayerIsHider;
    private boolean inSimulation;

    public static State initialize(Player[] players) {
        // todo: validate players
        PlayersState playersState = PlayersState.initialize(players);
        return new State(playersState);
    }

    private State(PlayersState playersState) {
        this.playersState = playersState;
        this.currentRound = 1;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = playersState.getNumberOfPlayers() - 1;
        this.inSimulation = false;
    }

    public void setCurrentPlayerAsSearchInvokingPlayer() {
        searchInvokingPlayerIsHider = playersState.playerIsHider(currentPlayerIndex);
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
        return playersState.getPlayerAtIndex(currentPlayerIndex);
    }

    @Override
    public Player getPreviousAgent() {
        return playersState.getPlayerAtIndex(previousPlayerIndex);
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        List<Action> availableActions;
        if (searchInvokingPlayerIsHider)
            availableActions = playersState.getAvailableActionsForPlayerFromActualPosition(currentPlayerIndex);
        else
            availableActions = playersState.getAvailableActionsForPlayerFromSeekersPov(currentPlayerIndex);
        availableActions = addBlackFareActionsIfHiderAndOptimal(availableActions);
        return availableActions;
    }

    private List<Action> addBlackFareActionsIfHiderAndOptimal(List<Action> actions) {
        if (playersState.playerIsHider(currentPlayerIndex)) {
            Hider hider = (Hider)playersState.getPlayerAtIndex(currentPlayerIndex);
            return addBlackFareActionsForHiderIfOptimal(hider, actions);
        }
        return actions;
    }

    List<Action> addBlackFareActionsForHiderIfOptimal(Hider hider, List<Action> actions) {
        if (hider.shouldUseBlackfareTicket(currentRound, actions))
            return addBlackFareActions(actions);
        return actions;
    }

    private List<Action> addBlackFareActions(List<Action> actions) {
        List<Action> blackFareActions = generateBlackfareActions(actions);
        actions.addAll(blackFareActions);
        return removeDuplicates(actions);
    }

    private List<Action> generateBlackfareActions(List<Action> actions) {
        return actions.stream()
                .map(Action::generateBlackFareAction).collect(Collectors.toList());
    }

    private List<Action> removeDuplicates(List<Action> actions) {
        return new ArrayList<>(new LinkedHashSet<>(actions));
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        validateIsAvailableAction(action);
        playersState.movePlayerWithAction(currentPlayerIndex, action);
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
        currentPlayerIndex = ++currentPlayerIndex % playersState.getNumberOfPlayers();
    }
}