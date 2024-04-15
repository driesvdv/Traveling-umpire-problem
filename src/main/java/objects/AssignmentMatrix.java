package objects;

import data.Instance;
import jdk.jshell.spi.ExecutionControl;

import java.util.Arrays;

public class AssignmentMatrix {
    private int nRounds;
    private int nUmpires;
    private int teams;

    /**
     * The assignment matrix is a 2D array that represents the assignment of umpires
     * to games.
     * The rows represent the rounds and the columns represent the umpires.
     * Each cell contains an integer which maps to the teams playing the match.
     * These teams can be found in the translation matrix.
     */
    private int[][] assignmentMatrix;
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
        nRounds = instance.getnTeams() * 2 - 2;
        nUmpires = instance.getnTeams() / 2;
        teams = instance.getnTeams();

        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[teams][teams];
        translationMatrix = new MatchPair[nRounds][nUmpires];

        initAssignMentMatrix();
        initWeightMatrix(instance);
        initTranslationMatrix(instance);

        System.out.println("debug");
    }

    private void initAssignMentMatrix() {
        for (int i = 0; i < nRounds; i++) {
            for (int j = 0; j < nUmpires; j++) {
                if (i == 0) {
                    // Fix the first round for symmetry breaking
                    assignmentMatrix[i][j] = j;
                } else {
                    // Initialize the rest of the matrix with -1 (no assignment)
                    assignmentMatrix[i][j] = -1;
                }
            }
        }
    }

    private void initWeightMatrix(Instance inst) {
        for (int i = 0; i < teams; i++) {
            for (int j = 0; j < teams; j++) {
                weightMatrix[i][j] = inst.getDist(i, j);
            }
        }
    }

    private void initTranslationMatrix(Instance inst){
        for (int i = 0; i < nRounds; i++) {
            int counter = 0;
            for (int j = 0; j < teams; j++) {
                int team1 = inst.getOpponents()[i][j];
                if (team1 > 0){
                    int team2 = inst.getOpponents()[i][Math.abs(team1)-1];
                    var test = new MatchPair(team1, team2);
                    translationMatrix[i][counter] = test;
                    counter++;
                }
            }
        }
    }

    /**
     * Returns true if all team locations have been visited by all the umpires
     * Every umpire should visit each team at least once
     *
     * @return boolean
     */
    public boolean haveUmpiresVisitedAllTeams() {
        // There should be nTeams distinct values in each column
        for (int[] row : assignmentMatrix) {
            if (Arrays.stream(row).distinct().count() != teams) {
                return false;
            }
        }

        return true;
    }

    /**
     * Can the umpire still visit each distinct team at last once?
     * If not return the amount of iterations to backtrack
     * 
     * @return
     */
    public int canUmpiresVisitAllTeams() {
        int[] teamCount = new int[teams];
        for (int[] round : assignmentMatrix) {
            for (int umpire : round) {
                teamCount[umpire]++;
            }
        }

        int max = Arrays.stream(teamCount).max().getAsInt();
        return max - 1;
    }

    /**
     * Returns true if all empires have only one match per round and thus no double
     * assignments
     *
     * @return boolean
     */
    public boolean haveUmpiresOneMatchPerRound() {
        // No double values per row
        for (int[] round : assignmentMatrix) {
            if (Arrays.stream(round).distinct().count() != nUmpires) {
                return false;
            }
        }

        return true;
    }
}
