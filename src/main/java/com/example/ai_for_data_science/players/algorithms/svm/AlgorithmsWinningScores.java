
package com.example.ai_for_data_science.players.algorithms.svm;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AlgorithmsWinningScores {

    private static final String[] ALGO_NAMES = new String[]{"BayesianClassifier", "DecisionTree", "Human", "LinearRegression", "Minimax", "RandomMinimax", "RandomMove", "SVM"};

//   public static void main(String[] args) {
//       setScoresToZero();
//       fillAlgoScores("BayesianClassifier", new double[]{0.2, 0.2});
//       System.out.println(Arrays.toString(getAlgScores("BayesianClassifier")));
//       System.out.println(Arrays.deepToString(getPlayersScoreForNextGamePrediction("BayesianClassifier", "Human")));
//   }

   public double[][] getPlayersScoreForNextGamePrediction(String p1Name, String p2Name){
       double[] p1Score = getAlgScores(p1Name);
       double[] p2Score = getAlgScores(p2Name);
       return new double[][]{{ (p1Score[0] + p1Score[1])/2, (p2Score[0] + p2Score[1])/2}};
   }

    /**
     * Updates one Algorithm scores
     * @param algoName the string algorithm name
     * @param algoScores a list with the two scores
     */
    public void fillAlgoScores(String algoName, double[] algoScores) {
        JSONArray algorithms = new JSONArray();
        addAlgorithmScores(algoName, algoScores, algorithms);
        jsonWriteSvmScores(algorithms);
    }

    private void jsonWriteSvmScores(JSONArray algorithms) {
        try (FileWriter file = new FileWriter(".json/svmScores.json")) {
            file.write(algorithms.toJSONString());
            file.flush();
        } catch (IOException e) {