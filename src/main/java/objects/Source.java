package objects;

public class Source extends Node {
    private Edge [] edges;

    public Source(int numEdges) {
        this.edges = new Edge[numEdges];
    }

    public void addEdge(Edge edge) {
        for (int i = 0; i < edges.length; i++) {
            if (edges[i] == null) {
                edges[i] = edge;
                return;
            }
        }
    }

    public Edge getEdge(int index) {
        return edges[index];
    }
}
