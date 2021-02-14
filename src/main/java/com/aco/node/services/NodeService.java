package com.aco.node.services;

import com.aco.node.domain.NodeBE;
import com.aco.node.exception.BadRequestException;
import com.aco.node.exception.DataNotFoundException;
import com.aco.node.models.Node;
import com.aco.node.repositories.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public List<Node> getDescendants(Long id) {
        return nodeRepository.findById(id)
                .map(node -> node.getDescendantsStream(node))
                .map(descendantsStream -> descendantsStream
                        .map(child -> Node.builder()
                                .nodeId(child.getId())
                                .parentNodeId(child.getParent().getId())
                                .rootNodeId(child.getRoot().getId())
                                .height(child.getHeight())
                                .build())
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new DataNotFoundException("Node not found"));
    }

    public void updateParent(Long nodeId, Long parentId) {
        NodeBE node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new DataNotFoundException("Node not found"));
        NodeBE newParent = nodeRepository.findById(parentId)
                .orElseThrow(() -> new DataNotFoundException("Parent node not found"));

        if (newParent.isChildPresent(node))
            throw new BadRequestException("Node is already a child");

        if (node.isRootNode())
            throw new BadRequestException("Root node can not be updated");

        newParent.addChild(node);
        nodeRepository.save(newParent);
    }
}
