package org.dpoqb.mongoquerybuilder.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import org.dpoqb.mongoquerybuilder.dtos.ProtoDataTypeEnum;
import org.dpoqb.mongoquerybuilder.dtos.ProtoQueryDto;
import org.dpoqb.mongoquerybuilder.dtos.ProtoQueryDtoPart;
import org.dpoqb.mongoquerybuilder.dtos.QueryConstantsEnum;
import org.dpoqb.mongoquerybuilder.interfaces.IQueryDSLPredicateBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;


public class QueryDSLPredicateBuilderImpl<T> implements IQueryDSLPredicateBuilder {

    private final String collectionName;

    private final Class<T> classType;

    public QueryDSLPredicateBuilderImpl(
            String collectionName,
            Class<T> classType
    ) {
        this.collectionName = collectionName;
        this.classType = classType;
        System.out.println("Que gonorrea: " + this.classType);

    }

    @Override
    public Predicate buildPredicate(ProtoQueryDto protoQueryDto) {
        PathBuilder<T> pathBuilder = new PathBuilder<>(this.classType, collectionName);

        BooleanExpression predicate = null;
        for (ProtoQueryDtoPart part : protoQueryDto.getQueryDtoPartList()) {
            BooleanExpression pred = buildSingleExpression(
                    part,
                    pathBuilder
            );
            predicate = predicate == null ? pred : predicate.and(pred);

        }

        return predicate;
    }

    private BooleanExpression buildSingleExpression(
            ProtoQueryDtoPart protoQueryDtoPart,
            PathBuilder<T> pathBuilder
    ) {
        if (protoQueryDtoPart.getDataType().equals(ProtoDataTypeEnum.FLOAT.getTypeCode())) {
            return buildFloatCondition(protoQueryDtoPart, pathBuilder);
        } else if (protoQueryDtoPart.getDataType().equals(ProtoDataTypeEnum.INTEGER.getTypeCode())) {
            return buildIntegerCondition(protoQueryDtoPart, pathBuilder);
        } else if (protoQueryDtoPart.getDataType().equals(ProtoDataTypeEnum.DATE.getTypeCode())) {
            return buildDateCondition(protoQueryDtoPart, pathBuilder);
        } else if (protoQueryDtoPart.getDataType().equals(ProtoDataTypeEnum.TIME.getTypeCode())) {
            return buildTimeCondition(protoQueryDtoPart, pathBuilder);
        } else {
            return buildStringCondition(protoQueryDtoPart, pathBuilder);
        }
    }

