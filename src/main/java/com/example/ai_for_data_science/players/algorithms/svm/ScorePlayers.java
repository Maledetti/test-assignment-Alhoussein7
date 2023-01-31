
package com.example.ai_for_data_science.players.algorithms.svm;

import java.io.*;
import java.util.*;

import com.example.ai_for_data_science.Connect4;
import com.example.ai_for_data_science.players.algorithms.svm.AlgorithmsWinningScores;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Two players considered: Bayesian Classifier and Human
 */
public class ScorePlayers {

    private static final int MAXIMUM_NUMBER_OF_RECORDS = 10;
    private static AlgorithmsWinningScores algScores;

    public ScorePlayers() {
        //this.algScores = new AlgorithmsWinningScores();
    }
    // after each game ( BayesianClassifier vs Human) score the players:
    // winningScore = (existingWS + (existingWS + 1))/2 ; range[0, 10]
    // winningSpedScore = nbrOfPlayerMovesToWin(or loose)/21  ; range[0, 1]
    //create a new record for each of the two players, with: [[winningScore, winningSpedScore],[1 for win || -1 for loose]]

    // append the record to svmTrainingData.csv - remove the older than 100 records
    // {{(winningScoreBC + winningSpedScoreBC)/2, (winningScoreH + winningSpedScoreH)/2},{1/-1}}

//    public static void main(String[] args) throws IOException {
//        //algScores = new AlgorithmsWinningScores();
//        addNewRecord(new double[]{6.345, 6.435}, -1);
//
////        double[][][] trainingData = getTrainingData();
////        for (double[][] row : trainingData) {
////            System.out.println(Arrays.deepToString(row));
////        }
////
////        updatePlayersScore(new double[][]{{4.55, 0.44}, {3.45, 0.33}});
//
//    }

    public static double[][][] getTrainingData() {
        reduceDataSet();
        return getTrainingDataSet();
    }

    private static double[][][] getTrainingDataSet() {
        try (CSVReader reader = new CSVReader(new FileReader(".csv/svmTrainingData.csv"))) {
            ArrayList<double[][]> trainingData = new ArrayList<>();
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                String feature1 = lineInArray[0].substring(1);
                String feature2 = lineInArray[1].substring(0, lineInArray[1].length() - 1);
                String label = lineInArray[2];
                double[][] row = new double[][]{{Double.parseDouble(feature1), Double.parseDouble(feature2)}, {Double.parseDouble(label)}};
                trainingData.add(row);
            }
            double[][][] data = new double[trainingData.size()][][];
            for (int i = 0; i < trainingData.size(); i++) {
                data[i] = trainingData.get(i);
            }
            return data;
        } catch (CsvException | IOException e) {
            e.printStackTrace();
        }
        return new double[1][][];
    }

    private static void reduceDataSet() {
        try (CSVReader reader = new CSVReader(new FileReader(".csv/svmTrainingData.csv"))) {
            List<String[]> data = reader.readAll();
            if (data.size() > MAXIMUM_NUMBER_OF_RECORDS) {
                CSVWriter writer = new CSVWriter(new FileWriter(".csv/svmTrainingData.csv"));
                writer.writeAll(data.subList(data.size() - MAXIMUM_NUMBER_OF_RECORDS, data.size()));
                writer.close();
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }


    public static void addNewRecord(double[] scores, int label) throws IOException {
        String features = Arrays.toString(scores);
        Scanner scanner = new Scanner(new File(".csv/svmTrainingData.csv"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(features + ",")) {
                return;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(".csv/svmTrainingData.csv", true));


        writer.write(String.format("%s,%s\n", features, label));
        writer.close();

    }

    public static void updatePlayersScore(double[][] newScores) {
        String[] algNames = new String[]{"BayesianClassifier", "Human"};
        for (int i = 0; i < newScores.length; i++) {
            algScores.fillAlgoScores(algNames[i], newScores[i]);
        }
    }
}