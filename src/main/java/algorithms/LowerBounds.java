package algorithms;

import objects.AssignmentMatrix;

public class LowerBounds {
    private AssignmentMatrix assignmentMatrix;
    private int[][] solutionMatrix; //Matrix containing the values of solutions for the subproblems
    private int[][] lowerboundsMatrix; //Matrix containing the lower bounds for all pairs of rounds

    public LowerBounds(AssignmentMatrix assignmentMatrix) {
        this.assignmentMatrix = assignmentMatrix;
        this.solutionMatrix = new int[assignmentMatrix.getnRounds()][assignmentMatrix.getnRounds()];
        this.lowerboundsMatrix = new int[assignmentMatrix.getnRounds()][assignmentMatrix.getnRounds()];
    }

}
