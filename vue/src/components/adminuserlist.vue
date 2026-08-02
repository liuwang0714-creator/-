<template>
  <el-input v-model="search" placeholder="搜索用户" style="width: 250px; margin-bottom: 15px;" />
  <el-table :data="filterTableData" style="width: 100%">
    <el-table-column label="id" prop="id" width="40" />
    <el-table-column label="用户名" prop="username"  width="95"/>
    <el-table-column label="昵称" prop="nickname" width="90" />
    <el-table-column label="手机号" prop="phone"  width="115"/>
    <el-table-column label="用户类型" prop="role_id"  width="92">
      <template #default="scope">
            <el-tag
            :type="
                scope.row.role_id === '管理员'
                ? 'success'
                : scope.row.role_id === '商家'
                ? 'danger'
                : 'primary'
            "
            >
            {{ scope.row.role_id }}
            </el-tag>
        </template>

    </el-table-column>
    <el-table-column label="限制登录状态" prop="ban_time" width="130">
      <template #default="scope">
            <el-tag
            :type="
                scope.row.ban_time === '未被限制'
                ? 'success'
                : 'danger'
            "
            >
            {{ scope.row.ban_time }}
            </el-tag>
        </template>
    </el-table-column>
    <el-table-column label="状态" width="80">
        <template #default="scope">
            <el-tag
            :type="
                scope.row.state === '正常'
                ? 'success'
                : scope.row.state === '永久封禁'
                ? 'danger'
                : 'info'
            "
            >
            {{ scope.row.state }}
            </el-tag>
        </template>
        </el-table-column>
    
      <el-table-column label="操作" width="215">
        <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
            改密码
            </el-button>
            <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
            >
            改状态
            </el-button>
            <el-button
            size="small"
            type="danger"
            @click="state_time_btn(scope.$index, scope.row)"
            >
            封禁
            </el-button>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="155" />
  </el-table>
  <el-dialog
    v-model="centerDialogVisible"
    title="修改密码"
    width="500"
    align-center
  >
    <div class="item">
                        <label for="">密码</label>
                        <el-input
                            v-model="password"
                            type="password"
                            placeholder="请输入密码"
                            show-password
                            >
                            <template #password-icon="{ visible }">
                                <el-icon :size="16">
                                <Unlock v-if="visible" />
                                <Lock v-else />
                                </el-icon>
                            </template>
                            </el-input>
                    </div>
                    <div class="item">
                        <label for="">密码</label>
                        <el-input
                            v-model="rpassword"
                            type="password"
                            placeholder="请再次输入密码"
                            show-password
                        >
                            <template #password-icon="{ visible }">
                                <el-icon :size="16">
                                    <Unlock v-if="visible" />
                                    <Lock v-else />
                                </el-icon>
                            </template>
                        </el-input>
                    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="edpassword">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog
    v-model="centerstate"
    title="修改账号状态"
    width="500"
    align-center
  >
    <el-select
        v-model="statevalue"
        :options="options"
        :props="props"
        placeholder="选择状态"
        style="width: 240px"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerstate = false">取消</el-button>
        <el-button type="primary" @click="edstate">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog
    v-model="state_time_con"
    title="封禁账号,输入0解封"
    width="500"
    align-center
  >
    <div class="item">
                        <label for="">封禁时间</label>
                        <el-input v-model="state_time" style="width: 240px" placeholder="请输入封禁时间，单位为小时" />
                    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="state_time_con = false">取消</el-button>
        <el-button type="primary" @click="state_time_fun">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import axios from 'axios'
import { computed, ref } from 'vue'
const centerDialogVisible = ref(false)
const centerstate = ref(false)
const state_time_con = ref(false)
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()

