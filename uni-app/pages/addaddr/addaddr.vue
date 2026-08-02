<template>
	<view class="page">

		<!-- 顶部 -->
		<view class="header">

			<view>
				<text class="title">新增收货地址</text>
				<text class="sub-title">
					请填写完整的收货信息
				</text>
			</view>

			<view class="header-icon">
				📍
			</view>

		</view>

		<!-- 表单 -->
		<view class="form-card">

			<!-- 姓名 -->
			<view class="form-item">

				<text class="label">
					收货人
				</text>

				<input
					type="text"
					placeholder="请输入姓名"
					v-model="tabadd.name"
					class="input"
					placeholder-class="placeholder"
				/>

			</view>

			<!-- 手机号 -->
			<view class="form-item">

				<text class="label">
					手机号码
				</text>

				<input
					type="text"
					placeholder="请输入手机号码"
					v-model="tabadd.phone"
					class="input"
					placeholder-class="placeholder"
				/>

			</view>

			<!-- 地址 -->
			<view class="form-item">

				<text class="label">
					收货地址
				</text>

				<input
					type="text"
					placeholder="请输入详细收货地址"
					v-model="tabadd.addr"
					class="input"
					placeholder-class="placeholder"
				/>

			</view>

		</view>

		<!-- 按钮 -->
		<button class="submit-btn" @click="addfun">
			确认添加
		</button>
	</view>
</template>

<script setup>
	import { ref, reactive } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	

	const id = ref()

	const tabadd = reactive({
		name: '',
		phone: '',
		addr: ''
	})

	function addfun() {

		if (tabadd.name == '', tabadd.phone == '', tabadd.addr == '') {

			console.log('表单不能为空')

			return
		}
		uni.showToast({
			title: '请稍候',
			icon: 'loading'
		})
		uni.request({
			url: 'http://127.0.0.1:8081/address/add',

			data: tabadd,

			method: 'PUT',

			header: {
				"content-type": "application/json",
				"satoken": uni.getStorageSync('tokenValue')
			},

			success: (res) => {

				if (res.data.code == 0) {

					console.log('追加成功')
					if(id.value){
						uni.navigateTo({
							url:`/pages/goodsaccount/goodsaccount?id=${id.value}`
						})
					}else{
						uni.navigateTo({
							url:`/pages/addr/addr`
						})
					}
				} else {

					console.log(res.data)
				}
			}
		})
	}

	onLoad((options) => {

		id.value = options.id

		console.log('订单ID：', id.value)

	})
</script>

<style scoped>
	page {
		background: #f5f5f7;
	}

	.page {
		padding: 24rpx;
		box-sizing: border-box;
	}

	/* 顶部 */
	.header {
		background: linear-gradient(135deg, #ff9f43, #ff6b01);
		border-radius: 28rpx;
		padding: 40rpx 30rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		color: white;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 24rpx rgba(255, 107, 1, 0.2);
		width: 80%;
	}

	.title {
		display: block;
		font-size: 40rpx;
		font-weight: bold;
		margin-bottom: 12rpx;
	}

	.sub-title {
		font-size: 24rpx;
		opacity: 0.9;
	}

	.header-icon {
		font-size: 62rpx;
	}

	/* 表单卡片 */
	.form-card {
		background: white;
		border-radius: 28rpx;
		padding: 20rpx 30rpx;
		box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.04);
	}

	/* 每一项 */
	.form-item {
		padding: 28rpx 0;
		border-bottom: 2rpx solid #f5f5f5;
	}

	.form-item:last-child {
		border-bottom: none;
	}

	/* 标题 */
	.label {
		display: block;
		font-size: 28rpx;
		color: #333;
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	/* 输入框 */
	.input {
		height: 80rpx;
		background: #f8f8f8;
		border-radius: 18rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
	}

	.placeholder {
		color: #aaa;
		font-size: 26rpx;
	}

	/* 提交按钮 */
	.submit-btn {
		margin-top: 50rpx;
		height: 88rpx;
		line-height: 88rpx;
		border-radius: 60rpx;
		font-size: 30rpx;
		font-weight: bold;
		color: white;
		background: linear-gradient(90deg, #ff9f43, #ff6b01);
		box-shadow: 0 8rpx 20rpx rgba(255, 107, 1, 0.25);
		border: none;
	}

	.submit-btn::after {
		border: none;
	}

	/* 底部订单ID */
	.bottom-id {
		text-align: center;
		margin-top: 40rpx;
		font-size: 24rpx;
		color: #999;
	}
</style>