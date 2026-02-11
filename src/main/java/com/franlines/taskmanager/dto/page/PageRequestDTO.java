package com.franlines.taskmanager.dto.page;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PageRequestDTO {
    private int page = 0;
    private int size = 10;
    private String sortBy;
    private String direction = "desc";

    public org.springframework.data.domain.Pageable toPageable() {
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Sort sort = Sort.by(sortDirection, sortBy);
            return org.springframework.data.domain.PageRequest.of(page, size, sort);
        }
        return org.springframework.data.domain.PageRequest.of(page, size);
    }
}