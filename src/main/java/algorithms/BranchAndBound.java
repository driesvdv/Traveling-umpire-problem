package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.*;
import java.util.stream.IntStream;

public class BranchAndBound {
    private final AssignmentMatrix assignmentMatrix;
    private final int amountOfUmpires;
    private final int amountOfRounds;
    private int upperBound = Integer.MAX_VALUE;
    private final boolean isSubProblem;
    private AssignmentMatrix bestSolution; // Reference to the best solution found so far

    public BranchAndBound(AssignmentMatrix assignmentMatrix) {
        this.assignmentMatrix = assignmentMatrix;
        this.amountOfUmpires = assignmentMatrix.getnUmpires();
        this.amountOfRounds = assignmentMatrix.getnRounds();
        this.isSubProblem = assignmentMatrix.isSubProblem();
    }

    public AssignmentMatrix executeBranchAndBound() {
        executeBranchAndBound(1, 1, 0); // Start with the first umpire and the first round with an initial weight of 0
        return bestSolution;
    }

    private void executeBranchAndBound(int currentUmpire, int currentRound, int currentWeight) {
        if (currentRound >= amountOfRounds || currentUmpire > amountOfUmpires) {
            return; // Base case: If all rounds or umpires are exhausted, return
        }

        int nextUmpire = (currentUmpire % amountOfUmpires) + 1; // Determine the next umpire
        int nextRound = ((currentUmpire == amountOfUmpires) ? currentRound + 1 : currentRound); // Determine the next round

        List<MatchPair> feasibleAllocations = getFeasibleAllocations(currentRound - 1, currentUmpire, assignmentMatrix.getQ1(), assignmentMatrix.getQ2(), currentWeight);

        for (MatchPair mp : feasibleAllocations) {
            assignmentMatrix.assignUmpireToMatch(currentRound, currentUmpire, mp); // Assign the match pair to the current umpire and round
            int newWeight = assignmentMatrix.getAssignmentsWeight(); // Calculate the new weight

            if (newWeight < upperBound) { // Early pruning: Prune branches with weights higher than the current upper bound
                if (!isSolutionComplete(currentRound)) { // Check if the solution is complete
                    //Todo add partial matching weight here.
                    //int partial_matching_weight = calculatePartialMatchingCost(nextUmpire, nextRound);
                    //Add the partial_matching_weight in the if statement below
                    if (newWeight + assignmentMatrix.getLowerboundPerRound(nextRound) <= upperBound) { // More aggressive pruning
                        executeBranchAndBound(nextUmpire, nextRound, newWeight); // Recursive call with updated umpire, round, and weight
                    }
                } else {
                    if (isSubProblem) { // Handle sub-problem scenario
                        updateBestSolution(newWeight);
                    } else {
                        if (checkIfAllTeamsAreVisited()) { // Ensure all teams are visited
                            updateBestSolution(newWeight);
                        }
                    }
                }
            }
            assignmentMatrix.assignUmpireToMatch(currentRound, currentUmpire, null); // Backtrack: Remove the match pair assignment
        }
    }

    private void updateBestSolution(int weight) {
        if (weight < upperBound) { // Update the best solution if the new weight is lower than the current upper bound
            upperBound = weight;
            bestSolution = assignmentMatrix;
            assignmentMatrix.setBestSolution(assignmentMatrix.getSolutionMatrix());
            assignmentMatrix.setBestWeight(upperBound);
            if (!isSubProblem) {
                System.out.println("New best solution found! Weight: " + upperBound);
            }
        }
    }

    private boolean isSolutionComplete(int currentRound) {
        if (currentRound == amountOfRounds - 1) { // Check if the current round is the last round
            for (int i = 0; i < amountOfUmpires; i++) {
                if (assignmentMatrix.getSolutionMatrix()[currentRound][i] == null) { // Ensure all umpires have assignments
                    return false;
                }
            }
            return true; // Solution is complete
        }
        return false;
    }

