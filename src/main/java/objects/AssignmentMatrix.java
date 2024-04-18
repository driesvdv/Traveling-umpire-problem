package objects;

import data.Instance;

import java.util.List;

public class AssignmentMatrix {
    private int nRounds;
    private int nUmpires;
    private int teams;
    private int q1;
    private int q2;
    private int n;

    /**
     * The assignment matrix is a 2D array that represents the assignment of umpires to games.
     * The rows represent the rounds and the columns represent the umpires.
     * Each cell contains an integer which maps to the teams playing the match. These teams can be found in the translation matrix.
     */
    private int[][] assignmentMatrix;
    private MatchPair[][] solutionMatrix;

    /**
     * The weight matrix is a 2D array that represents the distance between teams.
     */
    private int[][] weightMatrix;
    private MatchPair[][] translationMatrix;

    public AssignmentMatrix(Instance instance) {
        q1 = 1;
        q2 = 2;
        nRounds = instance.getnTeams() * 2 - 2;
        nUmpires = instance.getnTeams()/2;
        teams = instance.getnTeams();
        n = teams/2;
        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[teams][teams];
        translationMatrix = new MatchPair[nRounds][nUmpires];
        solutionMatrix = new MatchPair[nRounds][nUmpires];
        initTranslationMatrix(instance);
        initAssignMentMatrix();
        initWeightMatrix(instance);

        preprocessMatches();
        System.out.println("debug");
    }
    public void preprocessMatches(){
        Preprocessing preprocesser = new Preprocessing(this.assignmentMatrix, q1,q2, translationMatrix);
        preprocesser.preProcessQ1andQ2();
        this.translationMatrix = preprocesser.getMatchPairs();
    }
    //Todo make this with MatchPairs
    private void initAssignMentMatrix(){
        for (int i = 0; i < nRounds; i++) {
            for (int j = 0; j < nUmpires; j++) {
                if (i == 0) {
                    // Fix the first round for symmetry breaking
                    solutionMatrix[i][j] = translationMatrix[i][j];
                    assignmentMatrix[i][j] = j;
                }
                else {
                    // Initialize the rest of the matrix with -1 (no assignment)
                    assignmentMatrix[i][j] = -1;
                    solutionMatrix[i][j] = null;
                }
            }
        }
    }

    private void initWeightMatrix(Instance inst){
        for (int i = 0; i < teams; i++) {
            for (int j = 0; j < teams; j++) {
                weightMatrix[i][j] = inst.getDist(i,j);
            }
        }
    }

    private void initTranslationMatrix(Instance inst){
        for (int i = 0; i < nRounds; i++) {
            //int[] controleMatrix = new int[teams];
            int counter = 0;
            for (int j = 0; j < teams; j++) {
                int team1 = inst.getOpponents()[i][j];
                if (team1 > 0){
                    int team2 = inst.getOpponents()[i][Math.abs(team1)-1];
                    var test = new MatchPair(team1, team2);
                    translationMatrix[i][counter] = test;//tmp; //Klopt niet bij j groter dan 4
                    counter++;
                }
            }
        }
    }

    public MatchPair[][] getTranslationMatrix() {
        return translationMatrix;
    }

    public int getnRounds() {
        return nRounds;
    }

    public int getnUmpires() {
        return nUmpires;
    }

    public int[][] getAssignmentMatrix() {
        return assignmentMatrix;
    }
    public int getN(){
        return n;
    }
    public List<MatchPair> getPossibleAllocations(int round, int umpire){
        return solutionMatrix[round][umpire-1].getFeasibleChildren();
    }
    public void setTranslationMatrix(MatchPair[][] translationMatrix) {
        this.translationMatrix = translationMatrix;
    }

    public MatchPair[][] getSolutionMatrix() {
        return solutionMatrix;
    }
    public void assignUmpireToMatch(int round, int umpire, MatchPair match){
        solutionMatrix[round][umpire-1] = match;
    }
//    public MatchPair[][] getSolutionMatrix(){
//        return solutionMatrix;
//    }
}
