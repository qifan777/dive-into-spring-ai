<script lang="ts" setup>
import { type Component } from 'vue'
import { storeToRefs } from 'pinia'
import { useFeedbackStore } from '../store/feedback-store'
import FeedbackCreateForm from './feedback-create-form.vue'
import FeedbackUpdateForm from './feedback-update-form.vue'
import type { EditMode } from '@/typings'

const feedbackStore = useFeedbackStore()
const { dialogData } = storeToRefs(feedbackStore)

const formMap: Record<EditMode, Component> = {
  CREATE: FeedbackCreateForm,
  UPDATE: FeedbackUpdateForm
}
</script>
<template>
  <div>
    <el-dialog v-model="dialogData.visible" :title="dialogData.title" :width="dialogData.width">
      <component :is="formMap[dialogData.mode]"></component>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped></style>
