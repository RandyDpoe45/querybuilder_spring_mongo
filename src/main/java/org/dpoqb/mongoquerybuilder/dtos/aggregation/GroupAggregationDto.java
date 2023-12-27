package org.dpoqb.mongoquerybuilder.dtos.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GroupAggregationDto extends AggregationOperationDto{

    private List<String> fields;
    private List<GroupSpecification> groupSpecifications;

}
