<template>
  <!-- 筛选按钮栏 -->
  <div class="filter-bar">
    <div
      class="filter-item"
      :class="{ active: activeStatus === 'ev' }"
      @click="orderlist('ev')"
    >
      全部
    </div>
    <div
      class="filter-item"
      :class="{ active: activeStatus === 0 }"
      @click="orderlist(0)"
    >
      待支付
    </div>
    <div
      class="filter-item"
      :class="{ active: activeStatus === 1 }"
      @click="orderlist(1)"
    >
      待发货
    </div>
    <div
      class="filter-item"
      :class="{ active: activeStatus === 2 }"
      @click="orderlist(2)"
    >
      已发货
    </div>
    <div
      class="filter-item"
      :class="{ active: activeStatus === 3 }"
      @click="orderlist(3)"
    >
      待收货
    </div>
    <div
      class="filter-item"
      :class="{ active: activeStatus === 4 }"
      @click="orderlist(4)"
    >
      已收货
    </div>
    <div
      class="filter-item"
      :class="{ active: activeStatus === 5 }"
      @click="orderlist(5)"
    >
      已完成
    </div>
    <div
      class="filter-item"
      :class="{ active: activeStatus === 6 }"
      @click="orderlist(6)"
    >
      已取消
    </div>
  </div>

  <div class="order-list">
    <div class="order-card" v-for="(item, index) in ordertab" :key="index">
      <!-- 左侧：商品封面 -->
      <div class="goods-cover">
        <img :src="`/api/images/${item.goods_file_url}`" alt="商品图片" />
      </div>
      
      <!-- 中间：订单信息 -->
      <div class="order-info">
        <div class="goods-name">{{ item.goods_name }}</div>
        <div class="row">订单号：{{ item.order_id }}</div>
        <div class="row">数量：{{ item.buyNum }} | 总价：¥{{ item.total_price }}</div>
        <div class="row">下单时间：{{ item.create_time }}</div>
        <div class="row">
          订单状态：<span class="tag">{{ getOrderStatus(item.state) }}</span>
          发货方式：<span class="tag">{{ item.fahuo == 1 ? '快递发货' : '线下自提' }}</span>
        </div>
        <div class="row">收货人：{{ item.name || '未填写' }}</div>
        <div class="row">手机号：{{ item.phone || '未填写' }}</div>
        <div class="row">收货地址：{{ item.addr_detail || '未填写' }}</div>
        <div class="row">备注：{{ item.beizhu || '无' }}</div>
      </div>

      <!-- 右侧：商家头像 -->
      <div class="shop-avatar">
        <img :src="`/api/images/${item.file_url}`" alt="商家头像" />
        <div class="nickname">{{ item.nickname }}</div>
      </div>
       <el-button type="success" round size="small" class="contact-btn" @click="contactMerchant(item.merchant_id)">联系商家</el-button>
       <el-button type="primary" round class="shbtn" v-if="item.state === 3" @click="shouhuofun(item.order_id)">确认收货</el-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox  } from 'element-plus'
import { useImStore } from '../store/im'

const imStore = useImStore()

// 订单状态映射
function getOrderStatus(status) {
  switch (String(status).trim()) {
    case '0': case '未支付': return '未支付'
    case '1': case '待发货': return '待发货'
    case '2': case '已发货': return '已发货'
    case '3': case '待收货': return '待收货'
    case '4': case '已收货': return '已收货'
    case '5': case '已完成': return '已完成'
    case '6': case '已取消': return '已取消'
    default: return '未知状态'
  }
}

const ordertab = ref([])
const activeStatus = ref('ev') // 当前选中的筛选状态


function shouhuofun(orderid){
    ElMessageBox.confirm(
        '确认您已收到货了吗?',
        '提示',
        {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        }
    )
        .then(() => {
            axios.put(`/api/order/user/updatestate?order_id=${orderid}`).then((res) =>{
                if(res.data.code === 0){
                    ElMessage.success('收货成功')
                    orderlist(3)
                } else {
                    ElMessage.error('收货失败')
                }
            })
        })
        .catch(() => {
            ElMessage.info('已取消操作')
        })
    
}

// 获取订单列表 + 筛选
function orderlist(status) {
  activeStatus.value = status
  axios.get('/api/order/user/orderlist').then((res) => {
    const data = res.data.data || []
    if (status === 'ev') {
      ordertab.value = data
    } else {
      ordertab.value = data.filter(item => item.state == status)
    }
  })
}

// 联系商家
async function contactMerchant(merchantId) {
  if (!merchantId) return
  try {
    const res = await axios.get('/api/user/infoid', { params: { userid: merchantId } })
    if (res.data.code === 0) {
      imStore.openDrawer(res.data.data.username)
    }
  } catch { ElMessage.error('获取商家信息失败') }
}

onMounted(() => {
  orderlist('ev')
})
</script>

<style scoped>
/* ============== 筛选按钮优化 ============== */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 15px 20px;
  background: #fff;
  border-bottom: 1px solid #f2f2f2;
}

.filter-item {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  background: #f7f8fa;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.filter-item:hover {
  color: #409eff;
  background: #e6f7ff;
}

.filter-item.active {
  color: #fff;
  background: #409eff;
  font-weight: 500;
}

/* ============== 订单列表 ============== */
.order-list {
  padding: 20px;
  box-sizing: border-box;
  
}
.shbtn{
    position: absolute;
    right: 15vh;
    bottom: 40px;
}
.order-card {
    position: relative;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  margin-bottom: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.goods-cover img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}

.order-info {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.goods-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #222;
}

.row {
  line-height: 1.8;
  color: #555;
}

.tag {
  display: inline-block;
  padding: 2px 6px;
  margin: 0 4px;
  background: #f0f7ff;
  color: #409eff;
  border-radius: 4px;
  font-size: 12px;
}

.shop-avatar {
  text-align: center;
  width: 80px;
}

.shop-avatar img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #eee;
}

.nickname {
  margin-top: 4px;
  font-size: 12px;
  color: #666;
}

img {
  background: #f5f5f5;
}
</style>