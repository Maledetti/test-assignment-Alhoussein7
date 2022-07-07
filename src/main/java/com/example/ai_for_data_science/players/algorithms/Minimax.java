package com.example.ai_for_data_science.players.algorithms;

import com.example.ai_for_data_science.Algorithm;
import com.example.ai_for_data_science.Connect4;
import com.example.ai_for_data_science.DataSet;

public class Minimax implements Algorithm {

    double winningRate = 0;
    double speedWinningRate = 0;

    int depth; // -1 means infinite depth (this implementation is too slow for that ): )
    int nodesExamined = 0;
    int branchesPruned = 0;

    boolean isPlayerOne;

    long startTime;
    long timeElapsed;
    int movesMade;

    boolean collectData = false;

//        0  1  2  3  4  5  6
//        7  8  9  10 11 12 13
//        14 15 16 17 1