<template>
	<view class="page">
		<web-view 
			v-if="url" 
			:webview-styles="webviewStyles" 
			:src="url" 
			@message="onMessage" 
			@load="onWebLoad"
			class="web-view" 
			ref="webview"
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
		// 你的原有代码不变...
		const imWebUrl = 'http://127.0.0.1:8082'
		const user_id = '6a2d8ff2f93b01cdb4586419'
		const path = '/#/uni_modules/uni-im/pages/index/index'

		const param = {
			_id: "sdhfsdafhasjdlfkdas12111",
			nickname: '自定义用户昵称1111',
			avatar_file: {
				url: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.jYvYNzWVuUITIZrCiXgxdAHaHa?w=193&h=193&c=8&rs=1&qlt=90&o=6&dpr=1.8&pid=3.1&rm=2'
			}
		}

		if (!imWebUrl || !user_id || Object.keys(param).length === 0) {
			uni.showModal({ content: '参数配置不全', showCancel: false })
			return
		}

		// #ifdef APP-PLUS
		const { deviceId: device_id } = uni.getDeviceInfo()
		const { appId: appid } = uni.getAppBaseInfo()
		try {
			const { cid: push_clientid } = await uni.getPushClientId()
			param.mainAppInfo = { appid, device_id, push_clientid }
		} catch (err) {
			console.log('推送获取失败', err)
		}
		// #endif

		// 拼接地址 + 隐藏列表参数
		this.url = `${imWebUrl + path}?login=${encodeURIComponent(JSON.stringify(param))}&user_id=${user_id}&hideList=1`
		console.log('web-view 完整地址：', this.url)
	},
	methods: {
		onMessage(e) {
			console.log('web-view 接收消息：', e)
		},
		// web-view 加载完成后执行 JS 隐藏列表
		onWebLoad() {
			// #ifdef APP-PLUS || MP-WEIXIN
			const web = this.$refs.webview
			// 执行JS：匹配 uni-im 左侧会话列表容器并隐藏
			const jsCode = `
				var listDom = document.querySelector('.uni-im-list') || document.querySelector('.im-left');
				if(listDom){
					listDom.style.display = 'none';
				}
				// 让聊天区域占满全屏
				var contentDom = document.querySelector('.uni-im-content') || document.querySelector('.im-right');
				if(contentDom){
					contentDom.style.width = '100%';
				}
			`
			web.evalJs(jsCode)
			// #endif
		}
	}
}
</script>

<style scoped>
.page {
	width: 750rpx;
	height: 100%;
	flex: 1;
	background-color: #f8f8f8;
}
.web-view {
	width: 750rpx;
	flex: 1;
}
</style>