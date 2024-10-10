<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { SSE } from 'sse.js'
import CollapseTitle from './collapse-title.vue'
import MarkdownMessage from '@/views/chat/components/markdown-message.vue'

const props = defineProps<{ path: string }>()
const API_PREFIX = import.meta.env.VITE_API_PREFIX
type AnalyzeResult = {
  id: string
  content: string
  fileName: string
  fileContent: string
}
const resultMap = ref<Record<string, AnalyzeResult>>({})
onMounted(() => {
  const evtSource = new SSE(API_PREFIX + '/analyze?path=' + props.path, {
    withCredentials: true,
    // 禁用自动启动，需要调用stream()方法才能发起请求
    start: false,
    method: 'GET'
  })
  evtSource.addEventListener('message', async (event: any) => {
    const result = JSON.parse(event.data) as AnalyzeResult
    if (!resultMap.value[result.id]) {
      resultMap.value[result.id] = result
    } else {
      resultMap.value[result.id].content += result.content
    }
  })
  evtSource.stream()
})
const fileToMarkdown = (result: AnalyzeResult) => {
  const content = result.fileContent.substring(1, result.fileContent.length - 1).replace(/\\/g, '')
  return '```java\n' + content + '\n```'
}
</script>

<template>
  <div class="task-view">
    <el-collapse>
      <el-collapse-item :title="key" v-for="key in Object.keys(resultMap)" :key="key">
        <template #title>
          <collapse-title :title="key"></collapse-title>
        </template>
        <markdown-message :message="fileToMarkdown(resultMap[key])"></markdown-message>
        <markdown-message :message="resultMap[key].content"></markdown-message>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<style scoped lang="scss">
.task-view {
  padding: 20px;
}
</style>
