package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.agenda.Agenda;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class AgendaApi extends ModelApi {

    public final static String NEW_API = "v0";

    public static List<Agenda> getAgendas() {
        Response response = RequestService.request(Method.GET, NEW_API + "/agenda/");
        return populateList(response, Agenda.class);
    }

    public static Agenda createAgenda(final int projectId, final String body) {
        Response response = RequestService.request(Method.POST, NEW_API + "/project/" + projectId + "/agenda", body);
        return populateObject(response, Agenda.class);
    }

    public static Agenda createAgenda(final int projectId, final String body, final String user, final String pw) {
        Response response = RequestService.request(Method.POST, NEW_API + "/project/" + projectId + "/agenda", body, user, pw);
        return populateObject(response, Agenda.class);
    }

    public static Agenda getAgenda(final int projectId, final int agendaId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/agenda/" + agendaId);
        return populateObject(response, Agenda.class);
    }

    public static List<Agenda> getAgendas(final int projectId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/agenda");
        return populateList(response, Agenda.class);
    }

    public static Agenda updateAgenda(final int projectId, final int agendaId, final String body) {
        Response response = RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/agenda/" + agendaId, body);
        return populateObject(response, Agenda.class);
    }

    public static Agenda updateAgendaForOverlapBug(final int projectId, final int agendaId, final String body) {
        Response response = RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/agenda/" + agendaId, body);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response.asString(), Agenda.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Agenda updateAgenda(final int projectId, final int agendaId, final String body,
                                      final String user, final String pw) {
        Response response = RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/agenda/" + agendaId, body, user, pw);
        return populateObject(response, Agenda.class);
    }

    public static Agenda deleteAgenda(final int projectId, final int agendaId) {
        Response response = RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/agenda/" + agendaId);
        return populateDelete(response, Agenda.class);
    }

    public static Agenda deleteAgenda(final int projectId, final int agendaId, final String user, final String pw) {
        Response response = RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/agenda/" + agendaId, user, pw);
        return populateDelete(response, Agenda.class);
    }

}
