package github.salex9206.hlr.repository;

import github.salex9206.hlr.entity.HlrSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HlrSubscriberRepository extends JpaRepository<HlrSubscriber, String> {

}
