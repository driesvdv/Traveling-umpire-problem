package objects;

import data.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import java.util.List;

public class AssignmentMatrix {
    private int nRounds;
    private int nUmpires;
    private int nTeams;
    private int q1;
    private int q2;
    private int n;
    private int lowerbound;
    private AtomicInteger lowerboundsValue;
    private boolean isSubProblem;

    private MatchPair[][] bestSolution;
    private int bestWeight;

    private boolean isComplete;

    /**
     * The assignment matrix is a 2D array that represents the assignment of umpires
     * to games.
     * The rows represent the rounds and the columns represent the umpires.
     * Each cell contains an integer which maps to the teams playing the match.
     * These teams can be found in the translation matrix.
     */
    private int[][] assignmentMatrix;
    private MatchPair[][] solutionMatrix;

    /**
     * The weight matrix is a 2D array that represents the distance between teams.
     */
    private int[][] weightMatrix;
    /**
     * The translation matrix is a 2D array that represents a translation between
     * the assignment matrix and the teams playing the match.
     */
    private MatchPair[][] translationMatrix;
    private int[] lowerboundPerRound; //This needs to be synchonized between the b&b and lowerbounds

    public AssignmentMatrix(Instance instance) {
        q1 = 5;
        q2 = 2;
        nRounds = instance.getnTeams() * 2 - 2;
        nUmpires = instance.getnTeams() / 2;
        nTeams = instance.getnTeams();
        n = nTeams / 2;
        isSubProblem = false;
        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[nTeams][nTeams];
        translationMatrix = new MatchPair[nRounds][nUmpires];
        solutionMatrix = new MatchPair[nRounds][nUmpires];
        bestSolution = new MatchPair[nRounds][nUmpires];
        isComplete = false;
        initLowerboundPerRound();
        initTranslationMatrix(instance);

        initAssignMentMatrix();
        initWeightMatrix(instance);

        preprocessMatches();
        //System.out.println("debug");
    }
    public AssignmentMatrix(AssignmentMatrix totalMatrix, int startRound, int amountOfRounds, int lowerbound){
        q1 = totalMatrix.getQ1();
        q2 = totalMatrix.getQ2();
        nRounds = amountOfRounds;
        isSubProblem = true;
        nUmpires = totalMatrix.getnUmpires();
        nTeams = totalMatrix.getnTeams();
        n = totalMatrix.getN();
        this.lowerbound = lowerbound;
        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[nTeams][nTeams];
        translationMatrix = new MatchPair[nRounds][nUmpires];
        solutionMatrix = new MatchPair[nRounds][nUmpires];
        bestSolution = new MatchPair[nRounds][nUmpires];
        isComplete = false;
        initLowerboundPerRound();
        initTranslationMatrix(totalMatrix.getTranslationMatrix(),startRound);
        initAssignMentMatrix();
        initWeightMatrix(totalMatrix.getWeightMatrix());
    }
    //This implementation will be used to get the lowerbounds.
    //It will run from the last round to the first round, so the startround is the last round
    //The stepsize is the amount of rounds that are taken into account when calculating the lowerbound
    public AssignmentMatrix(AssignmentMatrix totalMatrix, int startRound, int stepSize){
        q1 = totalMatrix.getQ1();
        q2 = totalMatrix.getQ2();
        nRounds = stepSize;
        isSubProblem = true;
        nUmpires = totalMatrix.getnUmpires();
        nTeams = totalMatrix.getnTeams();
        n = totalMatrix.getN();
        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[nTeams][nTeams];
        translationMatrix = new MatchPair[nRounds][nUmpires];
        solutionMatrix = new MatchPair[nRounds][nUmpires];
        bestSolution = new MatchPair[nRounds][nUmpires];
        isComplete = false;
        initLowerboundPerRound();
        initTranslationMatrixLowerbounds(totalMatrix.getTranslationMatrix(),startRound, stepSize);
        initAssignMentMatrix();
        initWeightMatrix(totalMatrix.getWeightMatrix());
    }

    public int getAssignmentsWeight() {
        int weight = 0;
        for (int i = 0; i < nRounds - 1; i++) {
            for (int j = 0; j < nUmpires; j++) {
                if (solutionMatrix[i][j] != null && solutionMatrix[i+1][j] != null) {
                    weight += weightMatrix[solutionMatrix[i][j].getHomeTeam() - 1][solutionMatrix[i+1][j].getHomeTeam()
                            - 1];
                }
            }
        }
        return weight;
    }

    public void preprocessMatches() {
        Preprocessing preprocesser = new Preprocessing(this.assignmentMatrix, q1, q2, translationMatrix);
        preprocesser.preProcessQ1andQ2();
        this.translationMatrix = preprocesser.getMatchPairs();
    }

    // Todo make this with MatchPairs
    private void initAssignMentMatrix() {
        for (int i = 0; i < nRounds; i++) {
            for (int j = 0; j < nUmpires; j++) {
                if (i == 0) {
                    // Fix the first round for symmetry breaking
                    solutionMatrix[i][j] = translationMatrix[i][j];
                    assignmentMatrix[i][j] = j;
                } else {
                    // Initialize the rest of the matrix with -1 (no assignment)
                    assignmentMatrix[i][j] = -1;
                    solutionMatrix[i][j] = null;
                }
            }
        }
    }

