package technou.com.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import technou.com.dao.UsersRepository;
import technou.com.service.AppUserDetailsService;


@Configuration
@EnableWebSecurity //(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableJpaRepositories(basePackageClasses=UsersRepository.class)
@ComponentScan(basePackages = "technou.com")
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
	
	
	@Autowired
	AppSuccessHandler appSuccessHandler;
	
	@Autowired
	AppUserDetailsService appUserDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	
	/**
	 * To tell the authentication manager what kind of authentication you want
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}


	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
			
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
			
		provider.setPasswordEncoder(passwordEncoder);

		provider.setUserDetailsService(appUserDetailsService);
			
		return provider;
	}
		
	 @Override
     public void configure(WebSecurity web) throws Exception {
         web.ignoring()
            .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**","/vendor/**","/fonts/**");
     }
	

	@Override //here to secure the access to the app
	protected void configure(HttpSecurity http) throws Exception {
		
		//The order of the actions is very important
		http
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/admin/**").hasAnyRole(AppUserRole.ADMIN.name(), AppUserRole.ADMINTRAINEE.name()) //protect this path to be accessed only by admins
			.antMatchers("/user/**").hasAnyRole(AppUserRole.USER.name()) //protect this path to be accessed only by users
			.antMatchers("/index", "/createUser", "/register", 
					"/register/checkUsername/**",
					"/css/**", "/js/**", "/img/**","/webjars/**").permitAll() //accessible without authentication
			.anyRequest().authenticated() //any request must be authenticated
			.and()
			.formLogin()
				.loginPage("/login").permitAll()
				.successHandler(appSuccessHandler)
				.failureUrl("/login?error") //customize the failed login page
			.and()
			.rememberMe() //defaults 2 weeks
				.tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21)) //extend your session with remember-me
				.key("somethingverysecured")
				.userDetailsService(appUserDetailsService)
			.and()
		    .logout()
		    	.logoutUrl("/logout")
		    	.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) //Because we disabled csrf, otherwise its should be post / delete this line
		       	.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID", "remember-me")
		        .logoutSuccessUrl("/index");
	}
	
	

}
