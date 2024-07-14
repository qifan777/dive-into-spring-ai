<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue'
import { ElOption, ElSelect } from 'element-plus'
import _ from 'lodash'
export interface OptionItem {
  label: string
  value: string
}
const props = withDefaults(
  defineProps<{
    modelValue: string[] | string | undefined
    queryOptions: (query: string, defaultValue?: any) => Promise<any[]>
    multiple?: boolean
    labelProp: string | ((row: any) => string)
    valueProp?: string
    readonly?: boolean
  }>(),
  {
    multiple: false,
    valueProp: 'id',
    modelValue: '',
    readonly: false
  }
)
const emit = defineEmits<{ 'update:modelValue': [value: any] }>()

const options = ref<OptionItem[]>([])
const loading = ref(false)
const remoteMethod = (keyword: string) => {
  loading.value = true
  props.queryOptions(keyword.trim()).then((res) => {
    options.value = res.map((row) => {
      return {
        label:
          typeof props.labelProp === 'string' ? _.get(row, props.labelProp) : props.labelProp(row),
        value: _.get(row, props.valueProp)
      } satisfies OptionItem
    })
    loading.value = false
  })
}
const handleChange = (value: string[] | string) => {
  emit('update:modelValue', value)
}
onMounted(() => {
  remoteMethod('')
})
const isInit = ref(false)
watch(
  () => props.modelValue,
  (value) => {
    console.log(value)
    if (value && value.length > 0 && !isInit.value) {
      props.queryOptions('', value).then((res) => {
        const mapRes = res.map((row) => {
          return {
            label:
              typeof props.labelProp === 'string'
                ? _.get(row, props.labelProp)
                : props.labelProp(row),
            value: _.get(row, props.valueProp)
          } satisfies OptionItem
        })
        options.value = [
          ...options.value.filter(
            (option) => mapRes.findIndex((row) => row.value === option.value) < 0
          ),
          ...mapRes
        ]
      })
      isInit.value = true
    }
  },
  {
    immediate: true
  }
)
</script>
<template>
  <el-select
    v-if="!readonly"
    :model-value="modelValue"
    clearable
    collapseTags
    filterable
    :multiple="multiple"
    remote
    remote-show-suffix
    :remote-method="remoteMethod"
    :loading="loading"
    @change="handleChange"
    v-bind="$attrs"
  >
    <el-option
      :key="option.value"
      :value="option.value"
      :label="option.label"
      v-for="option in options"
    ></el-option>
  </el-select>
</template>
