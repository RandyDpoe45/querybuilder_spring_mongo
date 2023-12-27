package org.dpoqb.mongoquerybuilder.dtos.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MongoQueryDto {

    private List<MongoQueryDtoPart> mongoQueryDtoPartList;

    private PaginationDto paginationDto;
}
