package net.casim.task.newsletter.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean subscribed;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "newsletter_id")
    private Newsletter newsletter;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
}
