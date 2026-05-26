package com.cholosikhai.cholosikhaiai;

import com.cholosikhai.cholosikhaiai.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CholoSikhaiAiApplicationTests {

    @Autowired
    ChatService chatService;

    @Test
    void contextLoads() {
        chatService.saveInVDB();
    }

}
