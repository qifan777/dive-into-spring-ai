<script lang="ts" setup>
import { nextTick, onMounted, ref } from 'vue'
import SessionItem from './components/session-item.vue'
import { ChatRound, Close, Delete, EditPen } from '@element-plus/icons-vue'
import MessageRow from './components/message-row.vue'
import MessageInput from './components/message-input.vue'
import { storeToRefs } from 'pinia'
import { ElIcon, ElMessage, type UploadProps, type UploadUserFile } from 'element-plus'
import { api } from '@/utils/api-instance'
import { SSE } from 'sse.js'
import { type AiMessage, useChatStore } from './store/chat-store'
import type { AiMessageParams, AiMessageWrapper } from '@/apis/__generated/model/static'

type ChatResponse = {
  metadata: {
    usage: {
      totalTokens: number
    }
  }
  result: {
    metadata: {
      finishReason: string
    }
    output: {
      messageType: string
      content: string
    }
  }
}
const API_PREFIX = import.meta.env.VITE_API_PREFIX
const chatStore = useChatStore()
const { handleDeleteSession, handleUpdateSession, handleClearMessage } = chatStore
const { activeSession, sessionList, isEdit } = storeToRefs(chatStore)
const messageListRef = ref<InstanceType<typeof HTMLDivElement>>()
const loading = ref(true)

onMounted(async () => {
  // 查询自己的聊天会话
  api.aiSessionController.findByUser().then((res) => {
    // 讲会话添加到列表中
    sessionList.value = res.map((row) => {
      return { ...row, checked: false }
    })
    // 默认选中的聊天会话是第一个
    if (sessionList.value.length > 0) {
      activeSession.value = sessionList.value[0]
    } else {
      handleSessionCreate()
    }
    loading.value = false
  })
})

// ChatGPT的回复
const responseMessage = ref<AiMessage>({
  id: new Date().getTime().toString(),
  type: 'ASSISTANT',
  medias: [],
  textContent: '',
  sessionId: ''
})

const handleSendMessage = async (message: { text: string; image: string }) => {
  if (!activeSession.value) {
    ElMessage.warning('请创建会话')
    return
  }
  // 图片/语音
  const medias: AiMessage['medias'] = []
  if (message.image) {
    medias.push({ type: 'image', data: message.image })
  }
  // 用户的提问
  const chatMessage = {
    id: new Date().getTime().toString(),
    sessionId: activeSession.value.id,
    medias,
    textContent: message.text,
    type: 'USER'
  } satisfies AiMessage

  responseMessage.value = {
    id: new Date().getTime().toString(),
    medias: [],
    type: 'ASSISTANT',
    textContent: '',
    sessionId: activeSession.value.id
  }
  const body: AiMessageWrapper = { message: chatMessage, params: options.value }
  const form = new FormData()
  form.set('input', JSON.stringify(body))

  if (fileList.value.length && fileList.value[0].raw) {
    form.append('file', fileList.value[0].raw)
  }
  const evtSource = new SSE(API_PREFIX + '/message/chat', {
    withCredentials: true,
    // 禁用自动启动，需要调用stream()方法才能发起请求
    start: false,
    payload: form as any,
    method: 'POST'
  })
  evtSource.addEventListener('message', async (event: any) => {
    const response = JSON.parse(event.data) as ChatResponse
    const finishReason = response.result.metadata.finishReason
    if (response.result.output.content) {
      responseMessage.value.textContent += response.result.output.content
      // 滚动到底部
      await nextTick(() => {
        messageListRef.value?.scrollTo(0, messageListRef.value.scrollHeight)
      })
    }
    if (finishReason && finishReason.toLowerCase() == 'stop') {
      evtSource.close()
      // 保存用户的提问
      await api.aiMessageController.save({ body: chatMessage })
      // 保存大模型的回复
      await api.aiMessageController.save({ body: responseMessage.value })
    }
  })

  // 调用stream，发起请求。
  evtSource.stream()
  // 将两条消息显示在页面中
  activeSession.value.messages.push(...[chatMessage, responseMessage.value])
  await nextTick(() => {
    messageListRef.value?.scrollTo(0, messageListRef.value.scrollHeight)
  })
}

