
package com.example.ai_for_data_science.players.algorithms;
import com.example.ai_for_data_science.Algorithm;
import com.example.ai_for_data_science.Connect4;
import com.example.ai_for_data_science.DataSet;

import java.io.*;
import java.lang.Math;
import java.util.*;
import java.util.stream.Stream;


public class LinearRegression implements Algorithm {

    double winningRate = 0;
    double speedWinningRate = 0;

    public LinearRegression(float[][] independentFeatures, float[] dependentFeature, float[] weights, float bias,
                            float learningRate, int iterations, int batchSize, boolean isPlayerOne, DataSet dataSet) {
        this.independentFeatures = independentFeatures;
        this.dependentFeature = dependentFeature;
        this.weights = weights;
        this.bias = bias;
        this.learningRate = learningRate;
        this.iterations = iterations;
        this.batchSize = batchSize;

        this.isPlayerOne = isPlayerOne;

        this.dataSet = dataSet;
    }

    public LinearRegression(float learningRate, int iterations, int batchSize, boolean isPlayerOne, DataSet dataSet) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("winRates.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        float[][] independentFeatures = new float[42][];
        float[] dependentFeature = new float[42];
        float[] weights = new float[42];

        int c = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] data = line.split(",");

            float[] artificialGameBoard = new float[42];
            for (int i = 0; i < 42; i++) {
                artificialGameBoard[i] = Integer.parseInt(data[i]);
            }
            float winRate = Float.parseFloat(data[42]);

            dependentFeature[c] = winRate;
            independentFeatures[c] = artificialGameBoard;
            ++c;
        }

        this.independentFeatures = independentFeatures;
        this.dependentFeature = dependentFeature;
        this.weights = weights;
        this.bias = 0.0f;
        this.learningRate = learningRate;
        this.iterations = iterations;
        this.batchSize = batchSize;

        this.isPlayerOne = isPlayerOne;

        this.dataSet = dataSet;

        train();
    }

    private final float[][] independentFeatures;
    private final float[] dependentFeature;
    private float[] weights;
    private float bias;

    private final float learningRate;
    private final int iterations;
    private final int batchSize;

    public float[] getWeights() {
        return weights;
    }
    public float getBias() {
        return bias;
    }

    private boolean isPlayerOne;

    private DataSet dataSet;

    @Override
    public int returnMove(int[] gameBoard) {

        float bestPredictedWinRate = 0;
        int bestMoveCol = 0;

        for (int m : Connect4.getAvailableMoves(gameBoard)) {

            int[] nextGameBoard = Connect4.nextGameBoard(gameBoard, m, isPlayerOne);
            float[] nextGameBoard_f = new float[nextGameBoard.length];
            for (int i = 0 ; i < nextGameBoard.length; i++) {
                nextGameBoard_f[i] = (float)nextGameBoard[i];
            }

            float predictedWinRate = predict(nextGameBoard_f);

            if (predictedWinRate > bestPredictedWinRate) {
                bestPredictedWinRate = predictedWinRate;
                bestMoveCol = m;
            }
        }

        return bestMoveCol;
    }

    @Override
    public void printResults() {
        System.out.println("Linear Regression - Results:");
        System.out.println("  Weights: " + Arrays.toString(weights));
        System.out.println("  Bias: " + bias);