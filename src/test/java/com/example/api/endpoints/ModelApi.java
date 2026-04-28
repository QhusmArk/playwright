package com.example.api.endpoints;

import com.example.api.models.ScheduledReport;
import com.example.api.models.agenda.Agenda;
import com.example.api.models.blast.Blast;
import com.example.api.models.comment.Comment;
import com.example.api.models.device.*;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.message.MessageRule;
import com.example.api.models.message.NotificationMpValue;
import com.example.api.models.project.Project;
import com.example.api.models.report.BillingReportWrapper;
import com.example.api.models.report.DataReport;
import com.example.api.models.report.Search;
import com.example.api.models.report.analysis.Analysis;
import com.example.api.models.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModelApi {

    public static <T> T populateObject(final Response response, Class<T> clazz) {

        Function<Object, T> safeCast = (model) -> {
            if (clazz.isInstance(model)) {
                return clazz.cast(model);
            } else {
                throw new IllegalArgumentException("Cannot cast " + model.getClass() + " to " + clazz);
            }
        };

        T obj = switch (clazz.getSimpleName()) {
            case "Sensor" -> safeCast.apply(responseObject(response, Sensor.class));
            case "Agenda" -> safeCast.apply(responseObject(response, Agenda.class));
            case "Blast" -> safeCast.apply(responseObject(response, Blast.class));
            case "Comment" -> safeCast.apply(responseObject(response, Comment.class));
            case "DataReport" -> safeCast.apply(responseObject(response, DataReport.class));
            case "Search" -> safeCast.apply(responseObject(response, Search.class));
            case "Device" -> safeCast.apply(responseObject(response, Device.class));
            case "MeasuringPoint" -> safeCast.apply(responseObjectData(response, MeasuringPoint.class));
            case "MessageRule" -> safeCast.apply(responseObjectData(response, MessageRule.class));
            case "NotificationMpValue" -> safeCast.apply(responseObjectData(response, NotificationMpValue.class));
            case "Project" -> safeCast.apply(responseObjectData(response, Project.class));
            case "User" -> safeCast.apply(responseObjectData(response, User.class));
            case "Analysis" -> safeCast.apply(responseObject(response, Analysis.class));
            case "Change" -> safeCast.apply(responseObject(response, Change.class));
            case "Definition" -> safeCast.apply(responseObject(response, Definition.class));
            case "UserInput" -> safeCast.apply(responseObject(response, UserInput.class));
            case "BillingReportWrapper" -> safeCast.apply(responseObject(response, BillingReportWrapper.class));
            case "ScheduledReport" -> safeCast.apply(responseObject(response, ScheduledReport.class));
            default -> throw new IllegalArgumentException("Unsupported clazz: " + clazz.getSimpleName());
        };

        return obj;
    }

    public static <T> List<T> populateList(final Response response, Class<T> clazz) {

        Function<List<?>, List<T>> safeCast = (list) -> {
            List<T> castList = new ArrayList<>();
            for (Object item : list) {
                if (clazz.isInstance(item)) {
                    castList.add(clazz.cast(item));
                } else {
                    throw new IllegalArgumentException("Cannot cast " + list.getClass() + " to " + clazz);
                }
            }
            return castList;
        };

        List<T> list = switch (clazz.getSimpleName()) {
            case "Agenda" -> safeCast.apply(responseList(response, clazz));
            case "Blast", "Comment", "DataReport", "Device", "Search" -> responseList(response, clazz);
            case "MeasuringPoint", "MessageRule", "Project", "User", "NotificationMpValue", "ScheduledReport" ->
                    responseListData(response, clazz);
            default -> throw new IllegalStateException("Unexpected clazz: " + clazz.getSimpleName());
        };

        return list;
    }

    public static <T> T populateDelete(Response response, Class<T> clazz) {
        T provider = null;
        try {
            provider = clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            System.out.println(MessageFormat.format("Could not create an instance of class {} with status code {}", clazz, response.getStatusCode()));
        }

        return provider;
    }

    /**
     * Populate ONE Object from json in form: { "datetime": "2022-09-13 12:21",...
     */
    private static <T> T responseObject(Response response, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T provider = null;

        if (response.getStatusCode() == 200) {
            try {
                provider = mapper.readValue(response.asString(), clazz);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            // Custom, and temporary, fix to be able to return the stupid error we get when datetime_from is after datetime_to
            // {"datetime_to":"must be after datetime_from"}
        } else if ((response.getStatusCode() == 400) && clazz.getSimpleName().equals("Search")) {
            try {
                provider = mapper.readValue(response.asString(), clazz);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // todo: If possible needs to be removed, as these lines creates an empty pojo-shell everytime a non-200 is returned
                // The reason for this is that in the old days, status code was set as an attribute in provider.
                provider = clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException | NoSuchMethodException e) {
                System.out.println(MessageFormat.format("Could not create an instance of class {} with status code {}", clazz, response.getStatusCode()));
            }
        }

        return provider;
    }

    /**
     * Populate ONE Object from json in form: {"status_code": 200, "data": [{...
     * In this form all information is in attribute: data.
     */
    private static <T> T responseObjectData(Response response, Class<T> clazz) {

        T provider = null;
        if (response.getStatusCode() != 200) {
            try {
                provider = clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException | NoSuchMethodException e) {
                System.out.println(MessageFormat.format("Could not create an instance of class {} with status code {}", clazz, response.getStatusCode()));
            }
        } else {

            JsonPath jsonPath = response.jsonPath();
            List<T> list = jsonPath.getList("data", clazz);
            if (list.size() > 0) {
                provider = list.getFirst();
            } else {
                try {
                    provider = clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                         InvocationTargetException | NoSuchMethodException e) {
                    System.out.println(MessageFormat.format("Could not create an instance of class {} with status code {}", clazz, response.getStatusCode()));
                }
            }
        }
        return provider;
    }

    /**
     * Populate List<Object> from json in form: [{"datetime": "2023-04-18 14:06"}, {"datetime": "2023-04-18 14:06",...
     */
    private static <T> List<T> responseList(final Response response, Class<T> clazz) {
        List<T> list = null;
        T provider = null;

        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType listType =
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);

        if (response.getStatusCode() == 200) {
            try {
                list = objectMapper.readValue(response.asString(), listType);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                list = new ArrayList<>();
                provider = clazz.getDeclaredConstructor().newInstance();
                list.add(provider);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException | NoSuchMethodException e) {
                System.out.println(MessageFormat.format("Could not create an instance of class {} with status code {}", clazz, response.getStatusCode()));
            }
        }

        return list;
    }

    /**
     * Populate List<Object> from json in form: {"status_code": 200, "data": [{"id": 228756}, {"id": 228755}..
     */
    private static <T> List<T> responseListData(final Response response, Class<T> clazz) {
        List<T> list = null;
        T provider = null;

        if (response.getStatusCode() != 200) {
            try {
                provider = clazz.getDeclaredConstructor().newInstance();
                list = new ArrayList<>();
                list.add(provider);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException | NoSuchMethodException e) {
                System.out.println(MessageFormat.format("Could not create an instance of class {} with status code {}", clazz, response.getStatusCode()));
            }
        } else {
            JsonPath jsonPath = response.jsonPath();
            list = jsonPath.getList("data", clazz);
        }
        return list;
    }
}
