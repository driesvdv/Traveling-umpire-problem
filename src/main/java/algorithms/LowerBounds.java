package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.List;

public class LowerBounds {
    private AssignmentMatrix assignmentMatrix;
    private int[][][] solutionMatrix; //Matrix containing the values of solutions for the subproblems
    private int[][] lowerboundsMatrix; //Matrix containing the lower bounds for all pairs of rounds
    private AssignmentMatrix[][] LBAssignmentMatrices; //Matrix containing the assignment matrices for the lower bounds for every iteration of rounds
    private int totalLowerbound;
    private int test[];
    public LowerBounds(AssignmentMatrix assignmentMatrix) {
        this.assignmentMatrix = assignmentMatrix;
        this.solutionMatrix = new int[assignmentMatrix.getnRounds()][assignmentMatrix.getnRounds()][assignmentMatrix.getnUmpires()];
        this.lowerboundsMatrix = new int[assignmentMatrix.getnRounds()-1][assignmentMatrix.getnRounds()-1];
        totalLowerbound = 0;
        test = new int[assignmentMatrix.getnRounds()];
        CalculateInitialLowerBounds();
    }
    public void CalculateInitialLowerBounds(){
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
            int optimalAssignmentCost = getOptimalAssignmentCost(test, costMatrix);
            this.totalLowerbound += optimalAssignmentCost;
            lowerboundsMatrix[0][i] = optimalAssignmentCost;
        }
        test[0] = totalLowerbound;
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
    public void test2(){
        for (int i = 0; i < assignmentMatrix.getnRounds()-1;i++){
            int startValue = 2+i;
            int lastValue = 0;
            for (int j = 0; j < assignmentMatrix.getnRounds()-startValue;j+=startValue){
                AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, j,startValue+1,totalLowerbound);
                BranchAndBound branchAndBound = new BranchAndBound(matrix);
                AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();
                lowerboundsMatrix[i+1][j] = bestSolution.getBestWeight();
                lastValue = j;
                if (i == 3){
                    System.out.println();
                }
            }
            if (lastValue != assignmentMatrix.getnRounds()-1){
                if (startValue < assignmentMatrix.getnRounds()-1){
                    if (lowerboundsMatrix[i][lastValue+startValue] != 0){
                        lowerboundsMatrix[i+1][lastValue+startValue] = lowerboundsMatrix[i][lastValue+startValue];
                    }
                    else{
                        int amountOfSkiptRounds = assignmentMatrix.getnRounds()-lastValue-startValue;
                        AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, lastValue+startValue,amountOfSkiptRounds,totalLowerbound);
                        BranchAndBound branchAndBound = new BranchAndBound(matrix);
                        AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();
                        lowerboundsMatrix[i+1][lastValue+startValue] = bestSolution.getBestWeight();
                    }
                }
            }
            if (i < assignmentMatrix.getnRounds()-2){
                totalLowerbound = CalculateNewBound(i+1);
                test[i+1] = totalLowerbound;
            }
        }
        System.out.println();
    }
    public int CalculateNewBound(int round){
        int lb = 0;
        for (int i = 0; i < assignmentMatrix.getnRounds()-1; i++){
            lb += lowerboundsMatrix[round][i];
        }
        return lb;
    }
}

