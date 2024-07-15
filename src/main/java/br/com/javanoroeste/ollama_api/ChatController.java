package br.com.javanoroeste.ollama_api;

import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final OllamaChatModel chatModel;

    @Autowired
    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Por que o céu é azul?") String message) {
        return Map.of("generation", chatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
    @GetMapping("/ai/generateModal")
    public String generateModal(@RequestParam(value = "message", defaultValue = "Explain what do you see on this picture?") String message) throws IOException {
        byte[] imageData = new ClassPathResource("/multimodal.test.png").getContentAsByteArray();
        var userMessage = new UserMessage("Explain what do you see on this picture?",
                List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageData)));

        ChatResponse response = chatModel.call(
                new Prompt(List.of(userMessage), OllamaOptions.create().withModel("llava")));
        return response.getResult().getOutput().getContent();
    }

}
