package com.mycompany.domain;

import com.google.gson.Gson;
import com.mycompany.domain.MongoSavable;
import java.util.List;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new Gson();

    @Override
    public String render(Object model) {
        if (model instanceof List) {
            List rawObjects = (List) model;
            if (rawObjects.size() > 0 && rawObjects.get(0) instanceof MongoSavable) {

                List<MongoSavable> objects = (List<MongoSavable>) model;
                for (MongoSavable mongoSavable : objects) {
                    mongoSavable.setIdentifier(mongoSavable.getId().toHexString());
                    mongoSavable.setId(null);
                }
            }
        } else if (model instanceof MongoSavable) {
            MongoSavable object = (MongoSavable) model;
            object.setIdentifier(object.getId().toHexString());
            object.setId(null);
        }

        return gson.toJson(model);
    }
}
