
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

            writer.write(String.format("%s%s\n", artificialGameBoard_s, winRate));
        }
        writer.close();
    }


    private float[] predict() {
        int n = independentFeatures.length;
        int m = independentFeatures[0].length;
        float[] sums = new float[n];

        for (int i = 0; i < n; i++) {
            sums[i] = bias;
            for (int j = 0; j < m; j++) {
                sums[i] += independentFeatures[i][j] * weights[j];
            }
        }
        return sums;
    }
    private float predict(float[] independentFeature) {
        float sum = bias;
        for (int j = 0; j < independentFeature.length; j++) {
            sum += independentFeature[j] * weights[j];
        }
        return sum;
    }

    private float mseCost() {
        float[] squareErrors = pow(subtract(predict(), dependentFeature), 2);
        return sum(divide(squareErrors, (2 * dependentFeature.length)));
    }

    private float rSquared() {
        float residual_sum_of_squares = 0.0f;
        float total_sum_of_squares = 0.0f;

        float[] predictions = predict();
        float mean = sum(dependentFeature) / dependentFeature.length;

        residual_sum_of_squares = sum(pow(subtract(predictions, dependentFeature), 2));
        total_sum_of_squares =    sum(pow(subtract(predictions, mean),             2));

        return 1 - residual_sum_of_squares / total_sum_of_squares;
    }

    private void updateWeights() {
        int[] randomIndices = new int[batchSize];
        float[] predictions = new float[batchSize];

        ArrayList<Integer> indicesSampleSpace = new ArrayList<Integer>(independentFeatures.length);
        for(int i = 0; i < independentFeatures.length; i++) {
            indicesSampleSpace.add(i);
        }
        Random r = new Random();

        for (int i = 0; i < batchSize; i++) {
            randomIndices[i] = indicesSampleSpace.get(r.nextInt(indicesSampleSpace.size()));
            indicesSampleSpace.remove((Integer)randomIndices[i]);
            predictions[i] = predict(independentFeatures[randomIndices[i]]);
        }

        float[] d_dependentFeatures = new float[weights.length];
        float d_bias = 0.0f;

        for (int i = 0; i < batchSize; i++) {