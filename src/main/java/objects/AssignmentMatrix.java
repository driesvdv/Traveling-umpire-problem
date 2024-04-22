package objects;

import data.Instance;
import java.util.Arrays;
import java.util.stream.IntStream;

public class AssignmentMatrix {
    private int nRounds;
    private int nUmpires;
    private int nTeams;

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

    private MatchPair[][] solutionMatrix;

    public AssignmentMatrix(Instance instance) {
        nRounds = instance.getnTeams() * 2 - 2;
        nUmpires = instance.getnTeams() / 2;
        nTeams = instance.getnTeams();

        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[nTeams][nTeams];
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
        for (int i = 0; i < nTeams; i++) {
            for (int j = 0; j < nTeams; j++) {
                weightMatrix[i][j] = inst.getDist(i, j);
            }
        }
    }

    private void initTranslationMatrix(Instance inst) {
        for (int i = 0; i < nRounds; i++) {
            int counter = 0;
            for (int j = 0; j < nTeams; j++) {
                int team1 = inst.getOpponents()[i][j];
                if (team1 > 0) {
                    int team2 = inst.getOpponents()[i][Math.abs(team1) - 1];
                    var test = new MatchPair(team1, team2);
                    translationMatrix[i][counter] = test;
                    counter++;
                }
            }
        }
    }

    /**
     * Can the umpire still visit each distinct team location at last once?
     * If not return the amount of iterations to backtrack
     * 
     * @return false if umpire can't visit all teams anymore
     * @return true if solution is valid
     */
    public boolean canUmpiresVisitAllTeams() {
        // Check so that all teams are visited at least once by an umpire

        for (int i=0; i<nUmpires; i++) {
            boolean[] visited = new boolean[nTeams];
            for (int j=0; j<nRounds; j++) {
                int team1 = translationMatrix[j][i].getHomeTeam();
                int team2 = translationMatrix[j][i].getOutTeam();
                visited[team1] = true;
                visited[team2] = true;
            }
            if (IntStream.range(0, visited.length).anyMatch(x -> !visited[x])) {
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
}
