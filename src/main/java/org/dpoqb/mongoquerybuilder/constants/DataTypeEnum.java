package org.dpoqb.mongoquerybuilder.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum DataTypeEnum {

    BOOLEAN("Boolean"),

    STRING("String"),

    INTEGER("Integer"),

    FLOAT("Float"),

    DATE("Date"),

    TIME("Time"),

    DATETIME("DateTime");

    private String typeCode;

    public static DataTypeEnum getByCode(String code) {
        for (DataTypeEnum aux : DataTypeEnum.values()) {
            if (aux.getTypeCode().equals(code))
                return aux;
        }
        throw new RuntimeException("Data type: " + code + " not supported");
    }
}
