<script lang="ts" setup>
import {
  ElAvatar,
  ElButton,
  ElCard,
  ElCol,
  ElForm,
  ElFormItem,
  ElInput,
  ElRow,
  type FormInstance,
  type FormRules
} from 'element-plus'
import { reactive, ref } from 'vue'
import logo from '@/assets/logo.jpg'
import router from '@/router'
import background from '@/assets/background.jpg'
import { api } from '@/utils/api-instance'
import type { UserRegisterInput } from '@/apis/__generated/model/static'
import { assertFormValidate } from '@/utils/common'

const registerForm = reactive<UserRegisterInput>({ phone: '', password: '' })
const ruleFormRef = ref<FormInstance>()

const rules = reactive<FormRules<typeof registerForm>>({
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { max: 16, min: 6, message: '密码长度介于6，16' }
  ]
})
const handleRegister = async () => {
  if (!ruleFormRef.value) return
  await ruleFormRef.value.validate(
    assertFormValidate(() =>
      api.userController.register({ body: registerForm }).then((res) => {
        router.replace({ path: '/' })
        localStorage.setItem('token', res.tokenValue)
      })
    )
  )
}
</script>
<template>
  <div>
    <img alt="背景图片" class="background" :src="background" />
    <el-row class="panel-wrapper" justify="center" align="middle">
      <el-col :xs="18" :sm="16" :md="14" :lg="10" :xl="10">
        <transition name="el-zoom-in-top">
          <el-card class="panel">
            <div class="content">
              <div class="panel-left">
                <el-avatar alt="logo" :size="30" shape="square" :src="logo"></el-avatar>
                <div class="title">AI助手</div>
                <div class="description">构建你的AI助手</div>
              </div>
              <div class="panel-right">
                <div class="title">快速开始</div>
                <div class="description">创建你的账号</div>
                <el-form
                  ref="ruleFormRef"
                  :model="registerForm"
                  :rules="rules"
                  class="form"
                  label-position="top"
                  label-width="100px"
                >
                  <el-form-item label="手机号" prop="phone">
                    <el-input v-model="registerForm.phone"></el-input>
                  </el-form-item>
                  <el-form-item label="密码" prop="password">
                    <el-input v-model="registerForm.password" type="password"></el-input>
                  </el-form-item>
                </el-form>
                <div class="button-wrapper">
                  <el-button class="register" type="primary" @click="handleRegister">
                    注册
                  </el-button>
                  <el-button class="login" size="small" link @click="router.replace('/login')">
                    登录
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </transition>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.background {
  position: fixed;
  height: 100vh;
  width: 100vw;
  object-fit: cover;
  z-index: -10;
}

.panel-wrapper {
  height: 100vh;

  .panel {
    .content {
      display: flex;
      align-items: stretch;
      height: 50vh;

      .title {
        font-size: var(--el-font-size-extra-large);
        margin-top: 16px;
        font-weight: bold;
      }

      .description {
        margin-top: 20px;
        font-size: var(--el-font-size-base);
        color: var(--el-text-col);
      }

      .panel-left {
        box-sizing: border-box;
        padding: 30px;
        background-color: rgb(243, 245, 249);
        width: 50%;
        border-radius: 5px;
      }

      .panel-right {
        padding: 30px;
        width: 50%;

        .form {
          margin-top: 30px;

          .sms {
            display: flex;
            align-items: center;
            width: 100%;

            .send-sms {
              margin-left: 20px;
            }
          }
        }

        .button-wrapper {
          margin-top: 40px;
          display: flex;
          justify-content: center;
          position: relative;

          .register {
            width: 120px;
          }

          .login {
            position: absolute;
            right: 0;
            bottom: 0;
          }
        }
      }
    }
  }
}
</style>
