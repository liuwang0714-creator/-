<template>
	<view class="container">
		<view class="loading">
			<text>{{ statusText }}</text>
		</view>
	</view>
</template>

<script>
import uniIm from '@/uni_modules/uni-im/sdk/index.js'

export default {
	data() {
		return {
			statusText: '正在连接客服...'
		}
	},

	async onLoad() {

		// 客服用户ID
		const customerServiceUid = '6a2d51f52979cacdbb720850'

		try {

			// 获取本地游客ID
			let guestUid = uni.getStorageSync('guest_uid')

			if (!guestUid) {
				guestUid =
					'guest_' +
					Date.now() +
					'_' +
					Math.floor(Math.random() * 100000)

				uni.setStorageSync('guest_uid', guestUid)
			}

			this.statusText = '正在登录IM...'

			// 登录IM
			const loginRes = await uniIm.login({
				_id: guestUid,
				nickname: '游客用户',
				avatar_file: {
					url: 'https://img.yzcdn.cn/vant/cat.jpeg'
				}
			})

			console.log('IM登录成功', loginRes)

			this.statusText = '正在打开聊天窗口...'

			// 延迟一下让SDK完成初始化
			setTimeout(() => {

				uni.redirectTo({
					url:
						'/uni_modules/uni-im/pages/chat/chat?user_id=' +
						customerServiceUid
				})

			}, 500)

		} catch (e) {

			console.error('IM登录失败', e)

			this.statusText = '登录失败'

			uni.showModal({
				title: 'IM登录失败',
				content: JSON.stringify(e),
				showCancel: false
			})
		}
	}
}
</script>

<style scoped>
.container {
	width: 100%;
	height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f5f5f5;
}

.loading {
	font-size: 30rpx;
	color: #666;
}
</style>