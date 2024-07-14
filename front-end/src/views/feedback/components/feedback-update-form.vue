<script lang="ts" setup>
import { storeToRefs } from 'pinia'
import { reactive, ref, watch } from 'vue'
import { useFeedbackStore } from '../store/feedback-store'
import { assertFormValidate } from '@/utils/common'
import { api } from '@/utils/api-instance'
import ImageUpload from '@/components/image/image-upload.vue'
import FooterButton from '@/components/base/dialog/footer-button.vue'
import type { FormInstance, FormRules } from 'element-plus'

const feedbackStore = useFeedbackStore()
const { closeDialog, reloadTableData } = feedbackStore
const { updateForm, dialogData } = storeToRefs(feedbackStore)
const updateFormRef = ref<FormInstance>()
const rules = reactive<FormRules<typeof updateForm>>({
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }],
  pictures: [{ required: true, message: '请输入反馈图片', trigger: 'blur' }]
})
const init = async () => {
  dialogData.value.title = '编辑'
  updateForm.value = {
    ...(await api.feedbackForAdminController.findById({ id: updateForm.value.id || '' }))
  }
}
watch(
  () => dialogData.value.visible,
  (value) => {
    if (value) {
      init()
    }
  },
  { immediate: true }
)
const handleConfirm = () => {
  updateFormRef.value?.validate(
    assertFormValidate(() => {
      api.feedbackForAdminController.update({ body: updateForm.value }).then(async (res) => {
        assertSuccess(res).then(() => {
          closeDialog()
          reloadTableData()
        })
      })
    })
  )
}
</script>
<template>
  <div class="update-form">
    <el-form labelWidth="120" class="form" ref="updateFormRef" :model="updateForm" :rules="rules">
      <el-form-item label="反馈内容" prop="content">
        <el-input v-model="updateForm.content"></el-input>
      </el-form-item>
      <el-form-item label="反馈图片" prop="pictures">
        <image-upload v-model="updateForm.pictures"></image-upload>
      </el-form-item>
    </el-form>
    <footer-button @close="closeDialog" @confirm="handleConfirm"></footer-button>
  </div>
</template>

<style lang="scss" scoped>
.update-form {
  margin-right: 30px;
}
</style>
