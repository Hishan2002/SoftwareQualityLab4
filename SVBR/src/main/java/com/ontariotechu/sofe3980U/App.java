package com.ontariotechu.sofe3980U;

import com.opencsv.*;
import java.io.FileReader;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        double maxAUC = 0.0;
        String bestModel = "";

        for (String filePath : filePaths) {
            System.out.println("Evaluating " + filePath);
            double[] metrics = evaluateModel(filePath);

            if (metrics != null) {
                double bce = metrics[0], accuracy = metrics[1], precision = metrics[2], recall = metrics[3], f1 = metrics[4], auc = metrics[5];
                System.out.printf("BCE: %.4f, Accuracy: %.4f, Precision: %.4f, Recall: %.4f, F1-score: %.4f, AUC-ROC: %.4f\n\n",
                                  bce, accuracy, precision, recall, f1, auc);

                if (auc > maxAUC) {
                    maxAUC = auc;
                    bestModel = filePath;
                }
            }
        }

        System.out.println("Best model: " + bestModel + " (Highest AUC-ROC)");
    }

    public static double[] evaluateModel(String filePath) {
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            csvReader.close();

            int TP = 0, FP = 0, TN = 0, FN = 0;
            double bce = 0.0;
            int n = allData.size();
            double[] y_true = new double[n];
            double[] y_pred = new double[n];

            for (int i = 0; i < n; i++) {
                double yT = Double.parseDouble(allData.get(i)[0]);
                double yP = Double.parseDouble(allData.get(i)[1]);

                y_true[i] = yT;
                y_pred[i] = yP;

                // BCE Calculation
                if (yT == 1) {
                    bce += -Math.log(yP);
                } else {
                    bce += -Math.log(1 - yP);
                }

                // Apply threshold of 0.5
                int y_bin = (yP >= 0.5) ? 1 : 0;

                if (y_bin == 1 && yT == 1) TP++;  // True Positive
                if (y_bin == 1 && yT == 0) FP++;  // False Positive
                if (y_bin == 0 && yT == 0) TN++;  // True Negative
                if (y_bin == 0 && yT == 1) FN++;  // False Negative
            }

            bce /= n; // Average BCE

            // Compute metrics
            double accuracy = (double) (TP + TN) / (TP + TN + FP + FN);
            double precision = (TP + FP) > 0 ? (double) TP / (TP + FP) : 0;
            double recall = (TP + FN) > 0 ? (double) TP / (TP + FN) : 0;
            double f1 = (precision + recall) > 0 ? 2 * (precision * recall) / (precision + recall) : 0;
            double auc = calculateAUC(y_true, y_pred, n);

            return new double[]{bce, accuracy, precision, recall, f1, auc};

        } catch (Exception e) {
            System.out.println("Error reading file: " + filePath);
            return null;
        }
    }

    public static double calculateAUC(double[] y_true, double[] y_pred, int n) {
        double[] thresholds = new double[101];
        double[] tpr = new double[101];
        double[] fpr = new double[101];

        int posCount = 0, negCount = 0;
        for (double y : y_true) {
            if (y == 1) posCount++;
            else negCount++;
        }

        for (int i = 0; i <= 100; i++) {
            double th = i / 100.0;
            int TP = 0, FP = 0;

            for (int j = 0; j < n; j++) {
                int y_bin = (y_pred[j] >= th) ? 1 : 0;
                if (y_bin == 1 && y_true[j] == 1) TP++;
                if (y_bin == 1 && y_true[j] == 0) FP++;
            }

            tpr[i] = (double) TP / posCount;
            fpr[i] = (double) FP / negCount;
        }

        double auc = 0.0;
        for (int i = 1; i <= 100; i++) {
            auc += (tpr[i - 1] + tpr[i]) * Math.abs(fpr[i - 1] - fpr[i]) / 2;
        }

        return auc;
    }
}
