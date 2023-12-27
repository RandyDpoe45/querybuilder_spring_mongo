/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dpoqb.mongoquerybuilder.impl;


import org.dpoqb.mongoquerybuilder.constants.PaginationConstantsEnum;
import org.dpoqb.mongoquerybuilder.dtos.query.PaginationDto;
import org.dpoqb.mongoquerybuilder.constants.QueryOperationsEnum;
import org.dpoqb.mongoquerybuilder.dtos.query.SortPropertyDto;
import org.dpoqb.mongoquerybuilder.interfaces.IPaginationBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author randy
 */
public class PaginationBuilderImpl implements IPaginationBuilder {

    @Override
    public Pageable createPagination(PaginationDto paginationDto) {
        if (paginationDto == null)
            return Pageable.unpaged();

        if (paginationDto.getPageNumber() != null && paginationDto.getPageSize() != null) {
            if (paginationDto.getPageSize() < 1)
                return Pageable.unpaged();

            if (paginationDto.getSortPropertyDtoList() != null && !paginationDto.getSortPropertyDtoList().isEmpty()) {
                List<Sort.Order> orders = new ArrayList<>();
                for(SortPropertyDto dto : paginationDto.getSortPropertyDtoList()){
                    Direction x = dto.getSortDirection().equals(PaginationConstantsEnum.DESC_SORT_DIRECTION.getCode()) ?
                            Direction.DESC : Direction.ASC;
                    orders.add(new Sort.Order(x, dto.getSortProperty()));
                }
                return PageRequest.of(
                        paginationDto.getPageNumber(),
                        paginationDto.getPageSize(),
                        Sort.by(orders)
                );
            }
            return PageRequest.of(paginationDto.getPageNumber(), paginationDto.getPageSize());
        }
        return Pageable.unpaged();
    }

}
