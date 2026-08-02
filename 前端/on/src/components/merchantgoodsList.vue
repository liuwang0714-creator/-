<template>
  <el-button type="primary" @click="addgoodsbt = true">+添加商品</el-button>

  <el-table :data="filterTableData" style="width: 100%">
    <el-table-column label="id" prop="id" width="50" />
    <el-table-column label="商品名称" prop="goods_name" width="110"/>
    <el-table-column label="price" prop="price" width="80" />
    <el-table-column label="库存" prop="stock" width="60"/>
    <el-table-column label="商品描述" width="220">
      <template #default="scope">
        <span 
          :title="scope.row.description" 
          class="desc-text"
        >
          {{ scope.row.description.length > 15 
            ? scope.row.description.slice(0,15) + '...' 
            : scope.row.description 
          }}
        </span>
      </template>
    </el-table-column>
    <el-table-column label="封面图片" width="90">
      <template #default="scope">
        <button @click="seeImage(scope.row.file_url)">查看</button>
      </template>
    </el-table-column>
    <el-table-column label="发布时间" prop="create_time" width="160" />
    <el-table-column label="操作">
      <template #default="scope">
        <el-button size="small" @click="handleEdit(scope.row, scope.$index)">
          编辑
        </el-button>
        <el-button
          size="small"
          type="danger"
          @click="handleDelete(scope.row)"
        >
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 编辑商品弹出框 -->
  <el-dialog
    v-model="goods_ed"
    title="编辑商品"
    width="500"
    align-center
  >
    商品名称: <input type="text" v-model="editForm.goods_name" placeholder="请输入商品名称"/><br>
    价格: <input type="text" v-model="editForm.price" placeholder="请输入价格"/><br>
    库存: <input type="text" v-model="editForm.stock" placeholder="请输入库存"/><br>
    描述: <input type="text" v-model="editForm.description" placeholder="请输入商品描述"/><br>
    商品封面：<el-upload
      class="avatar-uploader"
      action="/api/file/upload"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :before-upload="beforeAvatarUpload"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
    </el-upload>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="goods_ed = false">取消</el-button>
        <el-button type="primary" @click="btn_edgoods" :disabled="submitLoading">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 添加商品 -->
  <el-dialog
    v-model="addgoodsbt"
    title="添加商品"
    width="500"
    align-center
  >
    商品名称: <input type="text" v-model="addgoodsform.goods_name" placeholder="请输入商品名称"/><br>
    价格: <input type="text" v-model="addgoodsform.price" placeholder="请输入价格"/><br>
    库存: <input type="text" v-model="addgoodsform.stock" placeholder="请输入库存"/><br>
    描述: <input type="text" v-model="addgoodsform.description" placeholder="请输入商品描述"/><br>
    商品封面：<el-upload
      class="avatar-uploader"
      action="/api/file/upload"
      :show-file-list="false"
      :on-success="addgoodsSuccess"
      :before-upload="addgoodsUpload"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
    </el-upload>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="addgoodsbt = false">取消</el-button>
        <el-button type="primary" @click="openAddGoods" :disabled="addLoading">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 查看图片 -->
  <el-dialog
    v-model="images_see"
    title="查看图片"
    width="500"
    align-center
  >
    <img :src="'/api/images/' + images_url" alt="" class="image_tan">

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="images_see = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import axios from 'axios'
import { computed, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const submitLoading = ref(false)
const addLoading = ref(false)    

const addgoodsbt = ref(false)
const search = ref('')
const goods_ed = ref(false)
const tableData = ref([])
const images_url = ref('')
const images_see = ref(false)
const imageUrl = ref('')
const imageid = ref(0) // 你漏定义了，我补上

const editForm = ref({
  id: '',
  goods_name: '',
  price: 0,
  stock: 0,
  description: '',
  goods_file_id: '',
})

const addgoodsform = ref({
  id: '',
  goods_name: '',
  price: '',
  stock: '',
  description: '',
  goods_file_id: '',
})

// ======================================
// ✅ 修复：添加商品提交（带禁用）
// ======================================
const openAddGoods = async () => {
  if (addLoading.value) return
  addLoading.value = true // 点击立刻禁用

  try {
    const addgoodsData = {
      ...addgoodsform.value,
      price: Number(addgoodsform.value.price) || 0,
      stock: Number(addgoodsform.value.stock) || 0,
      goods_file_id: Number(addgoodsform.value.goods_file_id)
    }

    await axios.post('/api/goods/add', addgoodsData)
    ElMessage.success('添加成功')
    addgoodsbt.value = false
    goodslist()
  } finally {
    addLoading.value = false // 无论成功失败都恢复
    addgoodsform.value = {
      goods_name: '',
      price: '',
      stock: '',
      description: '',
      goods_file_id: '',
    }
    imageUrl.value = ''
    imageid.value = 0
  }
}

// ======================================
// ✅ 修复：编辑提交（带禁用）
// ======================================
const btn_edgoods = async () => {
  if (submitLoading.value) return
  submitLoading.value = true // 禁用

  try {
    const submitData = {
      ...editForm.value,
      price: Number(editForm.value.price) || 0,
      stock: Number(editForm.value.stock) || 0,
      goods_file_id: Number(editForm.value.goods_file_id)
    }
    await axios.put('/api/goods/update', submitData)
    ElMessage.success('修改成功')
    goods_ed.value = false
    goodslist()
  } finally {
    submitLoading.value = false // 恢复
  }
}

// 查看图片
function seeImage(image_url) {
  images_url.value = image_url
  images_see.value = true
}

// 获取商品列表
function goodslist() {
  axios.get('/api/goods/list', { params: { pageSize: 10, pageNum: 1 } }).then((res) => {
    tableData.value = res.data.data.items
  })
}

// 搜索过滤
const filterTableData = computed(() =>
  tableData.value.filter((data) =>
    !search.value || data.goods_name.toLowerCase().includes(search.value.toLowerCase())
  )
)

// 编辑
const handleEdit = (row, index) => {
  imageUrl.value = row.file_url ? '/api/images/' + row.file_url : ''
  editForm.value = { ...row }
  editForm.value.goods_file_id = tableData.value[index]["goods_file_id"]
  goods_ed.value = true
}

// 删除
const handleDelete = (row) => {
  axios.delete('/api/goods/delete', { params: { id: row.id } }).then(() => {
    ElMessage.success('删除成功')
    goodslist()
  })
}

// 上传图片
const handleAvatarSuccess = (response, uploadFile) => {
  imageid.value = response.data
  imageUrl.value = URL.createObjectURL(uploadFile.raw)
}

const beforeAvatarUpload = (rawFile) => {
  const fileName = rawFile.name
  if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png")) {
    ElMessage.error('只能上传 JPG / PNG 格式！')
    return false
  } else if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('图片不能超过 10MB！')
    return false
  }
  return true
}

