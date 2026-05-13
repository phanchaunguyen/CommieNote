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

    public String generateMasterNote(String oldMasterNote, List<String> unmergedSubNotes){
        StringBuilder promtBuilder = new StringBuilder();

        promtBuilder.append("You are a professional individual in this field of work");

        if (oldMasterNote != null && oldMasterNote.isEmpty()) {
            promtBuilder.append("This is the current MasterNote: \n");
            promtBuilder.append("\"").append(oldMasterNote).append("\"\n");
            promtBuilder.append("Here are new subNotes, mix the new subNotes with the MasterNote to create a complete summary of the topic, add sections if needed, no repetition");
        }
        else {
            promtBuilder.append("Here are new subNotes, combine them together to create a complete summary of the topic, add sections if needed, no repetition \n");
        }

        for (int i = 0; i < unmergedSubNotes.size(); i++) {
            promtBuilder.append(i+1).append(". ").append(unmergedSubNotes.get(i)).append("\n");
        }

        return chatModel.call(promtBuilder.toString());
    }
}
