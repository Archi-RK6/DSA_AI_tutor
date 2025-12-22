package org.login.content;

import org.login.common.model.Content;
import org.login.common.repository.ContentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContentService {
    private final ContentRepository repo;

    public ContentService(ContentRepository repo) {
        this.repo = repo;
    }

    public List<Content> getContentsForTopic(Integer topicId) {
        return repo.findByTopicId(topicId);
    }

    public List<Content> getAllContentsByTopicId(Integer topicId) {
        return getContentsForTopic(topicId);
    }

    public Content save(Content c) {
        return repo.save(c);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
