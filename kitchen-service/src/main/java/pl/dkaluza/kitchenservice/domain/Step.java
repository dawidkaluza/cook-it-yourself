package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

import java.util.List;
import java.util.function.Function;

import static pl.dkaluza.domaincore.Validator.validator;

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

        static StepFactory newStep(String text) {
            return newStep(text, "");
        }

        static StepFactory newStep(String text, String prefix) {
            var textFactory = new TextFactory(text, prefix);
            return new StepFactory(
                () -> new Step(null, textFactory.assemble()),
                textFactory
            );
        }

        static StepFactory fromPersistence(Long id, String text) {
            return fromPersistence(id, text, "");
        }

        static StepFactory fromPersistence(Long id, String text, String prefix) {
            var idFactory = new StepId.StepIdFactory(id, prefix);
            var textFactory = new TextFactory(text, prefix);

            return new StepFactory(
                () -> new Step(idFactory.assemble(), textFactory.assemble()),
                idFactory, textFactory
            );
        }

        @Override
        protected Step assemble() {
            return super.assemble();
        }
    }
    
    static class TextFactory extends DefaultFactory<String> {
        TextFactory(String text) {
            this(text, "");
        }

        TextFactory(String text, String prefix) {
            //noinspection Convert2MethodRef
            super(
                () -> text.trim(),
                ValidationExecutor.of(validator(isTextValid(text), prefix + "text", "Text must be non-blank, must have from 3 to 16384 chars"))
            );
        }

        private static boolean isTextValid(String text) {
            if (text == null) {
                return false;
            }

            int length = text.trim().length();
            return !(length < 3 || length > 16384);
        }

        @Override
        protected String assemble() {
            return super.assemble();
        }
    }

    static class StepsFactory extends FactoriesList<Step> {
        private StepsFactory(List<? extends Factory<Step>> factories, List<Validator> validators) {
            super(factories, validators);
        }

        private static StepsFactory of(List<FactoryDto> dtos, Function<FactoryDto, StepFactory> mapper, boolean allowEmpty, String fieldName) {
            var factories = dtos.stream()
                .map(mapper)
                .toList();

            List<Validator> validators = allowEmpty
                ? List.of()
                : List.of(validator(!dtos.isEmpty(), fieldName, "List must not be empty."));

            return new StepsFactory(factories, validators);
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
