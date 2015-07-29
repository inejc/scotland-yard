package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.game.board.Connection;
import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.players.Seeker;

import java.util.*;
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

    public void printNewRound() {
        if (currentPlayerIsHider()) {
            System.out.println("ROUND: " + currentRound);
            if (isHiderSurfacesRound())
                System.out.println("HIDER SURFACES!");
            System.out.println("----------");
        }
    }

    public void printPositions() {
        Arrays.stream(playersOnBoard.getPlayersActualPositions()).forEach(
                position -> System.out.print(position + " ")
        );
        System.out.println();
    }

    @Override
    public boolean isTerminal() {
        return seekersWon() || hiderWon();
    }

    public boolean seekersWon() {
        if (inSearch && !searchInvokingPlayerIsHider)
            return playersOnBoard.anySeekerOnHidersMostProbablePosition();
        else
            return playersOnBoard.anySeekerOnHidersActualPosition();
    }

    public boolean hiderWon() {
        return currentRound == MAX_NUMBER_OF_ROUNDS;
    }

    public boolean seekerWon(Seeker seeker) {
        if (inSearch && !searchInvokingPlayerIsHider)
            return playersOnBoard.seekerOnHidersMostProbablePosition(seeker);
        else
            return playersOnBoard.seekerOnHidersActualPosition(seeker);
    }

    @Override
    public Player getCurrentAgent() {
        return playersOnBoard.getPlayerAtIndex(currentPlayerIndex);
    }

    public boolean currentPlayerIsHider() {
        return playersOnBoard.playerIsHider(currentPlayerIndex);
    }

    public boolean previousPlayerIsHider() {
        return playersOnBoard.playerIsHider(previousPlayerIndex);
    }

    public boolean currentPlayerIsHuman() {
        return playersOnBoard.playerIsHuman(currentPlayerIndex);
    }

    public boolean previousPlayerIsHuman() {
        return playersOnBoard.playerIsHuman(previousPlayerIndex);
    }

    @Override
    public Player getPreviousAgent() {
        return playersOnBoard.getPlayerAtIndex(previousPlayerIndex);
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        validateIsAvailableAction(action);
        if (inSearch && !searchInvokingPlayerIsHider)
            playersOnBoard.movePlayerFromSeekersPov(currentPlayerIndex, action);
        else
            playersOnBoard.movePlayerFromActualPosition(currentPlayerIndex, action);
        if (playersOnBoard.playerIsHider(currentPlayerIndex))
            lastHidersTransportation = action.getTransportation();
        prepareForNextPlayer();
        if (previousPlayerIsHider() && inSearch) {
            Hider hider = (Hider)getPreviousAgent();
            if (hider.shouldUseDoubleMove()) {
                skipAllSeekers();
                hider.removeDoubleMoveCard();
            }
        }
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


    public void skipAllSeekers() {
        currentPlayerIndex--;
        currentRound++;
    }

    @Override
    public MctsDomainState skipCurrentAgent() {
        prepareForNextPlayer();
        return this;
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        List<Action> availableActions;
        if (inSearch && !searchInvokingPlayerIsHider)
            availableActions = playersOnBoard.getAvailableActionsFromSeekersPov(currentPlayerIndex);
        else
            availableActions = playersOnBoard.getAvailableActionsForActualPosition(currentPlayerIndex);
        if (currentPlayerIsHider()) {
            if (currentPlayerIsHuman() && !inSearch && !inSimulation)
                availableActions = addBlackFareActionsIfAvailableTickets(
                        (Hider) playersOnBoard.getPlayerAtIndex(currentPlayerIndex), availableActions);
            else {
                availableActions = addBlackFareActionsForHiderIfOptimal(
                    (Hider) playersOnBoard.getPlayerAtIndex(currentPlayerIndex), availableActions);
            }
        }
        return availableActions;
    }

    List<Action> addBlackFareActionsIfAvailableTickets(Hider hider, List<Action> actions) {
        if (hider.hasBlackFareTicket())
            return addBlackFareActions(actions);
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