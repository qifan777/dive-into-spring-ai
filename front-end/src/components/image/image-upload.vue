<script lang="tsx">
import { defineComponent, ref } from 'vue'
import { ElIcon, ElMessage, ElUpload, type UploadProps } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { Result } from '@/typings'
const API_PREFIX = import.meta.env.VITE_API_PREFIX

export default defineComponent({
  props: {
    modelValue: { type: String, default: '' },
    size: { type: Number, default: 120 }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const loading = ref(false)
    const handleImageSuccess: UploadProps['onSuccess'] = (response: Result<{ url: string }>) => {
      loading.value = false
      emit('update:modelValue', response.result.url)
    }
    const types = ['image/png', 'image/jpeg', 'image/webp']

    const beforeImageUpload: UploadProps['beforeUpload'] = (rawFile) => {
      if (!types.includes(rawFile.type)) {
        ElMessage.error('Image picture must be JPG format!')
        return false
      } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('Image picture size can not exceed 2MB!')
        return false
      }
      loading.value = true
      return true
    }
    return () => (
      <ElUpload
        beforeUpload={beforeImageUpload}
        onSuccess={handleImageSuccess}
        action={API_PREFIX + '/oss/upload'}
        showFileList={false}
        class="image-uploader"
        v-loading={loading.value}
      >
        {props.modelValue ? (
          <img
            alt="图片"
            src={props.modelValue}
            style={{
              objectFit: 'cover',
              width: `${props.size}px`,
              height: `${props.size}px`,
              display: 'block'
            }}
          />
        ) : (
          <ElIcon size={props.size} class="image-uploader-icon">
            <Plus />
          </ElIcon>
        )}
      </ElUpload>
    )
  }
})
</script>

<style scoped>
.image-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.image-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  text-align: center;
}
</style>
