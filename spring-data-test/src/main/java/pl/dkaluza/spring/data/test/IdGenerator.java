package pl.dkaluza.spring.data.test;

public interface IdGenerator<ID> {
    ID generate();
}
