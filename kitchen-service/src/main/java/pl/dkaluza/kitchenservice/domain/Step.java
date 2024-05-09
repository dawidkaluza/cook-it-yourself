package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

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

        private static Factory<String> newTextFactory(String text, String prefix) {
            return DefaultFactory.newWithObject(
                ValidationExecutor.of(validator(isTextValid(text), prefix + "text", "Text must be non-blank, must have from 3 to 16384 chars")),
                text
            );
        }

        static StepFactory newStep(String text) {
            return newStep(text, "");
        }

        static StepFactory newStep(String text, String prefix) {
            return new StepFactory(
                () -> new Step(null, text),
                newTextFactory(text, prefix)
            );
        }

        static StepFactory fromPersistence(Long id, String text) {
            return fromPersistence(id, text, "");
        }

        static StepFactory fromPersistence(Long id, String text, String prefix) {
            var idFactory = new StepIdFactory(id, prefix);
            var textFactory = newTextFactory(text, prefix);

            return new StepFactory(
                () -> new Step(idFactory.assemble(), text),
                idFactory, textFactory
            );
        }

        @Override
        protected Step assemble() {
            return super.assemble();
        }
    }
}