function userlist(){
  axios.get('/api/admin/userlist', { params: { pageSize: 10, pageNum:1 } }).then((res) => {
    // 遍历数组，处理每一项数据
    const list = res.data.data.items.map(item => {
      return {
        ...item,
        // 状态 0/1/3 → 中文
        state: item.state === 0 ? '正常' : item.state === 1 ? '永久封禁' :item.state === 2 ? '已注销' : '未激活',
        ban_time: item.ban_time!==0 ? new Date(item.ban_time).toLocaleString() : "未被限制 ",
        role_id: item.role_id === 1 ? '管理员' : item.role_id === 2 ? '商家' : '普通用户',
        // 所有 null 字段 → 暂未设置
        nickname: item.nickname ?? '暂未设置',
        phone: item.phone ?? '暂未设置',
        birthday: item.birthday ?? '暂未设置',
      }
    })

    // 赋值给表格
    tableData.value = list
  })
}

const userid=ref('')
const search = ref('')
const password = ref('')
const rpassword = ref('')
const state_time = ref('')

const statevalue = ref('')

const props = {
  value: 'id',
  label: 'label',
  options: 'options',
  disabled: 'disabled',
}

function state_time_fun(){
  axios.get('/api/admin/state',{
    params:{
      userid: userid.value,
      times: state_time.value
    }
  }).then((res) => {
    if(res.data.code === 0){
      state_time_con.value = false
      success(res.data.data)
      userlist()
    }else{
      error('失败')
    }
  })
}

const options = [
  {
    id: '0',
    label: '正常',
  },
  {
    id: '1',
    label: '永久封禁',
  },
  {
    id: '2',
    label: '已注销',
  },
  {
    id: '3',
    label: '未激活',
  },
]
const success = (msg) => {
  ElMessage({
    message: msg,
    type: 'success',
    plain: true,
  })
}

// 搜索过滤
const filterTableData = computed(() => {
  const key = search.value.toLowerCase().trim()
  if (!key) return tableData.value

  return tableData.value.filter(item => {
    return (
      String(item.id).toLowerCase().includes(key) ||
      String(item.username).toLowerCase().includes(key) ||
      String(item.nickname).toLowerCase().includes(key) ||
      String(item.phone).toLowerCase().includes(key) ||
      String(item.role_id).toLowerCase().includes(key) ||
      String(item.state).toLowerCase().includes(key)
    )
  })
})
function edstate(){
    axios.get('/api/admin/state_user',{
        params:{
            userid: userid.value,
            state: statevalue.value
        }
    }).then((res) => {
        if(res.data.code === 0){
            success('修改成功')
            userlist()
            centerstate.value = false
        }else{
            error('修改失败')
        }
    })
}
const error = (msg) => {
  ElMessage({
    message: msg,
    type: 'error',
    plain: true,
  })
} 
const handleEdit = (index, row) => {
  userid.value=row.id
  password.value=''
  rpassword.value=''
  centerDialogVisible.value = true
  
}
const state_time_btn = (index, row) => {
  userid.value=row.id
  state_time_con.value=''
  
}

const handleDelete = (index, row) => {
  userid.value=row.id
  centerstate.value = true
}

const tableData = ref([])
import { onMounted } from 'vue'

function edpassword(){
    if(password.value !== rpassword.value){
      error('密码不一致')
      return
    }
   axios.put('/api/admin/updatepwd', null, {
    params: {
      userid: userid.value,
      password: password.value,
      rpassword: rpassword.value
    }
  }).then((res) => {
    if(res.data.code === 0){
      success('修改成功')
      centerDialogVisible.value = false
    }else{
      error('修改失败')
    }
  })
}
onMounted(() => {
  userlist()
})
</script>

<style scoped>
.user-table-box {
  width: 100%;
  margin: 0 auto;
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.form-item {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.input-style {
  height: 38px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  outline: none;
  font-size: 14px;
  transition: all 0.3s;
}

.input-style:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.dialog-footer {
  text-align: right;
}

:deep(.el-table) {
  --el-table-row-hover-bg-color: #f8f9fa;
}

:deep(.el-tag) {
  font-weight: 500;
}
</style>

