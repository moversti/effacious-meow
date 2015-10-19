package com.mycompany;

import com.google.gson.Gson;
import com.mycompany.domain.Error;
import com.mycompany.domain.Person;
import com.mycompany.domain.Product;
import com.mycompany.domain.Token;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class Main {

    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        String baseUrl = "http://localhost";
        String personsPort = ":4567";
        String sessionsPort = ":4567";
        String productsPort = ":4568";
        String personsUrl = baseUrl + personsPort + "/persons";
        String sessionUrl = baseUrl + sessionsPort + "/session";
        String productsUrl = baseUrl + productsPort + "/products";

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

                HttpResponse httpResponse = Request.Post(sessionUrl)
                        .bodyString(asJson, ContentType.APPLICATION_JSON)
                        .execute().returnResponse();

                int code = httpResponse.getStatusLine().getStatusCode();

                String responseBodyAsJson = IOUtils.toString(httpResponse
                        .getEntity().getContent(), Charset.forName("UTF-8"));
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
                HttpResponse httpResponse = Request.Post(personsUrl)
                        .bodyString(json, ContentType.APPLICATION_JSON)
                        .execute().returnResponse();
                int code = httpResponse.getStatusLine().getStatusCode();
                String responseBodyAsJson = IOUtils.toString(httpResponse.
                        getEntity().getContent(), Charset.forName("UTF-8"));
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
                String responseBodyAsJson = IOUtils.toString(httpResponse
                        .getEntity().getContent(), Charset.forName("UTF-8"));
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
            if (komento[0].equals("products")) {
                HttpResponse hr = Request.Get(productsUrl).execute().returnResponse();
                int code = hr.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(hr.getEntity(), "UTF-8");
                System.out.println("Response body:\n" + responseBody);
                if (code == 200) {
                    Product[] products = gson.fromJson(responseBody, Product[].class);
                    for (Product product : products) {
                        System.out.println(product);
                    }
                } else {
                    Error err = gson.fromJson(responseBody, Error.class);
                    System.out.println(err);
                }
            }
            if (komento[0].equals("add")) {
                String json;
                switch (komento[1]) {
                    case "product":
                        Product product = new Product(
                                komento[2],
                                komento[3],
                                Integer.parseInt(komento[4]),
                                Integer.parseInt(komento[5]));
                        json = gson.toJson(product);
                        break;
                    default:
                        json = "{}";
                }
                System.out.println("POST " + productsUrl + " token: " + token.toString() + " json: " + json);
                HttpResponse hr = Request.Post(productsUrl)
                        .addHeader("Authorization", token.toString())
                        .bodyString(json, ContentType.APPLICATION_JSON)
                        .execute().returnResponse();
                int code = hr.getStatusLine().getStatusCode();
                String responseBodyAsJson = EntityUtils.toString(hr.getEntity(), "UTF-8");
                System.out.println("Response body:\n" + responseBodyAsJson);

                if (code == 200) {
                    Product added = gson.fromJson(responseBodyAsJson, Product.class);
                    System.out.println(komento[1] + " added: " + added);
                } else {
                    Error err = gson.fromJson(responseBodyAsJson, Error.class);
                    System.out.println(err);
                }
            }
        }
    }
}
