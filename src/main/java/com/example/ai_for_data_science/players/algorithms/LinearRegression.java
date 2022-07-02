
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
        System.out.println("  MSE: " + mseCost());
        System.out.println("  R²: " + Math.round(rSquared() * 100000.0f) / 1000.0f + "%");
        System.out.println("\n  Accuracy: " + calculateAccuracy());
    }


    public static void preProcessData() throws IOException {
//        BufferedWriter writer = new BufferedWriter(new FileWriter("winRates.csv", true));
//
//        for (int i = 0; i < 42; i++) {      // for each cell
//            double totalPlays = 0.0d;
//            int totalP1Plays = 0;
//            int winsP1 = 0;
//
//            Scanner scanner = new Scanner(new File("gameData.csv"));
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine();
//                String gameResult = line.split(",")[1];
//
//                char cellValue = line.charAt(i);
//                if (cellValue == '1') {
//                    if (gameResult.equals("1")) {
//                        ++winsP1;
//                    }
//                    ++totalP1Plays;
//                }
//                if (cellValue == '1' || cellValue == '2') {
//                    double nominator = ((i / 7) * 7);
//                    nominator = nominator == 0 ? 1 : nominator;
//                    totalPlays += (double)1 / nominator;
//                }
//            }
//
//            String winRate = totalP1Plays == 0 ? "" : String.valueOf((double)winsP1/totalP1Plays);
//
//            String totalPlays_s = String.valueOf(totalPlays);
//            writer.write(String.format("%d,%d,%d,%s,%s\n", i, i % 7, totalP1Plays, totalPlays_s, winRate));
//        }
//        writer.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter("winRates.csv", true));

        for (int i = 0; i < 42; i++) {      // for each cell
            int totalP1Plays = 0;
            int winsP1 = 0;

            Scanner scanner = new Scanner(new File("gameData.csv"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String gameResult = line.split(",")[1];

                char cellValue = line.charAt(i);
                if (cellValue == '1') {
                    if (gameResult.equals("1")) {
                        ++winsP1;
                    }
                    ++totalP1Plays;
                }
            }

            String winRate = totalP1Plays == 0 ? "" : String.valueOf((double)winsP1/totalP1Plays);

            String artificialGameBoard_s = "";
            for (int j = 0; j < 42; j++) {
                if (j == i) {
                    artificialGameBoard_s += "1,";
                }
                else {
                    artificialGameBoard_s += "0,";
                }
            }