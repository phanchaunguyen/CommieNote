package com.CommieNote.masternote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ChatModel chatModel;

    public String summerizeNotes(List<String> rawNotes){
        StringBuilder promtBuilder = new StringBuilder();
        promtBuilder.append("You are a professional individual in this field of work");
        promtBuilder.append("Review these notes carefully, then, summarize them into a note with sections and subsections (if necessary), maintain important content, while keeping it reasonably long and coherent");

        for (int i = 0; i < rawNotes.size(); i++) {
            promtBuilder.append(i + 1).append(". ").append(rawNotes.get(i)).append("\n");
        }

        return chatModel.call(promtBuilder.toString());
    }
}
