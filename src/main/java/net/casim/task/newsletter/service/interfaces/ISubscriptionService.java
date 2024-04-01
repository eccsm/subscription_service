package net.casim.task.newsletter.service.interfaces;

import javassist.NotFoundException;
import net.casim.task.newsletter.model.Newsletter;
import net.casim.task.newsletter.model.SubscribeRequest;
import net.casim.task.newsletter.model.User;
import net.casim.task.newsletter.model.dto.UserDTO;

import java.util.List;

public interface ISubscriptionService {
    void subscribe(SubscribeRequest subscribeRequest) throws NotFoundException;

    void unsubscribe(SubscribeRequest subscribeRequest) throws NotFoundException;

    boolean checkSubscription(Long newsletterId, Long userId);

    List<UserDTO> getSubscribersBeforeDate(Long newsletterId, String date);

    List<UserDTO> getSubscribersAfterDate(Long newsletterId, String date);

    List<Newsletter> getAllNewsletters();

    Newsletter getNewsletterWithSubscribers(Long newsletterId) throws NotFoundException;

    User getUser(Long userId) throws NotFoundException;
}

