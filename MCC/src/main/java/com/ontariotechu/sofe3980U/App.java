package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        double[][] confusionMatrix;
        double crossEntropy;

        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            csvReader.close();

            // Number of classes (assuming 5 from CSV file structure)
            int numClasses = 5;
            confusionMatrix = new double[numClasses][numClasses];
            crossEntropy = 0.0;
            int totalSamples = allData.size();

            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]) - 1; // Convert class index (1-based) to (0-based)
                double[] y_predicted = new double[numClasses];

                for (int i = 0; i < numClasses; i++) {
                    y_predicted[i] = Double.parseDouble(row[i + 1]);
                }

                // Compute Cross-Entropy
                crossEntropy += -Math.log(y_predicted[y_true]);

                // Predicted class is the one with the highest probability
                int y_pred_class = argMax(y_predicted);

                // Update Confusion Matrix
                confusionMatrix[y_true][y_pred_class]++;
            }

            // Compute final Cross-Entropy
            crossEntropy /= totalSamples;

            // Print Results
            System.out.printf("Cross-Entropy: %.6f\n", crossEntropy);
            System.out.println("\nConfusion Matrix:");
            printConfusionMatrix(confusionMatrix);

        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
        }
    }

    // Helper function to get index of max probability
    public static int argMax(double[] arr) {
        int maxIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // Helper function to print the Confusion Matrix
    public static void printConfusionMatrix(double[][] matrix) {
        System.out.print("        Predicted → ");
        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("  %d ", i + 1);
        }
        System.out.println("\nActual ↓");
        
        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("Class %d | ", i + 1);
            for (int j = 0; j < matrix.length; j++) {
                System.out.printf("%4.0f ", matrix[i][j]);
            }
            System.out.println();
        }
    }
}
