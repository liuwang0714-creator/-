<template>
  <el-input v-model="search" placeholder="搜索商品名称" style="width: 250px; margin-bottom: 15px;" />
  <el-table :data="filterTableData" style="width: 100%">
    <el-table-column label="商品id" prop="good_id"  width="80"/>
    <el-table-column label="订单编号" prop="order_id" width="180" />
    <el-table-column label="购买数量" prop="buyNum"  width="90"/>
    <el-table-column label="发货方式" width="100">
      <template #default="scope">
        <el-tag
          :type="scope.row.fahuo == 1 ? 'primary' : 'warning'"
        >{{ scope.row.fahuo == 1 ? '快递' : '自提' }}</el-tag>
        
      </template>
    </el-table-column>
    <el-table-column label="订单状态"  width="80">
      <template #default="scope">
        <el-tag
          :type="getOrderTagType(scope.row.state)"
        >
          {{ getOrderStatus(scope.row.state) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="买方id" prop="user_id" width="80" />
    <el-table-column label="卖方id" prop="merchant_id" width="80" />
    <el-table-column label="创建时间" prop="create_time" width="160" />
    <el-table-column align="right">
    <el-table-column label="操作">
      <template #default="scope">
        <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
          修改状态
        </el-button>
      </template>
    </el-table-column>
    </el-table-column>
  </el-table>

  <el-dialog
    v-model="centerstate"
    title="修改订单状态"
    width="500"
    align-center
  >
  收货方式:<el-select v-model="updateorderstaterfahuo" placeholder="选择收货方式" style="width: 240px">
      <el-option
        v-for="item in [{
            value: '1',
            label: '快递发货',},
            {
            value: '2',
            label: '自提',
          },]"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select><br/>
    订单状态:<el-select v-model="orderstate" placeholder="选择订单状态" style="width: 240px">
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
  </el-select>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerstate=false">取消</el-button>
        <el-button type="primary" @click="centerDialogVisible">
          提交
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import axios from 'axios'
import { useRouter } from 'vue-router'
import { computed, ref,onMounted } from 'vue'
import { ElMessage } from 'element-plus'
const router=useRouter()
const centerstate=ref(false)

const updateorderstaterderid=ref()
const updateorderstaterfahuo=ref()

const options = [
  {
    value: '0',
    label: '待付款',
  },
  {
    value: '1',
    label: '待发货',
  },
  {
    value: '2',
    label: '已发货',
  },
  {
    value: '3',
    label: '待收货',
  },
  {
    value: '4',
    label: '已收货',
  },
  {
    value: '5',
    label: '已完成',
  },
  {
    value: '6',
    label: '取消订单',
  },
 
]

function getorderlist(){
  axios.get('/api/admin/orderlist', { params: { pageSize: 10, pageNum:1 } }).then((res) => {

    // 赋值给表格
    tableData.value = res.data.data.items
  })
}

function getOrderStatus(status) {
  switch (String(status).trim()) {
	case '0': case '未支付': return '未支付';
	case '1': case '待发货': return '待发货';
	case '2': case '已发货': return '已发货';
	case '3': case '待收货': return '待收货';
	case '4': case '已收货': return '已收货';
	case '5': case '已完成': return '已完成';
  case '6': case '已取消': return '已取消';
	default: return '未知状态';
  }
}

function getOrderTagType(status) {
  switch (String(status).trim()) {
    case '0': case '未支付': return 'info';    
    case '1': case '待发货': return 'warning';  
    case '2': case '已发货': return 'primary'; 
    case '3': case '待收货': return 'primary';  
    case '4': case '已收货': return 'success'; 
    case '5': case '已完成': return 'success';   
    case '6': case '已取消': return 'danger';  
    default: return '';
  }
}

const orderstate = ref()

const search = ref('')
const filterTableData = computed(() => {
  const key = search.value.trim()
  if (!key) return tableData.value

  return tableData.value.filter(item => {
    return (
      String(item.good_id).includes(key) || 
      String(item.order_id).includes(key) ||   
      String(item.user_id).includes(key) ||   
      String(item.merchant_id).includes(key)  
    )
  })
})
const handleEdit = (index, row) => {
  centerstate.value = true
  updateorderstaterderid.value=row.order_id
  updateorderstaterfahuo.value=String(row.fahuo)
  orderstate.value=String(row.state)
}

function centerDialogVisible() { 
  axios.get('/api/admin/updateorderstate',{
    params: {
      orderid: updateorderstaterderid.value,
      state: orderstate.value,
      fahuo: updateorderstaterfahuo.value
    }
  }).then(res => {
    if (res.data.code === 0) {
      ElMessage.success('修改成功')
      centerstate.value = false
      getorderlist()
    }
    else {
      ElMessage.error('修改失败')
    }
})
}

const tableData = ref([])


onMounted(() => {
  getorderlist()
})
</script>

<style scoped>

</style>

