<template>
	<view>
		<view>
			<image :src="userinfo.file_url" mode="" class="tx" @click="ghtx"></image>
			<text><text style="color: #fdc700;">{{userinfo.nickname}}</text>,你好</text>
		</view>
		<view @click="toorder">
			<uni-icons type="list" size="24" color="#ff8000" ></uni-icons>我的订单
		</view>
		<view @click="rzl">
			<uni-icons type="person-filled" size="24" color="#ff8000" ></uni-icons>修改资料
		</view>
		<view @click="rpassword">
			<uni-icons type="refresh-filled" size="24" color="#ff8000" ></uni-icons>修改密码
		</view>
		<view @click="addr">
			<uni-icons type="paperplane-filled" size="24" color="#ff8000" ></uni-icons>收货地址
		</view>
		<view @click="guke" v-if="role=='merchant'">
			<uni-icons type="contact-filled" size="24" color="#ff8000" ></uni-icons>顾客管理
		</view>
		<view @click="kefu" >
			<uni-icons type="contact-filled" size="24" color="#ff8000" ></uni-icons>客服
		</view>
		
		<button @click="logoutbtn">退出登录</button>
		<view class="ghtxbt" v-if="ghtxbt">
			<view class="off" @click="ghtxbt=!ghtxbt">
				X
			</view>
			请选择头像
			<img :src="usertximg" alt="" />
			<button @click="choose">选择其他图片</button>
			<button @click="uploadusertx">确认更换</button>
		</view>
	</view>
</template>

<script setup>
	import { reactive, ref, onMounted } from 'vue';
	const role=ref('')
	const ghtxbt=ref(false)
	const usertximg=ref('')
	const userinfo = reactive({
		userid:'',
		nickname:'',
		phone:'',
		file_url:"",
		birthday:''
	})
	
	
	function uploadusertx(){
		uni.uploadFile({
			url: "http://127.0.0.1:8081/file/upload",
			filePath: usertximg.value, 
			name: "file",
			header: {
			  "Content-Type": "multipart/form-data",
			  "satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
			  uni.showToast({ title: "上传成功" });
			  const result = JSON.parse(res.data)
			  console.log(result.data)
			  uni.request({
			  	url:`http://127.0.0.1:8081/user/updatetx?user_file_id=${result.data}`,
				header:{
					"content-type": "application/x-www-form-urlencoded",
					"satoken": uni.getStorageSync('tokenValue') 
				},
				method:"GET",
				success: (resl) => {
					console.log(resl.data)
					ghtxbt.value=false
					myinfo()
				}
			  })
			  
			  
			},
			fail: (err) => {
			  console.error("上传失败", err);
			  uni.showToast({ title: "上传失败", icon: "none" });
			}
		  });
	}
	
	
	function ghtx(){
		ghtxbt.value=true
	}
	const choose=()=>{
		uni.chooseMedia({
			count:1,
			mediaType:['image','video'],
			sourceType:['album','camera'],
			success:res=>{
				// bottomVal.value=-100
				// houduan.value=''
				console.log(res.tempFiles) //数组
				//数组中第一个图片的临时地址
				console.log(res.tempFiles[0].tempFilePath)
				usertximg.value=res.tempFiles[0].tempFilePath
				
			}
		})
	}
	
	// 我的订单
	function toorder(){
		uni.switchTab({
		  url: "/pages/order/order"  // tabBar 页面路径，**不能带参数**
		})
		return
	}
	// 修改资料
	function rzl(){
		uni.navigateTo({
			url:"/pages/updatezil/updatezil"
		})
	}
	function kefu(){
		uni.navigateTo({ url: '/pages/im/chat?username=admin' })
	}
	
	//重置密码
	function rpassword(){
		uni.navigateTo({
			url:"/pages/updatepwd/updatepwd"
		})
	}
	// 收货地址管理
	function addr(){
		uni.navigateTo({
			url:"/pages/addr/addr"
		})
	}
	// 顾客管理
	function guke(){
		console.log('顾客管理')
		return
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
					role.value=res.data.data
					uni.switchTab({
						url: '/pages/home/home'
					})
				}else{
					console.log('没有登录')
					uni.redirectTo({
						url:'/pages/login/login'
					})
				}
			},
			fail(error){
				console.log(error)
			}
		}); 
	} 
	
	
	function logoutbtn(){
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
	}
	
	function myinfo(){
		uni.request({
			url: 'http://127.0.0.1:8081/user/info',
			method: 'GET',
			header:{
				"content-type": "application/x-www-form-urlencoded",
				"satoken": uni.getStorageSync('tokenValue') 
			},
			success: res => {
				if(res.data.code==0){
					const userinfotab=res.data.data
					userinfo.userid=userinfotab.id,
					userinfo.file_url='http://127.0.0.1:8081/images/'+userinfotab.file_url
				
					userinfo.nickname=userinfotab.nickname
					console.log(userinfo.nickname)
					userinfo.phone=userinfotab.phone
					userinfo.birthday=userinfotab.birthday
					usertximg.value='http://127.0.0.1:8081/images/'+userinfotab.file_url
				}
			},
		});
	}
	
	onMounted(()=>{
		userrole()
		myinfo()
	})
	
	
