package net.casim.task.newsletter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.casim.task.newsletter.model.dto.NewsletterDTO;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Newsletter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsletterId;

    private String title;

    private String content;

    private String publicationDate;

    @OneToMany(mappedBy = "newsletter")
    private List<Subscription> subscribedUsers;

    public NewsletterDTO toDTO() {
        List<String> subscribedUsernames = subscribedUsers.stream()
                .filter(Subscription::isSubscribed)
                .map(subscription -> subscription.getUser().getUsername())
                .collect(Collectors.toList());

        return new NewsletterDTO(newsletterId, title, content, publicationDate, subscribedUsernames);
    }
}
