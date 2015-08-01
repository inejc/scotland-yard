package io.github.nejc92.sy.game;

import io.github.nejc92.mcts.MctsDomainState;
import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.players.Seeker;

import java.util.*;
import java.util.stream.Collectors;

public class State implements MctsDomainState<Action, Player> {

    private static final int MAX_NUMBER_OF_ROUNDS = 24;
    private static final List<Integer> HIDER_SURFACES_ROUNDS = new ArrayList<>(Arrays.asList(3, 8, 13, 18, 24));
    private static final int ALL_PLAYERS = 0;
    private static final int ONLY_SEEKERS = 1;

    private final PlayersOnBoard playersOnBoard;
    private final int numberOfPlayers;
    private int currentRound;
    private int currentPlayerIndex;
    private int previousPlayerIndex;
    private Action.Transportation lastHidersTransportation;
    private boolean inSearch;
    private boolean searchInvokingPlayerIsHider;

    public static State initialize(Player[] players) {
        PlayersOnBoard playersOnBoard = PlayersOnBoard.initialize(players);
        return new State(playersOnBoard, players.length);
    }

    private State(PlayersOnBoard playersOnBoard, int numberOfPlayers) {
        this.playersOnBoard = playersOnBoard;
        this.numberOfPlayers = numberOfPlayers;
        this.currentRound = 1;
        this.currentPlayerIndex = 0;
        this.previousPlayerIndex = numberOfPlayers - 1;
        this.lastHidersTransportation = null;
        this.inSearch = false;
        this.searchInvokingPlayerIsHider = false;
    }

    public PlayersOnBoard getPlayersOnBoard() {
        return playersOnBoard;
    }

    @Override
    public Player getCurrentAgent() {
        return playersOnBoard.getPlayerAtIndex(currentPlayerIndex);
    }

    @Override
    public Player getPreviousAgent() {
        return playersOnBoard.getPlayerAtIndex(previousPlayerIndex);
    }

    public void setSearchModeOn() {
        inSearch = true;
        searchInvokingPlayerIsHider = playersOnBoard.playerIsHider(currentPlayerIndex);
    }

    public void setSearchModeOff() {
        inSearch = false;
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

    private boolean inSearchFromSeekersPov() {
        return inSearch && !searchInvokingPlayerIsHider;
    }

    public void printNewRound() {
        if (currentPlayerIsHider()) {
            System.out.println("ROUND: " + currentRound);
            if (isHiderSurfacesRound())
                System.out.println("HIDER SURFACES!");
            System.out.println("----------");
        }
    }

    private boolean isHiderSurfacesRound() {
        return HIDER_SURFACES_ROUNDS.contains(currentRound);
    }

    @Override
    public boolean isTerminal() {
        return seekersWon() || hiderWon();
    }

    public boolean seekersWon() {
        if (inSearchFromSeekersPov())
            return playersOnBoard.anySeekerOnHidersMostProbablePosition();
        else
            return playersOnBoard.anySeekerOnHidersActualPosition();
    }

    public boolean hiderWon() {
        return currentRound == MAX_NUMBER_OF_ROUNDS;
    }

    public boolean seekerWon(Seeker seeker) {
        if (inSearchFromSeekersPov())
            return playersOnBoard.seekerOnHidersMostProbablePosition(seeker);
        else
            return playersOnBoard.seekerOnHidersActualPosition(seeker);
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        validateIsAvailableAction(action);
        if (inSearchFromSeekersPov())
            playersOnBoard.movePlayerFromSeekersPov(currentPlayerIndex, action);
        else
            playersOnBoard.movePlayerFromActualPosition(currentPlayerIndex, action);
        if (currentPlayerIsHider())
            lastHidersTransportation = action.getTransportation();
        setHidersMostProbablePosition(lastHidersTransportation);
        prepareStateForNextPlayer();
        performDoubleMoveIfShould();
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

    private void setHidersMostProbablePosition(Action.Transportation transportation) {
        if (currentPlayerIsHider())
            setHidersMostProbablePositionAfterHider(transportation);
        else
            playersOnBoard.removeCurrentSeekersPositionFromPossibleHidersPositions(currentPlayerIndex);
    }

    private void setHidersMostProbablePositionAfterHider(Action.Transportation transportation) {
        if (isHiderSurfacesRound())
            playersOnBoard.setHidersActualAsMostProbablePosition();
        else
            playersOnBoard.recalculateHidersMostProbablePosition(transportation);
    }

    private void  performDoubleMoveIfShould() {
        if (shouldCheckForHidersDoubleMoveAutomatically()) {
            Hider hider = (Hider)getPreviousAgent();
            if (hider.shouldUseDoubleMove(playersOnBoard)) {
                skipAllSeekers();
                hider.removeDoubleMoveCard();
            }
        }
    }

    private boolean shouldCheckForHidersDoubleMoveAutomatically() {
        return previousPlayerIsHider() && (previousPlayerIsHuman() && inSearch || !previousPlayerIsHuman());
    }

    public void skipAllSeekers() {
        currentPlayerIndex--;
        currentRound++;
    }

    @Override
    public MctsDomainState skipCurrentAgent() {
        prepareStateForNextPlayer();
        return this;
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        List<Action> availableActions;
        if (inSearchFromSeekersPov())
            availableActions = playersOnBoard.getAvailableActionsFromSeekersPov(currentPlayerIndex);
        else
            availableActions = playersOnBoard.getAvailableActionsForActualPosition(currentPlayerIndex);
        availableActions = addHidersBlackFairActions(availableActions);
        return availableActions;
    }

    private List<Action> addHidersBlackFairActions(List<Action> actions) {
        if (currentPlayerIsHider()) {
            if (notHumanInSearch())
                return addBlackFareActionsIfAvailableTickets(
                        (Hider) playersOnBoard.getPlayerAtIndex(currentPlayerIndex), actions);
            else
                return addBlackFareActionsForHiderIfOptimal(
                        (Hider) playersOnBoard.getPlayerAtIndex(currentPlayerIndex), actions);
        }
        return actions;
    }

    private boolean notHumanInSearch() {
        return currentPlayerIsHuman() && !inSearch;
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

    private void prepareStateForNextPlayer() {
        if (isLastPlayerOfRound())
            currentRound++;
        previousPlayerIndex = currentPlayerIndex;
        currentPlayerIndex = ++currentPlayerIndex % playersOnBoard.getNumberOfPlayers();
    }

    private boolean isLastPlayerOfRound() {
        return currentPlayerIndex == numberOfPlayers - 1;
    }

    public void printAllPositions() {
        playersOnBoard.printPlayers(ALL_PLAYERS);
    }

    public void printSeekersPositions() {
        playersOnBoard.printPlayers(ONLY_SEEKERS);
    }

    public void updateHidersProbablePosition() {
        playersOnBoard.fixHidersProbablePosition();
    }
}