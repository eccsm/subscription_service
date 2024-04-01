package net.casim.task.newsletter;

import javassist.NotFoundException;
import net.casim.task.newsletter.controller.SubscriptionController;
import net.casim.task.newsletter.model.*;
import net.casim.task.newsletter.model.dto.NewsletterDTO;
import net.casim.task.newsletter.model.dto.UserDTO;
import net.casim.task.newsletter.service.SubscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;


class NewsletterApplicationTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void subscribe_ShouldReturnBadRequest_WhenUserIsAlreadySubscribed() throws NotFoundException {
        SubscribeRequest subscribeRequest = new SubscribeRequest(1L, 1L);
        when(subscriptionService.checkSubscription(subscribeRequest.getNewsletterId(), subscribeRequest.getUserId()))
                .thenReturn(true);

        ResponseEntity<SubscriptionResponse> responseEntity = subscriptionController.subscribe(subscribeRequest);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals("User is already subscribed", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        verify(subscriptionService, never()).subscribe(subscribeRequest);
    }

    @Test
    void unsubscribe_ShouldReturnBadRequest_WhenUserIsNotSubscribed() throws NotFoundException {
        SubscribeRequest subscribeRequest = new SubscribeRequest(1L, 1L);
        when(subscriptionService.checkSubscription(subscribeRequest.getNewsletterId(), subscribeRequest.getUserId()))
                .thenReturn(false);

        ResponseEntity<SubscriptionResponse> responseEntity = subscriptionController.unsubscribe(subscribeRequest);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals("User is not subscribed", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        verify(subscriptionService, never()).unsubscribe(subscribeRequest);
    }

    @Test
    void checkSubscription_ShouldReturnUserIsSubscribed() {
        Long newsletterId = 1L;
        Long userId = 1L;
        when(subscriptionService.checkSubscription(newsletterId, userId)).thenReturn(true);

        ResponseEntity<Boolean> responseEntity = subscriptionController.checkSubscription(newsletterId, userId);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void checkSubscription_ShouldReturnUserIsNotSubscribed() {
        Long newsletterId = 1L;
        Long userId = 1L;
        when(subscriptionService.checkSubscription(newsletterId, userId)).thenReturn(false);

        ResponseEntity<Boolean> responseEntity = subscriptionController.checkSubscription(newsletterId, userId);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertFalse(Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void getSubscribersBeforeDate_ShouldReturnSubscribers() {
        Long newsletterId = 1L;
        String date = "2023-01-01";
        List<UserDTO> subscribers = Arrays.asList(new UserDTO(1L, "user1", null), new UserDTO(2L, "user2", null));
        when(subscriptionService.getSubscribersBeforeDate(newsletterId, date)).thenReturn(subscribers);

        ResponseEntity<List<UserDTO>> responseEntity = subscriptionController.getSubscribersBeforeDate(newsletterId, date);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(subscribers, responseEntity.getBody());
    }

    @Test
    void getSubscribersAfterDate_ShouldReturnSubscribers() {
        Long newsletterId = 1L;
        String date = "2023-01-01";
        List<UserDTO> subscribers = Arrays.asList(new UserDTO(1L, "user1", null), new UserDTO(2L, "user2", null));
        when(subscriptionService.getSubscribersAfterDate(newsletterId, date)).thenReturn(subscribers);

        ResponseEntity<List<UserDTO>> responseEntity = subscriptionController.getSubscribersAfterDate(newsletterId, date);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(subscribers, responseEntity.getBody());
    }

    @Test
    void getAllNewsletters_ShouldReturnNewsletters() {
        List<Newsletter> newsletters = Arrays.asList(
                new Newsletter(1L, "Newsletter 1", "Content 1", "2023-01-01",new ArrayList<>()),
                new Newsletter(2L, "Newsletter 2", "Content 2", "2023-01-02",new ArrayList<>())
        );
        when(subscriptionService.getAllNewsletters()).thenReturn(newsletters);

        ResponseEntity<List<NewsletterDTO>> responseEntity = subscriptionController.getAllNewsletters();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(newsletters.stream().map(Newsletter::toDTO).collect(Collectors.toList()), responseEntity.getBody());

    }

    @Test
    void getNewsletterWithSubscribers_ShouldReturnNewsletter() throws NotFoundException {
        Long newsletterId = 1L;
        Newsletter newsletter = new Newsletter(1L, "Newsletter 1", "Content 1", "2023-01-01",new ArrayList<>());
        when(subscriptionService.getNewsletterWithSubscribers(newsletterId)).thenReturn(newsletter);

        ResponseEntity<NewsletterDTO> responseEntity = subscriptionController.getNewsletterWithSubscribers(newsletterId);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(newsletter.toDTO(), responseEntity.getBody());
    }

    @Test
    void getUser_ShouldReturnUser() throws NotFoundException {
        Long userId = 1L;
        Subscription subscription = new Subscription(1L, true, new Newsletter(1L, "Newsletter", "Content", "2023-01-01", Collections.emptyList()),
                new User(1L, "user1", new HashSet<>()), LocalDateTime.now());
        when(subscriptionService.getUser(userId)).thenReturn(subscription.getUser());

        ResponseEntity<UserDTO> responseEntity = subscriptionController.getUser(userId);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Assertions.assertEquals(new UserDTO(userId, "user1", Collections.emptyList()), responseEntity.getBody());
    }


}
