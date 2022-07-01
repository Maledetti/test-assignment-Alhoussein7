package com.example.ai_for_data_science.players.algorithms;

import com.example.ai_for_data_science.Algorithm;
import com.example.ai_for_data_science.Connect4;

import java.util.Scanner;

public class Human implements Algorithm {

    double winningRate = 0;
    double speedWinningRate = 0;

    @Override
    public int returnMove(int[] gameBoard) {

        Scanner sc = new Scanner(System.in);
        int moveCol;

        while (true) {

            Connect4.printGameBoard(gameBoard);
            System.out.println("\nSelect column to play  ([0, 6])");
            System.out.print(">> ");

            try {
                moveCol = sc.nextInt();
            }
            catch (Exception e) {
                System.out.println("\u001B[31m" + "INVALID MOVE!" + "\u001B[0m");
                sc.nextLine();
                continue;
            }

            if (Connect4.validateMove(gameBoard, moveCol)) {
                break;
            }
            else {
                System.out.println("\u001B[31m" + "INVALID MOVE!" + "\u001B[0m");
            }
        }

        return moveCol;
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
        return speedWinningRa