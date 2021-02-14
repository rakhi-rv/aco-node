package com.aco.node.startup;

import com.aco.node.domain.NodeBE;
import com.aco.node.repositories.NodeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Component
@AllArgsConstructor
public class InitialDataLoader {

    private final NodeRepository nodeRepository;

    @PostConstruct
    public void init() {
        log.info("Setting up initial tree data");
        NodeBE root = new NodeBE();
        root.setRoot(root);

        NodeBE node1 = new NodeBE();
        NodeBE node2 = new NodeBE();
        NodeBE node3 = new NodeBE();

        root.setChildren(Arrays.asList(node1, node2));
        node1.setChildren(Collections.singletonList(node3));
        nodeRepository.save(root);
    }
}
