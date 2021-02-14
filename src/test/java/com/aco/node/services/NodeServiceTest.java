package com.aco.node.services;

import com.aco.node.domain.NodeBE;
import com.aco.node.exception.BadRequestException;
import com.aco.node.exception.DataNotFoundException;
import com.aco.node.models.Node;
import com.aco.node.repositories.NodeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class NodeServiceTest {

    private NodeService nodeService;

    @MockBean
    private NodeRepository nodeRepository;

    @BeforeEach
    void setUp() {
        this.nodeService = new NodeService(nodeRepository);
    }

    @AfterEach
    void tearDown() {
        this.nodeService = null;
    }

    @Test
    void getDescendants() {
        //given
        NodeBE node1 = new NodeBE();
        node1.setId(1L);
        node1.setRoot(node1);

        NodeBE node2 = new NodeBE();
        node2.setId(2L);

        NodeBE node3 = new NodeBE();
        node3.setId(3L);

        node1.setChildren(Collections.singletonList(node2));
        node2.setChildren(Collections.singletonList(node3));

        given(nodeRepository.findById(1L)).willReturn(Optional.of(node1));

        //when
        List<Node> descendants = nodeService.getDescendants(1L);

        //then
        assertEquals(2, descendants.size());
        assertEquals(2, descendants.get(0).getNodeId());
        assertEquals(1, descendants.get(0).getParentNodeId());
        assertEquals(1, descendants.get(0).getRootNodeId());
        assertEquals(1, descendants.get(0).getHeight());
        assertEquals(3, descendants.get(1).getNodeId());
        assertEquals(2, descendants.get(1).getParentNodeId());
        assertEquals(1, descendants.get(1).getRootNodeId());
        assertEquals(2, descendants.get(1).getHeight());
    }

    @Test
    void getDescendantsWhenNodeNotFoundShouldDelegateToDataNotFoundException() {
        //given
        given(nodeRepository.findById(1L)).willReturn(Optional.empty());

        //when
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> nodeService.getDescendants(1L));
        //then
        assertEquals("Node not found", exception.getMessage());
    }

    @Test
    void getDescendantsWhenNoDescendantsShouldReturnEmptyList() {
        //given
        NodeBE node1 = new NodeBE();
        node1.setId(1L);
        node1.setRoot(node1);

        given(nodeRepository.findById(1L)).willReturn(Optional.of(node1));

        //when
        List<Node> descendants = nodeService.getDescendants(1L);

        //then
        assertTrue(descendants.isEmpty());
    }

    @Test
    void updateParent() {
        //given
        NodeBE node1 = new NodeBE();
        node1.setId(1L);
        node1.setRoot(node1);

        NodeBE node2 = new NodeBE();
        node2.setId(2L);

        NodeBE node3 = new NodeBE();
        node3.setId(3L);

        List<NodeBE> children = new ArrayList<>();
        children.add(node2);
        node1.setChildren(children);
        node2.setChildren(Collections.singletonList(node3));

        given(nodeRepository.findById(1L)).willReturn(Optional.of(node1));
        given(nodeRepository.findById(3L)).willReturn(Optional.of(node3));
        given(nodeRepository.save(node1)).willReturn(node1);

        //when
        nodeService.updateParent(3L, 1L);

        //then
        verify(nodeRepository).save(node1);
    }

    @Test
    void updateParentWhenNodeNotFoundShouldDelegateToDataNotFoundException() {
        //given
        NodeBE node1 = new NodeBE();
        node1.setId(1L);
        node1.setRoot(node1);

        NodeBE node2 = new NodeBE();
        node2.setId(2L);

        node1.setChildren(Collections.singletonList(node2));

        given(nodeRepository.findById(1L)).willReturn(Optional.of(node1));
        given(nodeRepository.findById(3L)).willReturn(Optional.empty());

        //when
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> nodeService.updateParent(3L, 1L));
        //then
        assertEquals("Node not found", exception.getMessage());
    }

    @Test
    void updateParentWhenParentNodeNotFoundShouldDelegateToDataNotFoundException() {
        //given
        NodeBE node1 = new NodeBE();
        node1.setId(1L);
        node1.setRoot(node1);

        NodeBE node2 = new NodeBE();
        node2.setId(2L);

        NodeBE node3 = new NodeBE();
        node3.setId(3L);

        node1.setChildren(Collections.singletonList(node2));
        node2.setChildren(Collections.singletonList(node3));

        given(nodeRepository.findById(4L)).willReturn(Optional.empty());
        given(nodeRepository.findById(3L)).willReturn(Optional.of(node3));

        //when
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> nodeService.updateParent(3L, 1L));
        //then
        assertEquals("Parent node not found", exception.getMessage());
    }

    @Test
    void updateParentWhenExistingChildShouldDelegateToBadRequestException() {
        //given
        NodeBE node1 = new NodeBE();
        node1.setId(1L);
        node1.setRoot(node1);

        NodeBE node2 = new NodeBE();
        node2.setId(2L);

        NodeBE node3 = new NodeBE();
        node3.setId(3L);

        node1.setChildren(Collections.singletonList(node2));
        node2.setChildren(Collections.singletonList(node3));

        given(nodeRepository.findById(2L)).willReturn(Optional.of(node2));
        given(nodeRepository.findById(3L)).willReturn(Optional.of(node3));

        //when
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> nodeService.updateParent(3L, 2L));
        //then
        assertEquals("Node is already a child", exception.getMessage());
    }

    @Test
    void updateParentOfRootNodeShouldDelegateToBadRequestException() {
        //given
        NodeBE node1 = new NodeBE();
        node1.setId(1L);
        node1.setRoot(node1);

        NodeBE node2 = new NodeBE();
        node2.setId(2L);

        NodeBE node3 = new NodeBE();
        node3.setId(3L);

        node1.setChildren(Collections.singletonList(node2));
        node2.setChildren(Collections.singletonList(node3));

        given(nodeRepository.findById(1L)).willReturn(Optional.of(node1));
        given(nodeRepository.findById(3L)).willReturn(Optional.of(node3));

        //when
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> nodeService.updateParent(1L, 3L));
        //then
        assertEquals("Root node can not be updated", exception.getMessage());
    }
}