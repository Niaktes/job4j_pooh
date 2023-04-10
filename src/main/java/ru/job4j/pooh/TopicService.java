package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {

    Map<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        requestValidation(req);
        Resp response = null;
        if ("POST".equals(req.httpRequestType())) {
            response = postInfo(req.getSourceName(), req.getParam());
        } else if ("GET".equals(req.httpRequestType())) {
            response = getInfo(req.getSourceName(), req.getParam());
        }
        return response;
    }

    private Resp postInfo(String source, String info) {
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> clientsData = 
                topics.getOrDefault(source, new ConcurrentHashMap<>());
        for (ConcurrentLinkedQueue<String> data : clientsData.values()) {
            data.add(info);
        }
        return new Resp("", "200");
    }

    private Resp getInfo(String source, String client) {
        Resp response;
        String text = topics
                .computeIfAbsent(source, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(client, k -> new ConcurrentLinkedQueue<>()).poll();
        if (text == null || text.isEmpty()) {
            response = new Resp("", "204");
        } else {
            response = new Resp(text, "200");
        }
        return response;
    }

    private void requestValidation(Req req) {
        if(!"POST".equals(req.httpRequestType()) && !"GET".equals(req.httpRequestType())) {
            throw new IllegalArgumentException("Wrong type of request.");
        }
        if(!"topic".equals(req.getPoohMode())) {
            throw new IllegalArgumentException("Wrong request mode type.");
        }
        if(req.getSourceName().isEmpty()) {
            throw new IllegalArgumentException("You need to choose name of topic.");
        }
        if("GET".equals(req.httpRequestType()) && req.getParam().isEmpty()) {
            throw new IllegalArgumentException("You need designate client name for GET type of request.");
        }
    }

}