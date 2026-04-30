package com.example.helpers.builders;

import com.example.api.models.comment.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommentBuilder implements BuilderInterface<Comment> {

    private Comment c;

    private Integer userId;
    private String comment;

    public CommentBuilder withUserId(final Integer userId) {
        this.userId = userId;
        return this;
    }

    public CommentBuilder withComment(final String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public Comment getProvider() {
        return c;
    }

    @Override
    public void setProvider(Comment provider) { this.c = provider; }

    // As api accepts payload wo user_id, this method is wrong.
    @Override
//    public void build() {
//        if (c == null) {
//            c = new Comment();
//        }
//        if (userId != 0) {
//            c.setUserId(userId);
//        }
//        if (comment != null) {
//            c.setComment(comment);
//        }
//    }
    public void build() {
        if (c == null) {
            c = new Comment();
        }
        if (comment != null) {
            c.setComment(comment);
        }
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(c);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}

