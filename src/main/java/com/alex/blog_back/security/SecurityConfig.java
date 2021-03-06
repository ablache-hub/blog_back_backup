package com.alex.blog_back.security;

import com.alex.blog_back.jwt.JwtAuthFilter;
import com.alex.blog_back.jwt.JwtRequestTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //Permet l'utilisation de PreAuthorize dans le controller
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //TODO Tester avec cette variable plutôt que le bean
//    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilter(new JwtAuthFilter(authenticationManager()))
                .addFilterAfter(new JwtRequestTokenFilter(), JwtAuthFilter.class)
                .authorizeRequests()
//              .antMatchers(/*"/", "/index",*/ "/registration").permitAll().
                .anyRequest()
                .authenticated()

        ;
               /* .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();*/

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/api/user/save",
                "/error",
                "/api/role/save",
                "/api/role/addroleuser",
                "/article/get/**",
                "/api/user/get/**",
                "/api/categorie/**",
                "/file/**");
//        web.ignoring().antMatchers("**");
    }
}
