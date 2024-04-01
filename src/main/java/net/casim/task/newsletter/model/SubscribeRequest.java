package net.casim.task.newsletter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscribeRequest {
    private Long userId;
    private Long newsletterId;
}
