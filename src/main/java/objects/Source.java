package objects;

public class Source {
    private Edge [] edges;

    public Source(Edge[] edges) {
        this.edges = edges;
    }

    public Edge getEdge(int index) {
        return edges[index];
    }
}
