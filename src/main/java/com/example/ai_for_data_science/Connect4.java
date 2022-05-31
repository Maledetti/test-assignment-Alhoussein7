package com.example.ai_for_data_science;


import com.example.ai_for_data_science.players.algorithms.BayesianClassifier;
import com.example.ai_for_data_science.players.algorithms.Human;
import com.example.ai_for_data_science.players.algorithms.svm.AlgorithmsWinningScores;
import com.example.ai_for_data_science.players.algorithms.svm.ScorePlayers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Connect4 {

    private int[] gameBoard = new int[42];      // Default value is 0
    private ArrayList<Integer> moveOrder = new ArrayList<>();


    public Connect4() {
    }

    public void reset() {
        gameBoard = new int[42];
    }

    public void play(Algorithm player1, Algorithm player2) {
        int gameIsFinished = -1;
        double movesP1 = 0;
        double movesP2 = 0;

        boolean isPlayerOne = true;
        int moveCol;

        do {
            /* Alternate player and get the move decided by the player */
            if (isPlayerOne) {
                moveCol = player1.returnMove(gameBoard);
                movesP1++;
            }
            else {
                  moveCol = player2.returnMove(gameBoard);
                  movesP2++;
            }

            if (validateMove(gameBoard, moveCol)) {
                performMove(moveCol, isPlayerOne);
                moveOrder.add(moveCol);
            }
            else {
                try {
                    throw new Exception("Invalid move was played!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            isPlayerOne = !isPlayerOne;

        } while ((gameIsFinished = gameIsFinished(gameBoard)) == -1);

        if (player1 instanceof BayesianClassifier && player2 instanceof Human){
            scorePlayers(movesP1, movesP2, !isPlayerOne);
        }

        // The game has finished!
        System.out.println("Game is over! Result: " + gameIsFinished);
        printGameBoard(gameBoard);
        System.out.println("Move order: " + moveOrder.toString());
    }

    private void scorePlayers(double movesP1, double movesP2, boolean isP1Winning) {
        ScorePlayers sP = new ScorePlayers();
        AlgorithmsWinningScores aS = new AlgorithmsWinningScores();
        double[] bcScores = aS.getAlgScores("BayesianClassifier");
        double[] hScores = aS.getAlgScores("Human");
        double p1Speed = (movesP1/21) + bcScores[1];
        double p1WinScore = (2 * bcScores[0] + (isP1Winning ? 1 : -1))/2;
        double p2Speed = (movesP2/21) + bcScores[1];
        double p2WinScore = (2 * hScores[0] + (isP1Winning ? 1 : -1))/2;
        aS.fillAlgoScores("BayesianClassifier", new double[]{p1WinScore, p1Speed});
        aS.fillAlgoScores("Human", new double[]{p2WinScore, p2Speed});
        try {
            sP.addNewRecord(new double[]{(p1Speed +p1WinScore)/2, (p2Speed +p2WinScore)/2}, (isP1Winning ? 1 : -1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void printGameBoard(int[] gameBoard) {
        System.out.println("____ gameBoard ____");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(gameBoard[j + i * 7] + "  ");
            }
            System.out.println("");
        }
        System.out.println("___________________");
    }

    public static String gameBoardToString(int[] gameBoard) {
        String representation = "";
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                representation += (gameBoard[j + i * 7]);
            }
        }
        return representation;
    }


    public static boolean validateMove(int[] gameBoard, int moveCol) {
        return  moveCol >= 0 && moveCol <= 6 && gameBoard[moveCol] == 0;
    }

    private void performMove(int moveCol, boolean isPlayerOne) {
        gameBoard = nextGameBoard(gameBoard, moveCol, isPlayerOne);
    }


    /**
     * Returns how the next gameBoard will look, after the inputted move has been made.
     * Used by the minimax-algorithm to analyze the game in greater depth
     * @param gameBoard The current gameBoard
     * @param moveCol The move to make
     * @param isPlayerOne Whether the move should be a 1 or 2
     * @return The next gameBoard, after the move has been made
     */
    public static int[] nextGameBoard(int[] gameBoard, int moveCol, boolean isPlayerOne){
        int[] nextGameBoard = Arrays.copyOf(gameBoard, gameBoard.length);

        for (int i = moveCol + 35; i >= moveCol; i -= 7) {
            if (gameBoard[i] == 0) {
                nextGameBoard[i] = isPlayerOne ? 1 : 2;
                return nextGameBoard;
            }
        }
        return null;
    }

    /**
     * @return A list of valid columns where the disc can be placed
     */
    public static ArrayList<Integer> getAvailableMoves(int[] gameBoard) {
        ArrayList<Integer> moves = new ArrayList<>();
        for (int col = 0; col < 7; col++) {
            if (gameBoard[col] == 0) moves.add(col);
        }
        return moves;
    }

    /**
     * Evaluates if any player has won the game
     * @param gameBoard The gameBoard that is evaluated
     * @return -1: the game has _not_ been finished, 0: the game is a tie, 1: player1 won, 2: player2 won
     */
    public static int gameIsFinished(int[] gameBoard){

        // Horizontal check
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7-3; col++) {
                if (gameBoard[row * 7 + col] == 1 &&
                        gameBoard[row * 7 + col + 1] == 1 &&
                        gameBoard[row * 7 + col + 2] == 1 &&
                        gameBoard[row * 7 + col + 3] == 1){
                    return 1; // player 1 has 4 horizontal discs in a row
                }
                if (gameBoard[row * 7 + col] == 2 &&
                        gameBoard[row * 7 + col + 1] == 2 &&
                        gameBoard[row * 7 + col + 2] == 2 &&
                        gameBoard[row * 7 + col + 3] == 2){
                    return 2; // player 2 has 4 horizontal discs in a row
                }
            }
        }

        // Vertical check
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6-3; row++) {
                if (gameBoard[col + row * 7] == 1 &&
                        gameBoard[col + (row + 1) * 7] == 1 &&
                        gameBoard[col + (row + 2) * 7] == 1 &&
                        gameBoard[col + (row + 3) * 7] == 1){
                    return 1; // player 1 has 4 vertical discs in a row
                }
                if (gameBoard[col + row * 7] == 2 &&
                        gameBoard[col + (row + 1) * 7] == 2 &&
                        gameBoard[col + (row + 2) * 7] == 2 &&
                        gameBoard[col + (row + 3) * 7] == 2){
                    return 2; // player 2 has 4 vertical discs in a row
                }
            }
        }

        // diagonal (down + left) check
        for (int col = 3; col < 7; col++){
            for (int row = 0; row < 6-3; row++){
                if (gameBoard[col + row * 7] == 1 &&
                        gameBoard[col - 1 + (row + 1) * 7] == 1 &&
                        gameBoard[col - 2 + (row + 2) * 7] == 1 &&
                        gameBoard[col - 3 + (row + 3) * 7] == 1){
                    return 1; // player 1 has 4 diagonal discs in a row
                }
                if (gameBoard[col + row * 7] == 2 &&
                        gameBoard[col - 1 + (row + 1) * 7] == 2 &&
                        gameBoard[col - 2 + (row + 2) * 7] == 2 &&
                        gameBoard[col - 3 + (row + 3) * 7] == 2){
                    return 2; // player 2 has