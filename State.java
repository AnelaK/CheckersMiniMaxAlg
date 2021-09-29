import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

enum Piece {
    WHITEPOND,
    WHITEKING,
    BLACKPOND,
    BLACKKING,
    NONE
}

enum Player {
    WHITE, BLACK;

    public Player otherPlayer() {
        if (this == WHITE) {
            return BLACK;
        } else {
            return WHITE;
        }
    }
}

class Position {
    private int row;
    private int column;

    public Position() {
        this.row = 0;
        this.column = 0;
    }

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int num) {
        this.row = num;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int num) {
        this.column = num;
    }

    public char rowToChar() {
        return (char) (this.row + 65);
    }

    public String toString() {
        return (String.valueOf(rowToChar())) + (this.column + 1);
    }

    public boolean equals(Position position) {
        return this.row == position.row && this.column == position.column;
    }
}

class Action {
    private Position from;
    private Position to;

    public Action(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    public boolean moveDiagonallyTwo() {
        return Math.abs(to.getRow() - from.getRow()) == 2 && Math.abs(to.getColumn() - from.getColumn()) == 2;
    }

    public Position getFrom() {
        return this.from;
    }

    public void setFrom(Position origin) {
        this.from = origin;
    }

    public Position getTo() {
        return this.to;
    }

    public void setTo(Position destination) {
        this.to = destination;
    }

    public boolean isCapture() {
        return Math.abs(from.getRow() - to.getRow()) == 2;  //an Action is a capture if it goes two rows/columns above or below
    }

    public String toString() {
        return this.from.toString() + " -> " + this.to.toString();
    }

    public boolean equals(Action action) {
        return this.from.equals(action.from) && this.to.equals(action.to);
    }
}

class State {
    private Player player;
    private int level;
    private Piece[][] board;

    public State() {
        System.out.println("FOR TESTING ONLY");
    }

    public State(int sizeOfBoard) {
        this.player = Player.BLACK;
        this.level = 0;
        this.board = createInitialBoard(sizeOfBoard);
    }

    public State(State state) {
        this.player = state.player;
        this.level = state.level;
        this.board = state.boardClone();
    }

    private static Piece[][] createInitialBoard(int n) {
        Piece[][] initialBoard = new Piece[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                initialBoard[i][j] = Piece.NONE;
        switch (n) {
            case 4:
                initialBoard[0][1] = Piece.BLACKPOND;
                initialBoard[0][3] = Piece.BLACKPOND;
                initialBoard[3][0] = Piece.WHITEPOND;
                initialBoard[3][2] = Piece.WHITEPOND;
                break;
            case 8:
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 8; j++)
                        if ((i + j) % 2 == 1)
                            initialBoard[i][j] = Piece.BLACKPOND;
                for (int i = 5; i < 8; i++)
                    for (int j = 0; j < 8; j++)
                        if ((i + j) % 2 == 1)
                            initialBoard[i][j] = Piece.WHITEPOND;
        }
        return initialBoard;
    }

