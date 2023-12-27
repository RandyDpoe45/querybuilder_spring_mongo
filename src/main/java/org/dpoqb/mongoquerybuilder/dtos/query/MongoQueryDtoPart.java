package org.dpoqb.mongoquerybuilder.dtos.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MongoQueryDtoPart extends BaseQueryDtoPart {

    private String dataType;

}
