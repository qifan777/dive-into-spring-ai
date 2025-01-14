<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '@/utils/api-instance'
import {
  ElMessage,
  ElMessageBox,
  ElTable,
  type FormInstance,
  type FormRules,
  type TableInstance
} from 'element-plus'
import type { Scope } from '@/typings'
import type { AiFactoryDto, AiKeyDto } from '@/apis/__generated/model/dto'
import { Delete, Edit, Plus } from '@element-plus/icons-vue'
import type { AiKeyInput, AiKeySpec, Page, QueryRequest } from '@/apis/__generated/model/static'
import { assertFormValidate } from '@/utils/common'

type AiKey = AiKeyDto['AiKeyRepository/FETCHER']
type AiKeyScope = Scope<AiKey>
const table = ref<TableInstance>()
const pageData = ref<Page<AiKey>>()
const initQuery: AiKeySpec = {}
const queryRequest = ref<QueryRequest<AiKeySpec>>({
  pageNum: 1,
  pageSize: 10,
  query: { ...initQuery }
})
const loadData = async ({
  pageNum = 1,
  pageSize = 10
}: {
  pageNum?: number
  pageSize?: number
}) => {
  pageData.value = await api.aiKeyController.query({
    body: { ...queryRequest.value, pageNum, pageSize }
  })
}
const restQuery = () => {
  queryRequest.value.query = { ...initQuery }
}
onMounted(() => {
  loadData({})
})

const handleSingleDelete = (row: { id: string }) => {
  handleDelete([row.id])
}
const handleBatchDelete = () => {
  handleDelete(
    table.value?.getSelectionRows().map((row: AiKey) => {
      return row.id || ''
    })
  )
}
const handleDelete = (ids: string[]) => {
  ElMessageBox.confirm('此操作将删除数据且无法恢复, 是否继续?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    api.aiKeyController.delete({ body: ids }).then((res) => {})
  })
}
const dialogVisible = ref(false)
const initForm: AiKeyInput = { factoryId: '', name: '', value: '' }
const form = ref<AiKeyInput>({ ...initForm })
const formRef = ref<FormInstance>()

const rules = reactive<FormRules<AiKeyInput>>({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  value: [{ required: true, message: '请输入api key', trigger: 'blur' }],
  factoryId: [{ required: true, message: '请输入AI厂家', trigger: 'blur' }]
})

const handleEdit = (row: AiKey) => {
  dialogVisible.value = true
  form.value = { ...row }
}
const handleCreate = () => {
  dialogVisible.value = true
  form.value = { ...initForm }
}
const handleSubmit = () => {
  formRef.value?.validate(
    assertFormValidate(() =>
      api.aiKeyController.save({ body: form.value }).then(async (res) => {
        form.value.id = res
        dialogVisible.value = false
        await loadData({})
        ElMessage.success('操作成功')
      })
    )
  )
}
type Factory = AiFactoryDto['AiFactoryRepository/FETCHER']
const factories = ref<Factory[]>([])
onMounted(async () => {
  factories.value = (
    await api.aiFactoryController.query({ body: { pageSize: 1000, pageNum: 1, query: {} } })
  ).content
})
</script>
<template>
  <div>
    <div class="search">
      <el-form inline label-width="80" size="small">
        <el-form-item label="名称">
          <el-input v-model.trim="queryRequest.query.name"></el-input>
        </el-form-item>
        <el-form-item label="英文名称" prop="">
          <el-input v-model.trim="queryRequest.query.value"></el-input>
        </el-form-item>
        <el-form-item label=" ">
          <div class="btn-wrapper">
            <el-button type="primary" size="small" @click="loadData"> 查询 </el-button>
            <el-button type="warning" size="small" @click="restQuery"> 重置</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <div class="button-section">
      <el-button type="success" size="small" @click="handleCreate">
        <el-icon>
          <plus />
        </el-icon>
        新增
      </el-button>
      <el-button type="danger" size="small" @click="handleBatchDelete">
        <el-icon>
          <delete />
        </el-icon>
        删除
      </el-button>
    </div>
    <el-dialog v-model="dialogVisible" fullscreen>
      <div>
        <el-form labelWidth="120" class="form" ref="formRef" :model="form" :rules="rules">
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="API Key" prop="value">
            <el-input v-model.trim="form.value"></el-input>
          </el-form-item>
          <el-form-item label="AI厂家" prop="factoryId">
            <el-select v-model="form.factoryId">
              <el-option
                v-for="factory in factories"
                :key="factory.id"
                :label="factory.name"
                :value="factory.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="已使用token" prop="usageToken">
            <el-input-number v-model="form.usageToken"></el-input-number>
          </el-form-item>
          <el-form-item label="总token" prop="totalToken">
            <el-input-number v-model="form.totalToken"></el-input-number>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
    <el-table ref="table" :data="pageData?.content" :border="true" height="500">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="名称" prop="name" show-overflow-tooltip width="120">
        <template v-slot:default="{ row }: AiKeyScope">
          {{ row.name }}
        </template>
      </el-table-column>
      <el-table-column label="API Key" prop="value" show-overflow-tooltip width="120">
        <template v-slot:default="{ row }: AiKeyScope">
          {{ row.value }}
        </template>
      </el-table-column>
      <el-table-column label="厂家" prop="factoryId" show-overflow-tooltip width="120">
        <template v-slot:default="{ row }: AiKeyScope">
          {{ row.factory.name }}
        </template>
      </el-table-column>
      <el-table-column label="使用token" prop="usageToken" show-overflow-tooltip width="120">
        <template v-slot:default="{ row }: AiKeyScope">
          {{ row.usageToken }}
        </template>
      </el-table-column>
      <el-table-column label="全部token" prop="totalToken" show-overflow-tooltip width="120">
        <template v-slot:default="{ row }: AiKeyScope">
          {{ row.totalToken }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template v-slot:default="{ row }">
          <div>
            <el-button class="edit-btn" link size="small" type="primary" @click="handleEdit(row)">
              <el-icon>
                <edit />
              </el-icon>
            </el-button>
            <el-button
              class="delete-btn"
              link
              size="small"
              type="primary"
              @click="handleSingleDelete(row)"
            >
              <el-icon>
                <delete />
              </el-icon>
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div class="page">
      <el-pagination
        style="margin-top: 30px"
        size="small"
        :current-page="queryRequest.pageNum"
        :page-size="queryRequest.pageSize"
        :page-sizes="[10, 20, 30, 40, 50]"
        :total="pageData?.totalElements"
        background
        layout="prev, pager, next, jumper, total, sizes"
        @update:current-page="(pageNum: number) => loadData({ pageNum })"
        @update:page-size="(pageSize: number) => loadData({ pageSize })"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.search {
  display: flex;
  flex-flow: column nowrap;
  width: 100%;
  :deep(.el-form-item) {
    margin-bottom: 5px;
  }

  .btn-wrapper {
    margin-left: 20px;
  }
}

.button-section {
  margin: 20px 0;
}

.page {
  display: flex;
  justify-content: flex-end;
}
</style>
