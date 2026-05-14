package com.really.good.sir.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.really.good.sir.repository")
public class EntityManagerConfiguration {

    // These will be automatically injected from the active profile's properties file
    @Value("${hibernate.connection.driver_class}")
    private String driverClass;

    @Value("${hibernate.connection.url}")
    private String url;

    @Value("${hibernate.connection.username}")
    private String username;

    @Value("${hibernate.connection.password}")
    private String password;

    @Value("${hibernate.dialect:org.hibernate.dialect.MySQL8Dialect}")
    private String dialect;

    @Value("${hibernate.show_sql:false}")
    private boolean showSql;

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> start");
        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", driverClass);
        props.setProperty("hibernate.connection.url", url);
        props.setProperty("hibernate.connection.username", username);
        props.setProperty("hibernate.connection.password", password);
        props.setProperty("hibernate.dialect", dialect);
        props.setProperty("hibernate.show_sql", String.valueOf(showSql));
        props.setProperty("hibernate.format_sql", "false");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(props)
                .build();

        MetadataSources sources = new MetadataSources(registry);

        // Add all your entities here
        sources.addAnnotatedClass(com.really.good.sir.entity.CredentialEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.UserSessionEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.DoctorEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.PatientEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.AppointmentEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.AppointmentOutcomeEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.DoctorScheduleEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.PatientAppointmentEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.PatientAppointmentOutcomeEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.ServiceEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.SpecializationEntity.class);
        sources.addAnnotatedClass(com.really.good.sir.entity.AuditEntity.class);


        Metadata metadata = sources.getMetadataBuilder().build();

        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> end");

        return sessionFactory.unwrap(EntityManagerFactory.class);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}