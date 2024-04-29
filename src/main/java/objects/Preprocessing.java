package objects;

public class Preprocessing {
    private int[][] opponents;
    private int q1;
    private int q2;
    private MatchPair[][] matchPairs;

    public Preprocessing(int[][] opponents, int q1, int q2, MatchPair[][] inputMatchpairs){
        this.opponents = opponents;
        this.q1 = q1;
        this.q2 = q2;
        this.matchPairs = inputMatchpairs;

    }
    //Only look at the next round, looking at further rounds would require us to know which umpire follows this path.
    public void preProcessQ1andQ2(){
        for (int i = 0; i < matchPairs.length -1; i++){
            for (int j = 0; j < matchPairs[i].length;j++){
                for (int k = 0; k < matchPairs[i+1].length; k++) {
                    int isFeasibleMatch = 0;
                    if (q1 > 1){
                        if (matchPairs[i][j].getHomeTeam() != matchPairs[i+1][k].getHomeTeam()){
                            isFeasibleMatch += 1;
                            //matchPairs[i][j].addFeasibleChildMatch(matchPairs[i+1][k]);
                        }
                    }
                    else{
                        isFeasibleMatch += 1;
                    }
                    if (q2 > 1){
                        if (checkMatchesForSameTeams(matchPairs[i][j], matchPairs[i+1][k])){
                            isFeasibleMatch +=1;
                            //matchPairs[i][j].addFeasibleChildMatch(matchPairs[i+1][k]);
                        }
                    }
                    else{
                        isFeasibleMatch += 1;
                    }
                    if (isFeasibleMatch == 2){
                        matchPairs[i][j].addFeasibleChildMatch(matchPairs[i+1][k]);
                    }
                }
            }
        }
    }
    public boolean checkMatchesForSameTeams(MatchPair m1, MatchPair m2){
        if (m1.getHomeTeam() == m2.getHomeTeam() || m1.getHomeTeam() == m2.getOutTeam()){
            return false;
        }
        if (m1.getOutTeam() == m2.getHomeTeam() || m1.getOutTeam() == m2.getOutTeam()){
            return false;
        }
        return true;
    }
    public MatchPair[][] getMatchPairs(){
        return this.matchPairs;
    }
}
