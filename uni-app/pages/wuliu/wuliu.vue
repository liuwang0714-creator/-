<template>
  <view class="container">
    <!-- 筛选按钮 -->
    <view class="filter-bar">
      <view class="filter-btn" @click="orderlist(true)">全部</view>
      <view class="filter-btn" @click="orderlist(2)">未到货</view>
      <view class="filter-btn" @click="orderlist(3)">已到货</view>
    </view>

    <!-- 物流列表 -->
    <view class="wuliu-list" v-if="wuliutab && wuliutab.length">
      <view class="wuliu-item" v-for="(item, index) in wuliutab" :key="index">
        <view class="wuliu-info">物流状态: <text>{{ item.state == 2 ? "未到货" : "已到货" }}</text></view>
        <view class="wuliu-info">物流ID: <text>{{ item.express_no }}</text></view>
        <view class="wuliu-info">订单号: <text>{{ item.order_id }}</text></view>
        <button class="daohuo-btn" @click="daohuo(item.order_id)" v-if="item.state==2">到货了</button>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-else>暂无物流信息</view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
const wuliutab=ref()

function daohuo(order_id){
	uni.request({
		url:`http://127.0.0.1:8081/wuliu/updatestate?order_id=${order_id}`,
		method:"GET",
		header: {
			"satoken": uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			if(res.data.code==0){
				uni.showToast({
					title:res.data.data,
					icon:'success'
				})
				orderlist(2)
			}else{
				uni.showToast({
					title:"发生错误",
					icon:'success'
				})
			}
		}
	})
}

function orderlist(states){
	uni.request({
		url:"http://127.0.0.1:8081/wuliu/list",
		method:"GET",
		header: {
			"satoken": uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			if(states===true){
			  wuliutab.value=[]
			  wuliutab.value=res.data.data
			}else{
			  wuliutab.value=[]
			  for(let item of res.data.data){
				  if(item.state==states){
					wuliutab.value.push(item)
				  }
			  }
			}
		},
		
	})
}

onShow(()=>{
	orderlist(true)
})
</script>

<style>
/* 页面整体容器 */
.container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
  box-sizing: border-box;
  font-family: "Helvetica", "Arial", sans-serif;
}

/* 筛选按钮区域 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.filter-btn {
  flex: 1;
  margin: 0 5px;
  padding: 3px 0;
  text-align: center;
  background-color: #fff;
  border-radius: 20px;
  color: #333;
  font-size: 16px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

/* 物流列表 */
.wuliu-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.wuliu-item {
  background-color: #fff;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  width: 80%;
}

.wuliu-info {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

/* 到货按钮 */
.daohuo-btn {
  margin-top: 10px;
  background-color: #aaffff;
  color: #00007f;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
}

/* 空状态提示 */
.empty {
  text-align: center;
  color: #888;
  margin-top: 50px;
  font-size: 16px;
}
</style>