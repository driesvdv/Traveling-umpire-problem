package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.List;

public class LowerBounds {
    private int[][] minDistanceMatrix;
    private AssignmentMatrix assignmentMatrix;
    private int[] lowestDistancePerAmountOfSteps;
    private int[] lowerboundPerRound;

    public LowerBounds(AssignmentMatrix assignmentMatrix){
        this.assignmentMatrix = assignmentMatrix;
        this.minDistanceMatrix = new int[assignmentMatrix.getnRounds()-1][assignmentMatrix.getnRounds()-1];
        this.lowestDistancePerAmountOfSteps = new int[assignmentMatrix.getnRounds()];
        this.lowerboundPerRound = new int[assignmentMatrix.getnRounds() - 1];
    }

    public void calculateInitialViaHungarian() {
        int minDistanceOneStep = 0;
        int nRounds = assignmentMatrix.getnRounds();

        for (int round = 0; round < nRounds - 1; round++) {
            int[][] costMatrix = createInitialDistanceMatrix(round);
            int[][] costMatrixCopy = new int[costMatrix.length][];

            for (int row = 0; row < costMatrix.length; row++) {
                costMatrixCopy[row] = costMatrix[row].clone();
            }

            HungarianAlgorithm hungarian = new HungarianAlgorithm(costMatrixCopy);
            hungarian.reduceInitialMatrix();
            hungarian.solveReducedMatrix();
            int[][] assignments = hungarian.getAssignments();
            int optimalAssignmentCost = getOptimalAssignmentCost(assignments, costMatrix);

            minDistanceOneStep += optimalAssignmentCost;
            minDistanceMatrix[0][round] = optimalAssignmentCost;
            lowestDistancePerAmountOfSteps[0] = minDistanceOneStep;

            for (int j = 0; j <= round; j++) {
                lowerboundPerRound[j] += optimalAssignmentCost;
                setNewLowerboundInAssignmentMatrix(j, lowerboundPerRound[j]);
            }
        }

        lowerboundPerRound[0] = minDistanceOneStep;
        System.out.println("Hungarian algorithm calculation completed.");
    }

    public int[][] createInitialDistanceMatrix(int round){
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

    public int getOptimalAssignmentCost(int[][] assignmentMatrix, int[][] costMatrix){
        int distance = 0;
        for (int i = 0; i < assignmentMatrix.length; i++) {
            distance += costMatrix[i][assignmentMatrix[i][1]];
        }
        return distance;
    }

    public void setNewLowerboundInAssignmentMatrix(int round, int lowerbound){
        assignmentMatrix.setLowerboundPerRound(round, lowerbound);
    }

    public void calculateLowerbounds() {
        int stepValue = 2; // Initialize step value

        // Outer loop iterates all round sizes
        for (int i = 1; i < assignmentMatrix.getnRounds() - 1; i++) {
            int minDistanceStep = 0; // Initialize minimum distance for the current step

            // Inner loop iterates through the rounds in steps of stepValue
            for (int j = assignmentMatrix.getnRounds() - 1; j >= stepValue; j -= stepValue) {
                // Create a new AssignmentMatrix for the current sub-problem
                AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, j, stepValue + 1);

                // Execute Branch and Bound algorithm to find the best solution
                BranchAndBound branchAndBound = new BranchAndBound(matrix);
                AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();

                // Store the best weight (cost) from the solution
                minDistanceMatrix[i][j - 1] = bestSolution.getBestWeight();

                // Update lower bounds if the new cost is greater than the current lower bound
                int difference = bestSolution.getBestWeight() + minDistanceStep - lowerboundPerRound[j - stepValue];
                if (difference > 0) {
                    for  (int k = j-stepValue;  k>=0; k--) {
                        lowerboundPerRound[k] += difference;
                        setNewLowerboundInAssignmentMatrix(k, lowerboundPerRound[k]);
                    }
                }

                // Accumulate the minimum distance for the current step
                minDistanceStep += bestSolution.getBestWeight();
            }

            // Increment step value for the next iteration
            stepValue++;

            // Store the minimum distance for the current step size
            lowestDistancePerAmountOfSteps[i] = minDistanceStep;
        }

        System.out.println();
    }
}
