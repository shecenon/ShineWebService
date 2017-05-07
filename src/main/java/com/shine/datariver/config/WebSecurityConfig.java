package com.shine.datariver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.shine.datariver.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    UserDetailsService userDetailsService;

    @Autowired
    PersistentTokenRepository tokenRepository;
    /*
       @Bean
       @Qualifier("userDetailsServiceImpl")
       UserDetailsService customUserService(){ 
       return new UserDetailsServiceImpl(); 
       }*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxx======================================");
        logger.info("get PasswordEncoder");
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService(customUserService()); //3		
        auth.userDetailsService(userDetailsService); //3	
	
//user in memory

auth.inMemoryAuthentication()
.withUser("hill").password("asd@MBB").roles("ADMIN")
.and()
.withUser("zen").password("zen").roles("USER")
;

    }

    

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/", "/welcome").access("hasRole('USER') or hasRole('ADMIN') or hasRole('DBA')")
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            //.antMatchers("/resources/**").permitAll()
            //.anyRequest().authenticated()
            .and()
            .formLogin().loginPage("/login").failureUrl("/login?error=100").defaultSuccessUrl("/welcome").usernameParameter("username").passwordParameter("password").permitAll()
            .and()
            .rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository).tokenValiditySeconds(86400)
            //.and()
            //.csrf()
            //.and()
            //.exceptionHandling().accessDeniedPage("/Access_Denied")
            .and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll()
            ;
    }

//@Override
//public void configure(WebSecurity web) throws Exception {
//super.configure(web);
//}
   

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PersistentTokenBasedRememberMeServices getPersistentTokenBasedRememberMeServices() {
        PersistentTokenBasedRememberMeServices tokenBasedservice = new PersistentTokenBasedRememberMeServices(
                "remember-me", userDetailsService, tokenRepository);
        return tokenBasedservice;
    }
}
