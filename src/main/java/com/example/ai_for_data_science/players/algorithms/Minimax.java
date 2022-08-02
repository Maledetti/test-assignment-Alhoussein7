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
//        14 15 16 17 18 19 20
//        21 22 23 24 25 26 27
//        28 29 30 31 32 33 34
//        35 36 37 38 39 40 41

    public Minimax(boolean isPlayerOne, int depth) {
        this.isPlayerOne = isPlayerOne;
        nodesExamined = 0;
        branchesPruned = 0;
        timeElapsed = 0;
        movesMade = 0;

        this.depth = depth;
    }


    @Override
    public int returnMove(int[] gameBoard) {

        int bestEval = Integer.MIN_VALUE; // - 2^31 - 1.

        startTime = System.currentTimeMillis();

        int bestMove = -1;

        for (int col : Connect4.getAvailableMoves(gameBoard))
        {
            int moveEval = minimax(Connect4.nextGameBoard(gameBoard, col, isPlayerOne), depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (moveEval > bestEval) {
                bestMove = col;
                bestEval = moveEval;
            }
        }

        timeElapsed += System.currentTimeMillis() - startTime;
        ++movesMade;

        return bestMove;
    }

    @Override
    public void printResults() {
        if (isPlayerOne) {
            System.out.println("\nMinimax results:");
            System.out.println("  Depth: " + depth);
            System.out.println("  Time elapsed: " + timeElapsed + " (ms)");
            System.out.println("  Moves made: " + movesMade);
            System.out.println("  Avg. time per move: " + Math.round((float) timeElapsed / movesMade) + " (ms)");
            System.out.println("  Nodes Examined: " + nodesExamined);
            System.out.println("  Branches Pruned: " + branchesPruned);
        }
    }

    private int minimax(int[] gameBoard, int depth, int alpha, int beta, boolean maximizingPlayer){

        ++nodesExamined;
        int evalGameFinished;

        if (depth == 0)     // if the maximum depth was hit => evaluate the position to find the next best move
            return evalGame(gameBoard);

        if ((evalGameFinished = Connect4.gameIsFinished(gameBoard)) > -1) {     // if the game finished => evaluate the position to find the next best move

            if (collectData) {
                try {
                    DataSet.collectData(gameBoard, evalGameFinished);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return evalGameFinished(gameBoard, evalGameFinished, depth);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (int col : Connect4.getAvailableMoves(gameBoard)) {
                int moveEval = minimax(Connect4.nextGameBoard(gameBoard, col, isPlayerOne), depth-1, alpha, beta, false);
                maxEval = Math.max(maxEval, moveEval);

                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha)
                {
                    ++branchesPruned;
                    break;
                }
            }

            return maxEval;
        }
        else
        {
            int minEval = Integer.MAX_VALUE;

            for (int col: Connect4.getAvailableMoves(gameBoard)) {
                int moveEval = minimax(Connect4.nextGameBoard(gameBoard, col, !isPlayerOne), depth-1, alpha, beta, true);
                minEval = Math.min(minEval, moveEval);

                beta = Math.min(beta, minEval);
                if (beta <= alpha)
                {
                    ++branchesPruned;
                    break;
                }
            }

            return minEval;
        }
    }

    /**
     * Evaluates how good a won/tied position is based on who's turn it is and who won
     * The depth ensures that winning faster and losing slower is considered better
     */
    private int evalGameFinished(int[] gameBoard, int evalGameFinished, int depth) {   // (me)p1 loses in the next move
        if ((evalGameFinished == 2 && isPlayerOne) || (evalGameFinished == 1 && !isPlayerOne)) {
            return -100000 - depth; // minimax loses -> not a good situation -> return a huge negative value
        }
        else if ((evalGameFinished == 1) || (evalGameFinished == 2)){
            return 100000 + depth; // minimax wins -> 