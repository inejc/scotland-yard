package io.github.nejc92.sy;

import io.github.nejc92.mcts.Mcts;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.Board;
import io.github.nejc92.sy.game.State;
import io.github.nejc92.sy.players.*;

import java.util.List;
import java.util.Scanner;

public class ScotlandYard {

    private static final int MCTS_ITERATIONS = 7000;
    private static final double HIDERS_EXPLORATION = 0.2;
    private static final double SEEKERS_EXPLORATION = 2;
    private static final int NUMBER_OF_PLAYERS = 6;
    private static final int HUMAN_AS_HIDER = 1;
    private static final int HUMAN_AS_SEEKERS = 2;
    private static final int TEST_PLAYERS = 3;

    private static Player.Type humanType;
    private static int numberOfGames = 1;
    private static int numberOfSeekersWins = 0;
    private static int numberOfHidersWins = 0;

    public static void main(String... args) {
        printWelcomeText();
        Scanner scanner = new Scanner(System.in);
        Mcts<State, Action, Player> mcts = initializeSearch();
        setHumanPlayer(scanner);
        Player[] players = initializePlayers(humanType);
        for (int i = 0; i < numberOfGames; i++)
            playOneGame(mcts, players, scanner);
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

    private static void setHumanPlayer(Scanner scanner) {
        printSelectPlayerInstructions();
        int humanInput = Integer.parseInt(scanner.nextLine());
        if (humanInput == HUMAN_AS_HIDER)
            humanType = Player.Type.HIDER;
        else if (humanInput == HUMAN_AS_SEEKERS)
            humanType =  Player.Type.SEEKER;
        else {
            humanType = null;
            printSelectNumberOfGamesInstructions();
            numberOfGames = Integer.parseInt(scanner.nextLine());
        }
    }

    private static void printSelectPlayerInstructions() {
        System.out.print("To play as a hider enter " + HUMAN_AS_HIDER
                + "\nTo play as seekers enter " + HUMAN_AS_SEEKERS
                + "\nTo test players enter " + TEST_PLAYERS + "\nSelect player:\n");
    }

    private static void printSelectNumberOfGamesInstructions() {
        System.out.print("How many games should be played?\nEnter number of games:\n");
    }

    private static Player[] initializePlayers(Player.Type humanType) {
        if (humanType == Player.Type.HIDER)
            return initializePlayersWithOperator(Player.Operator.HUMAN, Player.Operator.COMPUTER);
        else if (humanType == Player.Type.SEEKER)
            return initializePlayersWithOperator(Player.Operator.COMPUTER, Player.Operator.HUMAN);
        else
            return initializePlayersWithOperator(Player.Operator.COMPUTER, Player.Operator.COMPUTER);
    }

    private static Player[] initializePlayersWithOperator(Player.Operator hider, Player.Operator seeker) {
        Player[] players = new Player[NUMBER_OF_PLAYERS];
        players[0] = new BiasedHider(hider);
        for (int i = 1; i < players.length; i++)
            players[i] = new BiasedSeeker(seeker, Seeker.Color.values()[i-1]);
        return players;
    }

    private static void playOneGame(Mcts<State, Action, Player> mcts, Player[] players, Scanner scanner) {
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
        System.out.println("\nCurrent player: " + state.getCurrentAgent() + "\n");
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
        System.out.print("Enter action:\n");
    }

    private static void printAvailableActions(List<Action> actions) {
        for (int i = 0; i < actions.size(); i++)
            System.out.println(i + ": " + actions.get(i));
        System.out.println();
    }

    private static Action getActionFromSearch(State state, Mcts<State, Action, Player> mcts) {
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
            System.out.println(action + "\n");
        else
            System.out.println(action.getTransportation() + "\n");
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
    }
}