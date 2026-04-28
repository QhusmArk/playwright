package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.comment.Comment;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class CommentApi extends ModelApi {

    public final static String NEW_API = "v0";

    public static Comment createComment(final int projectId, final String body) {
        Response response = RequestService.request(Method.POST, NEW_API + "/project/" + projectId + "/comment", body);
        return populateObject(response, Comment.class);
    }

    public static Comment createComment(final int projectId, final String body, final String user, final String pw) {
        Response response = RequestService.request(Method.POST, NEW_API + "/project/" + projectId + "/comment", body, user, pw);
        return populateObject(response, Comment.class);
    }

    public static Comment getComment(final int projectId, final String commentId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/comment/" + commentId);
        return populateObject(response, Comment.class);
    }

    public static List<Comment> getComments(final int projectId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/comment");
        return populateList(response, Comment.class);
    }

    public static List<Comment> getComments(final int projectId, final String user, final String pw) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/comment", user, pw);
        return populateList(response, Comment.class);
    }

    public static Comment updateComment(final int projectId, final String commentId, final String body) {
        Response response = RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/comment/" + commentId, body);
        return populateObject(response, Comment.class);
    }

    public static Comment updateComment(final int projectId, final String commentId, final String body,
                                        final String user, final String pw) {
        Response response = RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/comment/" + commentId, body, user, pw);
        return populateObject(response, Comment.class);
    }

    public static Comment deleteComment(final int projectId, final String commentId) {
        Response response = RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/comment/" + commentId);
        return populateDelete(response, Comment.class);
    }

    public static Comment deleteComment(final int projectId, final String commentId, final String user, final String pw) {
        Response response = RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/comment/" + commentId, user, pw);
        return populateDelete(response, Comment.class);
    }

    public static Response createCommentGetResponse(final int projectId, final String body) {
        return RequestService.requestWithoutRedirect(Method.POST, NEW_API + "/project/" + projectId + "/comment", body);
    }

    public static Response getCommentGetResponse(final int projectId, final String commentId) {
        return RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/comment/" + commentId);
    }

    public static Response updateCommentGetResponse(final int projectId, final String commentId, final String body) {
        return RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/comment/" + commentId, body);
    }

    public static Response deleteCommentGetResponse(final int projectId, final String commentId) {
        return RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/comment/" + commentId);
    }

}
