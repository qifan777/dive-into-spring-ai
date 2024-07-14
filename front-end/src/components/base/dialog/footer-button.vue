<script lang="ts" setup>
defineProps({
  showClose: { type: Boolean, required: false, default: true },
  showConfirm: { type: Boolean, required: false, default: true }
})
const emit = defineEmits(['close', 'confirm'])
</script>

<template>
  <el-row style="margin-top: 20px" :justify="'end'">
    <el-button v-if="showClose" size="small" type="info" @click="() => emit('close')">
      取消
    </el-button>
    <el-button v-if="showConfirm" size="small" type="primary" @click="() => emit('confirm')">
      确认
    </el-button>
  </el-row>
</template>

<style scoped lang="scss"></style>
