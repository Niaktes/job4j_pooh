package ru.job4j.pooh;

public class Validator {

    private final String mode;

    public Validator(String mode) {
        this.mode = mode;
    }

    public void validate(Req req) {
        if (!"POST".equals(req.httpRequestType()) && !"GET".equals(req.httpRequestType())) {
            throw new IllegalArgumentException("Wrong type of request.");
        }
        if (!mode.equals(req.getPoohMode())) {
            throw new IllegalArgumentException("Wrong request mode type.");
        }
        if (req.getSourceName().isEmpty()) {
            throw new IllegalArgumentException("You need to choose data source name.");
        }
        if ("queue".equals(mode) && "POST".equals(req.httpRequestType()) && req.getParam().isEmpty()) {
            throw new IllegalArgumentException("You need designate parameter for POST type of request.");
        }
        if ("topic".equals(mode) && "GET".equals(req.httpRequestType()) && req.getParam().isEmpty()) {
            throw new IllegalArgumentException("You need designate client name for GET type of request.");
        }
    }

}