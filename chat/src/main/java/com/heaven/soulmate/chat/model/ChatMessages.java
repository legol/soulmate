package com.heaven.soulmate.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by ChenJie3 on 2015/10/30.
 */

/*
{
    "uid":1,
    "target_uid":2,
    "messages":[
        {
            "type":123,
            "message":"abcde"
        },
        {
            "type":22,
            "message":"ccccc"
        }
    ]
}
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ChatMessages{

    @JsonProperty("uid")
    Long uid;

    @JsonProperty("target_uid")
    Long target_uid;

    @JsonProperty("messages")
    private List<MessageBean> messages;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getTarget_uid() {
        return target_uid;
    }

    public void setTarget_uid(Long target_uid) {
        this.target_uid = target_uid;
    }

    public List<MessageBean> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageBean> messages) {
        this.messages = messages;
    }
}
