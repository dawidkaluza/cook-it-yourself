package pl.dkaluza.userservice.domain;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import pl.dkaluza.userservice.ports.out.UserEventPublisher;
import pl.dkaluza.userservice.ports.out.UserRepository;

@Configuration
class DomainConfig {
    private final PlatformTransactionManager txManager;

    DomainConfig(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Bean
    DefaultUserService defaultUserService(UserRepository userRepository, UserEventPublisher userEventPublisher) {
        var userService = new DefaultUserService(userRepository, userEventPublisher);
        var attrSource = new NameMatchTransactionAttributeSource();
        var readOnlyTxAttr = new DefaultTransactionAttribute();
        readOnlyTxAttr.setReadOnly(true);

        attrSource.addTransactionalMethod("signUp", new DefaultTransactionAttribute());
        attrSource.addTransactionalMethod("loadUserByEmail", new DefaultTransactionAttribute(readOnlyTxAttr));
        return transactional(userService, attrSource);
    }

    private <T> T transactional(T service, TransactionAttributeSource attrSource) {
        var proxyFactory = new ProxyFactory(service);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TransactionInterceptor((TransactionManager) txManager, attrSource));
        //noinspection unchecked
        return (T) proxyFactory.getProxy();
    }
}