    private BooleanExpression buildFloatCondition(
            ProtoQueryDtoPart protoQueryDtoPart,
            PathBuilder<T> pathBuilder
    ){
        NumberPath<Double> numberPath = pathBuilder.getNumber(
                protoQueryDtoPart.getAttribute(),
                Double.class
        );
        Double value = Double.parseDouble(protoQueryDtoPart.getValue());
        if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER.getValue()))
            return numberPath.gt(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER_EQUAL.getValue()))
            return numberPath.goe(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER.getValue()))
            return numberPath.lt(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER_EQUAL.getValue()))
            return numberPath.loe(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_BETWEEN.getValue()))
            return numberPath.between(value, new BigDecimal(protoQueryDtoPart.getValue2()));
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.IN.getValue()))
            return numberPath.in(
                    Arrays.stream(protoQueryDtoPart.getValue()
                                    .split(protoQueryDtoPart.getDelimiter())
                            ).map(Double::parseDouble)
                            .collect(Collectors.toList())
            );
        return numberPath.eq(value);
    }
    private BooleanExpression buildIntegerCondition(
            ProtoQueryDtoPart protoQueryDtoPart,
            PathBuilder<T> pathBuilder
    ) {
        NumberPath<Long> numberPath = pathBuilder.getNumber(
                protoQueryDtoPart.getAttribute(),
                Long.class
        );
        Long value = Long.parseLong(protoQueryDtoPart.getValue());
        if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER.getValue()))
            return numberPath.gt(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER_EQUAL.getValue()))
            return numberPath.goe(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER.getValue()))
            return numberPath.lt(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER_EQUAL.getValue()))
            return numberPath.loe(value);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_BETWEEN.getValue()))
            return numberPath.between(value, new BigInteger(protoQueryDtoPart.getValue2()));
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.IN.getValue()))
            return numberPath.in(
                    Arrays.stream(protoQueryDtoPart.getValue()
                                    .split(protoQueryDtoPart.getDelimiter())
                            ).map(Long::parseLong)
                            .collect(Collectors.toList())
            );
        return numberPath.eq(value);
    }

    private BooleanExpression buildDateCondition(
            ProtoQueryDtoPart protoQueryDtoPart,
            PathBuilder<T> pathBuilder
    ) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DatePath<LocalDate> localPath = pathBuilder.getDate(
                protoQueryDtoPart.getAttribute(),
                LocalDate.class
        );
        LocalDate localDate = LocalDate.parse(protoQueryDtoPart.getValue(), dtf);
        if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER.getValue()))
            return localPath.gt(localDate);
        if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER_EQUAL.getValue()))
            return localPath.goe(localDate);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER.getValue()))
            return localPath.lt(localDate);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER_EQUAL.getValue()))
            return localPath.loe(localDate);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_BETWEEN.getValue()))
            return localPath.between(localDate, LocalDate.parse(protoQueryDtoPart.getValue2(), dtf));
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.IN.getValue()))
            return localPath.in(
                    Arrays.stream(protoQueryDtoPart.getValue()
                                    .split(protoQueryDtoPart.getDelimiter())
                            ).map(x -> LocalDate.parse(x, dtf))
                            .collect(Collectors.toList())
            );
        return localPath.eq(localDate);
    }

    private BooleanExpression buildTimeCondition(
            ProtoQueryDtoPart protoQueryDtoPart,
            PathBuilder<T> pathBuilder
    ) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        DatePath<LocalTime> localPath = pathBuilder.getDate(
                protoQueryDtoPart.getAttribute(),
                LocalTime.class
        );
        LocalTime localTime = LocalTime.parse(protoQueryDtoPart.getValue(), dtf);
        if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER.getValue()))
            return localPath.gt(localTime);
        if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER_EQUAL.getValue()))
            return localPath.goe(localTime);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER.getValue()))
            return localPath.lt(localTime);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER_EQUAL.getValue()))
            return localPath.loe(localTime);
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_BETWEEN.getValue()))
            return localPath.between(localTime, LocalTime.parse(protoQueryDtoPart.getValue2(), dtf));
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.IN.getValue()))
            return localPath.in(
                    Arrays.stream(protoQueryDtoPart.getValue()
                                    .split(protoQueryDtoPart.getDelimiter())
                            ).map(x -> LocalTime.parse(x, dtf))
                            .collect(Collectors.toList())
            );
        return localPath.eq(localTime);
    }

    private BooleanExpression buildStringCondition(
            ProtoQueryDtoPart protoQueryDtoPart,
            PathBuilder<T> pathBuilder
    ) {
        StringPath path = pathBuilder.getString(protoQueryDtoPart.getAttribute());
        if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER.getValue()))
            return path.gt(protoQueryDtoPart.getValue());
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER_EQUAL.getValue()))
            return path.goe(protoQueryDtoPart.getValue());
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER.getValue()))
            return path.lt(protoQueryDtoPart.getValue());
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER_EQUAL.getValue()))
            return path.loe(protoQueryDtoPart.getValue());
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_BETWEEN.getValue()))
            return path.between(protoQueryDtoPart.getValue(), protoQueryDtoPart.getValue2());
        else if (protoQueryDtoPart.getOperator().equals(QueryConstantsEnum.IN.getValue()))
            return path.in(
                    protoQueryDtoPart.getValue()
                            .split(protoQueryDtoPart.getDelimiter())
            );
        return path.containsIgnoreCase(protoQueryDtoPart.getValue());
    }

}