const addgoodsSuccess = (response, uploadFile) => {
  addgoodsform.value.goods_file_id = response.data
  imageUrl.value = URL.createObjectURL(uploadFile.raw)
}

const addgoodsUpload = (rawFile) => {
  const fileName = rawFile.name
  if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png")) {
    ElMessage.error('只能上传 JPG / PNG 格式！')
    return false
  } else if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('图片不能超过 10MB！')
    return false
  }
  return true
}

onMounted(() => {
  goodslist()
})
</script>

<style scoped>
/* 表格整体样式 */
:deep(.el-table) {
  margin: 20px 0;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 表格内容居中 */
:deep(.el-table td),
:deep(.el-table th) {
  text-align: center;
}

/* 所有弹窗通用样式 */
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__body) {
  padding: 25px 30px;
}

/* 输入框 */
input {
  width: 100%;
  height: 38px;
  margin: 10px 0 20px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  outline: none;
  transition: all 0.3s;
  box-sizing: border-box;
  font-size: 14px;
}

/* 弹窗底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 10px 0;
}

:deep(.el-button) {
  border-radius: 6px;
  padding: 8px 20px;
}

/* 上传组件 */
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

/* 图片查看弹窗 */
.image_tan {
  display: block;
  max-width: 100%;
  max-height: 450px;
  margin: 0 auto;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog:has(.image_tan) .el-dialog__body) {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f5f7fa;
  min-height: 300px;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}
</style>