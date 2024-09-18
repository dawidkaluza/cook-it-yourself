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

    public static pl.dkaluza.domaincore.Factory<Step> newStep(String text) {
        return Factory.newStep(text);
    }

    public static pl.dkaluza.domaincore.Factory<Step> fromPersistence(Long id, String text) {
        return Factory.fromPersistence(id, text);
    }

    public String getText() {
        return text;
    }
    
    record Dto(Long id, String text) {}

    static class Factory extends FactoriesComposite<Step> {
        private Factory(Assembler<Step> assembler, pl.dkaluza.domaincore.Factory<?>... factories) {
            super(assembler, factories);
        }

        static Factory newStep(String text) {
            return newStep(text, "");
        }

        static Factory newStep(String text, String prefix) {
            var textFactory = new TextFactory(text, prefix);
            return new Factory(
                () -> new Step(null, textFactory.assemble()),
                textFactory
            );
        }

        static Factory fromPersistence(Long id, String text) {
            return fromPersistence(id, text, "");
        }

        static Factory fromPersistence(Long id, String text, String prefix) {
            var idFactory = new StepId.Factory(id, prefix);
            var textFactory = new TextFactory(text, prefix);

            return new Factory(
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

    static class ListFactory extends FactoriesList<Step> {
        private ListFactory(List<? extends pl.dkaluza.domaincore.Factory<Step>> factories, List<Validator> validators) {
            super(factories, validators);
        }

        private static ListFactory of(List<Dto> dtos, Function<Dto, Factory> mapper, boolean allowEmpty, String fieldName) {
            var factories = dtos.stream()
                .map(mapper)
                .toList();

            List<Validator> validators = allowEmpty
                ? List.of()
                : List.of(validator(!dtos.isEmpty(), fieldName, "List must not be empty."));

            return new ListFactory(factories, validators);
        }

        public static ListFactory newSteps(List<Dto> steps, boolean allowEmpty, String fieldName) {
            return of(
                steps,
                step -> Factory.newStep(step.text(), fieldName + "."),
                allowEmpty,
                fieldName
            );
        }

        public static ListFactory fromPersistence(List<Dto> steps, boolean allowEmpty, String fieldName) {
            return of(
                steps,
                step -> Factory.fromPersistence(step.id(), step.text(), fieldName + "."),
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
