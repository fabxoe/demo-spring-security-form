package me.fabxoe.demospringsecurityform.config;

import me.fabxoe.demospringsecurityform.account.AccountService;
import me.fabxoe.demospringsecurityform.common.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    public AccessDecisionManager accessDecisionManager() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
//
//        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
//        handler.setRoleHierarchy(roleHierarchy);
//
//        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
//        webExpressionVoter.setExpressionHandler(handler);
//
//        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);
//
//        return new AffirmativeBased(voters);
//    }

    @Autowired
    AccountService accountService;

    public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        return handler;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().mvcMatchers("/favicon.ico");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class)
                .authorizeRequests()
                .mvcMatchers("/", "/info","/account/**","/signup").permitAll() //동적인 자원에는 인증과 인가 처리가 필요하다. WebSecurity보다 HttpSecurity사용 하길 권장. admin일때도 아닐때도 filter를 타야한다. 이 방법이 맞다.
                .mvcMatchers("/admin").hasRole("ADMIN")
                .mvcMatchers("/user").hasRole("USER")
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() //결과적으로 정적자원에 인증과 인가 처리를 안할꺼면(=시큐리티를 안쓸꺼면) 애초에 필터체인을 안타게 하는게 낫다. 이 방법 쓰지말라 리소스 낭비발생. WebSecurity 설정방법을 추천한다.
                .anyRequest().authenticated()
//                .accessDecisionManager(accessDecisionManager()
                .expressionHandler(expressionHandler());
        http.formLogin()
            .loginPage("/login")//커스텀한 로그인 페이지를 부여하겠다는 의미이다. 이걸 작성하면 FilterChainProxy의 목록에서 Filter DefaultLoginGeneratingFilter와 DefaultLogoutGeneratingFilter 2개가 사라진다.
            .permitAll();

        http.rememberMe()
//                .alwaysRemember(false) //기본값은 false이다. 체크박스 안해도 쿠키를 굽는다.
//                .tokenValiditySeconds() //기본값은 2주이다.
//                .useSecureCookie(true)// https만 이 쿠키에 접근가능하도록 한다.
                .userDetailsService(accountService)
                .key("remember-me-sample");

        http.httpBasic();
//        http.csrf().disable();
        http.logout()
//                .logoutSuccessHandler()
//                .logoutUrl("/logout")//작성하지 않으면 "/logout" 기본적용됨
                .logoutSuccessUrl("/");
//                .invalidateHttpSession(true)//작성하지 않으면 "true" 기본적용됨
//                .deleteCookies("");//쿠키를 지우고 새로운 쿠키를 받아야 이용할 수 있도록 하고 싶다면 쿠키의 이름을 작성
        http.exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = principal.getUsername();
                    System.out.println(username + " is denied to access " + request.getRequestURI());
                    response.sendRedirect("/access-denied");
                });
//                .accessDeniedPage("/access-denied"); //이 보다는 서버쪽에는 로그도 뜨게 만들자.
//        http.sessionManagement()
//                .sessionFixation()
//                    .changeSessionId()
//                .maximumSessions(1)
        ;

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    //아래처럼 명시적으로 하지않아도 UserDetailsService가 빈으로 등록되어 있으면 자동으로 사용한다. 패스워드 엔코더도 명시적으로 하지 않아도 빈으로 등록만 되어있으면 자동사용임
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(accountService);
//    }
}
