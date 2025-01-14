<script setup lang="ts">
import type { UserUpdateInput } from '@/apis/__generated/model/static'
import { onMounted, ref } from 'vue'
import { api } from '@/utils/api-instance'
import ImageUpload from '@/components/image/image-upload.vue'
import { useHomeStore } from '@/stores/home-store'
import { ElMessage } from 'element-plus'

const form = ref<UserUpdateInput>({ nickname: '', avatar: '', gender: 'PRIVATE' })
onMounted(async () => {
  form.value = await api.userController.userInfo()
})
const handleSubmit = async () => {
  await api.userController.save({ body: form.value })
  await useHomeStore().getUserInfo()
  ElMessage.success('操作成功')
}
</script>

<template>
  <div class="user-details">
    <el-form>
      <el-form-item label="昵称">
        <el-input v-model.trim="form.nickname"></el-input>
      </el-form-item>
      <el-form-item label="头像">
        <image-upload v-model="form.avatar"></image-upload>
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="form.gender">
          <el-option label="男" value="MALE"> </el-option>
          <el-option label="女" value="FEMALE"> </el-option>
          <el-option label="保密" value="PRIVATE"> </el-option>
        </el-select>
      </el-form-item>
      <div class="submit">
        <el-button type="primary" style="width: 150px" @click="handleSubmit">提交</el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped lang="scss">
.user-details {
  padding-left: 30px;
  .el-form {
    width: 250px;
  }
  .submit {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
