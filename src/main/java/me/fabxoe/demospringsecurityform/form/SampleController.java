package me.fabxoe.demospringsecurityform.form;

import me.fabxoe.demospringsecurityform.account.AccountContext;
import me.fabxoe.demospringsecurityform.account.AccountRepository;
import me.fabxoe.demospringsecurityform.account.AccountService;
import me.fabxoe.demospringsecurityform.account.UserAccount;
import me.fabxoe.demospringsecurityform.common.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.concurrent.Callable;

@Controller
public class SampleController {

    @Autowired
    SampleService sampleService;

    @Autowired
    AccountRepository accountRepository;

    @GetMapping("/")
//    public String index(Model model, Principal principal) {//이건 자바에서 제공하는 principal이다. Spring Security의 principal이 아님.
    public String index(Model model, @AuthenticationPrincipal UserAccount userAccount) {//이건 자바에서 제공하는 principal이다. Spring Security의 principal이 아님.
        if (userAccount == null) {
            model.addAttribute("message", "Hello Spring Security");
        } else {
            model.addAttribute("message", "Hello, " + userAccount.getUsername());
        }
        return "index";
    }

    @GetMapping("/info")
//    public String info(Model model, Principal principal) {
    public String info(Model model, Principal principal) {
        model.addAttribute("message", "Info");
        return "info";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("message", "Hello " + principal.getName());
//        AccountContext.setAccount(accountRepository.findByUsername(principal.getName()));
        sampleService.dashboard();
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        model.addAttribute("message", "Hello Admin, " + principal.getName());
        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        model.addAttribute("message", "Hello User, " + principal.getName());
        return "user";
    }

    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> asyncHandler() {//톰캣이 할당해준 별도의 NIO스레드
        SecurityLogger.log("MVC");
        return () -> {//별도의 스레드
            SecurityLogger.log("Callable");
            return "Async Handler";
        };
    }
    //스레드가 다름에도 동일한 principal이 참조되고 있다. Principal: org.springframework.security.core.userdetails.User@915230d7:

    @GetMapping("/async-service")
    @ResponseBody
    public String asyncService() {//톰캣이 할당해준 별도의 NIO스레드
        SecurityLogger.log("MVC, before async service");
        sampleService.asyncService();
        SecurityLogger.log("MVC, after async service");
        return "Async Service";
    }
}
