package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.List;

public class LowerBounds {
    private AssignmentMatrix assignmentMatrix;
    private int[][][] solutionMatrix; //Matrix containing the values of solutions for the subproblems
    private int[][] lowerboundsMatrix; //Matrix containing the lower bounds for all pairs of rounds

    public LowerBounds(AssignmentMatrix assignmentMatrix) {
        this.assignmentMatrix = assignmentMatrix;
        this.solutionMatrix = new int[assignmentMatrix.getnRounds()][assignmentMatrix.getnRounds()][assignmentMatrix.getnUmpires()];
        this.lowerboundsMatrix = new int[assignmentMatrix.getnRounds()][assignmentMatrix.getnRounds()];
    }
    public void CalculateInitialLowerBounds(){
        int totalLowerBound = 0;
        for (int i = 0; i < assignmentMatrix.getnRounds()-1; i++){
            int[][] costMatrix = CreateInitialDistanceMatrix(i);
            int[][] costMatrixCopy = new int[costMatrix.length][];
            for (int j = 0; j < costMatrix.length; j++) {
                costMatrixCopy[j] = costMatrix[j].clone();
            }
            HungarianAlgorithm h = new HungarianAlgorithm(costMatrixCopy);

            h.reduceInitialMatrix();
            h.solveReducedMatrix();

            int[][] test = h.getAssignments();
            System.out.println();
            totalLowerBound += getOptimalAssignmentCost(test, costMatrix);
        }

        System.out.println();
    }

    public int getOptimalAssignmentCost(int[][] assignmentMatrix, int[][] costMatrix){
        int distance = 0;
        for (int i = 0; i < assignmentMatrix.length; i++) {
            distance += costMatrix[i][assignmentMatrix[i][1]];
        }
        return distance;
    }
    public int[][] CreateInitialDistanceMatrix(int round){
        MatchPair[] firstRound = assignmentMatrix.getTranslationMatrix()[round];
        MatchPair[] secondRound = assignmentMatrix.getTranslationMatrix()[round+1];
        int[][] distanceMatrix = new int[firstRound.length][secondRound.length];

        for (int i = 0; i < firstRound.length; i++) {
            List<MatchPair> feasibleAllocations = firstRound[i].getFeasibleChildren();
            for (int j = 0; j < secondRound.length; j++) {
                if (feasibleAllocations.contains(secondRound[j])){
                    distanceMatrix[i][j] = assignmentMatrix.getDistance(firstRound[i].getHomeTeam()-1, secondRound[j].getHomeTeam()-1);
                }
                else{
                    distanceMatrix[i][j] = 999999;
                }
            }
        }
        return distanceMatrix;
    }
}
