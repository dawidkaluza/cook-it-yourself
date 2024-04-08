package pl.dkaluza.spring.data.test;

import java.util.function.UnaryOperator;

public class LongIdGenerator implements IdGenerator<Long> {
    private long nextId;
    private final UnaryOperator<Long> generator;

    public LongIdGenerator(long startingId, UnaryOperator<Long> generator) {
        this.nextId = startingId;
        this.generator = generator;
    }

    public LongIdGenerator() {
        this(1, (id) -> id + 1);
    }

    @Override
    public Long generate() {
        long id = nextId;
        nextId = generator.apply(nextId);
        return id;
    }
}
