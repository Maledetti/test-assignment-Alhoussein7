package com.example.ai_for_data_science;


import com.example.ai_for_data_science.players.algorithms.*;
import com.example.ai_for_data_science.players.algorithms.svm.SVM;

import java.io.FileNotFoundException;


public class Main {

    public static void main(String[] args) {
        run();
    }

    private static void run() {

        DataSet dataSet = new DataSet();
        try {
            dataSet.preProcessing();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dataSet.generateTrainTestSets();
        dataSet.generateWinLoseTieSets();

        //Algorithm player1 = new Human();
        Algorithm player1 = new BayesianClassifier(dataSet, true);
        //Algorithm player2 = new LinearRegression(0.001f, 10000, 42, false, dataSet);
        //Algorithm player2 = new BayesianClassifier(dataSet, false);
        //Algorithm player2 = new LinearRegression(0.001f, 10000, 42, false, dataSet);

        //Algorithm player1 = new Minimax(true, 15);
        //Algorithm player2 = new Minimax(f