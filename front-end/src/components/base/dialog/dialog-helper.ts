import { nextTick, ref } from 'vue'
import type { EditMode } from '@/typings'

export const useDialogHelper = () => {
  const dialogData = ref<{ width: number; title: string; visible: boolean; mode: EditMode }>({
    width: 600,
    title: '',
    visible: false,
    mode: 'CREATE'
  })
  const closeDialog = () => {
    dialogData.value.visible = false
  }
  const openDialog = async (mode?: EditMode) => {
    if (mode !== undefined) {
      dialogData.value.mode = mode
    }
    await nextTick()
    dialogData.value.visible = true
  }
  return { dialogData, closeDialog, openDialog }
}
