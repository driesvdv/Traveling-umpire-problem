package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.List;

public class LowerboundsV2 {
    private int[][] minDistanceMatrix;
    private AssignmentMatrix assignmentMatrix;
    private int[] lowestDistancePerAmountOfSteps;
    private int[] lowerboundPerRound;

    public LowerboundsV2(AssignmentMatrix assignmentMatrix){
        this.assignmentMatrix = assignmentMatrix;
        this.minDistanceMatrix = new int[assignmentMatrix.getnRounds()-1][assignmentMatrix.getnRounds()-1];
        this.lowestDistancePerAmountOfSteps = new int[assignmentMatrix.getnRounds()];
        this.lowerboundPerRound = new int[assignmentMatrix.getnRounds()];
    }

    public void calculateInitialViaHungarian(){
        int minDistanceOneStep = 0;
        for (int i = 0; i < assignmentMatrix.getnRounds()-1; i++){
            int[][] costMatrix = createInitialDistanceMatrix(i);
            int[][] costMatrixCopy = new int[costMatrix.length][];
            for (int j = 0; j < costMatrix.length; j++) {
                costMatrixCopy[j] = costMatrix[j].clone();
            }
            HungarianAlgorithm h = new HungarianAlgorithm(costMatrixCopy);

            h.reduceInitialMatrix();
            h.solveReducedMatrix();

            int[][] test = h.getAssignments();
            int optimalAssignmentCost = getOptimalAssignmentCost(test, costMatrix);
            minDistanceOneStep += optimalAssignmentCost;
            minDistanceMatrix[0][i] = optimalAssignmentCost;
            lowestDistancePerAmountOfSteps[0] = minDistanceOneStep;
            for (int j = 0; j <= i; j++){
                lowerboundPerRound[j] += optimalAssignmentCost;
                setNewLowerboundInAssignmentMatrix(j, lowerboundPerRound[j]);
            }
        }
        lowerboundPerRound[0] = minDistanceOneStep;
        System.out.println("Hungarian done");
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

    // Start calculating the lowerbounds at the last round
    // The first round is calculate with the Hungarian algorithm so we start on 1
    public void calculateLowerbounds(){
        //calculateLargeLowerboundFirst();
        int stepValue = 2;
        for (int i = 1; i < assignmentMatrix.getnRounds()-1; i++){
            int minDistanceStep = 0;
            int remainder = (assignmentMatrix.getnRounds()-1)%stepValue;

            for (int j = assignmentMatrix.getnRounds()-1; j >= stepValue; j-=stepValue){
                AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, j, stepValue+1);
                BranchAndBound branchAndBound = new BranchAndBound(matrix);
                AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound(); //TODO if this is null the problem is infeasible. At the moment no check because it might be null because of an implementation error.
                minDistanceMatrix[i][j-1] = bestSolution.getBestWeight();
                if (bestSolution.getBestWeight() + minDistanceStep > lowerboundPerRound[j-stepValue]){
                    lowerboundPerRound[j-stepValue] = bestSolution.getBestWeight() + minDistanceStep;
                    setNewLowerboundInAssignmentMatrix(j-stepValue, lowerboundPerRound[j-stepValue]); // This is what is used in branch and bound
                }
                minDistanceStep += bestSolution.getBestWeight();
            }
            updateAllLowerbounds(remainder, lowerboundPerRound[remainder+1], stepValue); //Not sure if this is correct
            stepValue++;
            lowestDistancePerAmountOfSteps[i] = minDistanceStep;
        }
    }

    //When we calculate a new lowerbound, we want to use this to update all the lowerbounds, combining previous found solutions to create better results.
    public void updateAllLowerbounds(int remainder, int lowerbound, int stepValue){
        int toAddValue = 0;
        for (int i = remainder; i > 0; i--){
            toAddValue += minDistanceMatrix[0][i-1];
            if (lowerboundPerRound[i] < lowerbound + toAddValue){

                System.out.println("Lowerbound updated for round: " + i + " with value: " + (lowerbound +toAddValue) + ", previous value: " + lowerboundPerRound[i]);
                lowerboundPerRound[i] = lowerbound +toAddValue;
                setNewLowerboundInAssignmentMatrix(i, lowerboundPerRound[i]);
            }

        }
    }

//    public void calculateLargeLowerboundFirst(){
//        int stepValue = 6;
//        for (int i = 1; i < assignmentMatrix.getnRounds()-1; i++){
//            int minDistanceStep = 0;
//            if (assignmentMatrix.getIsComplete()){
//                break;
//            }
//            int remainder = (assignmentMatrix.getnRounds()-1)%stepValue;
//            for (int j = assignmentMatrix.getnRounds()-1; j >= stepValue; j-=stepValue){
//                AssignmentMatrix matrix = new AssignmentMatrix(assignmentMatrix, j, stepValue+1);
//                BranchAndBound branchAndBound = new BranchAndBound(matrix);
//                AssignmentMatrix bestSolution = branchAndBound.executeBranchAndBound(); //TODO if this is null the problem is infeasible. At the moment no check because it might be null because of an implementation error.
//                minDistanceMatrix[i][j-1] = bestSolution.getBestWeight();
//                if (bestSolution.getBestWeight() + minDistanceStep > lowerboundPerRound[j-stepValue]){
//                    lowerboundPerRound[j-stepValue] = bestSolution.getBestWeight() + minDistanceStep;
//                    setNewLowerboundInAssignmentMatrix(j-stepValue, lowerboundPerRound[j-stepValue]);
//                }
//                minDistanceStep += bestSolution.getBestWeight();
//            }
//
//            //TODO: for each amount of rounds check if the bounds are better.
//
//            //The last values in the array are the most useful. So why bother to fix the first ones.
////            for (int j = remainder; j >0; j--){
////
////                minDistanceMatrix[i][j] = minDistanceMatrix[0][j];
////                minDistanceStep += minDistanceMatrix[0][j];
////            }
//            stepValue++;
//            lowestDistancePerAmountOfSteps[i] = minDistanceStep;
//            System.out.println("Lowerbound calculated for step: " + stepValue);
//            if (stepValue == 16){
//                break;
//            }
//
//        }
//    }

}
