package io.github.qifan777.knowledge.react;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReActThinkResult {
    private String thought;
    private String action;
    private String actionInput;
    private String originText;
    private String actionOutput;

    public String toString() {
        return "Thought: " + thought + "\nAction: " + action + "\nAction Input: " + actionInput + "\nObservation: " + actionOutput;
    }
}