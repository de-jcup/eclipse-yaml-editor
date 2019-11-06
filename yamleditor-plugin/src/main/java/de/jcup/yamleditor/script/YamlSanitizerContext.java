package de.jcup.yamleditor.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YamlSanitizerContext {

    private List<String> messages = new ArrayList<>();
    
    public void addSanitizerMessage(String message) {
        messages.add(message);
    }
    
    public boolean hasSanitizerMessage() {
        return ! messages.isEmpty();
    }
    
    public List<String> getSanitizerMessages() {
        return Collections.unmodifiableList(messages);
    }
}
