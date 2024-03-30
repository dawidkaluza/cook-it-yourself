package pl.dkaluza.domaincore;

class User extends AbstractPersistable<UserId> {
    public User(UserId id) {
        super(id);
    }
}
