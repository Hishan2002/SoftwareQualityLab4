package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        double minError = Double.MAX_VALUE;
        String bestModel = "";

        for (String filePath : filePaths) {
            System.out.println("Evaluating " + filePath);
            double[] metrics = evaluateModel(filePath);

            if (metrics != null) {
                double mse = metrics[0], mae = metrics[1], mare = metrics[2];
                System.out.printf("MSE: %.4f, MAE: %.4f, MARE: %.4f%%\n\n", mse, mae, mare * 100);

                if (mse < minError) {
                    minError = mse;
                    bestModel = filePath;
                }
            }
        }

        System.out.println("Best model: " + bestModel + " (Lowest MSE)");
    }

    public static double[] evaluateModel(String filePath) {
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            csvReader.close();

            double mse = 0.0, mae = 0.0, mare = 0.0;
            int n = allData.size();

            for (String[] row : allData) {
                double y_true = Double.parseDouble(row[0]);
                double y_pred = Double.parseDouble(row[1]);

                double error = y_true - y_pred;
                mse += error * error;
                mae += Math.abs(error);
                mare += Math.abs(error / y_true);
            }

            mse /= n;
            mae /= n;
            mare /= n;

            return new double[]{mse, mae, mare};

        } catch (Exception e) {
            System.out.println("Error reading file: " + filePath);
            return null;
        }
    }
}
