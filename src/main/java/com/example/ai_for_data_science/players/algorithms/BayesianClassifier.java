
package com.example.ai_for_data_science.players.algorithms;

import com.example.ai_for_data_science.Algorithm;
import com.example.ai_for_data_science.Connect4;
import com.example.ai_for_data_science.DataSet;


public class BayesianClassifier implements Algorithm {

    DataSet dataSet;
    boolean isPlayerOne;
    double winningRate = 0;
    double speedWinningRate = 0;


    public BayesianClassifier(DataSet dataSet, boolean isPlayerOne) {
        this.dataSet = dataSet;
        this.isPlayerOne = isPlayerOne;
    }

    @Override
    public int returnMove(int[] gameBoard) {

        float highestProbability = 0;
        int bestMoveCol = 0;

        //System.out.print("\nMove probabilities: [ ");
        for (int m : Connect4.getAvailableMoves(gameBoard)) {
            int[] nextGameBoard = Connect4.nextGameBoard(gameBoard, m, isPlayerOne);
            float probability = predictWinProbability(nextGameBoard);

            //System.out.print(probability + ", ");

            if (probability > highestProbability) {
                highestProbability = probability;
                bestMoveCol = m;
            }
        }
        //System.out.print("]  -  Probability chosen: " + highestProbability + ", moveCol: (" + bestMoveCol + ")");

        return bestMoveCol;
    }

    @Override
    public void printResults() {
        System.out.println("Bayesian Classifier - Results:");
        System.out.println("\n  Accuracy: " + calculateAccuracy());
        //System.out.println("  R²: " + Math.round(rSquared() * 100000.0f) / 1000.0f + "%");
    }


    /**
     * Predicts the probability that this gameBoard results in a win
     */
    private float predictWinProbability(int[] gameBoard) {
        return predict(gameBoard)[0];
    }

    /**
     * Predicts the probability that this gameBoards results in a win, lose, tie, respectively
     */
    private float[] predict(int[] gameBoard) {
        int wins = 0, losses = 0;
        int totalWins = dataSet.winning_gameBoards.size();
        int totalLosses = dataSet.losing_gameBoards.size();

        for (int i = 0; i < dataSet.results.length; i++) {
            switch (dataSet.results[i]) {
                case 1 -> ++wins;
                case -1 -> ++losses;
            }
        }

        int[] categoryWinCounts = new int[gameBoard.length];
        int[] categoryLoseCounts = new int[gameBoard.length];

        for (int i = 0; i < dataSet.winning_gameBoards.size(); i++) {
            for (int j = 0; j < gameBoard.length; j++) {
                if (dataSet.winning_gameBoards.get(i)[j] == gameBoard[j])
                    ++categoryWinCounts[j];
            }
        }
        for (int i = 0; i < dataSet.losing_gameBoards.size(); i++) {
            for (int j = 0; j < gameBoard.length; j++) {
                if (dataSet.losing_gameBoards.get(i)[j] == gameBoard[j])
                    ++categoryLoseCounts[j];
            }
        }

        float likelyhoodWin = (float)wins / totalWins;
        float likelyhoodLose = (float)losses / totalLosses;

        for (int i = 0; i < categoryWinCounts.length; i++) {
            likelyhoodWin *= (float)(categoryWinCounts[i] + 1) / totalWins;   // +1 acts as a bias