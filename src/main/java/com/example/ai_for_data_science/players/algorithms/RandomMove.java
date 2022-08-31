package com.example.ai_for_data_science.players.algorithms;

import com.example.ai_for_data_science.Algorithm;
import com.example.ai_for_data_science.Connect4;

import java.util.ArrayList;
import java.util.Random;

public class RandomMove implements Algorithm {

    double winningRate = 0;
    double speedWinningRate = 0;

    Random random = new Random();

    @Override
    public int returnMove(int[] gameBoard) {
        ArrayList<Integer> availableMoves = Connect4.getAvailableMoves(gameBoard);
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }

    @Override
    public void printResults() {

    }

    public double getWinningRate() {
        return winningRate;
    }

    public void setWinningRate(double winningRate) {
        this.winningRate = winningRate;
    }

    public double getSpeedWinni