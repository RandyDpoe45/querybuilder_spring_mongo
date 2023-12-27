package org.dpoqb.mongoquerybuilder.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum PaginationConstantsEnum {

    DESC_SORT_DIRECTION("desc"),
    ASC_SORT_DIRECTION("asc");

    private String code;


}
