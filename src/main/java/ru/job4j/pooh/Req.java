package ru.job4j.pooh;

public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode,String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        String[] text = content.split(System.lineSeparator());
        String httpRequestType = text[0].substring(0, text[0].indexOf(" "));
        String[] firstLine = text[0].substring(text[0].indexOf("/") + 1, text[0].indexOf(" HTTP")).split("/");
        String poohMode = firstLine[0];
        String sourceName = firstLine[1];
        String param = "";
        if ("POST".equals(httpRequestType)) {
            param = text[text.length - 1];
        }
        if ("GET".equals(httpRequestType) && "topic".equals(poohMode)){
            param = firstLine[firstLine.length - 1];
        }
        return  new Req(httpRequestType, poohMode, sourceName, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }

}