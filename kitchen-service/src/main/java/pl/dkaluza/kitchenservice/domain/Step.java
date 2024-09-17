package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static pl.dkaluza.domaincore.Validator.validator;
import static pl.dkaluza.kitchenservice.domain.StepId.StepIdFactory;

public class Step extends AbstractPersistable<StepId> {
    private final String text;

    private Step(StepId id, String text) {
        super(id);
        this.text = text;
    }

    public static Factory<Step> newStep(String text) {
        return StepFactory.newStep(text);
    }

    public static Factory<Step> fromPersistence(Long id, String text) {
        return StepFactory.fromPersistence(id, text);
    }

    public String getText() {
        return text;
    }
    
    record FactoryDto(Long id, String text) {}

    static class StepFactory extends FactoriesComposite<Step> {
        private StepFactory(Assembler<Step> assembler, Factory<?>... factories) {
            super(assembler, factories);
        }

        private static boolean isTextValid(String text) {
            if (text == null) {
                return false;
            }

            int length = text.trim().length();
            return !(length < 3 || length > 16384);
        }

        private static Assembler<String> textAssembler(String text) {
            return text == null ? () -> null : text::trim;
        }

        private static Factory<String> newTextFactory(String text, String prefix) {
            return DefaultFactory.newWithAssembler(
                ValidationExecutor.of(validator(isTextValid(text), prefix + "text", "Text must be non-blank, must have from 3 to 16384 chars")),
                textAssembler(text)
            );
        }

        static StepFactory newStep(String text) {
            return newStep(text, "");
        }

        static StepFactory newStep(String text, String prefix) {
            return new StepFactory(
                () -> new Step(null, textAssembler(text).assemble()),
                newTextFactory(text, prefix)
            );
        }

        static StepFactory fromPersistence(Long id, String text) {
            return fromPersistence(id, text, "");
        }

        static StepFactory fromPersistence(Long id, String text, String prefix) {
            var idFactory = new StepIdFactory(id, prefix);

            return new StepFactory(
                () -> new Step(idFactory.assemble(), textAssembler(text).assemble()),
                idFactory, newTextFactory(text, prefix)
            );
        }

        @Override
        protected Step assemble() {
            return super.assemble();
        }
    }

    static class StepsFactory extends FactoriesComposite<List<Step>> {
        private StepsFactory(Assembler<List<Step>> assembler, List<? extends Factory<?>> factories) {
            super(assembler, factories);
        }

        private static List<Step> assemble(List<StepFactory> ingredients) {
            return ingredients.stream()
                .map(StepFactory::assemble)
                .toList();
        }

        private static StepsFactory of(List<FactoryDto> steps, Function<FactoryDto, StepFactory> mapper, boolean allowEmpty, String fieldName) {
            var stepsFactories = steps.stream()
                .map(mapper)
                .toList();

            var allFactories = new ArrayList<Factory<?>>(stepsFactories);
            if (!allowEmpty) {
                var listFactory = DefaultFactory.newWithObject(
                    ValidationExecutor.of(validator(!steps.isEmpty(), fieldName, "Steps must not be empty.")),
                    steps
                );
                allFactories.add(listFactory);
            }

            return new StepsFactory(() -> assemble(stepsFactories), allFactories);
        }

        public static StepsFactory newSteps(List<FactoryDto> steps, boolean allowEmpty, String fieldName) {
            return of(
                steps,
                step -> StepFactory.newStep(step.text(), fieldName + "."),
                allowEmpty,
                fieldName
            );
        }

        public static StepsFactory fromPersistence(List<FactoryDto> steps, boolean allowEmpty, String fieldName) {
            return of(
                steps,
                step -> StepFactory.fromPersistence(step.id(), step.text(), fieldName + "."),
                allowEmpty,
                fieldName
            );
        }

        @Override
        protected List<Step> assemble() {
            return super.assemble();
        }
    }
}
