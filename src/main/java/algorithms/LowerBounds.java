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
    public LowerBounds(AssignmentMatrix assignmentMatrix) {
        this.assignmentMatrix = assignmentMatrix;
        this.solutionMatrix = new int[assignmentMatrix.getnRounds()][assignmentMatrix.getnRounds()][assignmentMatrix.getnUmpires()];
        this.lowerboundsMatrix = new int[assignmentMatrix.getnRounds()-1][assignmentMatrix.getnRounds()-1];
        totalLowerbound = 0;
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
    public void CalculateLowerBounds(){
        //We moeten een of andere structuur vinden waarbij we aan de hand van de initiele gevonden lowerbounds de branch en bound uitvoeren voor elke matching, maar telkens met een ronde erbij
        //Dit zodat we sterkere lowerbounds kunnen vinden.
        int startValue = 3;
        for (int i = 0; i < assignmentMatrix.getnRounds();i++){
//            AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, i, i+2,startValue,totalLowerbound);
//            BranchAndBound branchAndBound = new BranchAndBound(matrix);
//            AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();
//            lowerboundsMatrix[1][i*startValue] = bestSolution.getBestWeight();
        }


        System.out.println();
    }
    public int calculateNewLowerbound(int startValue,int round){
        int test = 0;
        for (int i=0; i < assignmentMatrix.getnRounds(); i+=startValue){
            test += lowerboundsMatrix[round][i];

        }
        return test;
    }
    public void test(){
        for(int k = 2; k < assignmentMatrix.getnRounds()-1; k++){
            int r = assignmentMatrix.getnRounds()-k;
            while (r >= 1){
                for (int rp = r+k-2; rp < r; rp++){
                    if (lowerboundsMatrix[rp][r+k] == 0){

                    }
                }
            }
        }
    }
//    public int getLowerboundOfSubproblem(int startround, int endround, int startvalue){
//        AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, startround,startvalue,totalLowerbound);
//        BranchAndBound branchAndBound = new BranchAndBound(matrix);
//        AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();
//        lowerboundsMatrix[1][i*startValue] = bestSolution.getBestWeight();
//    }

    public void test2(){
        for (int i = 0; i < assignmentMatrix.getnRounds()-2;i++){
            int startValue = 2+i;
            int lastValue = 0;
            for (int j = 0; j < assignmentMatrix.getnRounds()-startValue;j+=startValue){
                AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, j,startValue+1,totalLowerbound);
                BranchAndBound branchAndBound = new BranchAndBound(matrix);
                AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound();
                lowerboundsMatrix[i+1][j] = bestSolution.getBestWeight();
                lastValue = j;
            }
            if (lastValue != assignmentMatrix.getnRounds()-1){
                if (startValue < assignmentMatrix.getnRounds()-1){
                    lowerboundsMatrix[i+1][lastValue+startValue] = lowerboundsMatrix[i][lastValue+startValue];
                }
            }
            totalLowerbound = CalculateNewBound(i+1);
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

