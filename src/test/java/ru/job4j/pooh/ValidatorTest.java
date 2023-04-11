package ru.job4j.pooh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {

    @Test
    void whenWrongReqForTopicThenExceptionThrown() {
        Validator validator = new Validator("topic");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("x", "topic", "weather", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("POST", "queue", "weather", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("POST", "topic", "", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("GET", "topic", "weather", "")));
    }

    @Test
    void whenWrongReqForQueueThenExceptionThrown() {
        Validator validator = new Validator("queue");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("x", "queue", "weather", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("POST", "x", "weather", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("POST", "queue", "", "t=18")));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Req("POST", "queue", "weather", "")));
    }

}