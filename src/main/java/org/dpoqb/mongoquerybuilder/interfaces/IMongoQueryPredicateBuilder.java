package org.dpoqb.mongoquerybuilder.interfaces;

import org.dpoqb.mongoquerybuilder.dtos.query.MongoQueryDtoPart;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface IMongoQueryPredicateBuilder {

    Criteria buildPredicate(List<MongoQueryDtoPart> mongoQueryDtoPartList);

}
