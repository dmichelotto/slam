package pl.cyfronet.ltos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import pl.cyfronet.ltos.security.policy.Activity;
import pl.cyfronet.ltos.security.policy.Permissions;

/**
 * @author bwilk
 *
 */
@Configuration
@Profile("production")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends SecurityConfigBase {
    @Value("${unity.entryPointUnityUrl}")
    protected String entryPointUnityUrl;

    @Value("${unity.entryPointAuthUrl}")
    protected String entryPointAuthUrl;

    @Value("${unity.unauthorizedAction}")
    protected String unauthorizedAction;

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint(entryPointAuthUrl);
    }

    @Bean
    public OpenIDConnectAuthenticationFilter openIdConnectAuthenticationFilter() {
        return new OpenIDConnectAuthenticationFilter(entryPointUnityUrl);
    }

    @Bean
    public OAuth2ClientContextFilter oAuth2ClientContextFilter() {
        return new OAuth2ClientContextFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(entryPointAuthUrl, unauthorizedAction, "/bootstrap-3.3.1-dist/**", "/static/**","/resources/**", "/bootstrap/**",
                        "/css/**", "/images/**", "/js/**", "/javascript/**", "/fonts/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(oAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(openIdConnectAuthenticationFilter(), OAuth2ClientContextFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
        ;
    }
}