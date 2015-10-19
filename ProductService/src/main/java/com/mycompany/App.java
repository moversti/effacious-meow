package com.mycompany;

import com.google.gson.Gson;
import com.mycompany.domain.Error;
import com.mycompany.domain.JsonTransformer;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.lang.management.ManagementFactory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import com.mycompany.domain.Product;
import com.mycompany.domain.Token;
import java.util.Set;
import org.apache.http.HttpResponse;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import spark.Spark;
import static spark.Spark.before;

public class App {

    public static void main(String[] args) {
        Spark.port(4568);
        Gson gson = new Gson();
        String mongoLab = "mongodb://ohtu:ohtu@ds035674.mongolab.com:35674/kanta6";
        MongoClientURI uri = new MongoClientURI(mongoLab);
        Morphia morphia = new Morphia();
        MongoClient mongo = new MongoClient(uri);
        morphia.mapPackage("com.mycompany.domain");
        Datastore datastore = morphia.createDatastore(mongo, "kanta6");

        before((req, res) -> {
            String method = req.requestMethod();
            Set<String> headers = req.headers();
            String body = req.body();
            StringBuilder sb = new StringBuilder();
            sb.append("-------------------\n");
            sb.append(method).append('\n');
            headers.forEach(h -> sb.append(h).append(" = ").append(req.headers(h)).append('\n'));
            sb.append(body);
            System.out.println(sb.toString());
        });

        get("/ping", (request, response) -> {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String dir = System.getProperty("user.dir");

            return "{ \"name\": \"" + name + "\", \"dir\": \"" + dir + "\" }";
        });

        get("/products", (request, response) -> {
            return datastore.find(Product.class).asList();
        }, new JsonTransformer());

        before("/products", (request, response) -> {
            if (request.requestMethod().equals("POST")) {
                String token_value = request.headers("Authorization");
                if (token_value == null || token_value.isEmpty()) {
                    halt(401, gson.toJson(Error.withCause("Missing token")));
                }
                HttpResponse httpResponse = Request.Get("http://localhost:4567/tokens/" + token_value).execute().returnResponse();
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                Token token = gson.fromJson(responseBody, Token.class);
                if (!token.isValid()) {
                    halt(401, gson.toJson(Error.withCause("Token not valid")));
                }
            }
        });

        post("/products", (request, response) -> {
            Product p = gson.fromJson(request.body(), Product.class);
            if (p == null) {
                halt(400, gson.toJson(Error.withCause("p is null")));
            }
            if (!p.valid()) {
                halt(400, gson.toJson(Error.withCause("p is invalid: " + p.toString())));
            }
            datastore.save(p);
            return p;
        }, new JsonTransformer());

        after((request, response) -> {
            response.type("application/json");
        });
    }
}
