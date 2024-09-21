package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dkaluza.kitchenservice.domain.Ingredient;
import pl.dkaluza.kitchenservice.domain.Step;

import java.util.ArrayList;
import java.util.List;

@Mapper
interface StepEntityMapper {
    @Mapping(target = "id", source = "step.id.id")
    StepEntity toEntity(Step step, Integer position, Long recipeId);

    default List<StepEntity> toEntities(List<Step> steps, Long recipeId) {
        var entities = new ArrayList<StepEntity>();
        int stepsSize = steps.size();
        for (int i = 0; i < stepsSize; i++) {
            Step step = steps.get(i);
            entities.add(toEntity(step, i + 1, recipeId));
        }
        return entities;
    }
}
