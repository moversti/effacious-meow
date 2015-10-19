package com.mycompany.domain;

public class Product extends MongoSavable {

    private String name;
    private String producer;
    private int price;
    private int inStock;

    public Product() {
    }

    public Product(String name, String producer, int price, int inStock) {
        this.name = name;
        this.producer = producer;
        this.price = price;
        this.inStock = inStock;
    }

    public String getName() {
        return name;
    }

    public String getProducer() {
        return producer;
    }

    public int getPrice() {
        return price;
    }

    public int getInStock() {
        return inStock;
    }

    public boolean valid() {
        return name != null && producer != null
                && !name.isEmpty() && !producer.isEmpty();
    }

    @Override
    public String toString() {
        return producer +" "+ name +" "+ price + ", " + inStock + "kpl";
    }

}
