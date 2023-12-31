package com.geekhalo.relation.app;

import com.geekhalo.lego.annotation.web.AutoRegisterWebController;
import com.geekhalo.lego.core.query.QueryServiceDefinition;
import com.geekhalo.relation.domain.relation.Relation;
import com.geekhalo.relation.domain.relation.RelationQueryRepository;
import com.geekhalo.relation.domain.relation.RelationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


@QueryServiceDefinition(
        repositoryClass = RelationQueryRepository.class,
        domainClass = Relation.class
)
@AutoRegisterWebController(name = "relation")
public interface RelationQueryApplication {

    Page<Relation> getByKeyOwner(Long owner, Pageable pageable);

    Page<Relation> getByKeyOwnerAndStatusIn(Long owner, List<RelationStatus> statuses, Pageable pageable);

    com.geekhalo.lego.core.singlequery.Page<Relation> pageOf(QueryRelationByOwner query);
}
