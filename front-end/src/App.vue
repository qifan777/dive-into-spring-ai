<script setup lang="ts">
import { RouterView } from 'vue-router'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
</script>

<template>
  <el-config-provider :locale="zhCn">
    <router-view v-slot="{ Component }">
      <transition name="slide">
        <component :is="Component" />
      </transition> </router-view
  ></el-config-provider>
</template>

<style>
body {
  margin: 0;
  font-family:
    Noto Sans SC,
    SF Pro SC,
    SF Pro Text,
    SF Pro Icons,
    PingFang SC,
    Helvetica Neue,
    Helvetica,
    Arial,
    sans-serif;
}

.el-date-editor--datetime {
  width: 160px !important;
}

::-webkit-scrollbar {
  --bar-width: 5px;
  width: 5px;
  height: 5px;
}

::-webkit-scrollbar-track {
  background-color: transparent;
}

::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  background-clip: content-box;
  border: 1px solid transparent;
}

.slide-enter-active {
  transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-leave-active {
  transition: all 0.3s ease-out;
}

.slide-enter-from {
  transform: translateX(-100vw);
  opacity: 0;
}

.slide-enter-to {
  transform: translateX(0);
  opacity: 1;
}

.slide-leave-from {
  transform: translateX(0);
  opacity: 1;
}

.slide-leave-to {
  transform: translateX(100vw);
  opacity: 0;
}
</style>
