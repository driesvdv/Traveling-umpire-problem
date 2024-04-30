package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class BranchAndBound {
    private int umpire;
    private int round;
    private AssignmentMatrix assignmentMatrix;
    //private MatchPair[][] solution;
    private int amountOfUmpires;
    private int amountOfRounds;

    public BranchAndBound(AssignmentMatrix assignmentMatrix) {
        this.umpire = 1;
        this.round = 1;
        this.assignmentMatrix = assignmentMatrix;
        amountOfUmpires = assignmentMatrix.getnUmpires();
        amountOfRounds = assignmentMatrix.getnRounds();

    }
    public BranchAndBound(int umpire, int round, AssignmentMatrix assignmentMatrix){
        this.umpire = umpire;
        this.round = round;
        this.assignmentMatrix = assignmentMatrix;
        amountOfUmpires = assignmentMatrix.getnUmpires();
        amountOfRounds = assignmentMatrix.getnRounds();
    }

    /**
     * Executes the branch and bound algorithm
     * @return boolean
     * The assignmentMatrix.assignUmpireToMatch(round,umpire,null); need to happen before the return false statement.
     * This is because we only check if an match has been assigned to a previous umpire.
     * **/
    public boolean executeBranchAndBound(){
        int nextUmpire = (umpire % amountOfUmpires) + 1;
        int nextRound = ((umpire == assignmentMatrix.getN()) ? round +1 : round);
        List<MatchPair> feasibleAllcations = getFeasibleAllocations(round-1,umpire, assignmentMatrix.getQ1(), assignmentMatrix.getQ2());
        for (MatchPair mp : feasibleAllcations){
            assignmentMatrix.assignUmpireToMatch(round,umpire,mp);
            if (!solutionIsComplete(assignmentMatrix)){
                BranchAndBound branchAndBound = new BranchAndBound(nextUmpire, nextRound, assignmentMatrix);
                if (branchAndBound.executeBranchAndBound()){
                    return true;
                }

                if (round >= 12){
                    //System.out.println();
                }
                if(!assignmentMatrix.canUmpiresVisitAllTeams(nextRound)){
                    assignmentMatrix.assignUmpireToMatch(round,umpire,null);
                    return false;
                }
            }
            else{
                //TODO add local search
                if (checkIfAllTeamsAreVisited()){
                    return true;
                }
                assignmentMatrix.assignUmpireToMatch(round,umpire,null);
                return false;
            }
            assignmentMatrix.assignUmpireToMatch(round,umpire,null);
        }
        return false;
    }

    public boolean solutionIsComplete(AssignmentMatrix assignmentMatrix){
        //only check current round and next
        if (round == amountOfRounds-1){
            for (int i = 0; i < amountOfUmpires; i++){
                if (assignmentMatrix.getSolutionMatrix()[round][i] == null){
                    return false;
                }
            }
            return true;
        }
        else{
            return false;
        }
    }
    public boolean checkIfAllTeamsAreVisited(){
        for (int i=0; i<amountOfUmpires; i++) {
            boolean[] visited = new boolean[assignmentMatrix.getnTeams()];
            for (int j=0; j<amountOfRounds; j++) {
                int team1 = assignmentMatrix.getSolutionMatrix()[j][i].getHomeTeam();
                visited[team1 - 1] = true; // Subtract 1 because teams are 1-indexed
            }

            int unvisitedTeams = (int) IntStream.range(0, assignmentMatrix.getnTeams()).filter(x -> !visited[x]).count();

            if (unvisitedTeams != 0) {
                return false;
            }
        }
        return true;
    }
    //Volgens de paper zal de volgende toewijzing telkens die zijn met de kortste afstand. Er zal dus telkens op afstand gesorteerd moeten worden.
    public List<MatchPair> getFeasibleAllocations(int round, int umpire, int q1, int q2){
        List<MatchPair> feasibleAllocations = assignmentMatrix.getPossibleAllocations(round, umpire);
        for (int i = 0; i < umpire-1; i++){ //checks if an umpire isn't assigned to a match in the same round
            if (round+1 < amountOfRounds){
                if (feasibleAllocations.contains(assignmentMatrix.getSolutionMatrix()[round+1][i])){
                    feasibleAllocations.remove(assignmentMatrix.getSolutionMatrix()[round+1][i]);
                }
            }
        }
        Iterator<MatchPair> iterator = feasibleAllocations.iterator();
        while (iterator.hasNext()) {
            MatchPair mp = iterator.next();
            if (!checkPreviousMatches(round +1, umpire, q1, q2, mp)) {
                iterator.remove();
            }
        }
        int previousHomeTeamLocation = assignmentMatrix.getSolutionMatrix()[round][umpire-1].getHomeTeam() -1;
        feasibleAllocations.sort((mp1, mp2) -> {
            int distance1 = assignmentMatrix.getDistance(previousHomeTeamLocation, mp1.getHomeTeam()-1);
            int distance2 = assignmentMatrix.getDistance(previousHomeTeamLocation, mp2.getHomeTeam()-1);
            return Integer.compare(distance1, distance2);
        });

        return feasibleAllocations;
    }

    public boolean checkPreviousMatches(int round, int umpire, int q1, int q2, MatchPair mp){
        if (round == 3){
            //System.out.println();
        }
        for (int i = 1; i < q1; i++){
            if (round - i >=0){
                if (assignmentMatrix.getSolutionMatrix()[round-i][umpire-1].getHomeTeam() == mp.getHomeTeam()){
                    return false;
                }
            }
        }
        return checkQ2(round, umpire-1, mp, q2);
    }

    public boolean checkQ2(int round, int umpire, MatchPair mp, int q2){
        for (int i = 1; i < q2; i++){
            if (round ==2){
                //System.out.println();
            }
            if (round - i >=0){
                if (assignmentMatrix.getSolutionMatrix()[round-i][umpire].getHomeTeam() == mp.getHomeTeam() || assignmentMatrix.getSolutionMatrix()[round-i][umpire].getHomeTeam() == mp.getOutTeam()){
                    return false;
                } else if (assignmentMatrix.getSolutionMatrix()[round-i][umpire].getOutTeam() == mp.getHomeTeam() || assignmentMatrix.getSolutionMatrix()[round-i][umpire].getOutTeam() == mp.getOutTeam()){
                    return false;
                }
            }
        }
        return true;
    }

}
