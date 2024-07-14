import { type Ref, ref } from 'vue'
import type { LikeMode } from '@/apis/__generated/model/enums'

export const useQueryHelper = <T>(initQuery: T) => {
  const queryData = ref({ query: { ...initQuery }, likeMode: 'ANYWHERE' }) as Ref<{
    query: T
    likeMode: LikeMode
  }>
  const restQuery = () => {
    console.log('重置查询条件')
    queryData.value.query = { ...initQuery }
    queryData.value.likeMode = 'ANYWHERE'
  }
  return { queryData, restQuery }
}
