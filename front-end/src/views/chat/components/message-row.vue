<script lang="tsx" setup>
import TextLoading from './text-loading.vue'
import logo from '@/assets/logo.jpg'
import MarkdownMessage from './markdown-message.vue'
import type { AiMessage } from '../store/chat-store'
import { computed } from 'vue'
// message：接受消息对象，展示消息内容和头像，并且根据角色调整消息位置。
// avatar：用户头像，如果角色是 Assistant则使用 logo。
const props = defineProps<{
  message: AiMessage
  avatar?: string
}>()

const images = computed(() => {
  const medias = props.message.medias || []
  return medias.filter((media) => media.type === 'image').map((media) => media.data)
})
</script>

<!-- 整个div是用来调整内部消息的位置，每条消息占的空间都是一整行，然后根据right还是left来调整内部的消息是靠右边还是靠左边 -->
<template>
  <div :class="['message-row', message.type === 'USER' ? 'right' : 'left']">
    <!-- 消息展示，分为上下，上面是头像，下面是消息 -->
    <div class="row">
      <!-- 头像， -->
      <div class="avatar-wrapper">
        <el-avatar
          :src="avatar || logo"
          class="avatar"
          shape="square"
          v-if="message.type === 'USER'"
        />
        <el-avatar :src="logo" class="avatar" shape="square" v-else />
      </div>
      <!-- 发送的消息或者回复的消息 -->
      <div class="message">
        <!-- 如果消息是文本，用markdown展示 -->
        <markdown-message
          :type="message.type"
          :message="message.textContent"
          v-if="message.textContent"
        ></markdown-message>
        <!-- 如果消息的内容是图片，则显示图片  -->
        <el-image
          v-for="image in images"
          :key="image"
          class="image"
          fit="cover"
          :preview-src-list="images"
          :src="image"
        ></el-image>
        <!-- 如果消息的内容为空则显示加载动画 -->
        <TextLoading v-if="!message.textContent && !images.length"></TextLoading>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.message-row {
  display: flex;

  &.right {
    // 消息显示在右侧
    justify-content: flex-end;

    .row {
      // 头像也要靠右侧
      .avatar-wrapper {
        display: flex;
        justify-content: flex-end;
      }

      // 用户回复的消息和ChatGPT回复的消息背景颜色做区分
      .message {
        background-color: rgb(231, 248, 255);
        :deep(.md-editor) {
          background-color: rgb(231, 248, 255);
        }
      }
    }
  }

  // 默认靠左边显示
  .row {
    .avatar-wrapper {
      .avatar {
        box-shadow: 20px 20px 20px 3px rgba(0, 0, 0, 0.01);
        margin-bottom: 20px;
      }
    }

    .message {
      font-size: 15px;
      padding: 1.5px;
      // 限制消息展示的最大宽度
      max-width: 800px;
      // 圆润一点
      border-radius: 7px;
      // 给消息框加一些描边，看起来更加实一些，要不然太扁了轻飘飘的。
      border: 1px solid rgba(black, 0.1);
      // 背景颜色
      background-color: #f4f4f5;

      .image {
        width: 600px;
        height: 600px;
      }
    }
  }
}
</style>
