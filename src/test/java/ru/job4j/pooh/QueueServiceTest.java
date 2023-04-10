package ru.job4j.pooh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class QueueServiceTest {

    QueueService service;

    @BeforeEach
    void init() {
        service = new QueueService();
    }

    @Test
    void whenPostThenGetQueue() {
        String paramForPostMethod = "temperature=18";
        service.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = service.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("temperature=18"));
    }

    @Test
    void whenQueueDoesNotExistsThenGet204Response() {
        service.process(new Req("POST", "queue", "game", "health=18"));
        Resp response = service.process(new Req("GET", "queue", "weather", null));
        assertEquals("", response.text());
        assertEquals("204", response.status());
    }

    @Test
    void whenGetQueueIsEmptyThenGet204Response() {
        service.process(new Req("POST", "queue", "weather", "temp=18"));
        Resp response = service.process(new Req("GET", "queue", "weather", null));
        assertEquals("temp=18", response.text());
        assertEquals("200", response.status());
        response = service.process(new Req("GET", "queue", "weather", null));
        assertEquals("", response.text());
        assertEquals("204", response.status());
    }

    @Test
    void whenPostToDifferentQueuesThenGetAnotherData() {
        service.process(new Req("POST", "queue", "weather", "temp=18"));
        service.process(new Req("POST", "queue", "game", "health=18"));
        assertEquals("temp=18", service.process(new Req("GET", "queue", "weather", null)).text());
        assertEquals("health=18", service.process(new Req("GET", "queue", "game", null)).text());
    }

    @Test
    void whenPostMultipleValuesThenGetFromFirstToLast() {
        service.process(new Req("POST", "queue", "weather", "temp=18"));
        service.process(new Req("POST", "queue", "weather", "wind=5"));
        service.process(new Req("POST", "queue", "weather", "pressure=750"));
        assertEquals("temp=18", service.process(new Req("GET", "queue", "weather", null)).text());
        assertEquals("wind=5", service.process(new Req("GET", "queue", "weather", null)).text());
        assertEquals("pressure=750", service.process(new Req("GET", "queue", "weather", null)).text());
    }

    @Test
    void whenWrongReqThenExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> service.process(new Req("x", "queue", "weather", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> service.process(new Req("POST", "x", "weather", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> service.process(new Req("POST", "queue", "", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> service.process(new Req("POST", "queue", "weather", "")));
    }

}