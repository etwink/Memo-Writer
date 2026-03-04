package com.memoapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "memos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memo {

    @Id
    private String id;

    private String title;

    private String toRecipients;

    private String fromSender;

    private String ccRecipients;

    private String body;

    private String attachments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Memo(String title, String toRecipients, String fromSender, String ccRecipients, String body, String attachments) {
        this.title = title;
        this.toRecipients = toRecipients;
        this.fromSender = fromSender;
        this.ccRecipients = ccRecipients;
        this.body = body;
        this.attachments = attachments;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
