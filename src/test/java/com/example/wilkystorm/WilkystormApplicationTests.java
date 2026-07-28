package com.example.wilkystorm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"xai.api.key=",
		"grok.api.key="
})
class WilkystormApplicationTests {

	@Test
	void contextLoads() {
	}
}
