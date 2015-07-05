package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.game.board.Connection;
import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class State implements MctsDomainState<Action, Player> {

    private static final int MAX_NUMBER_OF_ROUNDS = 23;
    private static final List<Integer> HIDER_SURFACES_ROUNDS = new ArrayList<>(Arrays.asList(3, 8, 13, 18, 24));

    private final PlayersOnBoard playersOnBoard;
    private final int numberOfPlayers;
    private int currentRound;
    private int currentPlayerIndex;
    private int previousPlayerIndex;
    private Connection.Transportation lastHidersTransportation;
    private boolean inSearch;
    private boolean searchInvokingPlayerIsHider;
    private boolean inSimulation;

    public static State initialize(Player[] players) {
        PlayersOnBoard playersOnBoard = PlayersOnBoard.initialize(players);
        return new State(playersOnBoard, players.length);
    }

    private State(PlayersOnBoard playersOnBoard, int numberOfPlayers) {
        this.playersOnBoard = playersOnBoard;
        this.numberOfPlayers = numberOfPlayers;
        this.currentRound = 1;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = playersOnBoard.getNumberOfPlayers() - 1;
        this.lastHidersTransportation = null;
        this.inSearch = false;
        this.inSimulation = false;
    }

    public void setSearchModeOn() {
        inSearch = true;
        searchInvokingPlayerIsHider = playersOnBoard.playerIsHider(currentPlayerIndex);
    }

    public void setSearchModeOff() {
        inSearch = false;
    }

    private boolean isHiderSurfacesRound() {
        return HIDER_SURFACES_ROUNDS.contains(currentRound);
    }

    public void setSimulationModeOn() {
        inSimulation = true;
    }

    @Override
    public boolean isTerminal() {
        return seekersWon() || hiderWon();
    }

    public boolean seekersWon() {
        if (inSearch && !searchInvokingPlayerIsHider)
            return playersOnBoard.seekerOnHidersMostProbablePosition();
        else
            return playersOnBoard.seekerOnHidersActualPosition();
    }

    public boolean hiderWon() {
        return currentRound == MAX_NUMBER_OF_ROUNDS;
    }

    @Override
    public Player getCurrentAgent() {
        return playersOnBoard.getPlayerAtIndex(currentPlayerIndex);
    }

    public boolean currentPlayerIsHider() {
        return playersOnBoard.playerIsHider(currentPlayerIndex);
    }

    public boolean currentPlayerIsHuman() {
        return playersOnBoard.playerIsHuman(currentPlayerIndex);
    }

    @Override
    public Player getPreviousAgent() {
        return playersOnBoard.getPlayerAtIndex(previousPlayerIndex);
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        validateIsAvailableAction(action);
        if (searchInvokingPlayerIsHider)
            playersOnBoard.movePlayerFromActualPosition(currentPlayerIndex, action);
        else
            playersOnBoard.movePlayerFromSeekersPov(currentPlayerIndex, action);
        if (playersOnBoard.playerIsHider(currentPlayerIndex))
            lastHidersTransportation = action.getTransportation();
        prepareForNextPlayer();
        // HUMAN? check hider double move, if yes: currentPlayerIndex--
        // if (!inSimulation && currentPlayerIsHuman() && currentPlayerIsHider())
            // ask
        // else if (currentPlayerIsHuman() && currentPlayerIsHider() && playersOnBoard.c)
            // check
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

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        List<Action> availableActions;
        if (searchInvokingPlayerIsHider)
            availableActions = playersOnBoard.getAvailableActionsForActualPosition(currentPlayerIndex);
        else
            availableActions = playersOnBoard.getAvailableActionsFromSeekersPov(currentPlayerIndex);
        if (playersOnBoard.playerIsHider(currentPlayerIndex)) {
            // HUMAN?
            availableActions = addBlackFareActionsForHiderIfOptimal(
                    (Hider) playersOnBoard.getPlayerAtIndex(currentPlayerIndex), availableActions);
        }
        return availableActions;
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

    private void prepareForNextPlayer() {
        if (!inSearch)
            setHidersMostProbablePosition(lastHidersTransportation);
        if (isLastPlayerOfRound())
            currentRound++;
        previousPlayerIndex = currentPlayerIndex;
        currentPlayerIndex = ++currentPlayerIndex % playersOnBoard.getNumberOfPlayers();
    }

    private boolean isLastPlayerOfRound() {
        return currentPlayerIndex == numberOfPlayers - 1;
    }

    private void setHidersMostProbablePosition(Connection.Transportation transportation) {
        if (currentPlayerIsHider()) {
            if (isHiderSurfacesRound())
                playersOnBoard.setHidersActualAsMostProbablePosition();
            else
                playersOnBoard.recalculateHidersMostProbablePosition(transportation);
        }
        else {
            playersOnBoard.removeCurrentSeekersPositionFromPossibleHidersPositions(currentPlayerIndex);
        }
    }
}