package org.dpoqb.mongoquerybuilder.dtos.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaginationAggregationDto extends AggregationOperationDto{

    private Integer pageNumber = 0;
    private Integer pageSize = 50;

}
