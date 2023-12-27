/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dpoqb.mongoquerybuilder.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author randy
 */
@Getter
@AllArgsConstructor
public enum QueryOperationsEnum {

    COND_IS_NULL("isNull"),
    COND_EQUAL("eq"),
    COND_EQUAL_CONTAINS("eqc"),
    COND_GREATER_EQUAL("geq"),
    COND_GREATER("ge"),
    COND_LESSER("le"),
    COND_LESSER_EQUAL("leq"),
    COND_BETWEEN("between"),
    IN("in");

    
    private final String code;

    public static QueryOperationsEnum getByCode(String code){
        for (QueryOperationsEnum aux : QueryOperationsEnum.values()){
            if (aux.getCode().equals(code))
                return aux;
        }
        throw new RuntimeException("Operation type: "+ code+ " not supported");
    }
}