const handleSessionCreate = () => {
  chatStore.handleCreateSession({ name: '新的聊天' })
}
const options = ref<AiMessageParams>({
  enableVectorStore: false,
  enableAgent: false
})
const embeddingLoading = ref(false)
const onUploadSuccess = () => {
  embeddingLoading.value = false
  ElMessage.success('上传成功')
}
const beforeUpload: UploadProps['beforeUpload'] = () => {
  embeddingLoading.value = true
  return true
}
const fileList = ref<UploadUserFile[]>([])
</script>
<template>
  <!-- 最外层页面于窗口同宽，使聊天面板居中 -->
  <div class="home-view">
    <!-- 整个聊天面板 -->
    <div class="chat-panel" v-loading="loading">
      <!-- 左侧的会话列表 -->
      <div class="session-panel">
        <div class="title">AI助手</div>
        <div class="session-list" v-if="activeSession">
          <!-- for循环遍历会话列表用会话组件显示，并监听点击事件和删除事件。点击时切换到被点击的会话，删除时从会话列表中提出被删除的会话。 -->
          <session-item
            v-for="session in sessionList"
            :key="session.id"
            :active="session.id === activeSession.id"
            :session="session"
            class="session"
            @click="activeSession = session"
            @delete="handleDeleteSession"
          ></session-item>
        </div>
        <div class="button-wrapper">
          <el-button
            style="margin-right: 20px"
            :icon="ChatRound"
            size="small"
            @click="handleSessionCreate"
            >创建会话
          </el-button>
        </div>
      </div>
      <!-- 右侧的消息记录 -->
      <div class="message-panel">
        <!-- 会话名称 -->
        <div class="header" v-if="activeSession">
          <div class="front">
            <!-- 如果处于编辑状态则显示输入框让用户去修改 -->
            <div v-if="isEdit" class="title">
              <!-- 按回车代表确认修改 -->
              <el-input
                v-model="activeSession.name"
                @keydown.enter="handleUpdateSession"
              ></el-input>
            </div>
            <!-- 否则正常显示标题 -->
            <div v-else class="title">{{ activeSession.name }}</div>
            <div class="description">{{ activeSession.messages.length }}条对话</div>
          </div>
          <!-- 尾部的编辑按钮 -->
          <div class="rear">
            <el-icon :size="20" style="margin-right: 10px">
              <Delete @click="handleClearMessage(activeSession.id)" />
            </el-icon>
            <el-icon :size="20">
              <!-- 不处于编辑状态显示编辑按钮 -->
              <EditPen v-if="!isEdit" @click="isEdit = true" />
              <!-- 处于编辑状态显示取消编辑按钮 -->
              <Close v-else @click="isEdit = false"></Close>
            </el-icon>
          </div>
        </div>
        <el-divider :border-style="'solid'" />
        <div ref="messageListRef" class="message-list">
          <!-- 过渡效果 -->
          <transition-group name="list" v-if="activeSession">
            <message-row
              v-for="message in activeSession.messages"
              :key="message.id"
              :message="message"
            ></message-row>
          </transition-group>
        </div>
        <!-- 监听发送事件 -->
        <message-input @send="handleSendMessage" v-if="activeSession"></message-input>
      </div>
      <div class="option-panel">
        <el-form size="small">
          <el-form-item>
            <el-upload
              v-loading="embeddingLoading"
              :action="`${API_PREFIX}/document/embedding`"
              :show-file-list="false"
              :on-success="onUploadSuccess"
              :before-upload="beforeUpload"
            >
              <el-button type="primary">上传文档</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="知识库">
            <el-switch v-model="options.enableVectorStore"></el-switch>
          </el-form-item>
          <el-form-item label="agent（智能体）">
            <el-switch v-model="options.enableAgent"></el-switch>
          </el-form-item>
          <el-form-item label="文件">
            <div class="upload">
              <el-upload v-model:file-list="fileList" :auto-upload="false" :limit="1">
                <el-button type="primary">上传文本文件</el-button>
              </el-upload>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.home-view {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;

  .chat-panel {
    display: flex;
    background-color: white;
    width: 90%;
    height: 90%;
    box-shadow: 0 0 10px rgba(black, 0.1);
    border-radius: 10px;

    .session-panel {
      display: flex;
      flex-direction: column;
      box-sizing: border-box;
      padding: 20px;
      position: relative;
      border-right: 1px solid rgba(black, 0.07);
      background-color: rgb(231, 248, 255);
      height: 100%;
      /* 标题 */
      .title {
        margin-top: 20px;
        font-size: 20px;
      }

      .session-list {
        overflow-y: scroll;
        margin: 20px 0;
        flex: 1;

        .session {
          /* 每个会话之间留一些间距 */
          margin-top: 20px;
        }

        .session:first-child {
          margin-top: 0;
        }
      }

      .button-wrapper {
        /* entity-panel是相对布局，这边的button-wrapper是相对它绝对布局 */
        bottom: 20px;
        left: 0;
        display: flex;
        /* 让内部的按钮显示在右侧 */
        justify-content: flex-end;
        /* 宽度和session-panel一样宽*/
        width: 100%;

        /* 按钮于右侧边界留一些距离 */
        .new-session {
          margin-right: 20px;
        }
      }
    }

    /* 右侧消息记录面板*/
    .message-panel {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;

      .header {
        padding: 20px 20px 0 20px;
        display: flex;
        /* 会话名称和编辑按钮在水平方向上分布左右两边 */
        justify-content: space-between;

        /* 前部的标题和消息条数 */
        .front {
          .title {
            color: rgba(black, 0.7);
            font-size: 20px;
          }

          .description {
            margin-top: 10px;
            color: rgba(black, 0.5);
          }
        }

        /* 尾部的编辑和取消编辑按钮 */
        .rear {
          display: flex;
          align-items: center;
        }
      }

      .message-list {
        padding: 15px;
        width: 100%;
        flex: 1;
        box-sizing: border-box;
        // 消息条数太多时，溢出部分滚动
        overflow-y: scroll;
        // 当切换聊天会话时，消息记录也随之切换的过渡效果
        .list-enter-active,
        .list-leave-active {
          transition: all 0.5s ease;
        }

        .list-enter-from,
        .list-leave-to {
          opacity: 0;
          transform: translateX(30px);
        }
      }
    }

    //  选项面板
    .option-panel {
      width: 200px;
      padding: 20px;
      border-left: 1px solid rgba(black, 0.07);

      .upload {
        width: 160px;
      }
    }
  }
}
</style>
