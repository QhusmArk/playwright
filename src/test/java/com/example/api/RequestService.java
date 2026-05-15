package com.example.api;

import com.example.playwright.config.TestEnvironment;
import com.example.playwright.config.TestUserLoader;
import com.example.playwright.hooks.testUsers.TestUser;
import com.example.playwright.hooks.testUsers.TestUserPool;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class RequestService {

    public static void setUp() {
        System.out.println("Setting up Request service");
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.baseURI = TestEnvironment.getApiUrl(); // https://sigicom.test.indev.sigicom.net/api/
    }

    /**
     * Always uses admin credentials to gain access to api
     */
//    private static Map<String, String> getAdminCredentials(final String api) {
//        TestUser apiAdmin = TestUserPool.getCurrentUser();
//        if (!apiAdmin.role().equals("ADMIN")) {
//            throw new IllegalStateException("Only ADMIN roles can access this API for this purpose.");
//        }
//
//        Map<String, String> map = new HashMap<>();
//        if (api.startsWith("v0") || api.startsWith("v1")) {
//            map.put("user", "user:" + apiAdmin.id());   // "user:XXXX"
//            map.put("pw", apiAdmin.token());
//        } else {
//            map.put("user", apiAdmin.email());
//            map.put("pw", apiAdmin.password());
//        }
//        return map;
//    }
    private static Map<String, String> getAdminCredentials(final String api) {

        TestUser currentUser = TestUserPool.getCurrentUser();

        TestUser apiAdmin = (currentUser.role().equals("ADMIN"))
                ? currentUser
                : TestUserLoader.loadCleanupApiUser();
//        if (!currentUser.role().equals("ADMIN")) {
//            TestUser api = TestUserLoader.loadCleanupApiUser();
//            throw new IllegalStateException("Only ADMIN roles can access this API for this purpose.");
//        }

        Map<String, String> map = new HashMap<>();
        if (api.startsWith("v0") || api.startsWith("v1")) {
            map.put("user", "user:" + apiAdmin.id());   // "user:XXXX"
            map.put("pw", apiAdmin.token());
        } else {
            map.put("user", apiAdmin.email());
            map.put("pw", apiAdmin.password());
        }
        return map;
    }

    public static Response request(final Method verb, final String path) {
        Map<String, String> credentials = getAdminCredentials(path);
        return requestImpl(verb, path, new HashMap<>(), "", credentials.get("user"), credentials.get("pw"));
    }

    public static Response request(final Method verb, final String path, final String user, final String pw) {
        return requestImpl(verb, path, new HashMap<>(), "", user, pw);
    }

    public static Response request(final Method verb, final String path, final Map<String, Object> querys) {
        Map<String, String> credentials = getAdminCredentials(path);
        return requestImpl(verb, path, querys, "", credentials.get("user"), credentials.get("pw"));
    }

    public static Response request(final Method verb, final String path, final Map<String, Object> querys, final String user, final String pw) {
        return requestImpl(verb, path, querys, "", user, pw);
    }

    public static Response request(final Method verb, final String path, final String body) {
        Map<String, String> credentials = getAdminCredentials(path);
        return requestImpl(verb, path, new HashMap<>(), body, credentials.get("user"), credentials.get("pw"));
    }

    public static Response request(final Method verb, final String path, final String body, final String user, final String pw) {
        return requestImpl(verb, path, new HashMap<>(), body, user, pw);
    }

    //    Experimenting on non-redirecting requests

    public static Response requestWithoutRedirect(final Method verb, final String path, final String body) {
        Map<String, String> credentials = getAdminCredentials(path);
        return requestImpl(verb, path, new HashMap<>(), body, credentials.get("user"), credentials.get("pw"), false);
    }

    /**
     * This is needed bc requestImpl sends whole path as a String, thereby making it impossible to give RestAssured
     * a way to escape chars (e.g. those found in analysis_url).
     *
     * @param verb
     * @param pathTemplate
     * @param pathParams
     * @param body
     * @return
     */
    public static Response request(final Method verb, final String pathTemplate, final Map<String, Object> pathParams, final String body) {
        Map<String, String> credentials = getAdminCredentials(pathTemplate);

        return requestImpl(
                verb,
                pathTemplate,
                pathParams,
                Map.of(),          // queries
                body,              // body
                credentials.get("user"),
                credentials.get("pw"),
                false               // followRedirect
        );
    }

    private static Response requestImpl(final Method verb, final String path, final Map<String, Object> queries,
                                        final String body, final String user, final String pw) {
        return requestImpl(verb, path, queries, body, user, pw, true);
    }

    private static Response requestImpl(final Method verb, final String path, final Map<String, Object> queries,
                                        final String body, final String user, final String pw, boolean followRedirect) {
        // Added try/catch so that we can instruct user to use VPN if UnknownHostException is caught.
        try {
            Response response = given()
                    .auth().preemptive().basic(user, pw)
                    .headers("Content-Type", "application/json",
                            "Accept", "application/json")
                    .body(body)
                    .queryParams(queries)
                    .redirects().follow(followRedirect)
                    .request(verb, path);

            return response;

        } catch (Exception e) {
            System.out.println("\nHost could not be resolved. Use VPN perhaps?");
            throw e;
        }
    }

    private static Response requestImpl(final Method verb,
                                        final String pathTemplate,
                                        final Map<String, Object> pathParams,
                                        final Map<String, Object> queries,
                                        final String body,
                                        final String user,
                                        final String pw,
                                        final boolean followRedirect) {
        // Added try/catch so that we can instruct user to use VPN if UnknownHostException is caught.
        try {
            RequestSpecification spec = given()
                    .auth().preemptive().basic(user, pw)
                    .headers(
                            "Content-Type", "application/json",
                            "Accept", "application/json"
                    )
                    .redirects().follow(followRedirect);

            // Add body if present
            if (body != null) {
                spec = spec.body(body);
            }

            // Add query parameters if present
            if (queries != null && !queries.isEmpty()) {
                spec = spec.queryParams(queries);
            }

            // Add path parameters if present
            if (pathParams != null && !pathParams.isEmpty()) {
                spec = spec.pathParams(pathParams);
            }

            return spec.request(verb, pathTemplate);

        } catch (Exception e) {
            System.out.println("\nHost could not be resolved. Use VPN perhaps?");
            throw e;
        }
    }


}
