<template>
  <el-input v-model="search" placeholder="搜索商品名称" style="width: 250px; margin-bottom: 15px;" />
  <el-table :data="filterTableData" style="width: 100%">
    <el-table-column label="id" prop="id" width="50" />
    <el-table-column label="商品名称" prop="goods_name"  width="130"/>
    <el-table-column label="单价" prop="price" width="70" />
    <el-table-column label="库存" prop="stock"  width="70"/>
    <el-table-column label="商品描述" width="220">
      <template #default="scope">
        <!-- 限制显示20个字符，多余显示... -->
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
    <el-table-column label="发布人" prop="create_id" width="70" />
    <el-table-column label="商品封面" width="80">
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
        <el-button type="primary" @click="btn_edgoods">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
    v-model="images_see"
    title="查看图片"
    width="500"
    align-center
  >
    <img :src="'/api/images/' + images_url" alt="" class="image_tan"></img>

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

const search = ref('')
const goods_ed = ref(false)
const tableData = ref([])
const images_url = ref('')
const images_see=ref(false)
// 编辑表单对象（核心修复）


function seeImage(image_url){
  console.log(image_url)
  images_url.value = image_url
  images_see.value = true
}


const editForm = ref({
  id: '',
  goods_name: '',
  price: 0,
  stock: 0,
  description: '',
  goods_file_id: '',
})

// 获取商品列表
function goodslist(){
  axios.get('/api/goods/goodslist', 
    ).then((res) => {
    tableData.value = res.data.data
  })
}

// 搜索过滤
const filterTableData = computed(() => {
  const key = search.value.toLowerCase().trim()
  if (!key) return tableData.value

  return tableData.value.filter(item => {
    return (
      String(item.id).toLowerCase().includes(key) ||
      item.goods_name.toLowerCase().includes(key) ||
      item.description.toLowerCase().includes(key) ||
      String(item.price).toLowerCase().includes(key)
    )
  })
})

// 编辑按钮
const handleEdit = (row,index) => {
  editForm.value = { ...row }
  imageUrl.value = row.file_url ? '/api/images/' + row.file_url : ''
  editForm.value.goods_file_id=tableData.value[index]["goods_file_id"]
  goods_ed.value = true
}

// 删除
const handleDelete = (row) => {
  axios.delete('/api/goods/delete', { params: { id: row.id } }).then((res) => {
    ElMessage.success('删除成功')
    goodslist()
  })
}

// 提交编辑
function btn_edgoods(){
  const submitData = {
    ...editForm.value,
    price: Number(editForm.value.price) || 0,
    stock: Number(editForm.value.stock) || 0,
    goods_file_id: Number(editForm.value.id) || 0

  }
  axios.put('/api/goods/update', submitData).then(res => {
    ElMessage.success('修改成功')
    goods_ed.value = false
    goodslist() 
  })
}
  // 商品封面图片上传
  const imageUrl = ref('')

  const handleAvatarSuccess = (response, uploadFile) => {
    editForm.value.goods_file_id = response.data
    imageUrl.value = URL.createObjectURL(uploadFile.raw)
  }

  const beforeAvatarUpload = (rawFile) => {
    const fileName = rawFile.name
    if(!fileName.endsWith(".jpg") && !fileName.endsWith(".png")){
      ElMessage.error('Avatar picture must be JPG or PNG format!')
      return false
    } else if (rawFile.size / 1024 / 1024 > 10) {
      ElMessage.error('Avatar picture size can not exceed 10MB!')
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
  background-color: #ff8000;
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

/* ======================
🔥 图片查看弹窗 终极优化
======================= */
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