    private boolean checkIfAllTeamsAreVisited() {
        for (int i = 0; i < amountOfUmpires; i++) {
            boolean[] visited = new boolean[assignmentMatrix.getnTeams()];
            for (int j = 0; j < amountOfRounds; j++) {
                if (assignmentMatrix.getSolutionMatrix()[j][i] != null) {
                    int team1 = assignmentMatrix.getSolutionMatrix()[j][i].getHomeTeam();
                    visited[team1 - 1] = true; // Mark the home team as visited
                }
            }
            int unvisitedTeams = (int) IntStream.range(0, assignmentMatrix.getnTeams()).filter(x -> !visited[x]).count();
            if (unvisitedTeams != 0) { // If there are unvisited teams, return false
                return false;
            }
        }
        return true; // All teams are visited
    }

    private List<MatchPair> getFeasibleAllocations(int round, int umpire, int q1, int q2, int currDistance) {
        List<MatchPair> feasibleAllocations = assignmentMatrix.getPossibleAllocations(round, umpire);

        if (round >= 0 && round < amountOfRounds - 1) {
            feasibleAllocations.removeIf(mp -> {
                for (int i = 0; i < umpire - 1; i++) {
                    if (Objects.equals(mp, assignmentMatrix.getSolutionMatrix()[round + 1][i])) { // Remove infeasible allocations
                        return true;
                    }
                }
                return false;
            });
        }

        int previousHomeTeamLocation = -1;
        if (umpire - 1 >= 0 && round >= 0 && assignmentMatrix.getSolutionMatrix()[round][umpire - 1] != null) {
            previousHomeTeamLocation = assignmentMatrix.getSolutionMatrix()[round][umpire - 1].getHomeTeam() - 1; // Track previous home team location
        }

        int finalPreviousHomeTeamLocation = previousHomeTeamLocation;
        feasibleAllocations.removeIf(mp -> !checkPreviousMatches(round + 1, umpire, q1, q2, mp) ||
                (upperBound < currDistance + assignmentMatrix.getDistance(mp.getHomeTeam() - 1, finalPreviousHomeTeamLocation))); // Aggressive pruning based on distance

        feasibleAllocations.sort(Comparator.comparingInt(mp -> assignmentMatrix.getDistance(finalPreviousHomeTeamLocation, mp.getHomeTeam() - 1))); // Sort allocations by distance
        return feasibleAllocations;
    }

    private int getFeasibleAllocationsMatchParing(int round, int umpire){
        List<MatchPair> feasibleAllocations = assignmentMatrix.getPossibleAllocations(round, umpire);
        int shortestDistance = Integer.MAX_VALUE;
        if (round >= 0 && round < amountOfRounds - 1) {
            feasibleAllocations.removeIf(mp -> {
                for (int i = 0; i < umpire - 1; i++) {
                    if (Objects.equals(mp, assignmentMatrix.getSolutionMatrix()[round + 1][i])) { // Remove infeasible allocations
                        return true;
                    }
                }
                return false;
            });
        }
        int homeTeam = assignmentMatrix.getHomeTeamOfMatchInRound(round, umpire);
        for (int i = 0; i < feasibleAllocations.size(); i++){
            int distance = assignmentMatrix.getDistance(homeTeam - 1, feasibleAllocations.get(i).getHomeTeam() - 1);
            if (distance < shortestDistance){
                shortestDistance = distance;
            }
        }
        return shortestDistance;
    }

    private boolean checkPreviousMatches(int round, int umpire, int q1, int q2, MatchPair mp) {
        for (int i = 1; i < q1; i++) {
            if (round - i >= 0) {
                if (assignmentMatrix.getSolutionMatrix()[round - i][umpire - 1] != null &&
                        assignmentMatrix.getSolutionMatrix()[round - i][umpire - 1].getHomeTeam() == mp.getHomeTeam()) { // Check if the home team has been matched recently
                    return false;
                }
            }
        }
        return checkQ2(round, umpire - 1, mp, q2);
    }

