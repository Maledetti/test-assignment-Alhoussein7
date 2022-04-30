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
        aS.fillAlgoScores("BayesianClassifier", new double[]{p1WinScore, p