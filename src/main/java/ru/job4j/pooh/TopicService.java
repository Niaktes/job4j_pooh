package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {

    private final Map<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Validator validator = new Validator("topic");
        validator.validate(req);
        Resp response = null;
        if ("POST".equals(req.httpRequestType())) {
            response = postInfo(req.getSourceName(), req.getParam());
        } else if ("GET".equals(req.httpRequestType())) {
            response = getInfo(req.getSourceName(), req.getParam());
        }
        return response;
    }

    private Resp postInfo(String source, String info) {
        topics.getOrDefault(source, new ConcurrentHashMap<>()).values().forEach(q -> q.add(info));
        return new Resp("", "200");
    }

    private Resp getInfo(String source, String client) {
        String text = topics
                .computeIfAbsent(source, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(client, k -> new ConcurrentLinkedQueue<>()).poll();
        return text == null || text.isEmpty() ? new Resp("", "204") : new Resp(text, "200");
    }

}