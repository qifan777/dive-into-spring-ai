<template>
  <div class="values-chose">
    <div class="values">
      <!-- 显示当内的多个值，用','连接多个值 -->
      <el-input
        class="values-show"
        size="small"
        readonly
        :model-value="props.modelValue.join(',')"
      ></el-input>
    </div>
    <div class="values-input">
      <template v-for="(tag, index) in tags" :key="tag">
        <!-- 编辑值 -->
        <el-input
          v-if="editIndex === index"
          v-model="editInputValue"
          class="value value-edit"
          size="small"
          @blur="handleEditConfirm"
          @keydown.enter="handleEditConfirm"
        ></el-input>
        <!-- 显示值 -->
        <el-tag
          v-else
          class="value"
          closable
          :disable-transitions="false"
          @close="handleClose(index)"
          @click="handleEdit(index)"
          >{{ tag }}</el-tag
        >
      </template>
      <!-- 点击新增显示输入框，否则显示新增按钮  -->
      <el-input
        v-if="inputVisible"
        ref="inputRef"
        v-model="inputValue"
        class="value-input"
        size="small"
        @blur="handleInputConfirm"
        @keydown.enter="handleInputConfirm"
      ></el-input>
      <el-button v-else size="small" @click="showInput"> + 新增值 </el-button>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { computed, nextTick, ref } from 'vue'
import { ElButton, ElInput, ElTag } from 'element-plus'

const props = defineProps({ modelValue: { type: Array<string>, required: false, default: '' } })
const emit = defineEmits<{ 'update:modelValue': [values: string[]] }>()

const tags = computed({
  get: () => {
    return props.modelValue ? props.modelValue : []
  },
  set: (values) => {
    emit('update:modelValue', values)
  }
})
// 编辑值
const editIndex = ref(-1)
const editInputValue = ref('')
// 点击值标签，显示输入框编辑值
const handleEdit = (index: number) => {
  editInputValue.value = tags.value[index]
  editIndex.value = index
}
// 编辑值输入框确认
const handleEditConfirm = () => {
  if (editInputValue.value === tags.value[editIndex.value]) {
    return
  }
  tags.value[editIndex.value] = editInputValue.value
  tags.value = [...tags.value]
  editIndex.value = -1
}
// 点击值标签的右上角触发删除值
const handleClose = (index: number) => {
  tags.value.splice(index, 1)
  tags.value = [...tags.value]
}

// 新增值
const inputRef = ref<InstanceType<typeof ElInput>>()
const inputVisible = ref(false)
const inputValue = ref('')
const handleInputConfirm = () => {
  if (inputValue.value) {
    tags.value = [...tags.value, inputValue.value]
  }
  inputVisible.value = false
  inputValue.value = ''
}
const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    if (inputRef.value && inputRef.value.input) {
      inputRef.value.input.focus()
    }
  })
}
</script>

<style lang="scss" scoped>
.values {
  display: flex;
  justify-content: flex-start;

  .values-show {
    width: 100px;
  }
}

.values-input {
  margin-top: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 10px;
  .value-edit {
    width: 100px;
  }

  .value {
    margin-right: 5px;
  }

  .value-input {
    width: 100px;
  }
}
</style>
