package net.casim.task.newsletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javassist.NotFoundException;
import net.casim.task.newsletter.model.*;
import net.casim.task.newsletter.model.dto.NewsletterDTO;
import net.casim.task.newsletter.model.dto.UserDTO;
import net.casim.task.newsletter.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to a Newsletter", description = "Subscribe to a newsletter with the given userId and newsletterId.")
    @ApiResponse(responseCode = "201", description = "Subscription successful")
    @ApiResponse(responseCode = "400", description = "User is already subscribed or Subscription failed")
    @ApiResponse(responseCode = "404", description = "User or Newsletter not found")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody SubscribeRequest subscribeRequest) {
        try {
            if (subscriptionService.checkSubscription(subscribeRequest.getNewsletterId(), subscribeRequest.getUserId())) {
                SubscriptionResponse response = new SubscriptionResponse("User is already subscribed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            subscriptionService.subscribe(subscribeRequest);
            SubscriptionResponse response = new SubscriptionResponse("Subscription successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NotFoundException e) {
            SubscriptionResponse response = new SubscriptionResponse("Subscription failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe from a Newsletter", description = "Unsubscribe from a newsletter with the given userId and newsletterId.")
    @ApiResponse(responseCode = "200", description = "Unsubscription successful")
    @ApiResponse(responseCode = "400", description = "User is not subscribed or Unsubscription failed")
    @ApiResponse(responseCode = "404", description = "User or Newsletter not found")
    public ResponseEntity<SubscriptionResponse> unsubscribe(@RequestBody SubscribeRequest subscribeRequest) {
        try {
            if (!subscriptionService.checkSubscription(subscribeRequest.getNewsletterId(), subscribeRequest.getUserId())) {
                SubscriptionResponse response = new SubscriptionResponse("User is not subscribed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            subscriptionService.unsubscribe(subscribeRequest);
            SubscriptionResponse response = new SubscriptionResponse("Unsubscription successful");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NotFoundException e) {
            SubscriptionResponse response = new SubscriptionResponse("Unsubscription failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/checkSubscription")
    @Operation(summary = "Check Subscription", description = "Check if a user is subscribed to a newsletter.")
    @ApiResponse(responseCode = "200", description = "Returns true if subscribed, false otherwise")
    public ResponseEntity<Boolean> checkSubscription(@RequestParam Long newsletterId, @RequestParam Long userId) {
        boolean isSubscribed = subscriptionService.checkSubscription(newsletterId, userId);
        return ResponseEntity.ok(isSubscribed);
    }

    @GetMapping("/subscribersBeforeDate")
    @Operation(summary = "Get Subscribers Before Date", description = "Get subscribers for a newsletter before a specific date.")
    @ApiResponse(responseCode = "200", description = "Returns a list of subscribers")
    public ResponseEntity<List<UserDTO>> getSubscribersBeforeDate(
            @RequestParam Long newsletterId, @RequestParam String date) {
        List<UserDTO> subscribers = subscriptionService.getSubscribersBeforeDate(newsletterId, date);

        return ResponseEntity.ok(subscribers);
    }


    @GetMapping("/subscribersAfterDate")
    @Operation(summary = "Get Subscribers After Date", description = "Get subscribers for a newsletter after a specific date.")
    @ApiResponse(responseCode = "200", description = "Returns a list of subscribers")
    public ResponseEntity<List<UserDTO>> getSubscribersAfterDate(
            @RequestParam Long newsletterId, @RequestParam String date) {
        List<UserDTO> subscribers = subscriptionService.getSubscribersAfterDate(newsletterId, date);

        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/newsletters")
    @Operation(summary = "Get All Newsletters", description = "Get a list of all newsletters.")
    @ApiResponse(responseCode = "200", description = "Returns a list of newsletters")
    public ResponseEntity<List<NewsletterDTO>> getAllNewsletters() {
        List<Newsletter> newsletters = subscriptionService.getAllNewsletters();
        List<NewsletterDTO> newsletterDTOs = newsletters.stream()
                .map(Newsletter::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(newsletterDTOs);
    }

    @GetMapping("/newsletter/{newsletterId}")
    @Operation(summary = "Get Newsletter with Subscribers", description = "Get a specific newsletter with its subscribers.")
    @ApiResponse(responseCode = "200", description = "Returns the newsletter with subscribers")
    @ApiResponse(responseCode = "404", description = "Newsletter not found")
    public ResponseEntity<NewsletterDTO> getNewsletterWithSubscribers(@PathVariable Long newsletterId) {
        try {
            Newsletter newsletter = subscriptionService.getNewsletterWithSubscribers(newsletterId);
            NewsletterDTO newsletterDTO = newsletter.toDTO();
            return ResponseEntity.ok(newsletterDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Details", description = "Get details of a specific user.")
    @ApiResponse(responseCode = "200", description = "Returns user details")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        try {
            User user = subscriptionService.getUser(userId);
            List<NewsletterDTO> newsletterDTOs = user.getSubscriptions().stream()
                    .filter(Subscription::isSubscribed)
                    .map(subscription -> subscription.getNewsletter().toDTO())
                    .collect(Collectors.toList());

            UserDTO userDTO = new UserDTO(user.getUserId(), user.getUsername(), newsletterDTOs);
            return ResponseEntity.ok(userDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
