package pl.dkaluza.kitchenservice.domain;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

class DomainConfig {
    private final PlatformTransactionManager txManager;

    DomainConfig(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Bean
    DefaultKitchenService defaultUserService(RecipeRepository recipeRepository) {
        var kitchenService = new DefaultKitchenService(recipeRepository);
        var attrSource = new NameMatchTransactionAttributeSource();

        attrSource.addTransactionalMethod("addRecipe", new DefaultTransactionAttribute());
        return transactional(kitchenService, attrSource);
    }

    private <T> T transactional(T service, TransactionAttributeSource attrSource) {
        var proxyFactory = new ProxyFactory(service);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TransactionInterceptor((TransactionManager) txManager, attrSource));
        //noinspection unchecked
        return (T) proxyFactory.getProxy();
    }
}
