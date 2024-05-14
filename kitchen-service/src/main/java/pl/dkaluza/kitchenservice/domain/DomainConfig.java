package pl.dkaluza.kitchenservice.domain;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import pl.dkaluza.kitchenservice.ports.out.CookRepository;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

@Configuration
class DomainConfig {
    private final PlatformTransactionManager txManager;

    DomainConfig(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Bean
    DefaultKitchenService defaultUserService(RecipeRepository recipeRepository, CookRepository cookRepository) {
        var kitchenService = new DefaultKitchenService(recipeRepository, cookRepository);
        var attrSource = new NameMatchTransactionAttributeSource();

        attrSource.addTransactionalMethod("addRecipe", new DefaultTransactionAttribute());
        attrSource.addTransactionalMethod("registerCook", new DefaultTransactionAttribute());
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
