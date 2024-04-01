package net.casim.task.newsletter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.casim.task.newsletter.controller.SubscriptionController;
import net.casim.task.newsletter.model.Newsletter;
import net.casim.task.newsletter.model.SubscribeRequest;
import net.casim.task.newsletter.model.Subscription;
import net.casim.task.newsletter.model.User;
import net.casim.task.newsletter.model.dto.NewsletterDTO;
import net.casim.task.newsletter.model.dto.UserDTO;
import net.casim.task.newsletter.service.SubscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
class NewsletterApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubscriptionService subscriptionService;

    @Test
    void subscribe_ShouldReturnBadRequest_WhenUserIsAlreadySubscribed() throws Exception {
        SubscribeRequest subscribeRequest = new SubscribeRequest(1L, 1L);
        when(subscriptionService.checkSubscription(subscribeRequest.getNewsletterId(), subscribeRequest.getUserId()))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/subscriptions/subscribe")
                        .content("{\"newsletterId\": 1, \"userId\": 1}")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User is already subscribed"));
    }

    @Test
    void unsubscribe_ShouldReturnBadRequest_WhenUserIsNotSubscribed() throws Exception {
        SubscribeRequest subscribeRequest = new SubscribeRequest(1L, 1L);
        when(subscriptionService.checkSubscription(subscribeRequest.getNewsletterId(), subscribeRequest.getUserId()))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/subscriptions/unsubscribe")
                        .content("{\"newsletterId\": 1, \"userId\": 1}")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User is not subscribed"));
    }

    @Test
    void checkSubscription_ShouldReturnSubscriptionStatus() throws Exception {
        Long userId = 1L;
        Long newsletterId = 1L;

        doReturn(true).when(subscriptionService).checkSubscription(newsletterId, userId);

        MvcResult result = mockMvc.perform(get("/subscriptions/checkSubscription").param("userId", userId.toString()).param("newsletterId", newsletterId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        boolean actualResponse = Boolean.parseBoolean(result.getResponse().getContentAsString());
        Assertions.assertTrue(actualResponse);
    }


    @Test
    void getSubscribersBeforeDate_ShouldReturnSubscribers() throws Exception {
        Long newsletterId = 1L;
        String date = "2023-01-01";
        List<UserDTO> subscribers = List.of(new UserDTO(1L, "user1", Collections.emptyList()));

        when(subscriptionService.getSubscribersBeforeDate(newsletterId, date)).thenReturn(subscribers);

        MvcResult result = mockMvc.perform(get("/subscriptions/subscribersBeforeDate")
                        .param("newsletterId", String.valueOf(newsletterId))
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<UserDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        Assertions.assertEquals(subscribers, response);
    }

    @Test
    void getSubscribersAfterDate_ShouldReturnSubscribers() throws Exception {
        // Arrange
        Long newsletterId = 1L;
        String date = "2023-01-01";
        List<UserDTO> subscribers = List.of(new UserDTO(1L, "user1", Collections.emptyList()));

        when(subscriptionService.getSubscribersAfterDate(newsletterId, date)).thenReturn(subscribers);

        MvcResult result = mockMvc.perform(get("/subscriptions/subscribersAfterDate")
                        .param("newsletterId", String.valueOf(newsletterId))
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<UserDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        Assertions.assertEquals(subscribers, response);
    }

    @Test
    void getAllNewsletters_ShouldReturnAllNewsletters() throws Exception {
        List<Newsletter> newsletters = Arrays.asList(
                new Newsletter(1L, "Newsletter1", "Content1", "2023-01-01", Collections.emptyList()),
                new Newsletter(2L, "Newsletter2", "Content2", "2023-01-02", Collections.emptyList())
        );
        List<NewsletterDTO> newsletterDTOs = newsletters.stream().map(Newsletter::toDTO).collect(Collectors.toList());

        when(subscriptionService.getAllNewsletters()).thenReturn(newsletters);

        MvcResult result = mockMvc.perform(get("/subscriptions/newsletters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<NewsletterDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        Assertions.assertEquals(newsletterDTOs, response);
    }

    @Test
    void getNewsletterWithSubscribers_ShouldReturnNewsletter() throws Exception {
        Long newsletterId = 1L;
        Newsletter newsletter = new Newsletter(1L, "Newsletter", "Content", "2023-01-01", Collections.emptyList());

        when(subscriptionService.getNewsletterWithSubscribers(newsletterId)).thenReturn(newsletter);

        MvcResult result = mockMvc.perform(get("/subscriptions/newsletter/{newsletterId}", newsletterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        NewsletterDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), NewsletterDTO.class);
        Assertions.assertEquals(newsletter.toDTO(), response);
    }

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        User user = new User(1L, "user1", Collections.emptySet());
        Subscription subscription = new Subscription(1L, true,
                new Newsletter(1L, "Newsletter", "Content", "2023-01-01", Collections.emptyList()), user, LocalDateTime.now());

        when(subscriptionService.getUser(userId)).thenReturn(subscription.getUser());

        MvcResult result = mockMvc.perform(get("/subscriptions/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        UserDTO expectedUserDTO = new UserDTO(userId, "user1", Collections.emptyList());

        UserDTO actualUserDTO = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        Assertions.assertEquals(expectedUserDTO, actualUserDTO);
    }

}
