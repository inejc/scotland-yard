package io.github.nejc92.sy;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private static final String BOARD_FILE_NAME = "src/java/resources/board_file.xml";

    private List<List<Action>> boardPositions;

    protected static Board initialize() {
        Board board = new Board();
        board.initializeBoardPositions();
        return board;
    }

    private Board() {
        this.boardPositions = new ArrayList<>();
    }

    private void initializeBoardPositions() {
        BoardFileParser parser = new BoardFileParser(BOARD_FILE_NAME);
        this.boardPositions = parser.getParsedData();
    }

    protected List<Action> getPossibleActionsForPosition(int position) {
        return boardPositions.get(position - 1);
    }
}