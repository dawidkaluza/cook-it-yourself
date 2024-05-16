package pl.dkaluza.kitchenservice.adapters.in.web;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
abstract class PageWebMapper {
    <T> PageResponse<T> toResponse(List<T> items, int totalPages) {
        return new PageResponse<>(items, totalPages);
    }
}
