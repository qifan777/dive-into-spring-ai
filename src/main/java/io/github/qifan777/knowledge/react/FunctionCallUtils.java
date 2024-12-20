package io.github.qifan777.knowledge.react;

import org.springframework.ai.chat.model.AbstractToolCallSupport;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class FunctionCallUtils extends AbstractToolCallSupport {
    private static FunctionCallUtils utils;

    protected FunctionCallUtils(FunctionCallbackContext functionCallbackContext, List<FunctionCallback> functionCallbacks) {
        super(functionCallbackContext, FunctionCallingOptions.builder().build(), functionCallbacks);
        utils = this;
    }

    public static Set<String> findFunctions(FunctionCallingOptions runtimeFunctionOptions) {
        return utils.runtimeFunctionCallbackConfigurations(runtimeFunctionOptions);
    }

    public static List<FunctionCallback> getFunctionCallback(Set<String> names) {
        return utils.resolveFunctionCallbacks(names);
    }
    public static FunctionCallback getFunctionCallback(String name) {
        return utils.getFunctionCallbackRegister().get(name);
    }
}
