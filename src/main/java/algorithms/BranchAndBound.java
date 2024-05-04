package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class BranchAndBound {
    private int umpire;
    private int round;
    private AssignmentMatrix assignmentMatrix;
    private int amountOfUmpires;
    private int amountOfRounds;
    private int upperBound = Integer.MAX_VALUE;
    private int bestWeight = Integer.MAX_VALUE; // Variable to store the weight of the best solution found so far
    private AssignmentMatrix bestSolution; // Reference to the best solution found so far


    public BranchAndBound(AssignmentMatrix assignmentMatrix) {
        this.umpire = 1;
        this.round = 1;
        this.assignmentMatrix = assignmentMatrix;
        this.amountOfUmpires = assignmentMatrix.getnUmpires();
        this.amountOfRounds = assignmentMatrix.getnRounds();
    }

    public BranchAndBound(int umpire, int round, AssignmentMatrix assignmentMatrix) {
        this.umpire = umpire;
        this.round = round;
        this.assignmentMatrix = assignmentMatrix;
        this.amountOfUmpires = assignmentMatrix.getnUmpires();
        this.amountOfRounds = assignmentMatrix.getnRounds();
    }

    public List<AssignmentMatrix> executeBranchAndBound() {
        List<AssignmentMatrix> solutions = new ArrayList<>();
        int nextUmpire = (umpire % amountOfUmpires) + 1;
        int nextRound = ((umpire == assignmentMatrix.getN()) ? round + 1 : round);
        List<MatchPair> feasibleAllocations = getFeasibleAllocations(round - 1, umpire, assignmentMatrix.getQ1(), assignmentMatrix.getQ2());
        for (MatchPair mp : feasibleAllocations) {
            assignmentMatrix.assignUmpireToMatch(round, umpire, mp);
            if (!isSolutionComplete()) {
                // Save the current state
                int currentUmpire = umpire;
                int currentRound = round;
                // Update the state for the next recursive call
                umpire = nextUmpire;
                round = nextRound;
                solutions.addAll(executeBranchAndBound());
                // Restore the state
                umpire = currentUmpire;
                round = currentRound;
                if (!assignmentMatrix.canUmpiresVisitAllTeams(nextRound)) {
                    assignmentMatrix.assignUmpireToMatch(round, umpire, null);
                }
            } else {
                if (checkIfAllTeamsAreVisited()) {
                    int weight = assignmentMatrix.getAssignmentsWeight();
                    if (weight < bestWeight) {
                        bestWeight = weight;
                        bestSolution = assignmentMatrix;
                        System.out.println("New best solution found! Weight: " + bestWeight);
                    }
                }
                assignmentMatrix.assignUmpireToMatch(round, umpire, null);
            }
            assignmentMatrix.assignUmpireToMatch(round, umpire, null);
        }
        return solutions;
    }


    public boolean isSolutionComplete() {
        if (round == amountOfRounds - 1) {
            for (int i = 0; i < amountOfUmpires; i++) {
                if (assignmentMatrix.getSolutionMatrix()[round][i] == null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean checkIfAllTeamsAreVisited() {
        for (int i = 0; i < amountOfUmpires; i++) {
            boolean[] visited = new boolean[assignmentMatrix.getnTeams()];
            for (int j = 0; j < amountOfRounds; j++) {
                int team1 = assignmentMatrix.getSolutionMatrix()[j][i].getHomeTeam();
                visited[team1 - 1] = true;
            }
            int unvisitedTeams = (int) IntStream.range(0, assignmentMatrix.getnTeams()).filter(x -> !visited[x]).count();
            if (unvisitedTeams != 0) {
                return false;
            }
        }
        return true;
    }

    public List<MatchPair> getFeasibleAllocations(int round, int umpire, int q1, int q2) {
        List<MatchPair> feasibleAllocations = assignmentMatrix.getPossibleAllocations(round, umpire);
        for (int i = 0; i < umpire - 1; i++) {
            if (round + 1 < amountOfRounds) {
                if (feasibleAllocations.contains(assignmentMatrix.getSolutionMatrix()[round + 1][i])) {
                    feasibleAllocations.remove(assignmentMatrix.getSolutionMatrix()[round + 1][i]);
                }
            }
        }
        Iterator<MatchPair> iterator = feasibleAllocations.iterator();
        while (iterator.hasNext()) {
            MatchPair mp = iterator.next();
            if (!checkPreviousMatches(round + 1, umpire, q1, q2, mp)) {
                iterator.remove();
            }
        }
        int previousHomeTeamLocation = assignmentMatrix.getSolutionMatrix()[round][umpire - 1].getHomeTeam() - 1;
        feasibleAllocations.sort((mp1, mp2) -> {
            int distance1 = assignmentMatrix.getDistance(previousHomeTeamLocation, mp1.getHomeTeam() - 1);
            int distance2 = assignmentMatrix.getDistance(previousHomeTeamLocation, mp2.getHomeTeam() - 1);
            return Integer.compare(distance1, distance2);
        });
        return feasibleAllocations;
    }

    public boolean checkPreviousMatches(int round, int umpire, int q1, int q2, MatchPair mp) {
        for (int i = 1; i < q1; i++) {
            if (round - i >= 0) {
                if (assignmentMatrix.getSolutionMatrix()[round - i][umpire - 1].getHomeTeam() == mp.getHomeTeam()) {
                    return false;
                }
            }
        }
        return checkQ2(round, umpire - 1, mp, q2);
    }

    public boolean checkQ2(int round, int umpire, MatchPair mp, int q2) {
        for (int i = 1; i < q2; i++) {
            if (round - i >= 0) {
                if (assignmentMatrix.getSolutionMatrix()[round - i][umpire].getHomeTeam() == mp.getHomeTeam()
                        || assignmentMatrix.getSolutionMatrix()[round - i][umpire].getHomeTeam() == mp.getOutTeam()) {
                    return false;
                } else if (assignmentMatrix.getSolutionMatrix()[round - i][umpire].getOutTeam() == mp.getHomeTeam()
                        || assignmentMatrix.getSolutionMatrix()[round - i][umpire].getOutTeam() == mp.getOutTeam()) {
                    return false;
                }
            }
        }
        return true;
    }
}

