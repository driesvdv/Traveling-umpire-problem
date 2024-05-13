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
        });
        Thread lowerBoundsThread = new Thread(() -> {
            LowerboundsV2 lb = new LowerboundsV2(assignmentMatrix);
            lb.calculateInitialViaHungarian();
            lb.calculateLowerbounds();
        });

        lowerBoundsThread.start();
        branchAndBoundThread.start();

//        LowerboundsV2 lbV2 = new LowerboundsV2(assignmentMatrix);
//        lbV2.calculateInitialViaHungarian();
//        lbV2.calculateLowerbounds();

//        LowerBounds lb = new LowerBounds(assignmentMatrix);
//        lb.CalculateInitialLowerBounds();
//        lb.calculateLowerbounds();


        //BranchAndBound branchAndBound = new BranchAndBound(assignmentMatrix);
        //branchAndBound.executeBranchAndBound();

        //AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();

        //long endTime = System.currentTimeMillis();

        // Print optimal weight value
        //System.out.println("\nSolution:");
        //System.out.println("Optimal weight: " + bestSolution.getBestWeight());

//        for (int i = 0; i < assignmentMatrix.getnUmpires(); i++){
//            int[] homeTeams = new int[instance.getnTeams()];
//            for (int j = 0; j < assignmentMatrix.getnRounds(); j++){
//                homeTeams[assignmentMatrix.getBestSolution()[j][i].getHomeTeam()-1]++;
//            }
//            int counter = 0;
//            for (int team : homeTeams){
//                if (team  > 0){
//                    counter++;
//                }
//            }
//        }

//        System.out.println("Execution time: " + (endTime - startTime) + "ms");
//        System.out.println("Finished");
//
//        System.out.println("\nFull solution:");
//        SolutionConverter c = new SolutionConverter(assignmentMatrix.getBestSolution(), assignmentMatrix.getTranslationMatrix());
//        c.printSolution(c.convertSolutionMatrixMultipleLines());
    }
}