package org.dpoqb.mongoquerybuilder.interfaces;

import com.querydsl.core.types.Predicate;
import org.dpoqb.mongoquerybuilder.dtos.ProtoQueryDto;

public interface IQueryDSLPredicateBuilder {

    Predicate buildPredicate(ProtoQueryDto protoQueryDto);

}
