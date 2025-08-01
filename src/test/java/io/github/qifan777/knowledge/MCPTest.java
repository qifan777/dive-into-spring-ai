package io.github.qifan777.knowledge;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class MCPTest {
    @Autowired
    private List<McpSyncClient> mcpSyncClients;
    @Autowired
    ChatModel chatModel;

    @Test
    public void test() {
        ServerParameters parameters = ServerParameters
                // windows需要加后缀.cmd 其他系统不需要
                .builder("npx.cmd")
                .args("-y", "@executeautomation/playwright-mcp-server")
                .build();
        // 命令行调用协议
        StdioClientTransport transport = new StdioClientTransport(parameters);
        // 创建MCP客户端
        McpSyncClient client = McpClient.sync(transport).build();
        // 初始化MCP客户端
        client.initialize();
        // 查看MCP Sever提供的工具列表
        log.info("tools: {}", client.listTools());
        // 打开网站
        client.callTool(new McpSchema.CallToolRequest("playwright_navigate", Map.of("url", "https://www.jarcheng.top")));
        // 截图
        client.callTool(new McpSchema.CallToolRequest("playwright_screenshot", Map.of("savePang", true,
                "downloadsDir", "C:\\Users\\a1507\\Desktop")));

    }

    @Test
    public void test2() {
        ServerParameters parameters = ServerParameters
                // windows需要加后缀.cmd 其他系统不需要
                .builder("npx.cmd")
                .args("-y", "@executeautomation/playwright-mcp-server")
                .build();
        // 命令行调用协议
        StdioClientTransport transport = new StdioClientTransport(parameters);
        // 创建MCP客户端
        McpSyncClient client = McpClient.sync(transport).build();
        // 初始化MCP客户端
        client.initialize();
        List<ToolCallback> toolCallbacks = McpToolUtils.getToolCallbacksFromSyncClients(client);
        String content = ChatClient.create(chatModel)
                .prompt()
                .user("打开https://www.jarcheng.top 并截图保存到C:\\Users\\a1507\\Desktop")
                .toolCallbacks(toolCallbacks)
                .call()
                .content();
        log.info("content: {}", content);
    }

    @Test
    public void test3() {
        List<ToolCallback> toolCallbacks = McpToolUtils.getToolCallbacksFromSyncClients(mcpSyncClients);
        String content = ChatClient.create(chatModel)
                .prompt()
                .user("打开https://www.jarcheng.top 并截图保存到C:\\Users\\a1507\\Desktop")
                .toolCallbacks(toolCallbacks)
                .call()
                .content();
        log.info("content: {}", content);
    }
}
