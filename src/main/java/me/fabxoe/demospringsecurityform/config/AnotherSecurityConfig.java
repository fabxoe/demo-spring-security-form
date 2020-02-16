package me.fabxoe.demospringsecurityform.config;

import me.fabxoe.demospringsecurityform.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 100)
//@EnableWebSecurity //안 달아도 스프링부트가 알아서 처리함
public class AnotherSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/account/**")
                .authorizeRequests()
                .anyRequest().permitAll();
    }

    //아래처럼 명시적으로 하지않아도 UserDetailsService가 빈으로 등록되어 있으면 자동으로 사용한다. 패스워드 엔코더도 명시적으로 하지 않아도 빈으로 등록만 되어있으면 자동사용임
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(accountService);
//    }
}
