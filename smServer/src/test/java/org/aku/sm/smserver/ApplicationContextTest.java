package org.aku.sm.smserver;

import org.aku.sm.smserver.repository.DoctorRepository;
import org.aku.sm.smserver.repository.MedicationIntakeRepository;
import org.aku.sm.smserver.repository.MedicationRepository;
import org.aku.sm.smserver.repository.PatientRepository;
import org.aku.sm.smserver.repository.SymptomCheckinRepository;
import org.aku.sm.smserver.repository.SymptomPhotoRepository;
import org.aku.sm.smserver.repository.UserRepository;
import org.aku.sm.smserver.repository.VoiceRecordingRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Setup a Spring context to run integration tests
 * - services injected
 * - access to mock data
 */


//Tell Spring that this object represents a Configuration for the
//application
@Configuration

//Tell Spring to automatically inject any dependencies that are marked in
//our classes with @Autowired
@EnableAutoConfiguration(exclude = { 
		org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
		org.aku.sm.smserver.auth.SecurityConfiguration.class, org.aku.sm.smserver.Application.class })

//Tell Spring to turn on WebMVC (e.g., it should enable the DispatcherServlet
//so that requests can be routed to our Controllers)
@EnableWebMvc


//Tell Spring to automatically create a JPA implementation of our
//VideoRepository
@EnableJpaRepositories(basePackageClasses = {
		DoctorRepository.class,
		MedicationIntakeRepository.class,
		MedicationRepository.class,
		PatientRepository.class,
		SymptomCheckinRepository.class,
		SymptomPhotoRepository.class,
		UserRepository.class,
		VoiceRecordingRepository.class})

//Tell Spring to go and scan our controller package (and all sub packages) to
//find any Controllers or other components that are part of our applciation.
//Any class in this package that is annotated with @Controller is going to be
//automatically discovered and connected to the DispatcherServlet.
@ComponentScan

// <global-method-security pre-post-annotations="enabled"/>

public class ApplicationContextTest {

	
//		spring.jpa.show-sql - show sql in the logs
//		spring.jpa.database-platform - for hibernate this is the dialect
//		spring.jpa.database - The database used (don't use together with database-platform!).
//		spring.jpa.generate-ddl - should the ddl be generated, default false (overriden by the spring.jpa.hibernate.ddl-auto property)	
	
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.HSQL);
        return hibernateJpaVendorAdapter;
    }	
	
//    @Bean
//    MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize("-1");
//        factory.setMaxRequestSize("-1");
//        return factory.createMultipartConfig();
//    }    
    
//	
//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }
}


	