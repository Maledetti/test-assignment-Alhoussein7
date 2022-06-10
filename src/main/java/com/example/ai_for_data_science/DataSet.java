
package com.example.ai_for_data_science;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class DataSet {

    public int[][] gameBoards;
    public int[] results;

    public ArrayList<int[]> winning_gameBoards;
    public ArrayList<int[]> losing_gameBoards;

    public int[][] trainData_gameBoards;
    public int[] trainData_results;

    public int[][] testData_gameBoards;
    public int[] testData_results;


    /**
     * Converts the game data stored in file to manageable data.
     */
    public void preProcessing() throws FileNotFoundException {
        ArrayList<int[]> gameBoards = new ArrayList<>();
        ArrayList<Integer> results = new ArrayList<>();

        Scanner scanner = new Scanner(new File(".csv/gameData.csv"));
        while (scanner.hasNextLine()) {

            int[] gameBoard = new int[42];
            int result;

            String[] data = scanner.nextLine().split(",");

            for (int i = 0; i < gameBoard.length; i++) {
                gameBoard[i] = Character.getNumericValue((data[0].charAt(i)));
            }
            result = Integer.parseInt(data[1]);

            gameBoards.add(gameBoard);
            results.add(result);
        }

        this.gameBoards = new int[gameBoards.size()][];
        this.results = new int[results.size()];
        for (int i = 0; i < gameBoards.size(); i++) {
            this.gameBoards[i] = gameBoards.get(i);
            this.results[i] = results.get(i);
        }
    }

    /**
     * Shuffles and splits the data into a train and test set.
     */
    public void generateTrainTestSets() {
        float trainDataRatio = 0.8f;
        int trainDataLength = (int)(gameBoards.length * trainDataRatio);
        int testDataLength = gameBoards.length - trainDataLength;

        trainData_gameBoards = new int[trainDataLength][];
        trainData_results = new int[trainDataLength];

        testData_gameBoards = new int[testDataLength][];
        testData_results = new int[testDataLength];

        ArrayList<Integer> indexList = new ArrayList<>(gameBoards.length);

        for (int i = 0; i < gameBoards.length; i++) {
            indexList.add(i);
        }
        Collections.shuffle(indexList);

        for (int i = 0; i < trainDataLength; i++) {
            trainData_gameBoards[i] = gameBoards[(indexList.get(i))];
            trainData_results[i] = results[(indexList.get(i))];
        }
        for (int i = trainDataLength; i < testDataLength + trainDataLength; i++) {
            testData_gameBoards[i-trainDataLength] = gameBoards[(indexList.get(i))];
            testData_results[i-trainDataLength] = results[(indexList.get(i))];
        }
    }

    public void generateWinLoseTieSets() {
        winning_gameBoards = new ArrayList<>();
        losing_gameBoards = new ArrayList<>();
        for (int i = 0; i < gameBoards.length; i++) {
            if (results[i] == 1) {
                winning_gameBoards.add(gameBoards[i]);
            }
            else if (results[i] == -1) {
                losing_gameBoards.add(gameBoards[i]);
            }
        }
    }

    /**
     * Used by minimax to collect and store data from played games.
     * @param gameBoard The representation of the game board
     * @param evalGameFinished The minimax evaluation of which player who's guaranteed to win
     */
    public static void collectData(int[] gameBoard, int evalGameFinished) throws IOException {
        String gameBoardRepresentation = Connect4.gameBoardToString(gameBoard);

        Scanner scanner = new Scanner(new File(".csv/gameData.csv"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(gameBoardRepresentation + ",")) {
                return;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(".csv/gameData.csv", true));

        if (evalGameFinished == 2) { evalGameFinished = -1; }

        writer.write(String.format("%s,%s\n", Connect4.gameBoardToString(gameBoard), evalGameFinished));
        writer.close();
    }

}