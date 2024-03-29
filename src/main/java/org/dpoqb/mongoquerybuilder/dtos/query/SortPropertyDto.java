package org.dpoqb.mongoquerybuilder.dtos.query;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SortPropertyDto {
    private String sortDirection = null; //desc, asc
    private String sortProperty = null;
}
