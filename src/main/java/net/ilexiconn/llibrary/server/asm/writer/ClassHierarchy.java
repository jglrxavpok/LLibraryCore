package net.ilexiconn.llibrary.server.asm.writer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

public class ClassHierarchy {
    private final Node root;

    ClassHierarchy(Node root) {
        this.root = root;
    }

    public static ClassHierarchy build(Class<?> type) {
        return new ClassHierarchy(Node.of(type));
    }

    public static ClassHierarchy build(String type, ClassFetcher classFetcher) throws IOException {
        return new ClassHierarchy(Node.of(type, classFetcher));
    }

    private static ClassNode read(String type, ClassFetcher fetcher) throws IOException {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(fetcher.fetch(type));
        reader.accept(node, 0);
        return node;
    }

    public String findCommon(ClassHierarchy other) {
        if (other.root.instanceOf(this.root)) {
            return this.root.type;
        } else if (this.root.instanceOf(other.root)) {
            return other.root.type;
        } else if (this.root.isInterface || other.root.isInterface) {
            return "java.lang.Object";
        }

        Node node = this.root;
        do {
            node = node.getSuper();
            if (node == null) {
                return "java.lang.Object";
            }
        } while (!other.root.instanceOf(node));

        return node.type;
    }

    public static class Node {
        private final String type;
        private final boolean isInterface;

        private final Collection<Node> parents = new HashSet<>();
        private Node superNode;

        Node(String type, boolean isInterface) {
            this.type = type;
            this.isInterface = isInterface;
        }

        public static Node of(Class<?> type) {
            Node node = new Node(type.getName(), type.isInterface());

            Class<?> superclass = type.getSuperclass();
            if (superclass != null) {
                node.add(Node.of(superclass));
            }

            Class<?>[] interfaces = type.getInterfaces();
            for (Class<?> interfaceType : interfaces) {
                node.add(Node.of(interfaceType));
            }

            return node;
        }

        public static Node of(String type, ClassFetcher fetcher) throws IOException {
            ClassNode classNode = read(type, fetcher);
            Node node = new Node(type, Modifier.isInterface(classNode.access));

            if (classNode.superName != null) {
                node.add(Node.of(classNode.superName.replace('/', '.'), fetcher));
            }

            if (classNode.interfaces != null) {
                for (String interfaceType : classNode.interfaces) {
                    node.add(Node.of(interfaceType.replace('/', '.'), fetcher));
                }
            }

            return node;
        }

        public void add(Node parent) {
            if (!parent.isInterface) {
                this.offerSuper(parent);
            }
            this.parents.add(parent);
        }

        private void offerSuper(Node parent) {
            if (this.superNode != null) {
                throw new IllegalStateException("Node cannot have more than one superclass!");
            }
            this.superNode = parent;
        }

        public Node getSuper() {
            return this.superNode;
        }

        public boolean instanceOf(Node other) {
            if (this.parents.contains(other)) {
                return true;
            }
            return this.parents.stream().anyMatch(p -> p.instanceOf(other));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node) {
                return ((Node) obj).type.equals(this.type);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.type.hashCode();
        }
    }

    public interface ClassFetcher {
        byte[] fetch(String name) throws IOException;
    }
}
