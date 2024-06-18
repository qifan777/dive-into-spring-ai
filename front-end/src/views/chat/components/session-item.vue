<script lang="ts" setup>
import { CircleClose } from '@element-plus/icons-vue'
import type { AiSession } from '@/views/chat/store/chat-store'

// entity：用于展示的会话信息
const prop = defineProps<{ active: boolean; session: AiSession }>()
// 定义删除事件，当触发删除事件时会向外部发送被删除的会话。
const emit = defineEmits<{
  delete: [session: AiSession]
}>()
// 当鼠标放到会话上时，会弹出删除图标，点击删除图标发送删除事件。
const handleDeleteSession = () => {
  emit('delete', prop.session)
}
</script>

<template>
  <!-- 如果处于激活状态则增加 active class -->
  <div :class="['session-item', active ? 'active' : '']">
    <!-- 会话的名称 -->
    <div class="name">{{ session.name }}</div>
    <!-- 会话内的消息数量和最近修改的时间 -->
    <div class="count-time">
      <div class="count">{{ session.messages ? session.messages.length : 0 }}条对话</div>
      <div class="time">{{ session.editedTime }}</div>
    </div>
    <!-- 当鼠标放在会话上时会弹出遮罩 -->
    <div class="mask"></div>
    <!-- 当鼠标放在会话上时会弹出删除按钮 -->
    <div class="btn-wrapper">
      <el-icon :size="15" class="close">
        <el-popconfirm title="是否确认永久删除该聊天会话？" @confirm="handleDeleteSession">
          <template #reference>
            <CircleClose />
          </template>
        </el-popconfirm>
      </el-icon>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.session-item {
  /* 加一下padding不要让会话内容靠边界太近 */
  padding: 12px;
  background-color: white;
  /* 给边界一些圆角看起来圆润一些 */
  border-radius: 10px;
  /* 固定宽度 */
  width: 250px;
  /* 当鼠标放在会话上时改变鼠标的样式，暗示用户可以点击。目前还没做拖动的效果，以后会做。 */
  cursor: grab;
  /* 父相子绝，父元素是相对布局的情况下，子元素的绝对布局是相当于父元素绝对布局。 */
  position: relative;
  /* 子元素的遮罩一开始会在外面，让溢出的遮罩不显示 */
  overflow: hidden;

  .name {
    /* 会话名称字体要大一些 */
    font-size: 14px;
    /* 凸显名称，加粗 */
    font-weight: 700;
    width: 200px;
    /* 加粗后颜色淡一些 */
    color: rgba(black, 0.8);
  }

  .count-time {
    /* 增加一些距离 */
    margin-top: 10px;
    /* 让字体小一些不能比会话名称要大（14px） */
    font-size: 10px;
    color: rgba(black, 0.5);
    /* 让消息数量和最近更新时间显示水平显示 */
    display: flex;
    /* 让消息数量和最近更新时间分布在水平方向的两端 */
    justify-content: space-between;
  }

  /* 当处于激活状态时增加蓝色描边 */
  &.active {
    /* 增加一些过渡 */
    transition: all 0.12s linear;
    border: 2px solid #1d93ab;
  }

  /* 当鼠标放在会话上时触发下面的css样式*/
  &:hover {
    /* 遮罩入场，从最左侧滑进去，渐渐变得不透明 */
    .mask {
      opacity: 1;
      left: 0;
    }

    .btn-wrapper {
      /* 暗示用户这个按钮可以点击 */
      &:hover {
        cursor: pointer;
      }

      /* 按钮入场，从最右侧滑进去，渐渐变得不透明 */
      opacity: 1;
      right: 20px;
    }
  }

  .mask {
    /* 渐变样式 */
    transition: all 0.2s ease-out;
    /* 相当于父亲绝对布局 */
    position: absolute;
    background-color: rgba(black, 0.05);
    /* 和父亲元素一样宽盖住父元素 */
    width: 100%;
    /* 和父亲元素一样高 */
    height: 100%;
    /*位置移到父元素的最上面 */
    top: 0;
    /* 向父元素的最左侧再增加一个父亲元素当前宽度的距离 */
    left: -100%;
    /* 透明度为0 */
    opacity: 0;
  }

  /* 删除按钮样式的逻辑和mask类似 */
  .btn-wrapper {
    color: rgba(black, 0.5);
    transition: all 0.2s ease-out;
    position: absolute;
    top: 10px;
    right: -20px;
    z-index: 10;
    opacity: 0;

    .edit {
      margin-right: 5px;
    }
  }
}
</style>
