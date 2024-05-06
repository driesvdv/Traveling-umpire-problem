package org.example;

import algorithms.BranchAndBound;
import data.Instance;
import objects.AssignmentMatrix;
import objects.SolutionConverter;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Instance instance = new Instance();
        AssignmentMatrix assignmentMatrix = new AssignmentMatrix(instance);
        BranchAndBound branchAndBound = new BranchAndBound(assignmentMatrix);
        AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();

        long endTime = System.currentTimeMillis();
        
        // Print optimal weight value
        System.out.println("\nSolution:");
        System.out.println("Optimal weight: " + bestSolution.getBestWeight());

        for (int i = 0; i < assignmentMatrix.getnUmpires(); i++){
            int[] homeTeams = new int[instance.getnTeams()];
            for (int j = 0; j < assignmentMatrix.getnRounds(); j++){
                homeTeams[assignmentMatrix.getBestSolution()[j][i].getHomeTeam()-1]++;
            }
            int counter = 0;
            for (int team : homeTeams){
                if (team  > 0){
                    counter++;
                }
            }
        }

        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        System.out.println("Finished");

        System.out.println("\nFull solution:");
        SolutionConverter c = new SolutionConverter(assignmentMatrix.getBestSolution(), assignmentMatrix.getTranslationMatrix());
        c.printSolution(c.convertSolutionMatrixMultipleLines());
    }
}