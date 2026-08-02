<template>
	<view>
		<view class="input-wrapper">
			<input class="user-input" 
				type="text" 
				placeholder="请输入用户名" 
				v-model="data.username" />
		</view>
		<view class="input-wrapper">
			<input
				class="uni-input"
				password
				placeholder="请输入密码"
				v-model="data.inputPassword"
			/>
		</view>
		<view class="input-button">
			<button
				type="primary"
				@click="submit"
			>登录</button>
		</view>
		<view class="">
			<button @click="yijian">一键登录</button>
		</view>
		<view>{{ texts }}</view>
	</view>
</template>

<script setup>
import { reactive, ref,onMounted } from 'vue';

const texts = ref('');

const data = reactive({
	username: '',
	inputPassword: ''
})
// 一键登录函数
function yijian(){
		uni.showToast({
			title: '登录中...',
			icon: 'loading'
		})
		uni.login({
		provider: 'weixin', //使用微信登录
			success:(res)=>{
				console.log(res.code)

				uni.request({
					url:`http://127.0.0.1:8081/user/wxlogin?code=${res.code}`,
					method:'get',
					// data:{
					// 	code:res.code
					// },
					success:(res)=>{
						if(res.data.code==0){
							console.log(res.data)
							console.log(res.data.data.tokenValue)
							uni.setStorageSync("tokenValue",res.data.data.tokenValue)
							userrole()
						}
					},
					error:(err)=>{
						console.log("登录失败"+err)
					}
				})
			}

		})
	}	
function saveCurrentUser() {
	uni.request({
		url: "http://127.0.0.1:8081/user/info",
		header: {
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},
		method: "GET",
		success: (res) => {
			if (res.data.code === 0) {
				const u = res.data.data
				uni.setStorageSync('currentUser', {
					id: u.id,
					username: u.username,
					nickname: u.nickname,
					avatar: u.file_url,
					roleCode: uni.getStorageSync('role') || 'user'
				})
			}
		}
	})
}
function userrole(){
	uni.request({
		url:"http://127.0.0.1:8081/user/role",
		header:{
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},
		method:"GET",
		success:(res)=>{
			if(res.data.code==0){
				const role = res.data.data
				uni.setStorageSync('role', role)
				saveCurrentUser()
				console.log('跳转')
				uni.switchTab({
					url: '/pages/home/home'
				})
			}else{
				uni.removeStorageSync('tokenValue');
			}
		},
		fail(error){
			console.log(error)
		}
	});
}
onMounted(()=>{
	if(uni.getStorageSync("tokenValue")){
		userrole()
	}
})

function submit(){
	if(data.username==''|| data.inputPassword==''){
		texts.value='表单不能为空'
		return
	}
	uni.showToast({
		title: '登录中...',
		icon: 'loading'
	})
	uni.request({
		url:"http://127.0.0.1:8081/user/login",
		method:"POST",
		data:{
			"username":data.username,
			"password":data.inputPassword
		},
		success:(res)=>{
			if(res.data.code==0){
				console.log(res.data)
				uni.setStorageSync("tokenValue",res.data.data.tokenValue)
				userrole()
			}else{
				uni.showToast({
					title:res.data.message,
					icon:'error',
					duration:2000
				})
			}
		},
		fail: (error)=>{
			console.log(error)
		}
	})
}


</script>

<style scoped>
.user-input, .uni-input {
	border: 1px solid #ccc;
	width: 80%;
	margin: 10px auto;
	display: block;
	padding: 8px;
}
.input-button button {
	border: 1px solid #ccc;
	width: 80%;
	height: 50px;
	margin: 10px auto;
}
</style>