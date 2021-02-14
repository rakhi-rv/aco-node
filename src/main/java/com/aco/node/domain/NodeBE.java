package com.aco.node.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Setter
@Entity(name = "node")
public class NodeBE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent", referencedColumnName = "id")
    private NodeBE parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<NodeBE> children;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root", referencedColumnName = "id")
    private NodeBE root;

    private long height;

    public NodeBE() {
        this.children = new ArrayList<>();
    }

    public void setChildren(List<NodeBE> children) {
        this.children = children;
        children.forEach(child -> {
            child.setRoot(this.getRoot());
            child.setParent(this);
        });
    }

    public void setParent(NodeBE parent) {
        this.parent = parent;
        this.setHeight(parent.getHeight() + 1);
    }

    public void setHeight(long height) {
        this.height = height;
        this.getChildren().forEach(child -> child.setHeight(height + 1));
    }

    public Stream<NodeBE> getDescendantsStream(NodeBE nodeBE) {
        return nodeBE.isLeaf() ? Stream.empty() : Stream.concat(nodeBE.getChildrenStream(), nodeBE.getChildrenStream().flatMap(this::getDescendantsStream));
    }

    public Stream<NodeBE> getChildrenStream() {
        return this.getChildren().stream();
    }

    public boolean isLeaf() {
        return CollectionUtils.isEmpty(this.children);
    }

    public void addChild(NodeBE child) {
        child.setParent(this);
        child.setRoot(this.getRoot());
        this.children.add(child);
    }

    public boolean isChildPresent(NodeBE node) {
        return this.children.stream().anyMatch(child -> child.getId().equals(node.getId()));
    }

    public boolean isRootNode() {
        return Optional.ofNullable(this.getParent()).isEmpty();
    }
}
