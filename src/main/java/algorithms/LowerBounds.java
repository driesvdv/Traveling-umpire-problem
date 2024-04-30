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
            HungarianAlgorithm h = new HungarianAlgorithm(CreateInitialDistanceMatrix(i));
            int[] result = h.getResult();
            int total = h.getTotal();
            totalLowerBound += total;
            solutionMatrix[i][i+1] = result;
            lowerboundsMatrix[i][i+1] = total;
        }
        //System.out.println();
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
                    distanceMatrix[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        return distanceMatrix;
    }
}
