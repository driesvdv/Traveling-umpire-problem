package objects;

public class Node {
    private Edge source;
    private Edge target;
    private int weight;

    public Node(Edge source, Edge target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Edge getSource() {
        return source;
    }

    public Edge getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

    public void setSource(Edge source) {
        this.source = source;
    }

    public void setTarget(Edge target) {
        this.target = target;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
