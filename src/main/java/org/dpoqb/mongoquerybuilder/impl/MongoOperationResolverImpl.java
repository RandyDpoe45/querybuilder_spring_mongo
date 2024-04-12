package org.dpoqb.mongoquerybuilder.impl;


import com.mongodb.BasicDBObject;
import org.dpoqb.mongoquerybuilder.constants.GroupOperationsEnum;
import org.dpoqb.mongoquerybuilder.constants.PaginationConstantsEnum;
import org.dpoqb.mongoquerybuilder.dtos.aggregation.*;
import org.dpoqb.mongoquerybuilder.dtos.query.SortPropertyDto;
import org.dpoqb.mongoquerybuilder.interfaces.IMongoOperationResolver;
import org.dpoqb.mongoquerybuilder.interfaces.IMongoQueryPredicateBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MongoOperationResolverImpl<T> implements IMongoOperationResolver {

    private final IMongoQueryPredicateBuilder mongoQueryPredicateBuilder;

    private final String collectionName;

    private final MongoTemplate mongoTemplate;

    public MongoOperationResolverImpl(
            MongoTemplate mongoTemplate,
            String collectionName
    ) {
        this.mongoTemplate = mongoTemplate;
        this.collectionName = collectionName;
        this.mongoQueryPredicateBuilder = new MongoQueryPredicateBuilderImpl<>();
    }

    @Override
    public List<BasicDBObject> resolveOperations(List<AggregationOperationDto> aggregationOperationsDtos) {

        List<AggregationOperation> aggregationList = new ArrayList<>();
        for (AggregationOperationDto operationDto : aggregationOperationsDtos) {
            aggregationList.addAll(resolveOperationsAux(operationDto));
        }
        Aggregation aggregation = Aggregation.newAggregation(aggregationList);
        AggregationResults<BasicDBObject> result = mongoTemplate.aggregate(aggregation, this.collectionName, BasicDBObject.class);
        return result.getMappedResults();
    }

    private List<AggregationOperation> resolveOperationsAux(AggregationOperationDto operationDto) {
        if (operationDto instanceof PaginationAggregationDto dto)
            return createPaginationAggregation(dto);
        else if (operationDto instanceof SortAggregationDto dto)
            return List.of(createSortAggregation(dto));
        else if (operationDto instanceof ProjectAggregationDto dto)
            return List.of(createProjectionAggregation(dto));
        else if (operationDto instanceof UnwindAggregationDto dto)
            return List.of(createUnwindAggregation(dto));
        else if (operationDto instanceof QueryAggregationDto dto)
            return List.of(createQueryAggregation(dto));
        else if (operationDto instanceof GroupAggregationDto dto)
            return List.of(createGroupAggregation(dto));
        else if (operationDto instanceof FacetAggregationDto dto)
            return createFacetAggregation(dto);
        throw new RuntimeException("No aggregation operation implemented");
    }

    private List<AggregationOperation> createPaginationAggregation(PaginationAggregationDto paginationAggregationDto) {
        return Arrays.asList(
                Aggregation.skip((long) paginationAggregationDto.getPageNumber() * paginationAggregationDto.getPageSize()),
                Aggregation.limit(paginationAggregationDto.getPageSize())
        );
    }

    private AggregationOperation createSortAggregation(SortAggregationDto sortAggregationDto) {
        List<Sort.Order> orderList = new ArrayList<>();
        for (SortPropertyDto dto : sortAggregationDto.getSortPropertyList()) {
            Sort.Direction x = dto.getSortDirection().equals(PaginationConstantsEnum.DESC_SORT_DIRECTION.getCode()) ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            orderList.add(new Sort.Order(x, dto.getSortProperty()));
        }
        return Aggregation.sort(Sort.by(orderList));
    }

    private AggregationOperation createProjectionAggregation(ProjectAggregationDto projectAggregationDto) {
        if (Objects.isNull(projectAggregationDto.getPropertyList()) || projectAggregationDto.getPropertyList().isEmpty())
            throw new RuntimeException("No active properties to project");
        ProjectionOperation op = Aggregation.project(projectAggregationDto.getPropertyList().toArray(String[]::new));
        if (!Objects.isNull(projectAggregationDto.getExcludePropertyList()) && !projectAggregationDto.getExcludePropertyList().isEmpty())
            op = op.andExclude(projectAggregationDto.getExcludePropertyList().toArray(String[]::new));
        if (!Objects.isNull(projectAggregationDto.getProjectionExpressions()) && !projectAggregationDto.getProjectionExpressions().isEmpty())
            for (ProjectionExpressionDto exp : projectAggregationDto.getProjectionExpressions())
                op = op.andExpression(exp.getOperator()).as(exp.getAlias());
        return op;
    }

    private List<AggregationOperation> createFacetAggregation(FacetAggregationDto facetAggregationDto) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        facetAggregationDto.getFacets().forEach((name, operations) -> {
            List<AggregationOperation> ops = new ArrayList<>();
            operations.forEach(x -> {
                ops.addAll(resolveOperationsAux(x));
            });
            aggregationOperations.add(Aggregation.facet(ops.toArray(new AggregationOperation[0])).as(name));
        });
        return aggregationOperations;
    }

    private AggregationOperation createUnwindAggregation(UnwindAggregationDto unwindAggregationDto) {
        boolean withIndex = !(Objects.isNull(unwindAggregationDto.getArrayIndexName()) || unwindAggregationDto.getArrayIndexName().isEmpty());
        return withIndex ?
                Aggregation.unwind(unwindAggregationDto.getField(), unwindAggregationDto.getArrayIndexName())
                : Aggregation.unwind(unwindAggregationDto.getField());
    }

    private AggregationOperation createQueryAggregation(QueryAggregationDto queryAggregationDto) {
        return Aggregation.match(this.mongoQueryPredicateBuilder.buildPredicate(queryAggregationDto.getMongoQueryDtoPartList()));
    }

    private AggregationOperation createGroupAggregation(GroupAggregationDto groupAggregationDto) {
        GroupOperation groupOperation = Aggregation.group(groupAggregationDto.getFields().toArray(String[]::new));
        for (GroupSpecification spec : groupAggregationDto.getGroupSpecifications()) {
            GroupOperationsEnum operation = GroupOperationsEnum.getByCode(spec.getOperation());
            if (operation.equals(GroupOperationsEnum.GROUP_OP_COUNT))
                groupOperation = groupOperation.count().as(spec.getAlias());
            else if (operation.equals(GroupOperationsEnum.GROUP_OP_MAX))
                groupOperation = groupOperation.max(spec.getProperty()).as(spec.getAlias());
            else if (operation.equals(GroupOperationsEnum.GROUP_OP_MIN))
                groupOperation = groupOperation.min(spec.getProperty()).as(spec.getAlias());
            else if (operation.equals(GroupOperationsEnum.GROUP_OP_SUM))
                groupOperation = groupOperation.sum(spec.getProperty()).as(spec.getAlias());
            else if (operation.equals(GroupOperationsEnum.GROUP_OP_AVG))
                groupOperation = groupOperation.avg(spec.getProperty()).as(spec.getAlias());
        }

        return groupOperation;
    }
}
