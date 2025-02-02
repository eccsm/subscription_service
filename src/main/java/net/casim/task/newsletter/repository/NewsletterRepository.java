package net.casim.task.newsletter.repository;

import net.casim.task.newsletter.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

}

