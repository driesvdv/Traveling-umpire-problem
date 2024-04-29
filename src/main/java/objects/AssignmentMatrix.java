package objects;

import data.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import java.util.List;

public class AssignmentMatrix {
    private int nRounds;
    private int nUmpires;
    private int nTeams;
    private int q1;
    private int q2;
    private int n;

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

    public AssignmentMatrix(Instance instance) {
        q1 = 7;
        q2 = 2;
        nRounds = instance.getnTeams() * 2 - 2;

        nUmpires = instance.getnTeams() / 2;
        nTeams = instance.getnTeams();
        n = nTeams/2;

        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[nTeams][nTeams];
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

//    private void initTranslationMatrix(Instance inst) {
//        for (int i = 0; i < nRounds; i++) {
//            //int[] controleMatrix = new int[teams];
//            int counter = 0;
//            for (int j = 0; j < nTeams; j++) {
//                int team1 = inst.getOpponents()[i][j];
//                if (team1 > 0){
//                    int team2 = inst.getOpponents()[i][Math.abs(team1)-1];
//                    var test = new MatchPair(team1, team2);
//                    translationMatrix[i][counter] = test;//tmp; //Klopt niet bij j groter dan 4
//                    counter++;
//                }
//            }
//        }
//    }
private void initTranslationMatrix(Instance inst) {
    for (int i = 0; i < nRounds; i++) {
        List<Integer> teams = new ArrayList<>();
        int counter = 0;
        for (int j = 0; j < nTeams; j++) {
            if (!teams.contains(Math.abs(inst.getOpponents()[i][j]))){
                int team1 = inst.getOpponents()[i][j];
                int team2 = inst.getOpponents()[i][Math.abs(team1)-1];
                teams.add(Math.abs(team1));
                teams.add(Math.abs(team2));
                var test = new MatchPair(team1, team2);
                translationMatrix[i][counter] = test;//tmp; //Klopt niet bij j groter dan 4
                counter++;
            }
        }
    }
    System.out.println();
}

    /**
     * Can the umpire still visit each distinct team location at least once?
     *
     * @return false if umpire can't visit all teams anymore
     * @return true if solution is valid
     */
    public boolean canUmpiresVisitAllTeams(int currentRound) {
        for (int i=0; i<nUmpires; i++) {
            boolean[] visited = new boolean[nTeams];
            for (int j=0; j<nRounds; j++) {
                int team1 = translationMatrix[j][i].getHomeTeam();
                int team2 = translationMatrix[j][i].getOutTeam();
                visited[team1 - 1] = true; // Subtract 1 because teams are 1-indexed
                //visited[team2 - 1] = true; // Subtract 1 because teams are 1-indexed // Het gaat toch om de thuislocaties bezoeken? uit team maakt dan toch niet uit?
            }

            int unvisitedTeams = (int) IntStream.range(0, nTeams).filter(x -> !visited[x]).count();

            if (nRounds - currentRound < 0) {
                throw new RuntimeException("Current round is negative");
            }

            // todo: debug this later to make sure there are no off by one errors
            if (unvisitedTeams > nRounds - currentRound) {
                return false;
            }
        }

        return true;
    }
//    public boolean canUmpiresVisitAllTeams(int currentRound) {
//        for (int i=0; i<nUmpires; i++) {
//            boolean[] visited = new boolean[nTeams];
//            for (int j=0; j<currentRound; j++) {
//                int team1 = solutionMatrix[j][i].getHomeTeam();
//                visited[team1 - 1] = true; // Subtract 1 because teams are 1-indexed
//            }
//
//            int unvisitedTeams = (int) IntStream.range(0, nTeams).filter(x -> !visited[x]).count();
//
//            // todo: debug this later to make sure there are no off by one errors
//            if (unvisitedTeams > nRounds - currentRound) {
//                return false;
//            }
//        }
//
//        return true;
//    }

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

    public int getQ1() {
        return q1;
    }

    public int getQ2() {
        return q2;
    }
    public int getDistance(int team1, int team2){
        return weightMatrix[team1][team2];
    }
}
