<script lang="ts" setup>
import FeedbackTable from './components/feedback-table.vue'
import FeedbackQuery from './components/feedback-query.vue'
import FeedbackDialog from './components/feedback-dialog.vue'
</script>
<template>
  <div class="feedback-view">
    <feedback-query></feedback-query>
    <feedback-dialog></feedback-dialog>
    <feedback-table></feedback-table>
  </div>
</template>

<style lang="scss" scoped>
.feedback-view {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
</style>
