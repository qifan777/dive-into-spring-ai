package io.github.qifan777.knowledge.react;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReActThinkChainsResult {
    private List<ReActThinkResult> chains;
    private String thought;
    private String finalAnswer;
    private String originText;

    public String toString() {
        return chains.stream().map(ReActThinkResult::toString).collect(Collectors.joining("\n")) +
               "Thought: " + thought + "\nFinal Answer: " + finalAnswer;
    }
}
