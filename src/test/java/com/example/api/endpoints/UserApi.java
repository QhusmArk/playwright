package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.user.User;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;

public class UserApi extends ModelApi {

    public static User createUser(final String body) {
        Response response = RequestService.request(Method.POST, "user/", body);
        return populateObject(response, User.class);
    }

    public static User getUser(final int id) {
        Response response = RequestService.request(Method.GET, "user/" + id);
        return populateObject(response, User.class);
    }


    public static User getUserByMail(final String email) {
        List<User> allUsers = getUsers();

        return allUsers.stream()
                .filter(m -> m.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Search for Users whose mail starts with something.
     * Use this to find all Users like: test-auto-performance-(RANDOM STRING).
     * There is no service that returns users belonging to a project, therefore this type
     * of method is required to filter out the users we want from the list of all users.
     */
    public static List<User> getUsersByMailLike(final String email) {
        List<User> allUsers = getUsers();

        return allUsers.stream()
                .filter(m -> m.getEmail().startsWith(email))
                .toList();
    }

    public static User getUserByFullName(String fullName) {
        List<User> users = getUsers();

        return users.stream()
                .filter(user -> fullName.equals(user.getFirstName() + " " + user.getLastName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Note that a User can be in many projects.
     */
    public static List<User> getUsersBelongingToProject(final int projectId) {
        List<User> allUsers = getUsers();

        return allUsers.stream()
                .filter(m -> Arrays.stream(m.getProjectIds()).anyMatch(n -> n == projectId))
                .toList();
    }

    public static List<User> getUsers() {
        Response response = RequestService.request(Method.GET, "user/");
        return populateList(response, User.class);
    }

    public static List<User> getUsers(final String user, final String pw) {
        Response response = RequestService.request(Method.GET, "user", user, pw);
        return populateList(response, User.class);
    }

    /**
     * @return UserId of the user from Settings.getUsernameNewApi().
     */
    public static User getCurrentUser() {
        Response response = RequestService.request(Method.GET, "user/0");
        List<User> users = populateList(response, User.class);
        return users.getFirst();
    }

    public static String getUserToken() {
        Response response = RequestService.request(Method.GET, "v0/user/0/token/");
        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getString("token[0]");
    }

    public static User updateUser(final int id, final String body) {
        Response response = RequestService.request(Method.PUT, "user/" + id, body);
        return populateObject(response, User.class);
    }

    public static User updateUser(final int id, final String body, final String user, final String pw) {
        Response response = RequestService.request(Method.PUT, "user/" + id, body, user, pw);
        return populateObject(response, User.class);
    }

    public static User addProjectToUser(final int userId, final String body) {
        Response response = RequestService.request(Method.PUT, "user/" + userId, body);
        return populateObject(response, User.class);
    }

    public static int addProjectToUser(final int userId, final String body, final String userName, final String pw) {
        Response response = RequestService.request(Method.PUT, "user/" + userId, body, userName, pw);
        return response.statusCode();
    }

    public static User deleteUser(final int userId) {
        Response response = RequestService.request(Method.DELETE, "user/" + userId);
        return populateDelete(response, User.class);
    }

    public static User deleteProjectFromUser(final int userId, final String body) {
        Response response = RequestService.request(Method.PUT, "user/" + userId, body);
        return populateObject(response, User.class);
    }

//    public static Cookie makeApiLogin(String user, String pw) {
//        /*
//        Process for login via api:
//        1. Send a GET to /login
//        2. Capture the csrftoken-cookie
//        3. Capture the csrfmiddlewaretoken-text in the html
//        4. Prepare a POST to /login
//        5. Add the csrftoken-cookie to the POST
//        6. Add csrfmiddlewaretoken, user, pw to the POST
//        7. Send POST
//        8. Capture the sessionid-cookie
//        9. Return sessionid-cookie for all future requests
//         */
//
//        // Send the GET
//        Response getResponse = given()
//                .headers("Content-Type", "application/json",
//                        "Accept", "application/json")
//                .request(Method.GET, "https://sigicom.test.indev.sigicom.net/login/")
//                .then()
//                .extract().response();
//
//        // Get the csrftoken-token
//        String csrfToken = getResponse.getCookie("csrftoken");
//
//        // Parse the HTML to extract the CSRF token
//        Document document = Jsoup.parse(getResponse.getBody().asString());
//        Element csrfmiddlewaretokenElement = document.selectFirst("input[name=csrfmiddlewaretoken]");
//        String csrfmiddlewaretoken = csrfmiddlewaretokenElement.attr("value");
//
//        // Prepare and send the POST
//        Response postResponse = given()
//                .contentType("application/x-www-form-urlencoded")
//                .accept("*/*")
//                .formParam("csrfmiddlewaretoken", csrfmiddlewaretoken)
//                .formParam("next", "/")
//                .formParam("password", pw)
//                .formParam("submit", "Login")
//                .formParam("username", user)
//                .cookie("csrftoken", csrfToken)
//                .request(Method.POST, "https://sigicom.test.indev.sigicom.net/login/")
//                .then()
//                .extract().response();
//
//        io.restassured.http.Cookie cookie = postResponse.getDetailedCookie("sessionid");
//        Cookie sessionIdCookie = new Cookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getExpiryDate());
//        return sessionIdCookie;
//    }
}
