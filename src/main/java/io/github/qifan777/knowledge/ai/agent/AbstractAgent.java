package io.github.qifan777.knowledge.ai.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.function.Function;

@Slf4j
public abstract class AbstractAgent<Req, Resp> implements Function<Req, Resp> {
    public String[] getFunctions() {
        String[] names = new String[this.getClass().getClasses().length];
        Arrays.stream(this.getClass().getClasses()).map(aClass -> StringUtils.uncapitalize(aClass.getName())).toList().toArray(names);
        return names;
    }
}