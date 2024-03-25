package objects;

public class MatchPair {
    private int homeTeam;
    private int outTeam;

    public MatchPair(int team1, int team2) {
        this.outTeam = Math.min(team1, team2);
        this.homeTeam = Math.max(team1, team2);
    }

    public int getHomeTeam() {
        return homeTeam;
    }

    public int getOutTeam() {
        return outTeam;
    }
}