    private void initWeightMatrix(Instance inst) {
        for (int i = 0; i < nTeams; i++) {
            for (int j = 0; j < nTeams; j++) {
                weightMatrix[i][j] = inst.getDist(i, j);
            }
        }
    }
    private void initWeightMatrix(int[][] weightMatrix) {
        for (int i = 0; i < weightMatrix.length; i++) {
            for (int j = 0; j < weightMatrix.length; j++) {
                this.weightMatrix[i][j] = weightMatrix[i][j];
            }
        }
    }

    private void initTranslationMatrix(Instance inst) {
        for (int i = 0; i < nRounds; i++) {
            List<Integer> teams = new ArrayList<>();
            int counter = 0;
            for (int j = 0; j < nTeams; j++) {
                if (!teams.contains(Math.abs(inst.getOpponents()[i][j]))) {
                    int team1 = inst.getOpponents()[i][j];
                    int team2 = inst.getOpponents()[i][Math.abs(team1) - 1];
                    teams.add(Math.abs(team1));
                    teams.add(Math.abs(team2));
                    var test = new MatchPair(team1, team2);
                    translationMatrix[i][counter] = test;// tmp; //Klopt niet bij j groter dan 4
                    counter++;
                }
            }
        }
    }

    private void initTranslationMatrix(MatchPair[][] totalTranslationMatrix, int startround) {
        for (int i = 0; i < nRounds; i++) {
            for (int j = 0; j < nUmpires; j++) {
                translationMatrix[i][j] = totalTranslationMatrix[i+startround][j];
            }
        }
    }

    private void initTranslationMatrixLowerbounds(MatchPair[][] totalTranslationMatrix, int startRound ,int stepsize) {
        for (int i = 0; i < nRounds; i++) {
            for (int j = 0; j < nUmpires; j++) {
                translationMatrix[i][j] = totalTranslationMatrix[i+startRound+1 - stepsize][j];
            }
        }
       // System.out.println();
    }

    /**
     * Can the umpire still visit each distinct hometeah at least once?
     *
     * @return false if umpire can't visit all teams anymore
     * @return true if solution is valid
     */
    public boolean canUmpiresVisitAllTeams(int currentRound) {
        int roundsLeft = nRounds - currentRound;
        for (int i = 0; i < nUmpires; i++) {
            boolean[] visited = new boolean[nTeams];
            for (int j = 0; j <= currentRound; j++) {
                if (solutionMatrix[j][i] != null) {
                    int team1 = solutionMatrix[j][i].getHomeTeam();
                    visited[team1 - 1] = true;
                }
            }
            int unvisitedTeams = (int) IntStream.range(0, nTeams).filter(x -> !visited[x]).count();
            if (unvisitedTeams > roundsLeft) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if all empires have only one match per round and thus no double
     * assignments
     *
     * @return boolean
     */
    public boolean haveUmpiresOneMatchPerRound() {
        // No double values per row
        for (int i = 0; i < nRounds; i++) {
            int[] row = assignmentMatrix[i];

            if (Arrays.stream(row).distinct().count() != row.length) {
                return false;
            }
        }

        return true;
    }

    public int[][] getWeightMatrix() {
        return weightMatrix;
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

    public int getnTeams() {
        return nTeams;
    }

    public boolean isSubProblem() {
        return isSubProblem;
    }

    public int[][] getAssignmentMatrix() {
        return assignmentMatrix;
    }

    public int getN() {
        return n;
    }

    public List<MatchPair> getPossibleAllocations(int round, int umpire) {
        return solutionMatrix[round][umpire - 1].getFeasibleChildren();
    }

    public int getLowerbound() {
        return lowerbound;
    }
    public int getLowerboundsValueAtomic(){
        return lowerboundsValue.get();
    }
    public void setLowerboundsValue(int lb){
        this.lowerboundsValue.set(lb);
    }
    //public int getLowerboundsPerRound(int round, int)
    public void setTranslationMatrix(MatchPair[][] translationMatrix) {
        this.translationMatrix = translationMatrix;
    }

    public MatchPair[][] getSolutionMatrix() {
        return solutionMatrix;
    }

    public void assignUmpireToMatch(int round, int umpire, MatchPair match) {
        solutionMatrix[round][umpire - 1] = match;
    }

    public int getQ1() {
        return q1;
    }

    public int getQ2() {
        return q2;
    }

    public int getDistance(int team1, int team2) {
        return weightMatrix[team1][team2];
    }

    public void setBestSolution(MatchPair[][] bestSolution) {
        this.bestSolution = new MatchPair[nRounds][nUmpires];
        for (int i = 0; i < nRounds; i++) {
            for (int j = 0; j < nUmpires; j++) {
                this.bestSolution[i][j] = bestSolution[i][j];
            }
        }
    }

    public MatchPair[][] getBestSolution() {
        return bestSolution;
    }

    public void setBestWeight(int bestWeight) {
        this.bestWeight = bestWeight;
    }

    public int getBestWeight() {
        return bestWeight;
    }
    public synchronized int getLowerboundPerRound(int round){
        return lowerboundPerRound[round];
    }
    public synchronized void setLowerboundPerRound(int round, int value){
        lowerboundPerRound[round] = value;
    }
    public void initLowerboundPerRound(){
        lowerboundPerRound = new int[nRounds];
        for (int i = 0; i < nRounds; i++){
            lowerboundPerRound[i] = 0;
        }
    }
    public synchronized void setIsComplete(boolean value){
        isComplete = value;
    }
    public synchronized boolean getIsComplete(){
        return isComplete;
    }

}
