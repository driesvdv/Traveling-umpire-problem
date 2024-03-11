package objects;

public class Node {
    private Edge incomingEdge;
    private Edge outgoingEdge;
    private int weight;

    public Node(){}

    public Edge getIncomingEdge() {
        return incomingEdge;
    }

    public Edge getOutgoingEdge() {
        return outgoingEdge;
    }

    public int getWeight() {
        return weight;
    }

    public void setIncomingEdge(Edge incomingEdge) {
        this.incomingEdge = incomingEdge;
    }

    public void setOutgoingEdge(Edge outgoingEdge) {
        this.outgoingEdge = outgoingEdge;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
