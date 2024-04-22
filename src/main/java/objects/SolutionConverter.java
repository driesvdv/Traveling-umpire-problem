package objects;

public class SolutionConverter {
    MatchPair[][] solutionMatrix;
    MatchPair[][] originalMatrix;
    public SolutionConverter(MatchPair[][] solutionMatrix, MatchPair[][] originalMatrix){
        this.solutionMatrix = solutionMatrix;
        this.originalMatrix = originalMatrix;
    }
    public String convertSolutionMatrixSingleLine(){
        String solutionString = "";
        for (int i = 0; i < solutionMatrix.length; i++){
            for (int j = 0; j < solutionMatrix[i].length; j++){
                for (int k = 0; k < solutionMatrix[i].length; k++){
                    if (solutionMatrix[i][j] == originalMatrix[i][k]){
                        solutionString += k+1 + ",";
                    }
                }
            }
        }
        return solutionString;
    }

    public String[] convertSolutionMatrixMultipleLines(){
        String[] solutionString = new String[solutionMatrix[0].length];
        for (int i = 0; i < solutionString.length; i++){
            solutionString[i] = "";
        }
        for (int i = 0; i < solutionMatrix.length; i++){
            for (int j = 0; j < solutionMatrix[i].length; j++){
                solutionString[j] += solutionMatrix[i][j].getHomeTeam() + " " ;
            }
        }
        return solutionString;
    }

    public void printSolution(String[] solution){
        for (String s : solution){
            System.out.println(s);
        }
    }


}
