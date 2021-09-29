import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static com.company.Algorithm.heuristic_minimax_w_alpha_beta_pruning;
import static com.company.Algorithm.minimax;
import static com.company.Algorithm.minimax_H;



enum SearchAlgorithm {
    MINIMAX,
    A_B_H_MINIMAX,
    MINIMAX_H
}

class Runner {
    private int boardSize;
    private SearchAlgorithm searchAlgorithm;
    private int depthCutoff;
    private State currentState;

    public Runner(int boardSize, SearchAlgorithm searchAlgorithm, int depthCutoff) {
        this.boardSize = boardSize;
        this.searchAlgorithm = searchAlgorithm;
        this.depthCutoff = depthCutoff;
        this.currentState = new State(boardSize);
    }

    private static Position stringToPosition(String input) {
        //this method takes a string for example G77 and returns
        //a position on the board COMPLETED
        char letter = input.charAt(0);
        int row = letter - 65;
        int column = Integer.parseInt(input.substring(1)) - 1;
        return new Position(row, column);
    }

    private static Position[] convertUserMoveHelper(String userMove) { //this method takes a string userMove as an input and returns
        //an array of all the positions where the move is supposed to be played  COMPLETED
        String[] userMoveStringArray;                           
        if (userMove.indexOf('-') >= 0)
            userMoveStringArray = userMove.split("-");
        else
            userMoveStringArray = userMove.split("x");

        int size = userMoveStringArray.length;
        Position[] userMovePositionArray = new Position[size];
        for (int i = 0; i < size; i++) {
            userMovePositionArray[i] = stringToPosition(userMoveStringArray[i]);
        }
        return userMovePositionArray;
    }

    //this method takes an array of positions dependent
    //on users move and returns the list of actions
    //needed to complete to finish the move
    public static List<Action> convertUserMove(String userMove) {   //TODO: is it really public
        List<Action> userMoveList = new LinkedList<>();
        Position[] positionArray = convertUserMoveHelper(userMove);
        for (int i = 0; i < positionArray.length - 1; i++) {
            Action newAction = new Action(positionArray[i], positionArray[i + 1]);
            userMoveList.add(newAction);
        }
        return userMoveList;
    }

    private static State getNextState(SearchAlgorithm searchAlgorithm, State currentState, int depthCutoff) {
        switch (searchAlgorithm) {
            case MINIMAX:
                return currentState.result(minimax(currentState));
            case A_B_H_MINIMAX:
                return currentState.result(heuristic_minimax_w_alpha_beta_pruning(currentState, depthCutoff));
            case MINIMAX_H:
                return currentState.result(minimax_H(currentState, depthCutoff));
            default:
                throw new IllegalStateException("Unexpected value: " + searchAlgorithm);
        }
    }

    public List<Action> userAction() {
        System.out.println("The set of applicable actions is: " + this.currentState.applicableActions().toString());
        System.out.println("Please enter a valid move as a sequence of cells you visit.");
        System.out.println("Accepted formats example: G4-F3 (for a regular move), G3xD1 (for a capture), G8xE6xC8 (for multiple captures).");

        Scanner scanner = new Scanner(System.in); //we need to close scanner later on
        String userMove = scanner.next();
        return convertUserMove(userMove);
    }

    public boolean has(HashSet<List<Action>> applicableActionSet, List<Action> usersActions) {
        for (List<Action> applicableActions : applicableActionSet) {
            for (Action applicableAction : applicableActions) {
                for (Action userAction : usersActions) {
                    if (applicableAction.equals(userAction)) return true;
                }
            }
        }
        return false;
    }

    public void run(Player userPlayer) {
        while (!currentState.isTerminal()) {
            currentState.printBoard();
            if (currentState.getPlayer() == userPlayer) {
                List<Action> userMoveList = userAction();
                if (has(currentState.applicableActions(), userMoveList)) {//currentState.applicableActions().contains(userMoveList)) {   //the user input is valid
                    currentState = currentState.result(userMoveList);
                } else {
                    System.out.println("OOPS! The input you entered " + userMoveList.toString() + " wasn't in the set of applicable actions.");
                }
            } else {
                currentState = getNextState(searchAlgorithm, currentState, depthCutoff);
            }
        }
        currentState.printBoard();
        if (currentState.utility() == 0) {
            System.out.println("The game is a draw.");
            return;
        }
        if ((currentState.utility() == 1 && currentState.getPlayer() == Player.WHITE) || (currentState.utility() == -1 && currentState.getPlayer() == Player.BLACK))
            System.out.println("Congratulations, you win!");
        else
            System.out.println("AI win.");  //pun intended


    }

    public void runTwoPlayersMode(Player userPlayer) {
        while (!currentState.isTerminal()) {
            currentState.printBoard();
            List<Action> userMoveList = userAction();
            if (has(currentState.applicableActions(), userMoveList)) {//currentState.applicableActions().contains(userMoveList)) {   //the user input is valid
                currentState = currentState.result(userMoveList);
            } else {
                System.out.println("OOPS! The input you entered " + userMoveList.toString() + " wasn't in the set of applicable actions.");
            }
        }
        System.out.println(currentState.utility());
    }

    public void runAgainstSelf() {
        while (!currentState.isTerminal()) {
            currentState.printBoard();
            currentState = getNextState(searchAlgorithm, currentState, depthCutoff);
        }
        currentState.printBoard();
    }
}

