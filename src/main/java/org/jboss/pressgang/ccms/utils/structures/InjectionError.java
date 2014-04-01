package org.jboss.pressgang.ccms.utils.structures;

import java.util.ArrayList;
import java.util.List;

public class InjectionError {
    private final String injection;
    private List<String> messages = new ArrayList<String>();

    public InjectionError(final String injection) {
        this.injection = injection;
    }

    public String getInjection() {
        return injection;
    }

    public void addMessage(final String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(final List<String> messages) {
        this.messages = messages;
    }
}
