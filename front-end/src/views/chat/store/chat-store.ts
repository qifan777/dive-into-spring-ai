import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/utils/api-instance'
import { ElMessageBox } from 'element-plus'
import type { AiMessage, AiSession } from '@/apis/__generated/model/static'

export type AiSession2 = Pick<AiSession, 'id' | 'name' | 'editedTime'> & {
  messages: AiMessage2[]
}

export type AiMessage2 = Pick<AiMessage, 'textContent' | 'medias' | 'type' | 'aiSessionId'> & {
  id: string
}
export const useChatStore = defineStore('ai-chat', () => {
  const isEdit = ref(false)
  const activeSession = ref<AiSession2>()
  const sessionList = ref<AiSession2[]>([])
  const handleCreateSession = async (session: AiSession2) => {
    const res = await api.aiSessionController.save({ body: session })
    const sessionRes = await api.aiSessionController.findById({ id: res })

    sessionList.value.unshift({ ...sessionRes, messages: [] })
    activeSession.value = sessionList.value[0]
  }
  // 从会话列表中删除会话
  const handleDeleteSession = async (session: AiSession2) => {
    await api.aiSessionController.delete({ body: [session.id] })
    const index = sessionList.value.findIndex((value) => {
      return value.id === session.id
    })
    sessionList.value.splice(index, 1)
    if (index == sessionList.value.length) {
      activeSession.value = sessionList.value[index - 1]
    } else {
      activeSession.value = sessionList.value[index]
    }
  }
  // 修改会话
  const handleUpdateSession = async () => {
    if (!activeSession.value) {
      return
    }
    await api.aiSessionController.save({
      body: { ...activeSession.value }
    })
    isEdit.value = false
  }
  const handleClearMessage = async (sessionId: string) => {
    await ElMessageBox.confirm('是否清空会话记录？', '提示')
    await api.aiMessageController.deleteHistory({ sessionId })
    const index = sessionList.value.findIndex((value) => {
      return value.id === sessionId
    })
    activeSession.value = {
      ...(await api.aiSessionController.findById({ id: sessionId })),
      messages: []
    }
    activeSession.value.messages = await api.aiMessageController.getSessionMessages({ sessionId })
    sessionList.value[index] = activeSession.value
  }
  return {
    isEdit,
    activeSession,
    sessionList,
    handleUpdateSession,
    handleCreateSession,
    handleDeleteSession,
    handleClearMessage
  }
})
