package objects;

public class Sink {
    private Edge [] edges;

    public Sink(int numEdges) {
        this.edges = new Edge[numEdges];
    }

    public boolean isFeasible() {
        for (Edge edge : edges) {
            if (edge == null) {
                return false;
            }
        }
        return true;
    }

    public Edge getEdge(int index) {
        return edges[index];
    }
}
