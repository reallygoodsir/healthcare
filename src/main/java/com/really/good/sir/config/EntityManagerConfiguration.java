package com.really.good.sir.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EntityManagerConfiguration {

    private static EntityManagerFactory emf;

    private static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            Properties props = new Properties();

            try (InputStream is = EntityManagerConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties")) {

                if (is == null) {
                    throw new RuntimeException("Cannot find application.properties in classpath");
                }

                props.load(is);

            } catch (IOException e) {
                throw new RuntimeException("Failed to load application.properties", e);
            }

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(props)
                    .build();

            MetadataSources sources = new MetadataSources(registry);

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

            Metadata metadata = sources.getMetadataBuilder().build();

            SessionFactory sessionFactory = metadata
                    .getSessionFactoryBuilder()
                    .build();

            emf = sessionFactory.unwrap(EntityManagerFactory.class);
        }

        return emf;
    }

    public static EntityManager getEntityManager() {
        EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
        return entityManagerFactory.createEntityManager();
    }
}
