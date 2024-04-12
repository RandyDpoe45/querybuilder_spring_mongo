package org.dpoqb.mongoquerybuilder.impl;

import org.dpoqb.mongoquerybuilder.constants.DataTypeEnum;
import org.dpoqb.mongoquerybuilder.constants.QueryOperationsEnum;
import org.dpoqb.mongoquerybuilder.dtos.query.MongoQueryDtoPart;
import org.dpoqb.mongoquerybuilder.interfaces.IMongoQueryPredicateBuilder;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class MongoQueryPredicateBuilderImpl<T> implements IMongoQueryPredicateBuilder {

    private static final Map<DataTypeEnum, Converter> transformers;

    private static final Map<DataTypeEnum, List<QueryOperationsEnum>> availableOperations;

    interface Converter {
        Object convert(String value);
    }

    static {
        transformers = new HashMap<>();
        availableOperations = new HashMap<>();
        transformers.put(DataTypeEnum.BOOLEAN, Boolean::parseBoolean);
        transformers.put(DataTypeEnum.INTEGER, Integer::parseInt);
        transformers.put(DataTypeEnum.FLOAT, Double::parseDouble);
        transformers.put(DataTypeEnum.TIME, (value) -> LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm:ss")));
        transformers.put(DataTypeEnum.DATE, (value) -> LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        transformers.put(DataTypeEnum.DATETIME, (value) -> LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss")));
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

        List<QueryOperationsEnum> stringOperations = new ArrayList<>(basicOperationsWithIn);
        stringOperations.add(QueryOperationsEnum.COND_EQUAL_CONTAINS);

        availableOperations.put(DataTypeEnum.BOOLEAN, List.of(QueryOperationsEnum.COND_EQUAL));
        availableOperations.put(DataTypeEnum.INTEGER, basicOperationsWithIn);
        availableOperations.put(DataTypeEnum.FLOAT, basicOperations);
        availableOperations.put(DataTypeEnum.TIME, basicOperationsWithIn);
        availableOperations.put(DataTypeEnum.DATE, basicOperationsWithIn);
        availableOperations.put(DataTypeEnum.DATETIME, basicOperationsWithIn);
        availableOperations.put(DataTypeEnum.STRING, stringOperations);
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

        Converter conv = transformers.get(dataType);
        Criteria baseCriteria = Criteria.where(mongoQueryDtoPart.getAttribute());
        boolean negate = mongoQueryDtoPart.isNegate();

        if (operations.equals(QueryOperationsEnum.COND_GREATER))
            return negate ? baseCriteria.not().gt(conv.convert(mongoQueryDtoPart.getValue())) : baseCriteria.gt(conv.convert(mongoQueryDtoPart.getValue()));
        if (operations.equals(QueryOperationsEnum.COND_EQUAL_CONTAINS))
            return negate ? baseCriteria.not().regex("/^" + mongoQueryDtoPart.getValue() + "$/i") : baseCriteria.regex("/^" + mongoQueryDtoPart.getValue() + "$/i");
        else if (operations.equals(QueryOperationsEnum.COND_GREATER_EQUAL))
            return negate ? baseCriteria.not().gte(conv.convert(mongoQueryDtoPart.getValue())) : baseCriteria.gte(conv.convert(mongoQueryDtoPart.getValue()));
        else if (operations.equals(QueryOperationsEnum.COND_LESSER))
            return negate ? baseCriteria.not().lt(conv.convert(mongoQueryDtoPart.getValue())) : baseCriteria.lt(conv.convert(mongoQueryDtoPart.getValue()));
        else if (operations.equals(QueryOperationsEnum.COND_LESSER_EQUAL))
            return negate ? baseCriteria.not().lte(conv.convert(mongoQueryDtoPart.getValue())) : baseCriteria.lte(conv.convert(mongoQueryDtoPart.getValue()));
        else if (operations.equals(QueryOperationsEnum.COND_BETWEEN))
            return negate ? new Criteria()
                    .orOperator(
                            baseCriteria.lt(conv.convert(mongoQueryDtoPart.getValue())),
                            Criteria.where(mongoQueryDtoPart.getAttribute()).gt(conv.convert(mongoQueryDtoPart.getValue2()))
                    )
                    : new Criteria()
                    .andOperator(
                            baseCriteria.gte(conv.convert(mongoQueryDtoPart.getValue())),
                            Criteria.where(mongoQueryDtoPart.getAttribute()).lte(conv.convert(mongoQueryDtoPart.getValue2()))
                    );
        else if (operations.equals(QueryOperationsEnum.IN)) {
            baseCriteria = negate ? baseCriteria.not() : baseCriteria;
            List<?> objects = Arrays.stream(mongoQueryDtoPart
                            .getValue()
                            .split(mongoQueryDtoPart.getDelimiter())
                    ).map(conv::convert)
                    .collect(Collectors.toList());
            return baseCriteria.in(
                    objects
            );
        } else
            return negate ? baseCriteria.ne(conv.convert(mongoQueryDtoPart.getValue())) : baseCriteria.is(conv.convert(mongoQueryDtoPart.getValue()));
    }
}
