package io.github.nejc92.sy;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import io.github.nejc92.mcts.Mcts;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.Board;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.players.Hider;
import io.github.nejc92.sy.players.Player;
import io.github.nejc92.sy.players.Player.Operator;
import io.github.nejc92.sy.players.PlayerProvider;
import io.github.nejc92.sy.strategies.CoalitionReduction;
import io.github.nejc92.sy.strategies.MoveFiltering;
import io.github.nejc92.sy.strategies.Playouts;

public class ScotlandYard {

    private static final int MCTS_ITERATIONS = 20000;
    private static final double HIDERS_EXPLORATION = 0.2;
    private static final double SEEKERS_EXPLORATION = 2;
    private static final int HUMAN_AS_HIDER = 1;
    private static final int HUMAN_AS_SEEKERS = 2;
    private static final int TEST_PLAYERS = 3;

    private static Player.Type humanType;
    private static int numberOfPlayers = 0;
    private static PlayerProvider playerProvider;
    private static int numberOfGames = 1;
    private static int numberOfSeekersWins = 0;
    private static int numberOfHidersWins = 0;

    public static void main(String... args) throws Exception {
        printWelcomeText();
        Scanner scanner = new Scanner(System.in);
        Mcts<State, Action, Player> mcts = initializeSearch();
        playerProvider = new PlayerProvider()
            .setPlayouts(Playouts.Uses.GREEDY)
            .setCoalitionReduction(CoalitionReduction.Uses.YES)
            .setMoveFiltering(MoveFiltering.Uses.YES);
        setHumanPlayer(scanner);
        for (int i = 0; i < numberOfGames; i++)
            playOneGame(playerProvider, mcts, scanner);
        System.out.println("Number of seeker's wins: " + numberOfSeekersWins
                + ", number of hider's wins: " + numberOfHidersWins);
    }

    private static void printWelcomeText() {
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("|                SCOTLAND YARD BOARD GAME - MONTE CARLO TREE SEARCH                    |");
        System.out.println("|                                                                                      |");
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("    " +
                "Welcome to the Scotland Yard Board Game with Monte Carlo Tree Search AI players.\n");
    }

    private static Mcts<State, Action, Player> initializeSearch() {
        Mcts<State, Action, Player> mcts = Mcts.initializeIterations(MCTS_ITERATIONS);
        mcts.dontClone(Board.class);
        return mcts;
    }

    private static void setHumanPlayer(Scanner scanner) throws Exception {
        printSelectPlayerInstructions();
        int selectPlayerInput = Integer.parseInt(scanner.nextLine());
        printSelectNumberOfPlayersInstructions();
        numberOfPlayers = Integer.parseInt(scanner.nextLine());
        int index = 0;
        if (selectPlayerInput == HUMAN_AS_HIDER) {
            humanType = Player.Type.HIDER;
            printHumanPlayerNameInstructions();
            playerProvider.addPlayer(Player.Type.HIDER, Operator.HUMAN, scanner.nextLine());
            while (++index < numberOfPlayers)
                playerProvider.addPlayer(Player.Type.SEEKER, Operator.MCTS);
        } else if (selectPlayerInput == HUMAN_AS_SEEKERS) {
            humanType =  Player.Type.SEEKER;
            playerProvider.addPlayer(Player.Type.HIDER, Operator.MCTS); 
            while (++index < numberOfPlayers) {
                printHumanPlayerNameInstructions(index);
                playerProvider.addPlayer(Player.Type.SEEKER, Operator.HUMAN, scanner.nextLine());
            }
        } else {
            humanType = null;
            while (++index < numberOfPlayers)
                playerProvider.addPlayer( index==1 ? Player.Type.HIDER : Player.Type.SEEKER
                    , Operator.MCTS);
            printSelectNumberOfGamesInstructions();
            numberOfGames = Integer.parseInt(scanner.nextLine());
        }
    }

    private static void printSelectPlayerInstructions() {
        System.out.print("To play as a hider enter " + HUMAN_AS_HIDER
                + "\nTo play as seekers enter " + HUMAN_AS_SEEKERS
                + "\nTo test players enter " + TEST_PLAYERS + "\nSelect player:\n");
    }

    private static void printSelectNumberOfPlayersInstructions() {
        System.out.print("How many players in the game? This includes both seekers and hiders.\nEnter number of players:\n");
    }

    private static void printSelectNumberOfGamesInstructions() {
        System.out.print("How many games should be played?\nEnter number of games:\n");
    }

    private static void printHumanPlayerNameInstructions() {
        System.out.print("Enter the name of player (leave blank to auto-fill):\n");
    }

    private static void printHumanPlayerNameInstructions(int index) {
        System.out.printf("Enter the name of player %d (leave blank to auto-fill):\n", index);
    }

