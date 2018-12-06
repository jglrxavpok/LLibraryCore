package net.ilexiconn.llibrary.server.asm.writer;

import java.util.Collection;
import java.util.HashSet;

public class HierarchyNode {
    private final String type;
    private final boolean isInterface;

    private final Collection<HierarchyNode> parents = new HashSet<>();
    private HierarchyNode superNode;

    HierarchyNode(String type, boolean isInterface) {
        this.type = type;
        this.isInterface = isInterface;
    }

    public void add(HierarchyNode parent) {
        if (!parent.isInterface) {
            this.offerSuper(parent);
        }
        this.parents.add(parent);
    }

    private void offerSuper(HierarchyNode parent) {
        if (this.superNode != null) {
            throw new IllegalStateException("Node cannot have more than one superclass!");
        }
        this.superNode = parent;
    }

    public String getType() {
        return this.type;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    public HierarchyNode getSuper() {
        return this.superNode;
    }

    public boolean instanceOf(HierarchyNode other) {
        if (this.parents.contains(other)) {
            return true;
        }
        return this.parents.stream().anyMatch(p -> p.instanceOf(other));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HierarchyNode) {
            return ((HierarchyNode) obj).type.equals(this.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
}
