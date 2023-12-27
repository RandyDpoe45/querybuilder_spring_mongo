package org.dpoqb.mongoquerybuilder.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AggregationsOperationsEnum {


    OP_GROUP("group"),
    OP_SORT("sort"),
    OP_PAGINATION("pagination"),
    OP_PROJECTION("project"),
    OP_UNWIND("unwind"),
    OP_QUERY("query");

    private final String code;

    public static AggregationsOperationsEnum getByCode(String code){
        for (AggregationsOperationsEnum aux : AggregationsOperationsEnum.values()){
            if (aux.getCode().equals(code))
                return aux;
        }
        throw new RuntimeException("Aggregation operation: "+ code+ " not supported");
    }
}
