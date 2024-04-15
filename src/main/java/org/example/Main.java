package org.example;

import data.Instance;
import objects.AssignmentMatrix;
import objects.Preprocessing;

public class Main {
    public static void main(String[] args) {
        Instance instance = new Instance();
        AssignmentMatrix assignmentMatrix = new AssignmentMatrix(instance);
        Preprocessing p = new Preprocessing(instance.getOpponents(), 1,3, assignmentMatrix.getTranslationMatrix());
        p.preProcessQ1andQ2();




    }

}