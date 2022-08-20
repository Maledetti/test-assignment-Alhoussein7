
package com.example.ai_for_data_science.players.algorithms;


import com.example.ai_for_data_science.Algorithm;

public class RandomMinimax implements Algorithm {

    double winningRate = 0;
    double speedWinningRate = 0;

    RandomMove randomMove;
    Minimax minimax;

    int randomCount;
    public void setRandomCount(int randomCount) { this.randomCount = randomCount; }



    public RandomMinimax(boolean isPlayerOne, int randomCount, int depth) {
        randomMove = new RandomMove();
        minimax = new Minimax(isPlayerOne, depth);
        this.randomCount = randomCount;
    }

    @Override
    public int returnMove(int[] gameBoard) {
        if (randomCount-- > 0) {
            return randomMove.returnMove(gameBoard);
        }
        else {
            return minimax.returnMove(gameBoard);
        }
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

    public double getSpeedWinningRate() {
        return speedWinningRate;
    }

    public void setSpeedWinningRate(double speedWinningRate) {
        this.speedWinningRate = speedWinningRate;
    }
}