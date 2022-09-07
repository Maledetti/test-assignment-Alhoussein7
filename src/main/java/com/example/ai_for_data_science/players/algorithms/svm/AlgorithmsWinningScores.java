
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
            e.printStackTrace();
        }
    }

    private void addAlgorithmScores(String algoName, double[] algoScores, JSONArray algorithms) {
        for (String name : ALGO_NAMES) {
            JSONObject algorithm = new JSONObject();
            JSONObject algoItem = new JSONObject();
            double[] algScores = name.equals(algoName) ? algoScores : getAlgScores(name);
            algorithm.put("winningScore", algScores[1]);
            algorithm.put("winningSpeedScore", algScores[0]);
//            if (name.equals(algoName)) {
//                algorithm.put("winningScore", algoScores[1]);
//                algorithm.put("winningSpeedScore", algoScores[0]);
//            } else {
//                double[] oldScores = getAlgScores(name);
//                algorithm.put("winningScore", oldScores[1]);
//                algorithm.put("winningSpeedScore", oldScores[0]);
//            }
            algoItem.put(name, algorithm);
            algorithms.add(algoItem);
        }
    }

    /**
     * Returns one algorithm's scores
     * @param algoName the algorithm name
     * @return a list containing the winningScore and the winningSpeedRate
     */
    public double[] getAlgScores(String algoName) {
        int index = getIndex(algoName);
        JSONParser jsonParser = new JSONParser();
        return readAlgScores(algoName, index, jsonParser);
    }

    private double[] readAlgScores(String algoName, int index, JSONParser jsonParser) {
        try (FileReader reader = new FileReader(".json/svmScores.json")) {
            Object obj = jsonParser.parse(reader);
            JSONArray algorithms = (JSONArray) obj;
            return parseAlgorithm((JSONObject) algorithms.get(index), algoName);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new double[]{0};
    }

    public void setScoresToZero() {
        JSONArray algorithms = new JSONArray();

        for (String name : ALGO_NAMES) {
            JSONObject algorithm = new JSONObject();
            JSONObject algoItem = new JSONObject();
            double initScore = 0;
            algorithm.put("winningScore", initScore);
            algorithm.put("winningSpeedScore", initScore);
            algoItem.put(name, algorithm);
            algorithms.add(algoItem);
        }

        jsonWriteSvmScores(algorithms);
    }

    private int getIndex(String algoName) {
        return algoName.equals("BayesianClassifier") ? 0 : algoName.equals("DecisionTree") ? 1
                : algoName.equals("Human") ? 2 : algoName.equals("LinearRegression") ? 3
                : algoName.equals("Minimax") ? 4 : algoName.equals("RandomMinimax") ? 5
                : algoName.equals("RandomMove") ? 6 : 7;
    }

    private static double[] parseAlgorithm(JSONObject algo, String algoName) {

        JSONObject algoScores = (JSONObject) algo.get(algoName);
        double winningRate = (double) algoScores.get("winningScore");
        double winningSpeedRate = (double) algoScores.get("winningSpeedScore");

        return new double[]{winningRate, winningSpeedRate};
    }

}