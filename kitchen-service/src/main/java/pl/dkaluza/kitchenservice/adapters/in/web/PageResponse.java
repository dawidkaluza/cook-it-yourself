package pl.dkaluza.kitchenservice.adapters.in.web;

import java.util.List;

record PageResponse<T>(List<T> items, int totalPages) {
}
