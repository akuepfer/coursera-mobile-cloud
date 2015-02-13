package org.aku.sm.smserver;

import javax.servlet.MultipartConfigElement;

import org.aku.sm.smserver.auth.SecurityConfiguration;
import org.aku.sm.smserver.repository.DoctorRepository;
import org.aku.sm.smserver.repository.MedicationIntakeRepository;
import org.aku.sm.smserver.repository.MedicationRepository;
import org.aku.sm.smserver.repository.PatientRepository;
import org.aku.sm.smserver.repository.SymptomCheckinRepository;
import org.aku.sm.smserver.repository.SymptomPhotoRepository;
import org.aku.sm.smserver.repository.UserRepository;
import org.aku.sm.smserver.repository.VoiceRecordingRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Startup options for local testing with HTTPS
 * -Dserver.port=8443 -Dserver.ssl.key-store=file:///home/armin/ws/CourseraMobileCloud/smServer/src/main/resources/tomcathost.jks -Dserver.ssl.key-store-password=importkey
 */


//Tell Spring that this object represents a Configuration for the
//application
@Configuration

//Tell Spring to turn on WebMVC (e.g., it should enable the DispatcherServlet
//so that requests can be routed to our Controllers)
@EnableWebMvc

//Tell Spring to automatically inject any dependencies that are marked in
//our classes with @Autowired
@EnableAutoConfiguration

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

//We use the @Import annotation to include our SecurityConfiguration
//as part of this configuration so that we can have security
//setup by Spring
@Import(SecurityConfiguration.class)

// <global-method-security pre-post-annotations="enabled"/>

public class Application {

	
//	spring.jpa.show-sql - show sql in the logs
//	spring.jpa.database-platform - for hibernate this is the dialect
//	spring.jpa.database - The database used (don't use together with database-platform!).
//	spring.jpa.generate-ddl - should the ddl be generated, default false (overriden by the spring.jpa.hibernate.ddl-auto property)	
	
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.HSQL);
        return hibernateJpaVendorAdapter;
    }	
	
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("-1");
        factory.setMaxRequestSize("-1");
        return factory.createMultipartConfig();
    }    
    
	
    public static void main(String[] args) {
    	/**
    	 * Display SSL debug info
    	 */
    	//System.setProperty("javax.net.debug", "ssl");  
    	
    	/**
    	 * Start tomcat & application
    	 */
        SpringApplication.run(Application.class, args);
    }
    
	
}
