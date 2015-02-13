package org.aku.sm.smserver.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
// Setup Spring Security to intercept incoming requests to the Controllers
@EnableWebSecurity

/**
 * enable use of el based security annotation
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * hack to avoid exception during test;
	 * org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'securityConfiguration': 
	 * Injection of autowired dependencies failed; nested exception is java.lang.IllegalStateException: 
	 * Cannot apply org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer@55e903a to already built object
	 */
	public static boolean disableForTest = false;
	
	
	@Autowired
	JpaUserDetailService jpaUserDetailService;
	
	public static final String LOGIN_PATH = "/login";
	public static final String LOGOUT_PATH = "/logout";
	
	// This anonymous inner class' onAuthenticationSuccess() method is invoked
	// whenever a client successfully logs in. The class just sends back an
	// HTTP 200 OK status code to the client so that they know they logged
	// in correctly. The class does not redirect the client anywhere like the
	// default handler does with a HTTP 302 response. The redirect has been
	// removed to be friendlier to mobile clients and Retrofit.
	private static final AuthenticationSuccessHandler NO_REDIRECT_SUCCESS_HANDLER = new AuthenticationSuccessHandler() {
		@Override
		public void onAuthenticationSuccess(HttpServletRequest request,
				HttpServletResponse response, Authentication authentication)
				throws IOException, ServletException {
			response.setStatus(HttpStatus.OK.value());
		}
	};
	
	// Normally, the logout success handler redirects the client to the login page. We
	// just want to let the client know that it succcessfully logged out and make the
	// response a bit of JSON so that Retrofit can handle it. The handler sends back
	// a 200 OK response and an empty JSON object.
	private static final LogoutSuccessHandler JSON_LOGOUT_SUCCESS_HANDLER = new LogoutSuccessHandler() {
		@Override
		public void onLogoutSuccess(HttpServletRequest request,
				HttpServletResponse response, Authentication authentication)
				throws IOException, ServletException {
			response.setStatus(HttpStatus.OK.value());
			response.setContentType("application/json");
			response.getWriter().write("{}");
		}
	};
	
	/**
	 * This method is used to inject access control policies into Spring
	 * security to control what resources / paths / http methods clients have
	 * access to.
	 */
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		// By default, Spring inserts a token into web pages to prevent
		// cross-site request forgery attacks. 
		// See: http://en.wikipedia.org/wiki/Cross-site_request_forgery
		//
		// Unfortunately, there is no easy way with the default setup to communicate 
		// these CSRF tokens to a mobile client so we disable them.
		// Don't worry, the next iteration of the example will fix this
		// problem.
		http.csrf().disable();
		// We don't want to cache requests during login
		http.requestCache().requestCache(new NullRequestCache());
		
		
	    http.authorizeRequests()
	    	.antMatchers("/**", "/login/**").hasAnyRole("DOCTOR", "PATIENT")
	    	.and()
	    	.httpBasic();		
		
		http.authorizeRequests().antMatchers("/doctor/**").hasRole("DOCTOR");
		http.authorizeRequests().antMatchers("/patient/**").hasAnyRole("DOCTOR", "PATIENT");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/symptomCheckin", "/checkin").hasRole("PATIENT");
		http.authorizeRequests().antMatchers(HttpMethod.GET,  "/symptomCheckin", "/checkin").hasAnyRole("DOCTOR", "PATIENT");	
		http.authorizeRequests().antMatchers(HttpMethod.POST,  "/notify/**").hasAnyRole("DOCTOR", "PATIENT");	
		//http.authorizeRequests().antMatchers("/patient").hasAnyRole("DOCTOR", "PATIENT");

		/**
		 * For the meantime disable URLs exposed by spring data rest
		 */
		http.authorizeRequests().antMatchers(
				"/users",
				"/patients",
				"/doctors",
				"/symptomCheckins",
				"/medicationIntakes",
				"/symptomPhotos",
				"/voiceRecordings",
				"/symptomCheckinImages",
				"/medications").denyAll();
	}

	

	
	@Override
	protected UserDetailsService userDetailsService() {
		return jpaUserDetailService;
	}


	public void setJpaUserDetailService(JpaUserDetailService jpaUserDetailService) {
		this.jpaUserDetailService = jpaUserDetailService;
	}
	

   @Autowired
    public void registerAuthentication(AuthenticationManagerBuilder auth) throws     Exception {
	   if (disableForTest) return;
       auth.userDetailsService(jpaUserDetailService);
    }	

	
}
