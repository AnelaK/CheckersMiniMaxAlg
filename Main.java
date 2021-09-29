import java.util.Scanner;

public class Main {
    
    private static int boardSize(Scanner scanner) {
        int boardSizeChoice = 0;
        int boardSize = 0;
        while (boardSizeChoice != 1 && boardSizeChoice != 2) {
            System.out.println("Would you like to play on a: ");
            System.out.println("1: 4x4 board");
            System.out.println("2: 8x8 board");
            boardSizeChoice = scanner.nextInt();

            switch (boardSizeChoice) {
                case 1:
                    boardSize = 4;
                    break;
                case 2:
                    boardSize = 8;
                    break;
            }
        }
        return boardSize;
    }

    public static Player usersColor(Scanner scanner) {
        int usersColor = 0;
        Player userPlayer = Player.BLACK;
        while (usersColor != 1 && usersColor != 2) {
            System.out.println("Would you like to be: ");
            System.out.println("1: black");
            System.out.println("2: white");
            usersColor = scanner.nextInt();

            switch (usersColor) {
                case 1:
                    userPlayer = Player.BLACK;
                    break;
                case 2:
                    userPlayer = Player.WHITE;
                    break;
            }
        }
        return userPlayer;
    }

    private static SearchAlgorithm searchAlgorithm(Scanner scanner) {
        int usersChoiceAlgorithm = 0;
        SearchAlgorithm searchAlgorithm = SearchAlgorithm.MINIMAX;
        while (usersChoiceAlgorithm != 1 && usersChoiceAlgorithm != 2 && usersChoiceAlgorithm != 3) {
            System.out.println("Which algorithm do you want to use?");
            System.out.println("1: Minimax");
            System.out.println("2: Heuristic minimax with alpha-beta pruning");
            System.out.println("3: Heuristic minimax without alpha-beta pruning");
            usersChoiceAlgorithm = scanner.nextInt();

            switch (usersChoiceAlgorithm) {
                case 1:
                    searchAlgorithm = SearchAlgorithm.MINIMAX;
                    break;
                case 2:
                    searchAlgorithm = SearchAlgorithm.A_B_H_MINIMAX;
                    break;
                case 3:
                    searchAlgorithm = SearchAlgorithm.MINIMAX_H;
                    break;
                
            }
        }
        return searchAlgorithm;
    }

    private static int depthCutoff(Scanner scanner) {
        int depthCutoff = 0;
        while (depthCutoff <= 0) {
            System.out.println("Input a depth cutoff: ");
            depthCutoff = scanner.nextInt();
        }
        return depthCutoff;
    }

    private static int gameMode(Scanner scanner) {
        int usersChoiceGameMode = 0;
        int gameMode = 0;
        while (usersChoiceGameMode != 1 && usersChoiceGameMode != 2 && usersChoiceGameMode != 3) {
            System.out.println("Which game mode do you want to play?");
            System.out.println("1: Against computer?");
            System.out.println("2: 2-player mode");
            System.out.println("3: Computer vs Computer");
            usersChoiceGameMode = scanner.nextInt();

            switch (usersChoiceGameMode) {
                case 1:
                    gameMode = 1;
                    break;
                case 2:
                    gameMode = 2;
                    break;
                case 3:
                    gameMode = 3;
                    break;

            }
        }
        return gameMode;

    }

    private static void startGame(int gameMode, Runner start, Player usersColor) {
        switch (gameMode) {
            case 1:
                start.run(usersColor);
                break;
            case 2:
                start.runTwoPlayersMode(usersColor);
                break;
            case 3:
                start.runAgainstSelf();
                break;

        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int boardSize = boardSize(scanner);

        Player usersColor = usersColor(scanner);

        SearchAlgorithm searchAlgorithm = searchAlgorithm(scanner);

        int depthCutoff = 0;
        if (searchAlgorithm == SearchAlgorithm.A_B_H_MINIMAX || searchAlgorithm == SearchAlgorithm.MINIMAX_H) {
            depthCutoff = depthCutoff(scanner);
        }

        Runner start = new Runner(boardSize, searchAlgorithm, depthCutoff);

        int gameMode = gameMode(scanner);
        startGame(gameMode, start, usersColor);

        scanner.close();
        System.out.println("End of program");
    }
}
