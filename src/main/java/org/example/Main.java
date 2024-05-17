package org.example;

import algorithms.BranchAndBound;
import algorithms.LowerBounds;
import algorithms.LowerboundsV2;
import data.Instance;
import objects.AssignmentMatrix;
import objects.SolutionConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        //long startTime = System.currentTimeMillis();

        Instance instance = new Instance();
        AssignmentMatrix assignmentMatrix = new AssignmentMatrix(instance);

        Thread branchAndBoundThread = new Thread(() -> {
            long startTimeBranchAndBound = System.currentTimeMillis();
            AssignmentMatrix bestSolution = new BranchAndBound(assignmentMatrix).executeBranchAndBound();
            assignmentMatrix.setIsComplete(true);
            long EndTimeBranchAndBound = System.currentTimeMillis();
            System.out.println("\nSolution:");
            System.out.println("Optimal weight: " + bestSolution.getBestWeight());

            System.out.println("Finished");
            System.out.println("Execution time: " + (EndTimeBranchAndBound - startTimeBranchAndBound) + "ms");
            System.out.println("\nFull solution:");
            SolutionConverter c = new SolutionConverter(assignmentMatrix.getBestSolution(), assignmentMatrix.getTranslationMatrix());
            c.printSolution(c.convertSolutionMatrixMultipleLines());

            // Stop all threads
            System.exit(0);
        });
        Thread lowerBoundsThread = new Thread(() -> {
            LowerboundsV2 lb = new LowerboundsV2(assignmentMatrix);
            lb.calculateInitialViaHungarian();
            lb.calculateLowerbounds();
        });

        lowerBoundsThread.start();
        branchAndBoundThread.start();
    }
}