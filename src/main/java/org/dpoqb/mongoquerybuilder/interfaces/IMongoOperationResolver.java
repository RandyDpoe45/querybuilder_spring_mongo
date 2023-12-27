package org.dpoqb.mongoquerybuilder.interfaces;

import com.mongodb.BasicDBObject;
import org.dpoqb.mongoquerybuilder.dtos.aggregation.AggregationOperationDto;

import java.util.List;

public interface IMongoOperationResolver {

    List<BasicDBObject> resolveOperations(List<AggregationOperationDto> aggregationOperationDtos);
}