    private Piece[][] boardClone() {
        Piece[][] to = new Piece[board.length][board.length];
        for (int i = 0; i < board.length; i++)
            System.arraycopy(board[i], 0, to[i], 0, board.length);

        return to;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean coordinatesInRange(int row, int column) {
        return 0 <= row && row < this.board.length && 0 <= column && column < this.board.length;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public void printBoard() {
        System.out.print("  ");
        for (int i = 0; i < board.length; i++)
            System.out.print((i + 1) + " ");
        System.out.println();
        StringBuilder lineBetweenRowsBuilder = new StringBuilder(" +");
        for (int i = 0; i < board.length; i++)
            lineBetweenRowsBuilder.append("-+");
        String lineBetweenRows = lineBetweenRowsBuilder.toString();
        System.out.println(lineBetweenRows);
        char rowName = 'A';
        for (int i = 0; i < 2 * board.length; i++) {
            if (i % 2 == 0) {
                System.out.print(rowName + "|");
                for (int j = 0; j < board.length; j++) {
                    switch (board[i / 2][j]) {
                        case WHITEPOND:
                            System.out.print("w|");
                            break;
                        case WHITEKING:
                            System.out.print("W|");
                            break;
                        case BLACKPOND:
                            System.out.print("b|");
                            break;
                        case BLACKKING:
                            System.out.print("B|");
                            break;
                        case NONE:
                            System.out.print(" |");
                            break;
                    }
                }
                System.out.println();
                rowName++;
            } else {
                System.out.println(lineBetweenRows);
            }
        }
    }

    private HashSet<List<Action>> applicableActionsNoCapture(int row, int column, Player player) {
        HashSet<List<Action>> applicableActions = new HashSet<>();
        int[] changeInRowsPondBlack = {1, 1};
        int[] changeInColumnsPondBlack = {-1, 1};
        int[] changeInRowsPondWhite = {-1, -1};
        int[] changeInColumnsPondWhite = {-1, 1};
        int[] changeInRows = {1, 1, -1, -1};    //initially set for the King
        int[] changeInColumns = {-1, 1, -1, 1}; //initially set for the King
        if (this.board[row][column] == Piece.BLACKPOND || this.board[row][column] == Piece.WHITEPOND)
            switch (player) {
                case BLACK:
                    changeInRows = changeInRowsPondBlack;
                    changeInColumns = changeInColumnsPondBlack;
                    break;
                case WHITE:
                    changeInRows = changeInRowsPondWhite;
                    changeInColumns = changeInColumnsPondWhite;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + player);
            }
        for (int k = 0; k < changeInRows.length; k++) {   //We only check the first four scenarios
            int newRowCoordinate = row + changeInRows[k];
            int newColumnCoordinate = column + changeInColumns[k];

            if (!this.coordinatesInRange(newRowCoordinate, newColumnCoordinate))
                continue;
            if (board[newRowCoordinate][newColumnCoordinate] == Piece.NONE) {  //If there is no tile on a place we want to go to
                Position from = new Position(row, column);
                Position to = new Position(newRowCoordinate, newColumnCoordinate);
                LinkedList<Action> move = new LinkedList<>();
                move.push(new Action(from, to));
                applicableActions.add(move);
            }
        }
        return applicableActions;
    }

    private HashSet<List<Action>> applicableActionsPondCapture(int row, int column, Player player) {
        HashSet<List<Action>> applicableActions = new HashSet<>();
        int[] changeInRows;
        int[] changeInColumns;
        switch (player) {
            case BLACK:
                changeInRows = new int[]{1, 1, 2, 2};
                changeInColumns = new int[]{-1, 1, -2, 2};
                break;
            case WHITE:
                changeInRows = new int[]{-1, -1, -2, -2};
                changeInColumns = new int[]{-1, 1, -2, 2};
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + player);
        }
        int changeInIndex = changeInRows.length / 2;
        for (int k = 0; k < changeInIndex; k++) {
            int captureRow = row + changeInRows[k];
            int captureColumn = column + changeInColumns[k];
            if (!coordinatesInRange(captureRow, captureColumn))
                continue;
            if (board[captureRow][captureColumn] == Piece.NONE)
                continue;
            if (board[row][column] == Piece.BLACKPOND) {
                if (board[captureRow][captureColumn] != Piece.WHITEPOND && board[captureRow][captureColumn] != Piece.WHITEKING)
                    continue;
            }
            if (board[row][column] == Piece.WHITEPOND) {
                if (board[captureRow][captureColumn] != Piece.BLACKPOND && board[captureRow][captureColumn] != Piece.BLACKKING)
                    continue;
            }
            int newRowCoordinate = row + changeInRows[k + changeInIndex];
            int newColumnCoordinate = column + changeInColumns[k + changeInIndex];
            if (!coordinatesInRange(newRowCoordinate, newColumnCoordinate))
                continue;
            if (board[newRowCoordinate][newColumnCoordinate] == Piece.NONE) { //the capture is possible
                State newState = new State(this);
                newState.board[newRowCoordinate][newColumnCoordinate] = newState.board[row][column];
                newState.board[captureRow][captureColumn] = Piece.NONE;
                newState.board[row][column] = Piece.NONE;
                if (newRowCoordinate == 0)   //white pieces attack upwards
                    newState.board[newRowCoordinate][newColumnCoordinate] = Piece.WHITEKING;
                if (newRowCoordinate == board.length - 1)   //black pieces attack downwards
                    newState.board[newRowCoordinate][newColumnCoordinate] = Piece.BLACKKING;
                List<Action> move = new LinkedList<>(); //move consists of the entire move, i.e. all actions in order
                move.add(new Action(new Position(row, column), new Position(newRowCoordinate, newColumnCoordinate)));
                HashSet<List<Action>> nextMovesSet = new HashSet<>();
                if (newRowCoordinate != 0 && newRowCoordinate != board.length - 1)   //only allow further taking if not transformed into king
                    nextMovesSet = newState.applicableActionsPondCapture(newRowCoordinate, newColumnCoordinate, player); //call recursively this function from the new coordinates
                addNextMoves(applicableActions, move, nextMovesSet);
            }
        }
        return applicableActions;
    }

