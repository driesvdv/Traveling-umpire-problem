# Traveling-umpire-problem
This project created by Dries Van de Velde and Maxime Rombaut provides solutions for some TUP problems.

## Implementation
The program implements a branch-and-bound algorithm to find the optimal umpire assignments. To speed up this proces, pruning techniques are applied.
Lower bounds are created by first using an implementation of the Hungarian algorithm and later it uses a smaller instance of the branch-and-bound algorithm. To further increase the lower bound, partial matching is applied because the lowerbounds don't take the remaining matches in the round into consideration.
## Running the program
The program requires three parameters to run properly:
* inputfile ("umps8.txt","umps10.txt",...)
* q1 value
* q2 value
## Output
If a solution is found, it is given in the following structure: <br>
1 2 5 3 8 4 7 2 6 4 3 8 1 4 <br>
6 4 3 5 7 8 4 1 3 8 2 7 5 6 <br>
7 1 2 6 3 7 2 5 4 6 8 1 7 5 <br>
4 5 1 8 6 2 1 3 5 2 7 3 6 8 <br>
The first row represents the home venues visited by the first umpire, the second for the second umpire and so on.


