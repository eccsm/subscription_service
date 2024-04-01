package net.casim.task.newsletter.repository;

import net.casim.task.newsletter.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByNewsletter_NewsletterIdAndSubscribedTrue(Long newsletterId);

    boolean existsByUser_UserIdAndNewsletter_NewsletterIdAndSubscribedTrue(Long userId, Long newsletterId);

    Optional<Subscription> findByUser_UserIdAndNewsletter_NewsletterId(Long userId, Long newsletterId);
}
