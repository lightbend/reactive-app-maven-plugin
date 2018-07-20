package com.lightbend.app;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AppTest {
    @Test
    public void testApp() {
        assertEquals("Hello, world!", App.greeting);
    }
}
