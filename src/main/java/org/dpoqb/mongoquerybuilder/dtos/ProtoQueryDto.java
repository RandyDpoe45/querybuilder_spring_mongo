package org.dpoqb.mongoquerybuilder.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProtoQueryDto{

    private List<ProtoQueryDtoPart> queryDtoPartList;

    private PaginationDto paginationDto;
}
