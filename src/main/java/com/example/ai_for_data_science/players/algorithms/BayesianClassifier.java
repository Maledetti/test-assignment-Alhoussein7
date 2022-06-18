
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