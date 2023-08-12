package com.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.springboot.app.auth.handler.LoginSuccesHandler;
import com.springboot.app.models.service.JpaUserDetailsService;


@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityConfig {
	
	@Autowired
	private LoginSuccesHandler successHandler;
	
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JpaUserDetailsService userDetailService;
    
	/* 
	
	@Autowired
	private DataSource dataSource;
	
    @Bean
	public UserDetailsService userDetailsService() throws Exception {
		
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		
		manager.createUser(User.withUsername("user").password(this.passwordEncoder.encode("user")).roles("USER").build());
		manager.createUser(User.withUsername("admin").password(this.passwordEncoder.encode("admin")).roles("ADMIN", "USER").build());
		
		return manager;
	}
	*/
    
    @Autowired
    public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
    	
    	build.userDetailsService(userDetailService)
    		.passwordEncoder(passwordEncoder);
    }
    
    /*
    @Bean
    public UserDetailsService userDetailsService(AuthenticationManagerBuilder build) throws Exception {
    	
    	build.jdbcAuthentication()
    		.dataSource(dataSource)
    		.passwordEncoder(passwordEncoder)
    		.usersByUsernameQuery("select username, password, enabled from users where username=?")
    		.authoritiesByUsernameQuery("select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id) where u.username=?");
    	
    	return build.getDefaultUserDetailsService();
    }
    */
    
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests((authz) -> {
			try {
				authz.requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/locale").permitAll()
				/*.requestMatchers("/ver/**").hasAnyRole("USER")*/
				/*.requestMatchers("/uploads/**").hasAnyRole("USER")*/
				/*.requestMatchers("/factura/**").hasRole("ADMIN")*/
				/*.requestMatchers("/form/**").hasRole("ADMIN")*/
				/*.requestMatchers("/eliminar/**").hasRole("ADMIN")*/
				.anyRequest().authenticated()
				.and()
					.formLogin().successHandler(successHandler)
					.loginPage("/login")
					.permitAll()
				.and()
				.logout().permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/error_403");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return http.build();
	}
	
	
	

}
