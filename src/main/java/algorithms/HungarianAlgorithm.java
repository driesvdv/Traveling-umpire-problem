package algorithms;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


class HungarianAlgorithm {

    final private int[][] costMatrix;
    final private int[][] assignments;
    final private int[] rowAssignments;
    final private int[] colAssignments;
    final private int nRows;
    final private int nCols;
    final private boolean[] coveredRows;
    final private boolean[] coveredCols;
    final private int[] starredRows;
    final private int[] starredCols;
    final private int[] primedRows;
    final private int[] primedCols;
    private int numberCoveredCols;
    final private boolean transposed;


    public HungarianAlgorithm(int[][] costMatrix) {
        checkMatrixValidity(costMatrix);
        if (costMatrix.length > costMatrix[0].length){
            //flip matrix to have more columns than rows
            transposed = true;
            nRows = costMatrix[0].length;
            nCols = costMatrix.length;
            this.costMatrix = new int[nRows][nCols];
            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    this.costMatrix[i][j] = costMatrix[j][i];
                }
            }
        } else {
            this.costMatrix = costMatrix;
            nRows = costMatrix.length;
            nCols = costMatrix[0].length;
            transposed = false;
        }
        assignments = new int[nRows][2];
        rowAssignments = new int[transposed ? nCols : nRows];
        colAssignments = new int[transposed ? nRows : nCols];
        coveredRows = new boolean[nRows];
        coveredCols = new boolean[nCols];
        starredRows = new int[nRows];
        starredCols = new int[nCols];
        primedRows = new int[nRows];
        primedCols = new int[nCols];
        Arrays.fill(starredRows, -1);
        Arrays.fill(starredCols, -1);
        Arrays.fill(primedRows, -1);
        Arrays.fill(primedCols, -1);
        Arrays.fill(rowAssignments, -1);
        Arrays.fill(colAssignments, -1);
        for (int[] assignment : assignments) {
            Arrays.fill(assignment, -1);
        }
    }

    public static HungarianAlgorithm initialise(int[][] costMatrix) {
        HungarianAlgorithm result = new HungarianAlgorithm(costMatrix);
        result.reduceInitialMatrix();
        result.solveReducedMatrix();
        return result;
    }

    /**
     * Returns the column index assigned to each row.
     * @return The result of the assignment problem from the row perspective.
     * The i-th element of the output is the index of the column assigned to the
     * i-th row, or -1 if the row has not been assigned.
     */
    public int[] getRowAssignments() {
        return this.rowAssignments;
    }

    public int[] getColumnAssignemnts() {
        return this.colAssignments;
    }

    /**
     * Returns the pairs of row and column indices of the assignments.
     * @return The result of the assignment problem as pairs. Each element of
     * the output is an assigned pair whose first element is the index of the
     * row and the second element is the index of the column. Unassigned rows
     * and columns are not included.
     */
    public int[][] getAssignments() {
        return this.assignments;
    }

    /**
     * Reduces the values of the matrix to make zeroes appear. This
     * corresponds to the first step of the Hungarian Algorithm.
     */
    void reduceInitialMatrix() {
        //first part: reduce all rows
        for (int[] row : costMatrix) {
            int min = row[0];
            for (int val : row) {
                if (val < min) {
                    min = val;
                }
            }
            for (int j = 0; j < row.length; j++) {
                row[j] -= min;
            }
        }
        //second part: reduce all columns
        for (int j = 0; j < nCols; j++) {
            int min = costMatrix[0][j];
            for (int[] row : costMatrix) {
                if (row[j] < min) {
                    min = row[j];
                }
            }
            for (int[] row : costMatrix) {
                row[j] -= min;
            }
        }
    }

    /**
     * Performs the main loop of the Hungarian algorithm.
     */
    public void solveReducedMatrix() {
        //Steps 0 and 1 have been preprocessed
        //Step 2 : initial zero starring
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                if (costMatrix[i][j] == 0 && starredCols[j] == -1) {
                    coveredCols[j] = true;
                    numberCoveredCols++;
                    starredRows[i] = j;
                    starredCols[j] = i;
                    break;
                }
            }
        }
        while (numberCoveredCols < nRows) {
            int[] position = primeZero();
            while (position == null){
                //Perform step 6
                //Get minimal unmarked value
                int min = Integer.MAX_VALUE;
                for (int i = 0; i < nRows; i++) {
                    if (coveredRows[i]) {
                        continue;
                    }
                    for (int j = 0; j < nCols; j++) {
                        if (coveredCols[j]) {
                            continue;
                        }
                        if (costMatrix[i][j] < min) {
                            min = costMatrix[i][j];
                            if (min == 1){
                                break;
                            }
                        }
                        if (min == 1){
                            break;
                        }
                    }
                }
                //modify the matrix
                for (int i = 0; i < nRows; i++) {
                    for (int j = 0; j < nCols; j++) {
                        if (!coveredRows[i]) {
                            /* If the row is uncovered and the column is covered,
                        then it's a no-op: add and subtract the same value.
                             */
                            if (!coveredCols[j]) {
                                costMatrix[i][j] -= min;
                            }
                        } else if (coveredCols[j]) {
                            costMatrix[i][j] += min;
                        }
                    }
                }
                //go back to step 4
                position = primeZero();
            }
            //perform step 5
            invertPrimedAndStarred(position);
        }
        //format the result
        int assignmentIndex = 0;
        if (transposed){
            for (int i = 0; i < nCols; i++){
                rowAssignments[i] = starredCols[i];
                if (starredCols[i] != -1){
                    assignments[assignmentIndex][0] = starredCols[i];
                    assignments[assignmentIndex][1] = i;
                    assignmentIndex++;
                }
            }
            System.arraycopy(starredRows, 0, colAssignments, 0, nRows);
        } else {
            for (int i = 0; i < nRows; i++){
                rowAssignments[i] = starredRows[i];
                if (starredRows[i] != -1) {
                    assignments[assignmentIndex][0] = i;
                    assignments[assignmentIndex][1] = starredRows[i];
                    assignmentIndex++;
                }
            }
            System.arraycopy(starredCols, 0, colAssignments, 0, nCols);
        }
    }

    /**
     * Primes uncovered zeroes in the cost matrix.
     * Performs the fourth step of the Hungarian Algorithm.
     * @return the (rowIndex,colIndex) coordinates of the primed zero to star
     * that has been found, or null if no such zero has been found.
     */
    private int[] primeZero() {
        Queue<Integer> uncoveredColumnQueue = new LinkedList<>();
        for (int i = 0; i < nRows; i++) {
            if (coveredRows[i]) {
                continue;
            }
            for (int j = 0; j < nCols; j++) {
                if (coveredCols[j] || costMatrix[i][j] > 0) {
                    continue;
                }
                //Found a non-covered zero
                primedRows[i] = j;
                primedCols[j] = i;
                if (starredRows[i] == -1) {
                    return new int[]{i,j};
                } else {
                    coveredRows[i] = true;
                    coveredCols[starredRows[i]] = false;
                    numberCoveredCols -= 1;
                    //ignore the rest of the row but handle the uncovered column
                    uncoveredColumnQueue.add(starredRows[i]);
                    break;
                }
            }
        }
        while (!uncoveredColumnQueue.isEmpty()){
            int j = uncoveredColumnQueue.remove();
            for (int i = 0; i < nRows; i++){
                if(coveredRows[i] || costMatrix[i][j] > 0) {
                    continue;
                }
                primedRows[i] = j;
                primedCols[j] = i;
                if (starredRows[i] == -1){
                    return new int[]{i,j};
                } else {
                    coveredRows[i] = true;
                    coveredCols[starredRows[i]] = false;
                    numberCoveredCols -= 1;
                    uncoveredColumnQueue.add(starredRows[i]);
                }
            }
        }
        return null;
    }

    /**
     * Stars selected primed zeroes to increase the line coverage of the matrix.
     * Performs the fifth step of the Hungarian Algorithm.
     * @param position array of size 2 containing the row and column indices of
     * the first primed zero in the alternating series to modify.
     */
    private void invertPrimedAndStarred(int[] position){
        int currentRow = position[0];
        int currentCol = position[1];
        int tmp;
        starredRows[currentRow] = currentCol;
        while (starredCols[currentCol] != -1){
            //Move star to its new row in the column of the primed zero
            tmp = starredCols[currentCol];
            starredCols[currentCol] = currentRow;
            currentRow = tmp;
            //Move star to its new column in the column of the previously
            //starred zero
            tmp = primedRows[currentRow];
            starredRows[currentRow] = tmp;
            currentCol = tmp;
        }
        //set starredCols of last changed zero and reset primes and lines covering
        starredCols[currentCol] = currentRow;
        for (int i = 0; i < coveredRows.length; i++){
            coveredRows[i] = false;
            primedRows[i] = -1;
        }
        //in next step, all columns containing a starred zero will be marked
        //--> do it right away
        for (int j = 0; j < nCols; j++){
            if(!coveredCols[j] && starredCols[j] != -1){
                numberCoveredCols++;
                coveredCols[j] = true;
            }
            //if a column contained a prime zero, it will still contain one
            //after the inversion, so the case where a column needs to be
            //uncovered does not arise
            primedCols[j] = -1;
        }
    }

    /**
     * @return The internal state of the cost matrix.
     */
    int[][] getState() {
        return this.costMatrix;
    }

    /**
     * Checks the validity of the input cost matrix.
     * @param costMatrix the matrix to solve.
     * @throws IllegalArgumentException if {@code costMatrix } is not
     * rectangular (e.g. rows do not all have the same length).
     */
    static void checkMatrixValidity(int[][] costMatrix)
            throws IllegalArgumentException{
        if (costMatrix == null){
            throw new IllegalArgumentException("input matrix was null");
        }
        if (costMatrix.length == 0){
            throw new IllegalArgumentException("input matrix was of length 0");
        }
        for (int[] row : costMatrix){
            if (row.length != costMatrix[0].length){
                throw new IllegalArgumentException("input matrix was not rectangular");
            }
        }
    }
}