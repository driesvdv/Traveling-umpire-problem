package org.example;

import data.Instance;
import objects.Edge;
import objects.Node;
import objects.Source;

public class Main {
    public static void main(String[] args) {
        Instance instance = new Instance();

        Source src = new Source(instance.getnTeams()/2);


        for(int i = 0; i < instance.getnTeams()/2; i++){
            var edge = new Edge(src, new Node(), 0);
            edge.getTarget().setIncomingEdge(edge);
            src.addEdge(edge);
        }

        System.out.println("test");
    }
}