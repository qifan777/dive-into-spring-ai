<script setup lang="ts">
import { Key } from '@element-plus/icons-vue'
import { useHomeStore } from '@/stores/home-store'
import { storeToRefs } from 'pinia'
import logo from '@/assets/logo.jpg'
import router from '@/router'
const { userInfo } = storeToRefs(useHomeStore())
const handleEdit = () => {
  router.push('/admin/user-details')
}
</script>

<template>
  <div class="admin">
    <el-container>
      <el-header>
        <div class="logo">
          <el-avatar :src="logo" shape="circle"></el-avatar>
        </div>
        <div class="info" @click="handleEdit">
          <el-avatar :src="userInfo?.avatar" shape="square" fit="cover"></el-avatar>
          <div class="nickname">{{ userInfo?.nickname || '默认用户' }}</div>
        </div>
      </el-header>
      <el-container>
        <el-aside
          ><el-menu router default-active="key">
            <el-menu-item index="/admin/key">
              <el-icon>
                <key />
              </el-icon>
              API Key
            </el-menu-item>
            <el-menu-item index="/admin/factory"> AI厂家 </el-menu-item>
            <el-menu-item index="/admin/model"> AI模型 </el-menu-item>
          </el-menu></el-aside
        >
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped lang="scss">
.admin {
  .el-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid var(--el-menu-border-color);
    .info {
      display: flex;
      align-items: center;
      .nickname {
        margin-left: 20px;
      }
    }
  }
  .el-container {
    height: 100vh;
  }
  .el-menu {
    height: 100%;
  }
}
</style>
