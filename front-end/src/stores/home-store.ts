import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/utils/api-instance'
import type {User} from "@/apis/__generated/model/static";

export const useHomeStore = defineStore('home', () => {
  const userInfo = ref<User>()
  const getUserInfo = async () => {
    userInfo.value = await api.userController.userInfo()
    return userInfo.value
  }
  const init = async () => {
    await getUserInfo()
  }
  const logout = () => {
    userInfo.value = undefined
  }
  return { userInfo, getUserInfo, init, logout }
})
