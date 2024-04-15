package objects;

import data.Instance;

public class AssignmentMatrix {
    private int nRounds;
    private int nUmpires;
    private int teams;

    /**
     * The assignment matrix is a 2D array that represents the assignment of umpires to games.
     * The rows represent the rounds and the columns represent the umpires.
     * Each cell contains an integer which maps to the teams playing the match. These teams can be found in the translation matrix.
     */
    private int[][] assignmentMatrix;
    /**
     * The weight matrix is a 2D array that represents the distance between teams.
     */
    private int[][] weightMatrix;
    private MatchPair[][] translationMatrix;

    public AssignmentMatrix(Instance instance) {
        nRounds = instance.getnTeams() * 2 - 2;
        nUmpires = instance.getnTeams()/2;
        teams = instance.getnTeams();

        assignmentMatrix = new int[nRounds][nUmpires];
        weightMatrix = new int[teams][teams];
        translationMatrix = new MatchPair[nRounds][nUmpires];

        initAssignMentMatrix();
        initWeightMatrix(instance);
        initTranslationMatrix(instance);

        System.out.println("debug");
    }

    private void initAssignMentMatrix(){
        for (int i = 0; i < nRounds; i++) {
            for (int j = 0; j < nUmpires; j++) {
                if (i == 0) {
                    // Fix the first round for symmetry breaking
                    assignmentMatrix[i][j] = j;
                }
                else {
                    // Initialize the rest of the matrix with -1 (no assignment)
                    assignmentMatrix[i][j] = -1;
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

    //Zit hier geen fout in? De teams kloppen niet
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
}
