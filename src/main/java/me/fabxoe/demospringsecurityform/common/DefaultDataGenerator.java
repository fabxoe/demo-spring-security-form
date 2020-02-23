package me.fabxoe.demospringsecurityform.common;

import me.fabxoe.demospringsecurityform.account.Account;
import me.fabxoe.demospringsecurityform.account.AccountService;
import me.fabxoe.demospringsecurityform.book.Book;
import me.fabxoe.demospringsecurityform.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultDataGenerator implements ApplicationRunner {

    @Autowired
    AccountService accountService;

    @Autowired
    BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account sungmin = createUser("sungmin");
        Account fabxoe = createUser("fabxoe");

        createBook("spring", sungmin);
        createBook("hibernate", fabxoe);
    }

    private void createBook(String title, Account sungmin) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(sungmin);
        bookRepository.save(book);
    }

    private Account createUser(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword("123");
        account.setRole("USER");
        return  accountService.createNew(account);
    }
}
