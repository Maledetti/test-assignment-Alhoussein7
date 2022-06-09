
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