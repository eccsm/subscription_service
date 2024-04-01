package net.casim.task.newsletter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NewsletterDTO {
    private Long newsletterId;
    private String title;
    private String content;
    private String publicationDate;
    private List<String> subscribedUsernames;
}
