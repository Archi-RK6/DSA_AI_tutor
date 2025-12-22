package org.login.common.model;

import jakarta.persistence.*;

@Entity
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "topic_id", nullable = false)
    private Integer topicId;

    @Column(name = "content_chapter")
    private String contentChapter;

    @Column(name = "content_text")
    private String contentText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getContentChapter() {
        return contentChapter;
    }

    public void setContentChapter(String contentChapter) {
        this.contentChapter = contentChapter;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

}
