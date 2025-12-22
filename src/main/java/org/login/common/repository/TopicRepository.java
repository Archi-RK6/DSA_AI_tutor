package org.login.common.repository;

import org.login.common.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByPath(String path);
}
