package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.project.Project;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectApi extends ModelApi {
    public static Project createProject(final String body) {
        Response response = RequestService.request(Method.POST, "project/", body);
        if (response.getStatusCode() == 400) {
            System.out.println(response.getBody().prettyPrint());
            throw new IllegalStateException("400 from POST /project");
        }
        return populateObject(response, Project.class);
    }

    public static Project createProject(final String body, final String user, final String pw) {
        Response response = RequestService.request(Method.POST, "project/", body, user, pw);
        return populateObject(response, Project.class);
    }

    public static Project getProject(final int id) {
        Response response = RequestService.request(Method.GET, "project/" + id);
        if (response.getStatusCode() == 404) {
            return null;
        } else {
            return populateObject(response, Project.class);
        }
    }

    public static Project getProject(final int id, final String user, final String pw) {
        Response response = RequestService.request(Method.GET, "project/" + id, user, pw);
        return populateObject(response, Project.class);
    }

    public static Project getProjectByName(final String projectName) {
        Response response = RequestService.request(Method.GET, "project/");
        List<Project> projects = populateList(response, Project.class);

        List<Project> project = projects.stream()
                .filter(m -> m.getName().equals(projectName))
                .toList();

        return (project.isEmpty())
                ? null
                : project.getFirst();
    }

    public static List<Project> getProjectsByName(final String projectName) {
        Response response = RequestService.request(Method.GET, "project/");
        List<Project> projects = populateList(response, Project.class);

        return projects.stream()
                .filter(m -> m.getName().equals(projectName))
                .toList();
    }


    /**
     * First object in List received by method populateList does contain statuscode but as this list
     * is manipulated it must be set again.
     *
     * @param projectName
     * @return
     */
    public static List<Project> getProjectsByNameLike(final String projectName) {
        Response response = RequestService.request(Method.GET, "project/");
        List<Project> projects = populateList(response, Project.class);

        List<Project> list = projects.stream()
                .filter(m -> m.getName().contains(projectName))
                .collect(Collectors.toList());
        return list;
    }

    public static List<Project> getActiveProjects() {
        Response response = RequestService.request(Method.GET, "project/?active_only=true");
        return populateList(response, Project.class);
    }

    public static List<Project> getProjects() {
        Response response = RequestService.request(Method.GET, "project");
        return populateList(response, Project.class);
    }

    public static Project updateProject(final int id, final String body) {
        Response response = RequestService.request(Method.PUT, "project/" + id, body);
        return populateObject(response, Project.class);
    }

    public static Project updateProject(final int id, final String body, final String user, final String pw) {
        Response response = RequestService.request(Method.PUT, "project/" + id, body, user, pw);
        return populateObject(response, Project.class);
    }

    public static Project deleteProject(final int id) {
        Response response = RequestService.request(Method.DELETE, "project/" + id);
        return populateObject(response, Project.class);
    }

    public static Project deleteProject(final int id, final String user, final String pw) {
        Response response = RequestService.request(Method.DELETE, "project/" + id, user, pw);
        return populateObject(response, Project.class);
    }

}
