package org.dpoqb.mongoquerybuilder.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ProtoDataTypeEnum {

    STRING("String"),

    INTEGER("Integer"),

    FLOAT("Float"),

    DATE("Date"),

    TIME("Time");

    private String typeCode;


}