    private boolean checkQ2(int round, int umpire, MatchPair mp, int q2) {
        for (int i = 1; i < q2; i++) {
            if (round - i >= 0) {
                if (assignmentMatrix.getSolutionMatrix()[round - i][umpire] != null) {
                    int homeTeam = assignmentMatrix.getSolutionMatrix()[round - i][umpire].getHomeTeam();
                    int outTeam = assignmentMatrix.getSolutionMatrix()[round - i][umpire].getOutTeam();
                    if (homeTeam == mp.getHomeTeam() || homeTeam == mp.getOutTeam() ||
                            outTeam == mp.getHomeTeam() || outTeam == mp.getOutTeam()) { // Check if the match pair conflicts with recent matches
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int calculatePartialMatchingCost(int nextUmpire, int nextRound){ //TODO maybe don't do this in the beginning?
        if (nextUmpire == 1){ //If nextUmpire == 1, it goes to the next round so this isn't needed.
            return 0;
        }
//        if (nextRound < 2){
//            return 0;
//        }
//        if (nextRound > amountOfRounds - 2){
//            return 0;
//        }
        int distance = 0;

        for (int i = nextUmpire; i <= this.amountOfUmpires; i++){
            distance += getShortestFeasibleDistance(nextRound -1, nextUmpire);
            //distance += getFeasibleAllocationsMatchParing(nextRound -1, nextUmpire);
        }
//        List<MatchPair> possibleMatches = new ArrayList<>();
//        for (int i = nextUmpire; i < assignmentMatrix.getnUmpires(); i++){ //TODO check: n of n+1
//            possibleMatches.addAll(getFeasibleAllocationsMatchParing(nextRound -1, nextUmpire));
//        }
//        if (possibleMatches.size() == 0){
//            return 0;
//        }
        //return getMinimalCostUsingHungarian(possibleMatches);
        return distance;
    }

    public int getMinimalCostUsingHungarian(List<MatchPair> mps){
        int[][] costMatrix = createNewDistanceMatrix(mps);
        int[][] costMatrixCopy = new int[costMatrix.length][];

        for (int row = 0; row < costMatrix.length; row++) {
            costMatrixCopy[row] = costMatrix[row].clone();
        }

        HungarianAlgorithm hungarian = new HungarianAlgorithm(costMatrixCopy);
        hungarian.reduceInitialMatrix();
        hungarian.solveReducedMatrix();
        int[][] assignments = hungarian.getAssignments();
        int optimalAssignmentCost = getOptimalAssignmentCost(assignments, costMatrix);
        return optimalAssignmentCost;
    }
    public int[][] createNewDistanceMatrix(List<MatchPair> mps){
        int[][] distanceMatrix = new int[mps.size()][mps.size()];
        for (int i = 0; i < mps.size(); i++){
            for (int j = 0; j < mps.size(); j++){
                distanceMatrix[i][j] = assignmentMatrix.getDistance(mps.get(i).getHomeTeam()-1, mps.get(j).getHomeTeam()-1);
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

    private int getShortestFeasibleDistance(int round, int umpire) {
        List<MatchPair> feasibleAllocations = assignmentMatrix.getPossibleAllocations(round, umpire);
        if (feasibleAllocations.isEmpty()) {
            return 0;
        }

        int previousHomeTeamLocation = -1;
        if (umpire - 1 >= 0 && round >= 0 && assignmentMatrix.getSolutionMatrix()[round][umpire - 1] != null) {
            previousHomeTeamLocation = assignmentMatrix.getSolutionMatrix()[round][umpire - 1].getHomeTeam() - 1; // Track previous home team location
        }

        int finalPreviousHomeTeamLocation = previousHomeTeamLocation;
        return feasibleAllocations.stream()
                .mapToInt(mp -> assignmentMatrix.getDistance(finalPreviousHomeTeamLocation, mp.getHomeTeam() - 1))
                .min()
                .orElse(Integer.MAX_VALUE); // Return the shortest distance
    }
}
