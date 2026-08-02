<template>
	<view class="container">
		<view class="card">

			<view class="title">
				修改密码
			</view>

			<view class="form-item">
				<text class="label">旧密码</text>
				<input
					class="input"
					type="password"
					placeholder="请输入旧密码"
					v-model="oldpwd"
				/>
			</view>

			<view class="form-item">
				<text class="label">新密码</text>
				<input
					class="input"
					type="password"
					placeholder="请输入新密码"
					v-model="newpwd"
				/>
			</view>

			<view class="form-item">
				<text class="label">确认密码</text>
				<input
					class="input"
					type="password"
					placeholder="请再次输入新密码"
					v-model="tonewpwd"
				/>
			</view>

			<button class="submit-btn" @click="updatepwd">
				确认修改
			</button>

		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'

const oldpwd = ref('')
const newpwd = ref('')
const tonewpwd = ref('')

function updatepwd() {
	if (oldpwd.value == '' || newpwd.value == '' || tonewpwd.value == '') {
		uni.showToast({
			title: "请把表单输入完整",
			icon: 'error',
			duration: 2000
		})
		return
	}

	if (tonewpwd.value != newpwd.value) {
		uni.showToast({
			title: "两次密码不一致",
			icon: 'error',
			duration: 2000
		})
		return
	}

	uni.request({
		url: `http://127.0.0.1:8081/user/updatepwd?oldpassword=${oldpwd.value}&newpassword=${newpwd.value}`,
		method: 'PUT',
		header: {
			"content-type": "application/json",
			"satoken": uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			if (res.data.code == 0) {
				uni.showToast({
					title: res.data.data,
					icon: 'error',
					duration: 2000
				})
				uni.request({
					url:"http://127.0.0.1:8081/user/logout",
					header:{
						"content-type": "application/x-www-form-urlencoded",
						"satoken": uni.getStorageSync('tokenValue') 
					},
					method:"GET",
					success:((res)=>{
						console.log("退出成功")
						uni.removeStorageSync('tokenValue');
						uni.redirectTo({
							url:'/pages/login/login'
						})
					})
				})
			} else {
				uni.showToast({
					title: res.data.message,
					icon: 'error',
					duration: 2000
				})
			}
		}
	})
}
</script>

<style>
page {
	background: #f5f7fb;
}

.container {
	padding: 40rpx;
	min-height: 100vh;
	box-sizing: border-box;
}

.card {
	background: #ffffff;
	border-radius: 24rpx;
	padding: 40rpx;
	box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.06);
}

.title {
	text-align: center;
	font-size: 40rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 50rpx;
}

.form-item {
	margin-bottom: 35rpx;
}

.label {
	display: block;
	font-size: 28rpx;
	color: #666;
	margin-bottom: 15rpx;
}

.input {
	height: 90rpx;
	background: #f8f9fc;
	border: 2rpx solid #e5e7eb;
	border-radius: 16rpx;
	padding: 0 25rpx;
	font-size: 30rpx;
	color: #333;
	box-sizing: border-box;
	transition: all 0.3s;
}

.input:focus {
	border-color: #4a90e2;
	background: #fff;
}

.submit-btn {
	margin-top: 30rpx;
	height: 90rpx;
	line-height: 90rpx;
	border-radius: 45rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #fff;
	border: none;
	background: linear-gradient(135deg, #4a90e2, #6c63ff);
	box-shadow: 0 8rpx 20rpx rgba(108, 99, 255, 0.25);
}

.submit-btn::after {
	border: none;
}

.submit-btn:active {
	transform: scale(0.98);
	opacity: 0.9;
}
</style>