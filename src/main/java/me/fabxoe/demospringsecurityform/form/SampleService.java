package me.fabxoe.demospringsecurityform.form;

import me.fabxoe.demospringsecurityform.account.Account;
import me.fabxoe.demospringsecurityform.account.AccountContext;
import me.fabxoe.demospringsecurityform.common.SecurityLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

@Service
public class SampleService {

    @Secured("ROLE_USER")
//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
//    @RolesAllowed("ROLE_USER")
//    @PreAuthorize("ROLE_USER")
//    @PreAuthorize("hasRole('USER')")
//    @PostAuthorize("ROLE_USER")
    public void dashboard() {
//        Account account = AccountContext.getAccount();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("======");
        System.out.println(authentication);//2ab2d733
        System.out.println(userDetails.getUsername());
    }

    @Async
    public void asyncService() {
        SecurityLogger.log("Async Service");
        System.out.println("Async service is called/");
    }
}
