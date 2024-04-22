package org.example;

import algorithms.BranchAndBound;
import data.Instance;
import objects.AssignmentMatrix;

public class Main {
    public static void main(String[] args) {
        Instance instance = new Instance();
        AssignmentMatrix assignmentMatrix = new AssignmentMatrix(instance);
//        Preprocessing p = new Preprocessing(instance.getOpponents(), 1,3, assignmentMatrix.getTranslationMatrix());
//        p.preProcessQ1andQ2();
        //assignmentMatrix.setTranslationMatrix(p.getMatchPairs());
        BranchAndBound branchAndBound = new BranchAndBound(assignmentMatrix);
        branchAndBound.executeBranchAndBound();

        System.out.println("debug");
    }
}