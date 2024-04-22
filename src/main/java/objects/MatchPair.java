package objects;


import java.util.ArrayList;
import java.util.List;

public class MatchPair {
    private final int homeTeam;
    private final int outTeam;
    private List<MatchPair> feasibleChildren;


    public MatchPair(int team1, int team2) {
        if (team1 < 0 || team2 < 0) {
            homeTeam = Math.abs(Math.min(team1, team2));
            outTeam = Math.max(team1, team2);
        } else {
            homeTeam = team1;
            outTeam = team2;
        }
        feasibleChildren = new ArrayList<>();
    }

    public int getHomeTeam() {
        return homeTeam;
    }

    public int getOutTeam() {
        return outTeam;
    }

    public String toString() {
        return "(" + homeTeam + ", " + outTeam + ")";
    }
    public void addFeasibleChildMatch(MatchPair mp){
        feasibleChildren.add(mp);
    }
    public List<MatchPair> getFeasibleChildren(){
        return new ArrayList<>(feasibleChildren);
    }
}
