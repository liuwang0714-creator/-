<template>
  <view class="wrap">
    <view class="title">收货地址列表</view>
    <!-- 地址项循环 -->
    <view class="addr-item" v-for="item in tab_addr" :key="item.id">
		<uni-icons type="trash-filled" size="30" color="red" class="delete" @click="deleteaddr(item.id)"></uni-icons>
      <view class="addr-row">
        <text class="label">收货人：</text>
        <text class="text">{{ item.name }}</text>
      </view>
      <view class="addr-row">
        <text class="label">联系电话：</text>
        <text class="text">{{ item.phone }}</text>
      </view>
      <view class="addr-row">
        <text class="label">详细地址：</text>
        <text class="text">{{ item.addr }}</text>
      </view>
    </view>
	<view class="addaddr">
		<button @click="addaddr">+新增地址</button>
	</view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const tab_addr = ref([]) // 初始为空数组，避免渲染报错

function deleteaddr(id){
	uni.request({
		url: `http://127.0.0.1:8081/address/delete?id=${id}`,
		method: 'DELETE',
		header: {
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			addrlistfin()
			uni.showToast({title:'删除成功'})
		}
	})
}

function addrlistfin() {
  uni.request({
    url: 'http://127.0.0.1:8081/address/list',
    method: 'PUT',
    header: {
      "content-type": "application/x-www-form-urlencoded",
      "satoken": uni.getStorageSync('tokenValue')
    },
    success: (res) => {
      tab_addr.value = res.data.data
      console.log(res.data.data)
    }
  })
}
// 跳转新增地址
function addaddr() {
	uni.navigateTo({
		url: `/pages/addaddr/addaddr`
	})
}

onLoad(() => {
  addrlistfin()
})
</script>

<style scoped>
.addaddr{
	position: fixed;
	bottom: 7vh;
	width: 80%;
}
.addaddr button{
	background-color: aqua;
	text-align: center;
	width: 100%;
}
page {
  background-color: #f5f5f5;
}
.wrap {
  padding: 30rpx;
}
.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}
/* 地址卡片样式 */
.addr-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 25rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.06);
  position: relative;
}
/* 每一行布局 */
.addr-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16rpx;
  font-size: 28rpx;
}
.addr-row:last-child {
  margin-bottom: 0;
}
/* 标签文字 */
.label {
  color: #666;
  width: 140rpx;
  flex-shrink: 0;
}
/* 内容文字 */
.text {
  color: #333;
  flex: 1;
  line-height: 1.5;
}
.delete{
	position: absolute;
	top: 10px;
	right: 5vh;
}
</style>