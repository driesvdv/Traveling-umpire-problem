package org.example;

import algorithms.BranchAndBound;
import algorithms.LowerBounds;
import data.Instance;
import objects.AssignmentMatrix;
import objects.SolutionConverter;

public class Main {
    public static void main(String[] args) {
        //long startTime = System.currentTimeMillis();
        if (args.length < 3) {
            System.out.println("Usage: java -jar yourjarfile.jar <filename> <q1> <q2>");
            System.exit(1);
        }

        String filename = args[0];
        int q1 = Integer.parseInt(args[1]);
        int q2 = Integer.parseInt(args[2]);

        Instance instance = new Instance(filename);
        AssignmentMatrix assignmentMatrix = new AssignmentMatrix(instance, q1, q2);

        Thread branchAndBoundThread = new Thread(() -> {
            long startTimeBranchAndBound = System.currentTimeMillis();
            AssignmentMatrix bestSolution = new BranchAndBound(assignmentMatrix).executeBranchAndBound();
            assignmentMatrix.setIsComplete(true);
            long EndTimeBranchAndBound = System.currentTimeMillis();
            System.out.println("\nSolution:");
            if (bestSolution != null) {
                System.out.println("Optimal weight: " + bestSolution.getBestWeight());
            } else {
                System.out.println("Infeasible");
            }
            //System.out.println("Optimal weight: " + bestSolution.getBestWeight());

            System.out.println("Finished");
            System.out.println("Execution time: " + (EndTimeBranchAndBound - startTimeBranchAndBound) + "ms");
            System.out.println("\nFull solution:");
            SolutionConverter c = new SolutionConverter(assignmentMatrix.getBestSolution(), assignmentMatrix.getTranslationMatrix());
            c.printSolution(c.convertSolutionMatrixMultipleLines());

            // Stop all threads
            System.exit(0);
        });
        Thread lowerBoundsThread = new Thread(() -> {
            LowerBounds lb = new LowerBounds(assignmentMatrix);
            lb.calculateInitialViaHungarian();
            //lb.calculateInitialWithoutHungarian();
            lb.calculateLowerbounds();
        });

        lowerBoundsThread.start();
        branchAndBoundThread.start();
    }
}