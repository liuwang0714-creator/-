<template>
	<view class="page">
		<web-view 
			v-if="url" 
			:webview-styles="webviewStyles" 
			:src="url" 
			@message="onMessage" 
			class="web-view"
			ref="web-view"
		></web-view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			url: '',
			webviewStyles: {
				progress: {
					color: "#00aa55"
				}
			}
		}
	},
	async onLoad(e) {
		// IM 部署地址
		const imWebUrl = 'https://env-00jy6fo7gadj-static.normal.cloudstatic.cn'
		const user_id = '6a2d51f52979cacdbb720850'
		const path = '/#/uni_modules/uni-im/pages/index/index'

		const param = {
			_id: "sdhfsdafhasjdlfkdas12111",
			nickname: '自定义用户昵称1111',
			avatar_file: {
				url: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.jYvYNzWVuUITIZrCiXgxdAHaHa?w=193&h=193&c=8&rs=1&qlt=90&o=6&dpr=1.8&pid=3.1&rm=2'
			}
		}

		// 基础参数校验
		if (!imWebUrl) {
			return uni.showModal({
				content: '请配置imWebUrl',
				showCancel: false
			});
		}
		if (!user_id) {
			return uni.showModal({
				content: '请配置user_id',
				showCancel: false
			});
		}
		if (Object.keys(param).length === 0) {
			return uni.showModal({
				content: '请配置param',
				showCancel: false
			});
		}

		// #ifdef APP-PLUS
		const { deviceId: device_id } = uni.getDeviceInfo()
		const { appId: appid } = uni.getAppBaseInfo()
		try {
			const { cid: push_clientid } = await uni.getPushClientId()
			param.mainAppInfo = {
				appid,
				device_id,
				push_clientid
			}
		} catch (err) {
			console.log('推送获取失败', err)
		}
		// #endif

		// ✅ 修复 URL 拼接：参数放在 # 前面，保证 IM 页面能正确解析登录状态
		const loginStr = encodeURIComponent(JSON.stringify(param))
		const query = `login=${loginStr}&user_id=${user_id}`
		this.url = `${imWebUrl}?${query}${path}`
		console.log('web-view 完整地址：', this.url)
	},
	methods: {
		onMessage(e) {
			console.log('web-view 接收消息：', e)
		}
	}
}
</script>

<style scoped>
/* ✅ 修复容器样式：撑满屏幕，避免缩放和留白 */
.page {
	width: 100%;
	height: 100vh;
	margin: 0;
	padding: 0;
	background-color: #f8f8f8;
}

.web-view {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
}
</style>