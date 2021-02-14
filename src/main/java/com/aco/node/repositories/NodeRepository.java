package com.aco.node.repositories;

import com.aco.node.domain.NodeBE;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface NodeRepository extends CrudRepository<NodeBE, Long> {

    @Override
    @EntityGraph(attributePaths = {"parent", "children", "root"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<NodeBE> findById(@NonNull Long id);
}
