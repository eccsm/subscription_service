package net.casim.task.newsletter.service;

import javassist.NotFoundException;
import net.casim.task.newsletter.model.Newsletter;
import net.casim.task.newsletter.model.SubscribeRequest;
import net.casim.task.newsletter.model.Subscription;
import net.casim.task.newsletter.model.User;
import net.casim.task.newsletter.model.dto.NewsletterDTO;
import net.casim.task.newsletter.model.dto.UserDTO;
import net.casim.task.newsletter.repository.NewsletterRepository;
import net.casim.task.newsletter.repository.SubscriptionRepository;
import net.casim.task.newsletter.repository.UserRepository;
import net.casim.task.newsletter.service.interfaces.ISubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SubscriptionService implements ISubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final NewsletterRepository newsletterRepository;

    private final UserRepository userRepository;


    public SubscriptionService(SubscriptionRepository subscriptionRepository, NewsletterRepository newsletterRepository, UserRepository userRepository1) {
        this.subscriptionRepository = subscriptionRepository;
        this.newsletterRepository = newsletterRepository;
        this.userRepository = userRepository1;
    }


    @Override
    @Transactional
    public void subscribe(SubscribeRequest subscribeRequest) throws NotFoundException {
        Long userId = subscribeRequest.getUserId();
        Long newsletterId = subscribeRequest.getNewsletterId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new NotFoundException("Newsletter not found"));

        Subscription subscription = subscriptionRepository.findByUser_UserIdAndNewsletter_NewsletterId(userId, newsletterId).orElse(null);

        if (subscription == null) {
            subscription = Subscription.builder()
                    .subscribed(true)
                    .user(user)
                    .newsletter(newsletter)
                    .createdAt(LocalDateTime.now())
                    .build();
        } else {
            subscription.setSubscribed(true);
            subscription.setCreatedAt(LocalDateTime.now());
        }

        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void unsubscribe(SubscribeRequest subscribeRequest) throws NotFoundException {
        Long userId = subscribeRequest.getUserId();
        Long newsletterId = subscribeRequest.getNewsletterId();

        Subscription subscription = subscriptionRepository.findByUser_UserIdAndNewsletter_NewsletterId(userId, newsletterId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        subscription.setSubscribed(false);
        subscriptionRepository.save(subscription);

        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new NotFoundException("Newsletter not found"));

        newsletterRepository.save(newsletter);

        convertToDTO(newsletter);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean checkSubscription(Long newsletterId, Long userId) {
        return subscriptionRepository.existsByUser_UserIdAndNewsletter_NewsletterIdAndSubscribedTrue(userId, newsletterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getSubscribersBeforeDate(Long newsletterId, String date) {
        LocalDateTime targetDate = LocalDateTime.parse(date);
        List<Subscription> subscribers = subscriptionRepository.findByNewsletter_NewsletterIdAndSubscribedTrue(newsletterId).stream()
                .filter(subscription -> subscription.getCreatedAt() != null && subscription.getCreatedAt().isBefore(targetDate))
                .collect(Collectors.toList());

        return getUserDTOS(subscribers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getSubscribersAfterDate(Long newsletterId, String date) {
        LocalDateTime targetDate = LocalDateTime.parse(date);
        List<Subscription> subscribers = subscriptionRepository.findByNewsletter_NewsletterIdAndSubscribedTrue(newsletterId).stream()
                .filter(subscription -> subscription.getCreatedAt() != null && subscription.getCreatedAt().isAfter(targetDate))
                .collect(Collectors.toList());

        return getUserDTOS(subscribers);
    }

    @Override
    public Newsletter getNewsletterWithSubscribers(Long newsletterId) throws NotFoundException {
        List<Subscription> subscribers = subscriptionRepository.findByNewsletter_NewsletterIdAndSubscribedTrue(newsletterId);

        if (subscribers.isEmpty()) {
            throw new NotFoundException("Newsletter not found or no subscribers");
        }

        Newsletter newsletter = subscribers.get(0).getNewsletter();
        newsletter.setSubscribedUsers(subscribers);

        return newsletter;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Newsletter> getAllNewsletters() {
        return newsletterRepository.findAll();
    }

    @Override
    public User getUser(Long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void convertToDTO(Newsletter newsletter) {
        if (newsletter == null)
            throw new IllegalArgumentException("Newsletter cannot be null");

        List<String> subscribedUsernames = Optional.ofNullable(newsletter.getSubscribedUsers())
                .orElse(Collections.emptyList()).stream()
                .filter(subscription -> subscription != null && subscription.isSubscribed())
                .map(subscription -> {
                    User user = subscription.getUser();
                    return (user != null) ? user.getUsername() : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        new NewsletterDTO(newsletter.getNewsletterId(), newsletter.getTitle(),
                newsletter.getContent(), newsletter.getPublicationDate(), subscribedUsernames);
    }

    private List<UserDTO> getUserDTOS(List<Subscription> subscribers) {
        return subscribers.stream()
                .map(subscription -> {
                    User user = subscription.getUser();
                    if (user != null) {
                        List<NewsletterDTO> newsletterDTOs = user.getSubscriptions().stream()
                                .filter(sub -> sub != null && sub.isSubscribed() && sub.getNewsletter() != null)
                                .map(sub -> sub.getNewsletter().toDTO())
                                .collect(Collectors.toList());

                        return new UserDTO(user.getUserId(), user.getUsername(), newsletterDTOs);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
