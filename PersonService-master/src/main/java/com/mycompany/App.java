package com.mycompany;
import com.mycompany.domain.Person;
import com.mycompany.domain.Error;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mycompany.domain.JsonTransformer;
import com.mycompany.domain.Token;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        Gson gson = new Gson();

        // vaihda seuraavaan joku vapaa tietokanta
        String mongoLab = "mongodb://ohtu:ohtu@ds027519.mongolab.com:27519/kanta1";
        MongoClientURI uri = new MongoClientURI(mongoLab);
        Morphia morphia = new Morphia();
        MongoClient mongo = new MongoClient(uri);
        // jos käytät lokaalia mongoa, luo client seuraavasti
        //MongoClient mongo = new MongoClient();

        morphia.mapPackage("com.mycompany.domain");
        // vaihda seuraavaan sama kun kannan nimi kuin mongourlissa
        Datastore datastore = morphia.createDatastore(mongo, "kanta1");

        Set<String> validTokens = new HashSet<>();

        get("/ping", (request, response) -> {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String dir = System.getProperty("user.dir");

            return "{ \"name\": \""+name+"\", \"dir\": \""+dir+"\" }";
        });

        before("/persons", (request, response) -> {
            if ( request.requestMethod().equals("GET") &&
                 !validTokens.contains(request.headers("Authorization") ) ){
                halt(401, gson.toJson(Error.withCause("missing or invalid token")));
            }
        });

        get("/persons", (request, response) -> {
            return datastore.find(Person.class).asList();
        }, new JsonTransformer());

        post("/persons", (request, response) -> {
            Person person = gson.fromJson(request.body(), Person.class);

            if ( person == null || !person.valid()) {
                halt(400, gson.toJson(Error.withCause("all fields must have a value")));
            }

            if ( datastore.createQuery(Person.class).field("username").equal(person.username()).get() != null ){
                halt(400, gson.toJson(Error.withCause("username must be unique")));
            }

            datastore.save(person);
            return person;
        }, new JsonTransformer());

        post("/session", (request, response) -> {
            Person dataInRequest = gson.fromJson(request.body(), Person.class);

            Person person = datastore.createQuery(Person.class).field("username").equal(dataInRequest.username()).get();

            if ( person==null || !person.password().equals(dataInRequest.password()) ) {
                halt(401, gson.toJson(Error.withCause( "invalid credentials")));
            }

            Token token = Token.generate();
            validTokens.add(token.toString());
            return token;
        }, new JsonTransformer());

        after((request, response) -> {
            response.type("application/json");
        });

    }
}
