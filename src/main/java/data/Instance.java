package data;

import java.io.*;

public class Instance {
    private int nTeams;
    private int[][] dist;
    private int[][] opponents;

    public Instance(String instanceFile) {
        //String fileName = "../src/instances/umps12.txt"; // Change this to your file path
        String fileName = instanceFile; // Change this to your file path
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Read nTeams
            br.readLine(); // Skip first line
            line = br.readLine();
            String str = line.split("=")[1];
            nTeams = Integer.parseInt(str.substring(0,str.length()-1));
            //nTeams = Integer.parseInt(line.split("=")[1]);

            // Initialize arrays
            dist = new int[nTeams][nTeams];
            opponents = new int[nTeams * 2 - 2][nTeams];

            br.readLine(); // Skip line
            br.readLine(); // Skip line
            // Read dist matrix
            for (int i = 0; i < nTeams; i++) {
                line = br.readLine();
                line = line.replace("[", "").replace("]", "").trim();
                String[] values = line.trim().split("\\s+");
                for (int j = 0; j < nTeams; j++) {
                    dist[i][j] = Integer.parseInt(values[j]);
                }
            }

            br.readLine(); // Skip line
            br.readLine(); // Skip line
            br.readLine(); // Skip line
            // Read opponents matrix
            for (int i = 0; i < nTeams * 2 - 2; i++) {
                line = br.readLine().replace("[", "").replace("]", "").trim();
                String[] values = line.trim().replace("]", "").split("\\s+");

                for (int j = 0; j < nTeams; j++) {
                    opponents[i][j] = Integer.parseInt(values[j]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getnTeams() {
        return nTeams;
    }

    public int getDist(int i, int j) {
        return dist[i][j];
    }

    public int[][] getOpponents() {
        return opponents;
    }
}
