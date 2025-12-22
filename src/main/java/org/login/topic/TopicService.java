package org.login.topic;

import org.login.common.model.Topic;
import org.login.common.repository.TopicRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TopicService {
    private final TopicRepository repo;

    public TopicService(TopicRepository repo) {
        this.repo = repo;
    }

    public List<Topic> findAll() {
        return repo.findAll();
    }

    public Topic save(Topic t) {
        return repo.save(t);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
