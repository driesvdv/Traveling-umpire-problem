package org.example;

import algorithms.BranchAndBound;
import algorithms.LowerBounds;
import data.Instance;
import objects.AssignmentMatrix;
import objects.SolutionConverter;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Instance instance = new Instance();
        AssignmentMatrix assignmentMatrix = new AssignmentMatrix(instance);
//        Preprocessing p = new Preprocessing(instance.getOpponents(), 1,3, assignmentMatrix.getTranslationMatrix());
//        p.preProcessQ1andQ2();
        //assignmentMatrix.setTranslationMatrix(p.getMatchPairs());
        LowerBounds lb = new LowerBounds(assignmentMatrix);
        lb.CalculateInitialLowerBounds();
        //BranchAndBound branchAndBound = new BranchAndBound(assignmentMatrix);
        //branchAndBound.executeBranchAndBound();

        //System.out.println("debug");
//        for (int i = 0; i < assignmentMatrix.getnUmpires(); i++){
//            //System.out.println("Umpire " + i);
//            int[] homeTeams = new int[instance.getnTeams()];
//            for (int j = 0; j < assignmentMatrix.getnRounds(); j++){
//                homeTeams[assignmentMatrix.getBestSolution()[j][i].getHomeTeam()-1]++;
//                System.out.print(assignmentMatrix.getBestSolution()[j][i] + " ");
//            }
//            int counter = 0;
//            for (int team : homeTeams){
//                if (team  > 0){
//                    counter++;
//                }
//            }
//            if (counter != instance.getnTeams()){
//                System.out.println("Not all teams have been assigned to umpire " + i);
//            }
//            System.out.println();
//        }
//
//        long endTime = System.currentTimeMillis();
//
//
//
//        SolutionConverter c = new SolutionConverter(assignmentMatrix.getBestSolution(), assignmentMatrix.getTranslationMatrix());
//        c.printSolution(c.convertSolutionMatrixMultipleLines());
//        System.out.println("Total distance travelled: " + assignmentMatrix.getUpperBound());
//
//        System.out.println("Execution time: " + (endTime - startTime) + "ms");
//        System.out.println("Finished");
    }
}