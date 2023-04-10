package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {

    Map<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        requestValidation(req);
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
        Resp response;
        String text = queue.getOrDefault(source, new ConcurrentLinkedQueue<>()).poll();
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
        if(!"queue".equals(req.getPoohMode())) {
            throw new IllegalArgumentException("Wrong request mode type.");
        }
        if(req.getSourceName().isEmpty()) {
            throw new IllegalArgumentException("You need to choose name of queue.");
        }
        if("POST".equals(req.httpRequestType()) && req.getParam().isEmpty()) {
            throw new IllegalArgumentException("You need designate parameter for POST type of request.");
        }
    }

}