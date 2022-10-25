package com.example.ai_for_data_science.players.algorithms.svm;


import org.apache.commons.math3.linear.MatrixUtils;

import java.util.Arrays;

public class SVM {

    private double[][] currentScores;
    private double[][][] testingData;
    private ScorePlayers scorePlayers;
    private AlgorithmsWinningScores algScores;

    private SupportVectorMachine svm = null;

    public SVM() {
        this.testingData = new double[5][][];
        this.scorePlayers = new ScorePlayers();
        this.algScores = new AlgorithmsWinningScores();
        this.currentScores = algScores.getPlayersScoreForNextGamePrediction("BayesianClassifier", "Human");
        train();
        printModelAccuracy();
        printPrediction();

    }

    private int predictNewGame() {
        return this.svm.predict(MatrixUtils.createRealMatrix(currentScores));
    }
    private int testPrediction(double[][] feature) {
        return this.svm.predict(MatrixUtils.createRealMatrix(feature));
    }

    private void printPrediction() {
        System.out.println("\u001B[34m" + "\nNEXT GAME SVM WINNING PREDICTION :\n" +  "\u001B[0m" + "\u001B[32m" + (predictNewGame() == 1 ? "BAYESIAN CLASSIFIER" : "HUMAN") +  "\n \u001B[0m");
    }

    private void printModelAccuracy(){
        System.out.println("SVM ACCURACY:" + (testingSVM() * 100) + "% ");
    }

    public void train() {
        long startTime = System.nanoTime();
        double[][][] trainingSet = getTrainingDataSet();
        double[][] features = new double[trainingSet.length][2];
        double[][] labels = new double[trainingSet.length][1];

        for (int i = 0; i < trainingSet.length; i++) {
            setFeaturesAndLabels(trainingSet, features, labels, i);
            if (i < 5){
                setTestingSet(trainingSet, i);
            }
        }
        //System.out.println("testing data: " + Arrays.deepToString(testingData));
        //System.out.println("testing data accuracy: " + testingSVM());

        System.out.println("\u001B[34m" + "SVM Training Data Set:" + "\u001B[0m");
        //System.out.println(Arrays.deepToString(trainingSet));
        System.out.println("FEATURES :  " + Arrays.deepToString(features));
        System.out.println("LABELS   :  " + Arrays.deepToString(labels));

        this.svm = new SupportVectorMachine(MatrixUtils.createRealMatrix(features), MatrixUtils.createRealMatrix(labels));
        long endTime = System.nanoTime();
        long trainingTime = endTime - startTime;
        System