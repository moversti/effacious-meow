package com.mycompany;

import com.google.gson.Gson;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

public class Main {

    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        String baseUrl = "http://localhost:4567";
        String personsUrl = baseUrl + "/persons";
        String sessionUrl = baseUrl + "/session";

        Scanner scanner = new Scanner(System.in);
        Token token = new Token("invalid");

        while (true) {
            System.out.print("> ");
            String komentorivi = scanner.nextLine();
            String[] komento = komentorivi.split(" ");

            if (komento[0].equals("login")) {
                // form an object to help the json generation
                Person person = new Person(komento[1], null, komento[2], null);
                String asJson = gson.toJson(person);
                System.out.println("making a http post with body: " + asJson);

                HttpResponse httpResponse = Request.Post(sessionUrl).bodyString(asJson, ContentType.APPLICATION_JSON).execute().returnResponse();

                int code = httpResponse.getStatusLine().getStatusCode();

                String responseBodyAsJson = IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
                System.out.println("response body was " + responseBodyAsJson);

                if (code == 200) {
                    // login succeded, create and save the Token-object        
                    token = gson.fromJson(responseBodyAsJson, Token.class);
                    System.out.println("success! got token " + token.toString());
                } else {
                    Error error = gson.fromJson(responseBodyAsJson, Error.class);
                    System.out.println(error);
                }
            }
            if (komento[0].equals("register")) {
                Person person = new Person(komento[1], komento[2], komento[3], komento[4]);
                String json = gson.toJson(person);
                System.out.println("making a http post with body: " + json);
                HttpResponse httpResponse = Request.Post(personsUrl).bodyString(json, ContentType.APPLICATION_JSON).execute().returnResponse();
                int code = httpResponse.getStatusLine().getStatusCode();
                String responseBodyAsJson = IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
                System.out.println("response body was " + responseBodyAsJson);
                if (code == 200) {
                    System.out.println("success!");
                } else {
                    Error error = gson.fromJson(responseBodyAsJson, Error.class);
                    System.out.println(error);
                }
            }
            if (komento[0].equals("persons")) {

                // a get request with a header set
                HttpResponse httpResponse = Request.Get(personsUrl)
                        .addHeader("Authorization", token.toString())
                        .execute().returnResponse();

                int code = httpResponse.getStatusLine().getStatusCode();

                // read the response body as a json string
                String responseBodyAsJson = IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
                System.out.println("response body was " + responseBodyAsJson);

                // turn the response body to an object of the right type
                if (code == 200) {
                    Person[] persons = gson.fromJson(responseBodyAsJson, Person[].class);
                    for (Person person : persons) {
                        System.out.println(person);
                    }
                } else {
                    Error error = gson.fromJson(responseBodyAsJson, Error.class);
                    System.out.println(error);
                }
            }
        }
    }
}
