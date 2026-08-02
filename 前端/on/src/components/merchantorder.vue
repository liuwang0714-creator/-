<template>
<h1>订单管理</h1>
<el-input v-model="search" placeholder="搜索商品名称或订单号" style="width: 250px; margin-bottom: 15px;" /><br>
    <el-select v-model="saixuanstate" placeholder="选择订单状态" style="width: 240px">
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        />
    </el-select>
    <el-select v-model="saixuanfahuo" placeholder="选择发货方式" style="width: 240px">
        <el-option
            v-for="item in [{value: '全部', label: '全部'}, {value: 1, label: '快递'}, {value: 2, label: '自提'}]"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        />
    </el-select>
    <el-button type="primary" @click="saixuan(saixuanfahuo,saixuanstate)">查询</el-button>
    <el-button type="danger" @click="funorderlist">重置</el-button>
    
    <!-- 表格绑定 筛选后的计算属性 -->
    <el-table :data="filterTableData" style="width: 100%">
        <el-table-column label="id" prop="good_id"  width="70"/>
    <el-table-column label="订单编号" prop="order_id" width="180" />
    <el-table-column label="数量" prop="buyNum"  width="60"/>
    <el-table-column label="发货方式" width="70">
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
    <el-table-column label="买家昵称" prop="nickname" width="110" />
    <el-table-column label="买家头像" width="110">
        <template #default="scope">
            <button @click="txstate = true; touxurl = scope.row.file_url">点击查看</button>
        </template>
    </el-table-column>
    <el-table-column label="购买时间" prop="create_time" width="160" />
    <el-table-column label="操作">
      <template #default="scope">
        <el-button size="small" type="success" @click="contactUser(scope.row.user_id)">联系顾客</el-button>
        <el-button size="small" type="primary" v-if="scope.row.state == 1" @click="fahuo(scope.row.order_id, scope.row.fahuo)">
          去发货
        </el-button>
      </template>
    </el-table-column>
    </el-table>

    <el-dialog
        v-model="txstate"
        title="用户头像"
        width="500"
        align-center
    >
    <img :src="`/api/images/${touxurl}`" style="width: 100%; height: 100%;">
    </el-dialog>

    <el-dialog
        v-model="fahuotanc"
        title="请输入快递单号"
        width="500"
        align-center
    >
    <el-input v-model="express" placeholder="请输入快递单号"></el-input>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="fahuotanc=false">取消</el-button>
                <el-button type="primary" @click="fahuoshiji(fahuoorder_id, express)">
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
    import { ElMessage, ElMessageBox  } from 'element-plus'
import { useImStore } from '../store/im'

const imStore = useImStore()
    
    const touxurl=ref()
    const txstate=ref(false)
    const fahuotanc=ref(false)

    const orderlist = ref([])
    const fahuoorder_id=ref()
    const express=ref()
    const saixuanstate=ref('全部')
    const saixuanfahuo=ref('全部')
    const options = [
        { value: '全部', label: '全部' },
        { value: 0, label: '待付款' },
        { value: 1, label: '待发货' },
        { value: 2, label: '已发货' },
        { value: 3, label: '待收货' },
        { value: 4, label: '已收货' },
        { value: 5, label: '已完成' },
        { value: 6, label: '取消订单' },
    ]
    const search = ref('')

    const filterTableData = computed(() => {
        const key = search.value.trim().toLowerCase()
        if (!key) return orderlist.value

        return orderlist.value.filter(item => {
            
            const goodsName = item.goods_name || item.name || ''
            return (
                goodsName.toLowerCase().includes(key) ||
                String(item.order_id).toLowerCase().includes(key) ||
                String(item.nickname).toLowerCase().includes(key)
            )
        })
    })

    // 获取订单列表
    function funorderlist(){
        axios.get('/api/order/merchant/orderlist').then((res) =>{
            if(res.data.code === 0){
                orderlist.value = res.data.data
            }
        })
    }
    
    // 发货提交
    function fahuoshiji(order_id, express){
        axios.put('/api/order/merchant/updatestate', null,
        {  params:{ order_id: order_id , express: express } }
        ).then((res) =>{
            if(res.data.code === 0){
                ElMessage.success('发货成功')
                funorderlist()
                fahuotanc.value = false
                express.value = ''
            } else {
                ElMessage.error('发货失败')
            }
        })
    }

    // 筛选
    function saixuan(fahuo,state){ 
        const originList = [...orderlist.value]
        orderlist.value=[]
        for(let i = 0; i < originList.length; i++){
            if((originList[i].state === state || state==='全部')
            && (originList[i].fahuo === fahuo || fahuo==='全部')){
                orderlist.value.push(originList[i])
            }
        }
    }

    // 发货操作
    function fahuo(order_id, fahuo){
        if(fahuo === 1){
            fahuotanc.value = true
            fahuoorder_id.value = order_id
        }else{
            ElMessageBox.confirm(
                '确认对方已经自取了吗?',
                '提示',
                {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                type: 'warning',
                }
            )
                .then(() => {
                    fahuoshiji(order_id, '')
                })
                .catch(() => {
                    ElMessage.info('已取消操作')
                })
        }
    }

    // 订单状态文字
    function getOrderStatus(status) {
    switch (String(status).trim()) {
        case '0': return '未支付';
        case '1': return '待发货';
        case '2': return '已发货';
        case '3': return '待收货';
        case '4': return '已收货';
        case '5': return '已完成';
        case '6': return '已取消';
        default: return '未知状态';
    }
    }

    // 订单标签颜色
    function getOrderTagType(status) {
    switch (String(status).trim()) {
        case '0': return 'info';    
        case '1': return 'warning';  
        case '2': case '3': return 'primary'; 
        case '4': case '5': return 'success'; 
        case '6': return 'danger';  
        default: return '';
    }
    }

    // 联系顾客
    async function contactUser(userId) {
      if (!userId) return
      try {
        const res = await axios.get('/api/user/infoid', { params: { userid: userId } })
        if (res.data.code === 0) {
          imStore.openDrawer(res.data.data.username)
        }
      } catch { ElMessage.error('获取用户信息失败') }
    }

    onMounted(() => {
      funorderlist()
    })

</script>

<style scoped>
/* 页面背景色生效 */
:root {
  background-color: rgb(255, 128, 0);
  margin: 0;
  padding: 15px;
}
</style>