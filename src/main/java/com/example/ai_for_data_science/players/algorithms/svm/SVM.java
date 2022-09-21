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
        this.currentScores = algScores.getPlayersScoreForNextGamePrediction("BayesianClas