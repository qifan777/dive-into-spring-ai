<template>
  <div class="key-value-section">
    <div class="key-value-wrapper" v-for="(keyValue, index) in keyValueList" :key="index">
      <el-button
        class="close-btn"
        type="warning"
        size="small"
        circle
        @click="deleteKeyValue(index)"
      >
        <el-icon>
          <close></close>
        </el-icon>
      </el-button>
      <el-form label-width="80" label-position="left">
        <el-form-item label="键名称">
          <el-input style="width: 100px" v-model="keyValueList[index].name" size="small">
          </el-input>
        </el-form-item>
        <el-form-item label="值列表">
          <value-input v-model="keyValueList[index].values"></value-input>
        </el-form-item>
      </el-form>
    </div>
    <el-button type="primary" size="small" class="plus" @click="addKeyValue">
      <el-icon>
        <plus></plus>
      </el-icon>
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Close, Plus } from '@element-plus/icons-vue'
import ValueInput from './value-input.vue'
import type { KeyValue } from '@/apis/__generated/model/static'

const props = defineProps<{ modelValue?: KeyValue[] }>()
const emit = defineEmits<{
  change: [data: KeyValue[]]
  'update:modelValue': [data: KeyValue[]]
}>()
const keyValueList = ref<KeyValue[]>([])
const deleteKeyValue = (index: number) => {
  keyValueList.value.splice(index, 1)
}
const addKeyValue = () => {
  keyValueList.value.push({
    name: '',
    values: []
  })
}
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue) {
      keyValueList.value = newValue
    }
  }
)
watch(
  () => keyValueList.value,
  () => {
    emit('update:modelValue', keyValueList.value)
  },
  { deep: true }
)
</script>

<style lang="scss" scoped>
.key-value-section {
  width: 100%;
  .plus {
    margin-top: 20px;
  }
  .key-value-wrapper {
    margin-bottom: 20px;
    border: rgba(114, 207, 222, 0.5) 1px dashed;
    padding: 10px;
    position: relative;
    margin-top: 20px;
    .close-btn {
      position: absolute;
      top: 5px;
      right: 5px;
      width: 15px;
      height: 15px;
    }
  }
}
</style>
