<template>
	<view class="container">
		<view class="form-item">
			<input class="input-box" type="text" placeholder="请输入昵称" v-model="nickname"/>
		</view>
		<view class="form-item">
			<input class="input-box" type="text" placeholder="请输入手机号码" v-model="phone"/>
		</view>
		<view class="form-item">
			<input class="input-box" type="text" placeholder="请输入生日" v-model="birthday"/>
		</view>
		<button class="submit-btn" @click="submit">提交更改</button>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 表单数据
const nickname = ref('')
const phone = ref('')
const birthday = ref('')

// 提交更改
function submit(){
	if(nickname.value=='' || phone.value=='' ||birthday.value==''){
		uni.showToast({
			title:"请填写完整",
			icon:"error",
			duration:2000
		})
		return
	}
	uni.request({
		url:`http://127.0.0.1:8081/user/updateprofile?nickname=${nickname.value}&phone=${phone.value}&birthday=${birthday.value}`,
		method: 'PUT',
		header: {
			"content-type": "application/json",
			"satoken": uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			console.log(res.data)
		}
	})
}

onLoad(() => {
	uni.request({
		url: "http://127.0.0.1:8081/user/info",
		method: 'GET',
		header: {
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			if (res.data.code === 0) {
				const user = res.data.data
				nickname.value = user.nickname || ''
				phone.value = user.phone || ''
				birthday.value = user.birthday || ''
			}
		}
	})
})
</script>

<style scoped>
.container {
	padding: 30rpx;
	box-sizing: border-box;
}
.form-item {
	margin-bottom: 25rpx;
}
.input-box {
	width: 100%;
	height: 80rpx;
	padding: 0 20rpx;
	border: 1rpx solid #eee;
	border-radius: 8rpx;
	background-color: #fff;
	font-size: 28rpx;
	box-sizing: border-box;
}
/* 输入框聚焦浅高亮 */
.input-box:focus {
	border-color: #409eff;
}
.submit-btn {
	width: 100%;
	height: 80rpx;
	line-height: 80rpx;
	background-color: #409eff;
	color: #fff;
	border-radius: 8rpx;
	font-size: 30rpx;
	margin-top: 20rpx;
}
/* 按钮点击变暗效果 */
.submit-btn::after {
	display: none;
}
.submit-btn:active {
	opacity: 0.8;
}
</style>