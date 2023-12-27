package org.dpoqb.mongoquerybuilder.impl;

import org.dpoqb.mongoquerybuilder.constants.DataTypeEnum;
import org.dpoqb.mongoquerybuilder.constants.QueryOperationsEnum;
import org.dpoqb.mongoquerybuilder.dtos.query.MongoQueryDtoPart;
import org.dpoqb.mongoquerybuilder.interfaces.IMongoQueryPredicateBuilder;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class MongoQueryPredicateBuilderImpl<T> implements IMongoQueryPredicateBuilder {

    private static Map<DataTypeEnum, Converter> transformers;

    private static Map<DataTypeEnum, List<QueryOperationsEnum>> availableOperations;

    interface Converter {
        Object convert(String value);
    }

    static {
        transformers = new HashMap<>();
        availableOperations = new HashMap<>();
        transformers.put(DataTypeEnum.BOOLEAN, (value) -> Boolean.parseBoolean(value));
        transformers.put(DataTypeEnum.INTEGER, (value) -> Integer.parseInt(value));
        transformers.put(DataTypeEnum.FLOAT, (value) -> Double.parseDouble(value));
        transformers.put(DataTypeEnum.TIME, (value) -> LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm:ss")));
        transformers.put(DataTypeEnum.DATE, (value) -> LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        transformers.put(DataTypeEnum.STRING, (value) -> value);

        List<QueryOperationsEnum> basicOperations = Arrays.asList(
                QueryOperationsEnum.COND_EQUAL,
                QueryOperationsEnum.COND_LESSER,
                QueryOperationsEnum.COND_LESSER_EQUAL,
                QueryOperationsEnum.COND_GREATER,
                QueryOperationsEnum.COND_GREATER_EQUAL,
                QueryOperationsEnum.COND_BETWEEN
        );
        List<QueryOperationsEnum> basicOperationsWithIn = new ArrayList<>(basicOperations);
        basicOperationsWithIn.add(QueryOperationsEnum.IN);

        availableOperations.put(DataTypeEnum.BOOLEAN, Arrays.asList(QueryOperationsEnum.COND_EQUAL));
        availableOperations.put(DataTypeEnum.INTEGER, basicOperationsWithIn);
        availableOperations.put(DataTypeEnum.FLOAT, basicOperations);
        availableOperations.put(DataTypeEnum.TIME, basicOperationsWithIn);
        availableOperations.put(DataTypeEnum.DATE, basicOperationsWithIn);
        availableOperations.put(DataTypeEnum.STRING, basicOperationsWithIn);
    }

    @Override
    public Criteria buildPredicate(List<MongoQueryDtoPart> mongoQueryDtoPartList) {
        if (Objects.isNull(mongoQueryDtoPartList) || mongoQueryDtoPartList.isEmpty())
            return new Criteria();

        List<Criteria> criteriaList = new ArrayList<>();
        for (MongoQueryDtoPart part : mongoQueryDtoPartList) {
            Criteria cond = buildSingleExpression(part);
            criteriaList.add(cond);
        }
        return new Criteria().andOperator(criteriaList);
    }

    private Criteria buildSingleExpression(MongoQueryDtoPart mongoQueryDtoPart) {
        if (mongoQueryDtoPart.getOperator().equals(QueryOperationsEnum.COND_IS_NULL.getCode()))
            return Criteria.where(mongoQueryDtoPart.getAttribute()).isNull();
        else
            return buildGenericCondition(mongoQueryDtoPart);
    }

    private Criteria buildGenericCondition(
            MongoQueryDtoPart mongoQueryDtoPart
    ) {
        DataTypeEnum dataType = DataTypeEnum.getByCode(mongoQueryDtoPart.getDataType());
        QueryOperationsEnum operations = QueryOperationsEnum.getByCode(mongoQueryDtoPart.getOperator());
        if (!availableOperations.get(dataType).contains(operations))
            throw new RuntimeException("Operation " + mongoQueryDtoPart.getOperator() + " for data type " + mongoQueryDtoPart.getDataType() + " not supported");

        Object value = this.transformers.get(dataType).convert(mongoQueryDtoPart.getValue());
        Criteria baseCriteria = Criteria.where(mongoQueryDtoPart.getAttribute());
        Boolean negate = mongoQueryDtoPart.isNegate();

        if (operations.equals(QueryOperationsEnum.COND_GREATER))
            return negate ? baseCriteria.not().gt(value) : baseCriteria.gt(value);
        else if (operations.equals(QueryOperationsEnum.COND_GREATER_EQUAL))
            return negate ? baseCriteria.not().gte(value) : baseCriteria.gte(value);
        else if (operations.equals(QueryOperationsEnum.COND_LESSER))
            return negate ? baseCriteria.not().lt(value) : baseCriteria.lt(value);
        else if (operations.equals(QueryOperationsEnum.COND_LESSER_EQUAL))
            return negate ? baseCriteria.not().lte(value) : baseCriteria.lte(value);
        else if (operations.equals(QueryOperationsEnum.COND_BETWEEN))
            return negate ? new Criteria()
                    .orOperator(
                            baseCriteria.lt(value),
                            Criteria.where(mongoQueryDtoPart.getAttribute()).gt(this.transformers.get(dataType).convert(mongoQueryDtoPart.getValue2()))
                    )
                    : new Criteria()
                    .andOperator(
                            baseCriteria.gte(value),
                            Criteria.where(mongoQueryDtoPart.getAttribute()).lte(this.transformers.get(dataType).convert(mongoQueryDtoPart.getValue2()))
                    );
        else if (operations.equals(QueryOperationsEnum.IN))
            return baseCriteria.in(
                    Arrays.stream(
                                    mongoQueryDtoPart.getValue()
                                            .split(mongoQueryDtoPart.getDelimiter())
                            ).map(x -> this.transformers.get(dataType).convert(x))
                            .collect(Collectors.toList())
            );
        else
            return negate ? baseCriteria.ne(value) : baseCriteria.is(value);
    }
}
