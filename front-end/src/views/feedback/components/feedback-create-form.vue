<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { assertFormValidate } from '@/utils/common'
import { api } from '@/utils/api-instance'
import FooterButton from '@/components/base/dialog/footer-button.vue'
import {
  ElMessage,
  type FormInstance,
  type FormRules,
  type UploadProps,
  type UploadUserFile
} from 'element-plus'
import type { FeedbackCreateInput } from '@/apis/__generated/model/static'
import { Edit, Plus } from '@element-plus/icons-vue'
import type { Result } from '@/typings'
const createForm = ref<FeedbackCreateInput>({ content: '', pictures: [] })
const createFormRef = ref<FormInstance>()
const rules = reactive<FormRules<typeof createForm>>({
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }],
  pictures: [{ required: true, message: '请输入反馈图片', trigger: 'blur' }]
})

const handleConfirm = () => {
  createForm.value.pictures = fileList.value
    .filter((item) => item.url)
    .map((item) => item.url) as string[]
  createFormRef.value?.validate(
    assertFormValidate(() =>
      api.feedbackForAdminController.create({ body: createForm.value }).then(async () => {
        ElMessage.success('提交成功')
        visible.value = false
      })
    )
  )
}
const visible = ref(false)
const API_PREFIX = import.meta.env.VITE_API_PREFIX
const fileList = ref<UploadUserFile[]>([])
const handleImageSuccess: UploadProps['onSuccess'] = (
  response: Result<{ url: string }>,
  uploadFile
) => {
  uploadFile.url = response.result.url
}
</script>
<template>
  <el-button
    @click="visible = true"
    type="warning"
    :icon="Edit"
    size="small"
    style="margin-right: 20px"
    >反馈建议</el-button
  >
  <el-dialog v-model="visible">
    <div class="create-form">
      <el-form labelWidth="120" class="form" ref="createFormRef" :model="createForm" :rules="rules">
        <el-form-item label="反馈内容" prop="content">
          <el-input v-model="createForm.content"></el-input>
        </el-form-item>
        <el-form-item label="反馈图片" prop="pictures">
          <el-upload
            v-model:file-list="fileList"
            :action="`${API_PREFIX}/oss/upload`"
            list-type="picture-card"
            :on-success="handleImageSuccess"
          >
            <el-icon><plus></plus></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <footer-button @confirm="handleConfirm"></footer-button>
    </div>
  </el-dialog>
</template>

<style lang="scss" scoped>
.create-form {
  margin-right: 30px;
}
</style>
