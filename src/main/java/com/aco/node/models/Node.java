package com.aco.node.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {

    private Long nodeId;
    private Long rootNodeId;
    private Long parentNodeId;
    private Long height;
}
