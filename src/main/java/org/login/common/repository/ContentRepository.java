package org.login.common.repository;

import org.login.common.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    List<Content> findByTopicId(Integer topicId);
}