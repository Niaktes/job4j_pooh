package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {

    private final Map<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Validator validator = new Validator("queue");
        validator.validate(req);
        Resp response = null;
        if ("POST".equals(req.httpRequestType())) {
            response = postInfo(req.getSourceName(), req.getParam());
        } else if ("GET".equals(req.httpRequestType())) {
            response = getInfo(req.getSourceName());
        }
        return response;
    }

    private Resp postInfo(String source, String info) {
        queue.putIfAbsent(source, new ConcurrentLinkedQueue<>());
        queue.get(source).add(info);
        return new Resp("", "200");
    }

    private Resp getInfo(String source) {
        String text = queue.getOrDefault(source, new ConcurrentLinkedQueue<>()).poll();
        return text == null || text.isEmpty() ? new Resp("", "204") : new Resp(text, "200");
    }

}