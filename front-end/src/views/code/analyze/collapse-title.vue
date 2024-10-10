<script setup lang="ts">
import type { ButtonProps } from 'element-plus/es/components/button/src/button'

defineProps<{
  title: string
  icon?: string
  type?: ButtonProps['type']
}>()
</script>

<template>
  <div class="title-wrapper">
    <div class="right-icon"></div>
    <div class="title">{{ title }}</div>
    <el-button :type="type" :icon="icon" v-if="icon" plain link></el-button>
  </div>
</template>

<style scoped lang="scss">
.title-wrapper {
  display: flex;
  align-items: center;
  color: var(--el-color-primary);
  width: 100%;

  .title {
    &:hover {
      cursor: copy;
    }
  }

  .right-icon {
    margin-right: 5px;
    height: 14px;
    width: 3px;
    background-color: var(--el-color-primary);
  }
}
</style>
