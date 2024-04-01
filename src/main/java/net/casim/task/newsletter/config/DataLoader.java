package net.casim.task.newsletter.config;

import net.casim.task.newsletter.model.Newsletter;
import net.casim.task.newsletter.model.User;
import net.casim.task.newsletter.repository.NewsletterRepository;
import net.casim.task.newsletter.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final NewsletterRepository newsletterRepository;
    private final UserRepository userRepository;

    public DataLoader(NewsletterRepository newsletterRepository, UserRepository userRepository) {
        this.newsletterRepository = newsletterRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 5; i++) {
            Newsletter newsletter = createRandomNewsletter(i);
            newsletterRepository.save(newsletter);

            User user = createRandomUser(i);
            userRepository.save(user);

        }
    }

    private Newsletter createRandomNewsletter(int i) {
        Newsletter newsletter = new Newsletter();
        newsletter.setTitle("Newsletter " + i);
        newsletter.setContent("Content of Newsletter " + i);
        newsletter.setPublicationDate(String.valueOf(LocalDate.now()));
        return newsletter;
    }

    private User createRandomUser(int i) {
        return User.builder().username("User" + i).build();
    }
}

