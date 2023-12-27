package org.dpoqb.mongoquerybuilder.dtos.aggregation;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, visible = true, property = "operationType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PaginationAggregationDto.class, name = "pagination"),
        @JsonSubTypes.Type(value = ProjectAggregationDto.class, name = "project"),
        @JsonSubTypes.Type(value = SortAggregationDto.class, name = "sort"),
        @JsonSubTypes.Type(value = UnwindAggregationDto.class, name = "unwind"),
        @JsonSubTypes.Type(value = GroupAggregationDto.class, name = "group"),
        @JsonSubTypes.Type(value = QueryAggregationDto.class, name = "query")
})
public abstract class AggregationOperationDto {

    protected String operationType;
}
