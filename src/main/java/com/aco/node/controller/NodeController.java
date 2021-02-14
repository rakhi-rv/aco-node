package com.aco.node.controller;

import com.aco.node.models.Node;
import com.aco.node.services.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/")
@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    /**
     * Method to get all the direct and non-direct descendant nodes of a given node
     *
     * @param nodeId node identification
     * @return the list of descendant nodes
     */
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "/nodes/{nodeId}/descendants", produces = {"application/json"})
    public List<Node> getDescendants(@NotNull @PathVariable(value = "nodeId") Long nodeId) {
        return nodeService.getDescendants(nodeId);
    }

    /**
     * Change the parent node of a given node
     *
     * @param nodeId   node identification
     * @param parentId node identification of parent node
     */
    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "/nodes/{nodeId}/parent")
    public void updateParent(@NotNull @PathVariable(value = "nodeId") Long nodeId,
                             @NotNull @RequestParam(value = "parentId") Long parentId) {
        nodeService.updateParent(nodeId, parentId);
    }
}