</script>


<style scoped>
	.tx{
		width: 70px;
		height: 70px;
		border-radius: 50%;
		border: 3px solid #fdc700;
		margin-right: 12px;
	}
	
	/* ---------- 优化后的头像更换弹窗 ---------- */
	.ghtxbt {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 80%;
		max-width: 380px;
		background: #ffffff;
		border-radius: 24px;
		padding: 30px 20px 20px;
		box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
		z-index: 1000;
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
		font-size: 16px;
		color: #333;
		font-weight: 500;
	}
	
	/* 关闭按钮 */
	.off {
		position: absolute;
		top: 12px;
		right: 12px;
		width: 32px;
		height: 32px;
		border-radius: 50%;
		background: #f5f5f5;
		color: #888;
		font-size: 14px;
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1001;
		transition: 0.2s;
	}
	.off:active {
		background: #e0e0e0;
	}
	
	/* 头像预览图片 */
	.ghtxbt img {
		width: 100px;
		height: 100px;
		border-radius: 50%;
		object-fit: cover;
		border: 3px solid #fdc700;
		margin: 15px 0 10px;
		box-shadow: 0 4px 12px rgba(0,0,0,0.1);
	}
	
	/* 弹窗内的按钮统一样式 */
	.ghtxbt button {
		width: 75%;
		margin: 8px 0;
		height: 44px;
		line-height: 44px;
		border-radius: 44px;
		font-size: 15px;
		font-weight: 500;
		border: none;
		background: linear-gradient(135deg, #ff8000, #ff5500);
		color: #fff;
		box-shadow: 0 4px 10px rgba(255,128,0,0.3);
		transition: all 0.2s;
	}
	
	/* 第一个按钮（选择其他图片）改为次要样式 */
	.ghtxbt button:first-of-type {
		background: #f0f0f0;
		color: #333;
		box-shadow: none;
	}
	.ghtxbt button:first-of-type:active {
		background: #e4e4e4;
	}
	
	.ghtxbt button:active {
		opacity: 0.9;
		transform: scale(0.98);
	}
	
	/* ---------- 其余原有样式 ---------- */
	page{
		background: #f5f5f5;
	}
	
	view{
		box-sizing: border-box;
	}
	
	view > view:first-child{
		display: flex;
		align-items: center;
		padding: 20px;
		background: linear-gradient(135deg,#ffb300,#ff8000);
		color: #fff;
		font-size: 18px;
		font-weight: bold;
		border-radius: 0 0 20px 20px;
		box-shadow: 0 4px 10px rgba(0,0,0,0.1);
		margin-bottom: 15px;
	}
	
	view > view:not(:first-child){
		display: flex;
		align-items: center;
		gap: 10px;
		background: #fff;
		margin: 12px 15px;
		padding: 16px;
		border-radius: 14px;
		font-size: 16px;
		color: #333;
		box-shadow: 0 2px 8px rgba(0,0,0,0.05);
		transition: all 0.2s;
	}
	
	view > view:not(:first-child):active{
		transform: scale(0.98);
		background: #f9f9f9;
	}
	
	button{
		margin: 30px 20px;
		background: linear-gradient(135deg,#ff8000,#ff5500);
		color: #fff;
		border: none;
		border-radius: 50px;
		font-size: 16px;
		font-weight: bold;
		height: 45px;
		line-height: 45px;
		box-shadow: 0 4px 10px rgba(255,128,0,0.3);
	}
	
	button:active{
		opacity: 0.9;
		transform: scale(0.98);
	}
</style>