package com.example.ai_for_data_science;


import com.example.ai_for_data_science.players.algorithms.*;
import com.example.ai_for_data_science.players.algorithms.svm.SVM;

import java.io.FileNotFoundException;


public class Main {

    public static void main(String[] args) {
        run();
    }

    private static void run() {

        DataSet dataSet = new DataSet();
        try {
            dataSet.preProcessing();
        } catch (FileNotFoundException e) {
            e.printSt