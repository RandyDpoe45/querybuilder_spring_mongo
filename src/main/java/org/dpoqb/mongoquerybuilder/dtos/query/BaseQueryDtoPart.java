package org.dpoqb.mongoquerybuilder.dtos.query;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BaseQueryDtoPart {

    private boolean negate = false;
    private String operator; //eq, between, geq, ge, le, leq
    private String attribute;
    private boolean multipleValues = false;
    private String delimiter = ",";
    private String value;
    private String value2 = "";
}
