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
        System.out.println("SVM ACCURACY:" + (testingSVM() * 100) +