    private HashSet<List<Action>> applicableActionsKingCapture(int row, int column, Player player) {
        HashSet<List<Action>> applicableActions = new HashSet<>();
        int[] changeInRows = {1, 1, -1, -1, 2, 2, -2, -2};
        int[] changeInColumns = {-1, 1, -1, 1, -2, 2, -2, 2};
        int changeInIndex = changeInRows.length / 2;
        for (int k = 0; k < changeInIndex; k++) {
            int captureRow = row + changeInRows[k];
            int captureColumn = column + changeInColumns[k];
            if (!coordinatesInRange(captureRow, captureColumn))
                continue;
            if (board[captureRow][captureColumn] == Piece.NONE)
                continue;
            if (board[row][column] == Piece.BLACKKING) {
                if (board[captureRow][captureColumn] != Piece.WHITEPOND && board[captureRow][captureColumn] != Piece.WHITEKING)
                    continue;
            }
            if (board[row][column] == Piece.WHITEKING) {
                if (board[captureRow][captureColumn] != Piece.BLACKPOND && board[captureRow][captureColumn] != Piece.BLACKKING)
                    continue;
            }
            int newRowCoordinate = row + changeInRows[k + changeInIndex];
            int newColumnCoordinate = column + changeInColumns[k + changeInIndex];
            if (!coordinatesInRange(newRowCoordinate, newColumnCoordinate))
                continue;
            if (board[newRowCoordinate][newColumnCoordinate] == Piece.NONE) { //the capture is possible
                State newState = new State(this);
                newState.board[newRowCoordinate][newColumnCoordinate] = newState.board[row][column];
                newState.board[captureRow][captureColumn] = Piece.NONE;
                newState.board[row][column] = Piece.NONE;
                List<Action> move = new LinkedList<>(); //move consists of the entire move, i.e. all actions in order
                move.add(new Action(new Position(row, column), new Position(newRowCoordinate, newColumnCoordinate)));
                HashSet<List<Action>> nextMovesSet = newState.applicableActionsKingCapture(newRowCoordinate, newColumnCoordinate, player); //call recursively this function from the new coordinates
                addNextMoves(applicableActions, move, nextMovesSet);
            }
        }
        return applicableActions;
    }

    private void addNextMoves(HashSet<List<Action>> applicableActions, List<Action> move, HashSet<List<Action>> nextMovesSet) {
        if (nextMovesSet.isEmpty())  //if there are no other captures within the same move
            applicableActions.add(move);
        else {
            for (List<Action> listNextMove : nextMovesSet) {
                List<Action> addMove = new LinkedList<>(move);
                addMove.addAll(listNextMove);
                applicableActions.add(addMove);
            }
        }
    }


    private static HashSet<List<Action>> implementMaxCaptureApplicableActions(HashSet<List<Action>> allMoves) {
        HashSet<List<Action>> maxCaptureApplicableActions = new HashSet<>();
        int maxMoves = 0;
        for (List<Action> move : allMoves) {
            if (move.size() > maxMoves) {
                maxCaptureApplicableActions = new HashSet<>();
                maxCaptureApplicableActions.add(move);
                maxMoves = move.size();
            } else if (move.size() == maxMoves)
                maxCaptureApplicableActions.add(move);
        }
        if (maxCaptureApplicableActions.isEmpty()) { //no moves
            return maxCaptureApplicableActions;
        } else {
            for (List<Action> move : maxCaptureApplicableActions) {
                if (move.size() >= 2)    //all possible moves are captures
                    return maxCaptureApplicableActions;
            }
        }
        //The case we are left with is when all moves are of size 1, meaning a move is either a capture or a move
        //If there are captures, we choose them. Otherwise, just take the normal move 1 diagonally.
        HashSet<List<Action>> captures = new HashSet<>();
        for (List<Action> move : maxCaptureApplicableActions)
            if (move.get(0).isCapture()) //if the only move is a capture
                captures.add(move);
        if (captures.isEmpty())
            return maxCaptureApplicableActions;
        return captures;
    }