    private static void playOneGame(PlayerProvider playerProvider, Mcts<State, Action, Player> mcts, Scanner scanner) {
        Player[] players = playerProvider.initializePlayers();
        State state = State.initialize(players);
        while (!state.isTerminal()) {
            performOneAction(state, mcts, scanner);
        }
        saveAndPrintResult(state);
    }

    private static void performOneAction(State state, Mcts<State, Action, Player> mcts, Scanner scanner) {
        if (shouldPrintGameStateInfo())
            printBeforeMove(state);
        if (currentPlayerCanMove(state)) {
            Action mostPromisingAction = getNextAction(state, mcts, scanner);
            state.performActionForCurrentAgent(mostPromisingAction);
        }
        else
            state.skipCurrentAgent();
        askHumanForDoubleMove(state, scanner);
    }

    private static void printBeforeMove(State state) {
        state.printNewRound();
        if (humanType == Player.Type.HIDER)
            state.printAllPositions();
        else
            state.printSeekersPositions();
    }

    private static boolean currentPlayerCanMove(State state) {
        return state.getAvailableActionsForCurrentAgent().size() > 0;
    }

    private static Action getNextAction(State state, Mcts<State, Action, Player> mcts, Scanner scanner) {
        Action mostPromisingAction;
        if (state.currentPlayerIsHuman())
            mostPromisingAction = getActionFromInput(state, scanner);
        else
            mostPromisingAction = getActionFromSearch(state, mcts);
        return mostPromisingAction;
    }

    private static Action getActionFromInput(State state, Scanner scanner) {
        printInputActionInstructions(state);
        int actionIndex = Integer.parseInt(scanner.nextLine());
        return state.getAvailableActionsForCurrentAgent().get(actionIndex);
    }

    private static void printInputActionInstructions(State state) {
        System.out.println("Available actions:");
        printAvailableActions(state.getAvailableActionsForCurrentAgent());
        System.out.printf("Enter action for %s:\n", state.getCurrentAgent());
    }

    private static void printAvailableActions(List<Action> actions) {
        for (int i = 0; i < actions.size(); i++)
            System.out.println(i + ": " + actions.get(i));
        System.out.println();
    }

    private static Action getActionFromSearch(State state, Mcts<State, Action, Player> mcts) {
        if (state.currentPlayerIsRandom())
            return getRandomAction(state);
        else
            return getActionFromMctsSearch(state, mcts);
    }

    private static Action getRandomAction(State state) {
        List<Action> actions = state.getAvailableActionsForCurrentAgent();
        Collections.shuffle(actions);
        return actions.get(0);
    }

    private static Action getActionFromMctsSearch(State state, Mcts<State, Action, Player> mcts) {
        Action mostPromisingAction;
        state.setSearchModeOn();
        updateHidersMostProbablePosition(state);
        double explorationParameter = getAppropriateExplorationParameter(state);
        mostPromisingAction = mcts.uctSearchWithExploration(state, explorationParameter);
        if (shouldPrintGameStateInfo())
            printSelectedAction(state, mostPromisingAction);
        state.setSearchModeOff();
        return mostPromisingAction;
    }

    private static boolean shouldPrintGameStateInfo() {
        return humanType != null;
    }

    private static void updateHidersMostProbablePosition(State state) {
        if (state.isTerminal())
            state.updateHidersProbablePosition();
    }

    private static double getAppropriateExplorationParameter(State state) {
        if (state.currentPlayerIsHider())
            return HIDERS_EXPLORATION;
        else
            return SEEKERS_EXPLORATION;
    }

    private static void printSelectedAction(State state, Action action) {
        if (humanType == Player.Type.HIDER || state.isHiderSurfacesRound())
            System.out.println(state.getCurrentAgent() + ": " + action + "\n");
        else
            System.out.println(state.getCurrentAgent() + ": " + action.getTransportation() + "\n");
    }

    private static void askHumanForDoubleMove(State state, Scanner scanner) {
        if (shouldAskForDoubleMove(state)) {
            Hider hider = (Hider)state.getPreviousAgent();
            if (hider.hasDoubleMoveCard())
                askHumanForDoubleMoveConfidently(state, hider, scanner);
        }
    }

    private static void askHumanForDoubleMoveConfidently(State state, Hider hider, Scanner scanner) {
        System.out.println("Use double move? y/n");
        String doubleMove = scanner.nextLine();
        if (doubleMove.equals("y")) {
            state.skipAllSeekers();
            hider.removeDoubleMoveCard();
        }
    }

    private static boolean shouldAskForDoubleMove(State state) {
        return state.previousPlayerIsHider() && state.previousPlayerIsHuman();
    }

    private static void saveAndPrintResult(State state) {
        if (state.seekersWon()) {
            numberOfSeekersWins++;
            System.out.println("Seekers won!");
        }
        else {
            numberOfHidersWins++;
            System.out.println("Hider won!");
        }
        state.printAllPositions();
    }
}