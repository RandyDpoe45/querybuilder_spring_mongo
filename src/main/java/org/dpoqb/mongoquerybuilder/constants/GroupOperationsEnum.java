package org.dpoqb.mongoquerybuilder.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum GroupOperationsEnum {

    GROUP_OP_COUNT("count"),
    GROUP_OP_SUM("sum"),
    GROUP_OP_AVG("avg"),
    GROUP_OP_MAX("max"),
    GROUP_OP_MIN("min");

    private String code;

    public static GroupOperationsEnum getByCode(String code){
        for (GroupOperationsEnum aux : GroupOperationsEnum.values()){
            if (aux.getCode().equals(code))
                return aux;
        }
        throw new RuntimeException("Group operation: "+ code+ " not supported");
    }
}
