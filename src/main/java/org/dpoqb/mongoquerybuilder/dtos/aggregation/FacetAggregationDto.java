package org.dpoqb.mongoquerybuilder.dtos.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FacetAggregationDto extends AggregationOperationDto {

    private Map<String, List<AggregationOperationDto>> facets;

}
