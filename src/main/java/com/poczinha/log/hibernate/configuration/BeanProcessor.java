package com.poczinha.log.hibernate.configuration;

import com.poczinha.log.hibernate.domain.Constants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

import java.lang.reflect.Field;

public class BeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LocalContainerEntityManagerFactoryBean) {
            injectEntityScan((LocalContainerEntityManagerFactoryBean) bean);
        }
        return bean;
    }

    private void injectEntityScan(LocalContainerEntityManagerFactoryBean em) {
        try {
            Field field = LocalContainerEntityManagerFactoryBean.class.getDeclaredField("internalPersistenceUnitManager");
            field.setAccessible(true);

            DefaultPersistenceUnitManager dpum = ((DefaultPersistenceUnitManager) field.get(em));

            Field pts = DefaultPersistenceUnitManager.class.getDeclaredField("packagesToScan");
            pts.setAccessible(true);

            String[] packagesToScan = (String[]) pts.get(dpum);

            int size = packagesToScan.length + 1;
            String[] newPackagesToScan = new String[size];

            System.arraycopy(packagesToScan, 0, newPackagesToScan, 0, size - 1);

            newPackagesToScan[size - 1] = Constants.LOG_ENTITY_SCAN;

            pts.set(dpum, newPackagesToScan);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
