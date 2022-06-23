package com.example.ai_for_data_science.players.algorithms;

import com.example.ai_for_data_science.Algorithm;
import com.example.ai_for_data_science.Connect4;

import java.util.Scanner;

public class Human implements Algorithm {

    double winningRate = 0;
    double speedWinningRate = 0;

    @Override
    public int returnMove(int[] gameBoard) {

        Scanner sc = n