    public HashSet<List<Action>> applicableActions() {
        HashSet<List<Action>> applicableActions = new HashSet<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (this.player == Player.BLACK)
                    switch (board[i][j]) {
                        case BLACKPOND:
                            applicableActions.addAll(this.applicableActionsNoCapture(i, j, this.player));
                            applicableActions.addAll(this.applicableActionsPondCapture(i, j, this.player));
                            break;
                        case BLACKKING:
                            applicableActions.addAll(this.applicableActionsNoCapture(i, j, this.player));
                            applicableActions.addAll(this.applicableActionsKingCapture(i, j, this.player));
                            break;
                        default:
                            break;
                    }
                else
                    switch (board[i][j]) {
                        case WHITEPOND:
                            applicableActions.addAll(this.applicableActionsNoCapture(i, j, this.player));
                            applicableActions.addAll(this.applicableActionsPondCapture(i, j, this.player));
                            break;
                        case WHITEKING:
                            applicableActions.addAll(this.applicableActionsNoCapture(i, j, this.player));
                            applicableActions.addAll(this.applicableActionsKingCapture(i, j, this.player));
                            break;
                        default:
                            break;
                    }
            }
        }

//        Update the applicableActions set so that it contains only the Linked lists with maximum length
//        since these will be the moves with the most captures
        return implementMaxCaptureApplicableActions(applicableActions);
    }

    private void tryKing(Action action) {
        switch (this.board[action.getTo().getRow()][action.getTo().getColumn()]) {
            case WHITEPOND:
                if (action.getTo().getRow() == 0) {
                    this.board[action.getTo().getRow()][action.getTo().getColumn()] = Piece.WHITEKING;
                }
                break;
            case BLACKPOND:
                if (action.getTo().getRow() == this.board.length - 1) {
                    this.board[action.getTo().getRow()][action.getTo().getColumn()] = Piece.BLACKKING;
                }
                break;
        }
    }

    private void updateBoard(List<Action> actionList) {
        for (Action action : actionList) { // update board
            this.board[action.getTo().getRow()][action.getTo().getColumn()] = this.board[action.getFrom().getRow()][action.getFrom().getColumn()];
            this.board[action.getFrom().getRow()][action.getFrom().getColumn()] = Piece.NONE;

            tryKing(action);

            if (action.moveDiagonallyTwo()) { // if the move is a capture
                Position capturePosition = new Position();
                if (action.getTo().getRow() - action.getFrom().getRow() >= 0) {
                    capturePosition.setRow(action.getFrom().getRow() + 1);
                } else {
                    capturePosition.setRow(action.getFrom().getRow() - 1);
                }
                if (action.getTo().getColumn() - action.getFrom().getColumn() >= 0) {
                    capturePosition.setColumn(action.getFrom().getColumn() + 1);
                } else {
                    capturePosition.setColumn(action.getFrom().getColumn() - 1);
                }
                this.board[capturePosition.getRow()][capturePosition.getColumn()] = Piece.NONE;
            }
        }
    }

    /*
     * Result assumes that the actions it applies are valid
     */
    public State result(List<Action> actionList) {
        State resultState = new State(this);

        resultState.level += 1; // update level
        resultState.setPlayer(resultState.getPlayer().otherPlayer()); // update player
        resultState.updateBoard(actionList);

        return resultState;
    }

    public boolean isTerminal() {
        if ((this.board.length == 4 && this.level == 17) || (this.board.length == 8 && this.level == 50)) {
            return true;
        }
        if (this.applicableActions().isEmpty()) //state is a terminal state if a player has no possible move
            return true;
        boolean existingWhite = false;
        boolean existingBlack = false;
        for (Piece[] row : board) { //looping through the board to check if there are white or black ponds on it
            for (int j = 0; j < this.board[0].length; j++) {
                if (row[j] == Piece.WHITEKING || row[j] == Piece.WHITEPOND)
                    existingWhite = true;
                else if (row[j] == Piece.BLACKKING || row[j] == Piece.BLACKPOND)
                    existingBlack = true;
            }
        }
        // state is a terminal state if there are only white or only black ponds left
        return !existingBlack || !existingWhite;
    }

    public int utility() {
        if ((this.board.length == 4 && this.level == 17) || (this.board.length == 8 && this.level == 50))
            return 0;
        if (this.player == Player.BLACK && this.applicableActions().isEmpty())
            return 1; //win for white
        else if (this.player == Player.WHITE && this.applicableActions().isEmpty())
            return -1; //win for black
        return 0;
    }

    public int boardMajority() {
        int player;
        if (this.player == Player.WHITE) {
            player = 1;
        } else {
            player = -1;
        }
        int boardMajority = 0;
        for (Piece[] row : this.board) {
            for (Piece piece : row) {
                switch (piece) {
                    case WHITEPOND:
                        boardMajority += 3 * player;
                        break;
                    case WHITEKING:
                        boardMajority += 5 * player;
                        break;
                    case BLACKPOND:
                        boardMajority -= 3 * player;
                        break;
                    case BLACKKING:
                        boardMajority -= 5 * player;
                        break;
                }
            }
        }
        return boardMajority;
    }
}
