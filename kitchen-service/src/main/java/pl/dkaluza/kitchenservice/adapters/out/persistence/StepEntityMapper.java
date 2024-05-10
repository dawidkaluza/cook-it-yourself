package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dkaluza.kitchenservice.domain.Step;

@Mapper
interface StepEntityMapper {
    @Mapping(target = "id", source = "step.id.id")
    StepEntity toEntity(Step step, Integer position, Long recipeId);
}
