package com.example.ai_for_data_science;


import com.example.ai_for_data_science.players.algorithms.BayesianClassifier;
import com.example.ai_for_data_science.players.algorithms.Human;
import com.example.ai_for_data_science.players.algorithms.svm.AlgorithmsWinningScores;
import com.example.ai_for_data_science.players.algorithms.svm.ScorePlayers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Connect4 {

    private int[] gameBoard = new int[42];      // Default value is 0
    private ArrayList<Integer> moveOrder = new