package dev.pulceo.pna.model.ping;

public enum IPVersion {
    IPv4(4), IPv6(6);

    public final int label;

    private IPVersion(int label) {
        this.label = label;
    }
}
