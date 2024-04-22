package algorithms;

import objects.AssignmentMatrix;
import objects.MatchPair;

import java.util.List;

public class BranchAndBound {
    private int umpire;
    private int round;
    private AssignmentMatrix assignmentMatrix;
    //private MatchPair[][] solution;
    private int amountOfUmpires;
    private int amountOfRounds;

//    public BranchAndBound(int umpire, int round, AssignmentMatrix assignmentMatrix, MatchPair[][] solution) {
//        this.umpire = umpire;
//        this.round = round;
//        this.assignmentMatrix = assignmentMatrix;
//        this.solution = solution;
//        amountOfUmpires = assignmentMatrix.getnUmpires();
//        amountOfRounds = assignmentMatrix.getnRounds();
//    }
    public BranchAndBound(AssignmentMatrix assignmentMatrix) {
        this.umpire = 1;
        this.round = 1;
        this.assignmentMatrix = assignmentMatrix;
        //this.solution = assignmentMatrix.getSolutionMatrix();
        //this.solution = assignmentMatrix.getSolutionMatrix();
        amountOfUmpires = assignmentMatrix.getnUmpires();
        amountOfRounds = assignmentMatrix.getnRounds();

    }
//    public BranchAndBound(int umpire, int round, MatchPair[][] solution, int amountOfUmpires, int amountOfRounds, ) {
//        this.umpire = umpire;
//        this.round = round;
//        //this.solution = solution;
//        this.amountOfUmpires = amountOfUmpires;
//        this.amountOfRounds = amountOfRounds;
//    }
    public BranchAndBound(int umpire, int round, AssignmentMatrix assignmentMatrix){
        this.umpire = umpire;
        this.round = round;
        this.assignmentMatrix = assignmentMatrix;
        amountOfUmpires = assignmentMatrix.getnUmpires();
        amountOfRounds = assignmentMatrix.getnRounds();
    }
    public boolean executeBranchAndBound(){
        int nextUmpire = (umpire % amountOfUmpires) +1;
        int nextRound = ((umpire == assignmentMatrix.getN()) ? round +1 : round);
        List<MatchPair> feasibleAllcations = getFeasibleAllocations(round-1,umpire);
        if (umpire == 4 && round == 13){
            System.out.println();
        }
        for (MatchPair mp : feasibleAllcations){
            assignmentMatrix.assignUmpireToMatch(round,umpire,mp);
            if (!solutionIsComplete(assignmentMatrix)){
                BranchAndBound branchAndBound = new BranchAndBound(nextUmpire, nextRound, assignmentMatrix);
                if (branchAndBound.executeBranchAndBound()){
                    return true;
                }
            }
            else{
                //TODO fix local search

                return true;
            }
            assignmentMatrix.assignUmpireToMatch(round,umpire,null);
        }
        return false;
    }
    //TODO assignmentmatrix aanpassen zodat daar matchpairs inzitten ipv integers
//    public void executeBranchAndBound(){
//        int nextUmpire = (umpire % amountOfUmpires) +1;
//        int nextRound = ((umpire == amountOfRounds) ? round +1 : round);
//        List<MatchPair> feasibleAllcations = assignmentMatrix.getPossibleAllocations(umpire, round-1);
//        for (MatchPair mp : feasibleAllcations){
//            assignmentMatrix.getsolution[round][umpire] = mp;
//            if (!solutionIsComplete()){
//                BranchAndBound branchAndBound = new BranchAndBound(nextUmpire, nextRound, solution, amountOfUmpires, amountOfRounds);
//                branchAndBound.executeBranchAndBound();
//            }
//            else{
//                //TODO fix local search
//            }
//            solution[round][umpire] = null;
//        }
//    }

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
    public List<MatchPair> getFeasibleAllocations(int round, int umpire){
        List<MatchPair> feasibleAllocations = assignmentMatrix.getPossibleAllocations(round, umpire);
        for (int i = 0; i < umpire-1; i++){
            if (round+1 < amountOfRounds){
                if (feasibleAllocations.contains(assignmentMatrix.getSolutionMatrix()[round+1][i])){
                    feasibleAllocations.remove(assignmentMatrix.getSolutionMatrix()[round+1][i]);
                }
            }
        }
        return feasibleAllocations;
    }

}
