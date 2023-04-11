package ru.job4j.pooh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class TopicServiceTest {

    TopicService service;

    @BeforeEach
    void init() {
        service = new TopicService();
    }

    @Test
    void whenTopic() {
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        service.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        service.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        Resp result1 = service.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        Resp result2 = service.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is(""));
    }

    @Test
    void whenTopicDoesNotExistsThenInformationIgnored() {
        service.process(new Req("GET", "topic", "game", "client"));
        service.process(new Req("POST", "topic", "weather", "t=18"));
        service.process(new Req("POST", "topic", "game", "h=50"));
        Resp responseGame = service.process(new Req("GET", "topic", "game", "client"));
        Resp responseWeather = service.process(new Req("GET", "topic", "weather", "client"));
        assertEquals("h=50", responseGame.text());
        assertEquals("200", responseGame.status());
        assertEquals("", responseWeather.text());
        assertEquals("204", responseWeather.status());
    }

    @Test
    void whenTopicQueueIsEmptyThenGet204Response() {
        service.process(new Req("GET", "topic", "weather", "client"));
        service.process(new Req("POST", "topic", "weather", "t=18"));
        Resp response = service.process(new Req("GET", "topic", "weather", "client"));
        assertEquals("t=18", response.text());
        assertEquals("200", response.status());
        response = service.process(new Req("GET", "topic", "weather", "client"));
        assertEquals("", response.text());
        assertEquals("204", response.status());
    }

    @Test
    void whenAddTopicToMultipleClientsThenGetDataAfterClientConnection() {
        service.process(new Req("GET", "topic", "weather", "client_1"));
        service.process(new Req("POST", "topic", "weather", "temp=18"));
        Resp responseClient2 = service.process(new Req("GET", "topic", "weather", "client_2"));
        assertEquals("", responseClient2.text());
        assertEquals("204", responseClient2.status());
        service.process(new Req("POST", "topic", "weather", "wind=5"));
        Resp responseClient1 = service.process(new Req("GET", "topic", "weather", "client_1"));
        responseClient2 = service.process(new Req("GET", "topic", "weather", "client_2"));
        assertEquals("temp=18", responseClient1.text());
        assertEquals("wind=5", responseClient2.text());
        responseClient1 = service.process(new Req("GET", "topic", "weather", "client_1"));
        responseClient2 = service.process(new Req("GET", "topic", "weather", "client_2"));
        assertEquals("wind=5", responseClient1.text());
        assertEquals("", responseClient2.text());
    }

}