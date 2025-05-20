package com.java.ai.langchain4j;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2025/5/20 10:14
 */
@SpringBootTest
public class  LLMTest {

    @Test
    void test(){
        OpenAiChatModel model = OpenAiChatModel.builder().baseUrl("http://langchain4j.dev/demo/openai/v1").apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        String answer = model.chat("520该如何邀约女生");
        System.out.println(answer);


    }
}
