package com.aco.node.controller;

import com.aco.node.exception.DataNotFoundException;
import com.aco.node.models.Node;
import com.aco.node.services.NodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureWebClient
class NodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NodeService nodeService;

    @Test
    void getDescendantsOk() throws Exception {
        //given
        var child = Node.builder()
                .nodeId(2L)
                .parentNodeId(1L)
                .rootNodeId(1L)
                .height(1L)
                .build();
        given(nodeService.getDescendants(1L)).willReturn(List.of(child));

        //when
       mockMvc.perform(MockMvcRequestBuilders.get("/nodes/{nodeId}/descendants", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(getExpectedResponse()));
    }

    @Test
    void getDescendantsResourceNotFound() throws Exception {
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/nodes//descendants"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDescendantsNoDataFound() throws Exception {
        //given
        given(nodeService.getDescendants(1L)).willThrow(new DataNotFoundException("Node not found"));

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/nodes/{nodeId}/descendants", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Node not found\"}"));
    }

    @Test
    void updateParentOk() throws Exception {
        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/{nodeId}/parent", "2")
                .param("parentId", "4"))
                .andExpect(status().isOk());
        //then
        verify(nodeService).updateParent(2L, 4L);
    }

    @Test
    void updateParentBadRequestWhenNodeIdNotPresent() throws Exception {
        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/parent")
                .param("parentId", "4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateParentBadRequestWhenParentIdNotPresent() throws Exception {
        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/{nodeId}/parent", 2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateParentNodeNotFound() throws Exception {
        //given
        doThrow(new DataNotFoundException("Node not found")).when(nodeService).updateParent(2L, 4L);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/{nodeId}/parent", 2)
                .param("parentId", "4"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Node not found\"}"));
    }

    private String getExpectedResponse() {
        return "[{\"nodeId\":2,\"rootNodeId\":1,\"parentNodeId\":1,\"height\":1}]";
    }
}
