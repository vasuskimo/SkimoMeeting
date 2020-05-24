package tv.skimo.meeting.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
   @Override
   protected void configure(HttpSecurity http) throws Exception {
	   
       String[] resources = new String[]{
               "/","/css/**","/js/**","/img/**",
               "/oauth.html", "/about.html", "/copyright.html", 
               "/privacy.html", "/plans.html", "/terms.html"
       };

      http
         .csrf()
         .disable()
         .antMatcher("/**")
         .authorizeRequests()
         .antMatchers(resources)
         .permitAll()
         .anyRequest()
         .authenticated()   
        .and()
        .oauth2Login();
   }

}