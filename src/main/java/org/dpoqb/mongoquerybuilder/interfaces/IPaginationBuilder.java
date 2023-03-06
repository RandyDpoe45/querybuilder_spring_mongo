/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dpoqb.mongoquerybuilder.interfaces;

import org.dpoqb.mongoquerybuilder.dtos.PaginationDto;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author randy
 */
public interface IPaginationBuilder {
    
    Pageable createPagination(PaginationDto paginationDto);
}
