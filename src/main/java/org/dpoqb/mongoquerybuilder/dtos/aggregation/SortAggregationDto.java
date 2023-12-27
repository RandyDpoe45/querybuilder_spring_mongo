package org.dpoqb.mongoquerybuilder.dtos.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dpoqb.mongoquerybuilder.dtos.query.SortPropertyDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SortAggregationDto extends AggregationOperationDto{

    private List<SortPropertyDto> sortPropertyList;

}
