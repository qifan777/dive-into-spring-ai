package io.github.qifan777.knowledge.ai.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class AbstractAgent {

    /**
     * 获取指定的function bean名称
     *
     * @return Function Call名称列表
     */
    public String[] getFunctions(Class<?>... clazz) {
        List<Class<?>> classList = Arrays.stream(clazz).filter(aClass -> aClass.isAnnotationPresent(Description.class)).toList();
        String[] names = new String[classList.size()];
        classList.stream().map(aClass -> StringUtils.uncapitalize(aClass.getSimpleName())).toList().toArray(names);
        return names;
    }
    /**
     * 获取内嵌的Function Call也就是Agent的Tools
     *
     * @return Function Call名称列表
     */
    public String[] getAgentFunctions(Class<?> clazz) {
        List<Class<?>> classList = Arrays.stream(clazz.getClasses()).filter(aClass -> aClass.isAnnotationPresent(Description.class)).toList();
        String[] names = new String[classList.size()];
        classList.stream().map(aClass -> StringUtils.uncapitalize(this.getClass().getSimpleName()) + "." + aClass.getSimpleName()).toList().toArray(names);
        return names;
    }

}