import { ElTable } from 'element-plus'
import { type Ref, ref } from 'vue'
import type { Page, QueryRequest } from '@/apis/__generated/model/static'
import _ from 'lodash'

export type PageResult<T> = Pick<
  Page<T>,
  'content' | 'number' | 'size' | 'totalElements' | 'totalPages'
>
export type SortChange = {
  prop: string
  order: 'ascending' | 'descending'
}
// 忽略空字符串，undefined，null
export const recursiveOmit = (obj: any) => {
  if (obj instanceof Array) {
    return obj
  }
  const obj2 = {
    ..._.omitBy(obj, (row) => {
      if (_.isString(row)) {
        return _.isEmpty(row)
      } else {
        return _.isNil(row)
      }
    })
  }
  const keys = Object.keys(
    _.pickBy(obj, (row) => {
      return _.isObject(row)
    })
  )
  keys.forEach((key) => {
    obj2[key] = recursiveOmit(obj[key])
  })
  return obj2
}
export const useTableHelper = <T extends Object, E>(
  // 调用后端的查询接口
  queryApi: (options: { readonly body: QueryRequest<T> }) => Promise<PageResult<E>>,
  object: unknown,
  // 查询条件
  initQuery?: T,
  // 分页数据后置处理
  postProcessor?: (data: PageResult<E>) => void
) => {
  const pageData = ref({
    content: <E>[],
    number: 0,
    size: 0,
    totalElements: 0,
    totalPages: 0
  }) as Ref<PageResult<E>>
  const queryRequest = ref({
    query: initQuery ?? {},
    pageNum: 1,
    pageSize: 10,
    likeMode: 'ANYWHERE',
    sorts: [{ property: 'createdTime', direction: 'DESC' }]
  }) as Ref<QueryRequest<T>>
  const loading = ref(false)
  const tableSelectedRows = ref([]) as Ref<E[]>
  // ElTable实例
  const table = ref<InstanceType<typeof ElTable>>()

  // 请求分页数据
  const loadTableData = (request: Partial<QueryRequest<T>>) => {
    queryRequest.value = {
      ...queryRequest.value,
      ..._.omitBy(request, _.isNull)
    }
    queryRequest.value.query = recursiveOmit(queryRequest.value.query) as T
    loading.value = true
    queryApi.apply(object, [{ body: queryRequest.value }]).then(
      (res) => {
        if (postProcessor !== undefined) {
          postProcessor(res)
        }
        pageData.value = res
        loading.value = false
      },
      (res) => {
        console.log(res)
      }
    )
  }
  // 重新请求分页数据，pageNum=1, pageSize=10
  const reloadTableData = (
    queryRequest: Partial<QueryRequest<T>> = { pageNum: 1, pageSize: 10 }
  ) => {
    loadTableData(queryRequest)
  }
  // 获取表格选中的数据
  const getTableSelectedRows = () => {
    return tableSelectedRows.value
  }
  // 当表格选择变动时更新选中的数据
  const handleSelectChange = (selectedRows: E[]) => {
    tableSelectedRows.value = selectedRows
  }
  const handleSortChange = ({ prop, order }: SortChange) => {
    const directionMap: { ascending: 'ASC'; descending: 'DESC' } = {
      ascending: 'ASC',
      descending: 'DESC'
    }
    const sorts = queryRequest.value.sorts || []
    sorts.splice(sorts.length - 1, 1, { direction: directionMap[order], property: prop })
    reloadTableData()
  }

  return {
    table,
    loading,
    queryRequest,
    tableSelectedRows,
    pageData,
    loadTableData,
    reloadTableData,
    getTableSelectedRows,
    handleSortChange,
    handleSelectChange
  }
}
