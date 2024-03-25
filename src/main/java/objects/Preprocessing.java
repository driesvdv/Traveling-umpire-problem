package objects;

public class Preprocessing {
    private int[][] opponents;
    private int q1;
    private int q2;
    private int[][] isFeasible;
    private int[][] feasibleMatches;
    //private PreprocessedMatchPairs[][] matchPairs;
    private MatchPair[][] matchPairs;

    public Preprocessing(int[][] opponents, int q1, int q2, MatchPair[][] inputMatchpairs){
        this.opponents = opponents;
        this.q1 = q1;
        this.q2 = q2;
        this.matchPairs = inputMatchpairs;

    }
    //Only look at the next round, looking at further rounds would require us to know which umpire follows this path.
    public void preProcessQ1(){
        for (int i = 0; i < matchPairs.length -1; i++){
            for (int j = 0; j < matchPairs[i].length;j++){
                for (int k = 0; k < matchPairs[i+1].length; k++) {
                    if (matchPairs[i][j].getHomeTeam() != matchPairs[i+1][k].getHomeTeam()){
                        matchPairs[i][j].addFeasibleChildMatch(matchPairs[i+1][k]);
                    }
                }
            }
        }
    }

    public static boolean contains(int[] arr, int value) {
        for (int num : arr) {
            if (num == value) {
                return true;
            }
        }
        return false;
    }

}
