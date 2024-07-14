import { defineStore } from 'pinia'
import { useTableHelper } from '@/components/base/table/table-helper'
import { useDialogHelper } from '@/components/base/dialog/dialog-helper'
import { useQueryHelper } from '@/components/base/query/query-helper'
import type {
  FeedbackCreateInput,
  FeedbackSpec,
  FeedbackUpdateInput
} from '@/apis/__generated/model/static'
import { api } from '@/utils/api-instance'
import { ref } from 'vue'

export const feedbackQueryOptions = async (keyword: string, id: string) => {
  return (await api.feedbackForAdminController.query({ body: { query: { name: keyword, id } } }))
    .content
}
export const useFeedbackStore = defineStore('feedback', () => {
  const initQuery: FeedbackSpec = {}
  const initForm: FeedbackCreateInput & FeedbackUpdateInput = {}
  const tableHelper = useTableHelper(
    api.feedbackForAdminController.query,
    api.feedbackForAdminController,
    initQuery
  )
  const dialogHelper = useDialogHelper()
  const queryHelper = useQueryHelper<FeedbackSpec>(initQuery)
  const updateForm = ref<FeedbackUpdateInput>({ ...initForm })
  const createForm = ref<FeedbackCreateInput>({ ...initForm })
  return { ...tableHelper, ...dialogHelper, ...queryHelper, updateForm, createForm, initForm }
})
