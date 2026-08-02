<template>
	<view class="container" v-if="goods && user">

		<!-- 商品详情 -->
		<view class="card goods-card">

			<!-- 商品图片 -->
			<view class="img-wrap">
				<image
					class="goods-img"
					mode="aspectFill"
					:src="`http://127.0.0.1:8081/images/${goods.file_url}`"
				></image>
			</view>

			<!-- 商品信息 -->
			<view class="info-box">

				<view class="price-stock">
					<text class="price">￥{{goods.price}}</text>

					<view class="stock-box">
						<text class="stock-text">
							库存 {{goods.stock}}
						</text>
					</view>
				</view>

				<text class="goods-name">
					{{goods.goods_name}}
				</text>

				<!-- 商品描述 -->
				<view class="section">

					<view class="section-title">
						<view class="line"></view>
						<text>商品描述</text>
					</view>

					<text class="desc">
						{{goods.description}}
					</text>

				</view>

				<!-- 发布时间 -->
				<view class="section">

					<view class="section-title">
						<view class="line"></view>
						<text>发布时间</text>
					</view>

					<text class="time">
						{{goods.create_time}}
					</text>

				</view>

				<button
					type="primary"
					class="buy-btn"
					@click="goumai"
				>
					立即购买
				</button>

			</view>

		</view>
		<button v-if="shangchaunimgurl"  @click="shangchaunimgurl=''" class="offcommentimg" >X</button>
		<image :src="shangchaunimgurl" mode="" v-if="shangchaunimgurl"  class='shangchaunimgurl'></image>
		<!-- 回复模块 -->
		<view class="addcomment">
			
			<button @click="uploadimgshow=!uploadimgshow,shangchaunimgurl=''" style="width: 7vh;">
				<uni-icons type="image-filled" size="30" color="#FFFFFF"></uni-icons></button>
			<input
				type="text"
				placeholder="期待您的评论"
				@confirm="addcomment"
				v-model="huivalue"
			/>
			<button @click="addcomment">提交</button>
		</view>

		<!-- 发布人 -->
		<view class="card user-card">

			<view class="user-header">
				<text class="user-title">
					发布人信息
				</text>
			</view>

			<view class="user-info">

				<view class="left-user">

					<image
						class="avatar"
						mode="aspectFill"
						:src="`http://127.0.0.1:8081/images/${user.file_url}`"
					></image>

					<view class="user-text">
						<text class="nickname">
							{{user.nickname}}
						</text>

						<text class="tips">
							诚信卖家
						</text>
					</view>

				</view>

				<button
					class="contact-btn"
					size="mini"
					@click="lianxi"
				>
					联系他
				</button>

			</view>

		</view>

		<!-- 评论区 -->
		<view class="card comment-card">

			<view class="comment-header">
				<text class="comment-title">
					商品评论
				</text>
			</view>

			<!-- 无评论 -->
			<view
				class="empty-box"
				v-if="!tabcomment || tabcomment.length===0"
			>
				暂无评论
			</view>

			<!-- 评论列表 -->
			<view
				class="comment-item"
				v-for="item in tabcomment"
				:key="item.comment_id"
				@click="huifucomment(item.comment_id)"
			>

				<!-- 评论顶部 -->
				<view class="comment-top">

					<view class="comment-user-box">

						<image
							class="comment-avatar"
							mode="aspectFill"
							:src="`http://127.0.0.1:8081/images/${item.file_url}`"
						></image>

						<view class="comment-user-info">

							<text class="comment-user">
								{{ item.nickname }}
							</text>

							<text class="comment-time">
								{{ item.createTime }}
							</text>
							
							
						</view>

						<!-- 删除评论按钮 -->
						<view class="deletecomment" @click.stop="deletecomment(item.comment_id,item.comment_url)" v-if="item.user_id==nowuserid">
							<uni-icons
								type="trash-filled"
								size="30"
								color="red"
							></uni-icons>
						</view>

					</view>

				</view>

				<!-- 评论内容 -->
				<view class="comment-content">
					<text>{{ item.content }}</text> <br/>
					<!-- 评论图片 已优化 -->
					<image 
						v-if="item.comment_url!=null"
						:src="`http://127.0.0.1:8081/images/${item.comment_url}`"
						mode="aspectFill"
						class="comment-pic"
						@click.stop="zoom(item.comment_url)"
					></image>
				</view>

				<!-- 查看回复按钮 -->
				<view class="reply-btn-box" v-if="item.to_coment_num>0">

					<button
						class="reply-btn"
						size="mini"
						@click.stop="tocommentfun(item)"
					>
						{{ item.showReply ? '收起回复' : '查看回复' }}
					</button>

				</view>

				<!-- 回复列表 -->
				<view
					class="reply-list"
					v-if="item.showReply"
				>

					<view
						class="reply-item"
						v-for="reply in item.replyList"
						:key="reply.comment_id"
					>

						<text class="reply-name">
							{{reply.nickname}}：
						</text>

						<text class="reply-content">
							{{reply.content}}
						</text>
						<image
							v-if="reply.comment_url"
							:src="`http://127.0.0.1:8081/images/${reply.comment_url}`"
							mode="aspectFill"
							class="comment-to-pic"
							@click.stop="zoom(reply.comment_url)"
						></image>
						<view 
							class="deletetocomment"
							@click.stop="deletetocomment(reply.comment_id,reply.one_comment_id,reply.comment_url)"
							v-if="reply.user_id==nowuserid"
						>
							<uni-icons
								type="trash-filled"
								size="25"
								color="red"
							></uni-icons>
						</view>

					</view>

					<view
						class="empty-reply"
						v-if="item.replyList && item.replyList.length===0"
					>
						暂无回复
					</view>

				</view>

			</view>

		</view>

		<view class="huifutanchu" v-if="huifshow">

			<view
				class="close-btn"
				@click="closeReplyPopup"
			>
				X
			</view>

			<text class="reply-title">回复评论</text>

			<input
				type="text"
				v-model="to_comment"
				placeholder="请输入回复内容"
				@confirm="huifucommentbtn"
			/>
			<button  @click="uploadimgshow=true" class="tocommentimg">添加图片</button>
			<button
				type="default"
				@click="huifucommentbtn"
				class="tocommentbtn"
			>
				回复
			</button>

		</view>

		<view
			class="mask"
			v-if="maskshow"
			@click="closeReplyPopup"
		></view>
		<!-- 图片上传 -->
		<view class="uploadimg" v-if="uploadimgshow">
			<button class="off" @click="uploadimgshow=false">X</button>
			
			<text>请选择图片上传方式</text>
			<button @click="showCameraimg" class='uploadimgbtn'>拍照上传</button>
			<button @click="choose" class='uploadimgbtn'>本地相册选择</button>
		</view>
		<camera
		  v-if="showCamera"
		  device-position="front"
		  flash="off"
		  style="width: 100vh; height: 100vh; position: fixed; top: 0; left: 0; z-index:999; "
		></camera>
		<button @click="takePhoto" class="pz" v-if='pzbtn'>拍照</button>
	</view>
</template>

<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { ref } from 'vue'

const goods = ref()
const user = ref()
const tabcomment = ref([])
const huivalue = ref('')
const id = ref()
const nowuserid = ref()

const maskshow = ref(false)
const huifshow = ref(false)

const to_comment_one_id = ref()
const to_comment = ref('')
const to_commentdelet_commetnid=ref()

const uploadimgshow=ref(false)
const shangchaunimgurl=ref()

const showCamera=ref(false)
const pzbtn=ref(false)


// 联系 - 打开 IM 聊天
function lianxi() {
	const token = uni.getStorageSync('tokenValue')
	if (!token) {
		uni.showToast({ title: '请先登录', icon: 'none' })
		return
	}
	const merchantUsername = user.value?.username
	if (!merchantUsername) {
		uni.showToast({ title: '无法获取商家信息', icon: 'none' })
		return
	}
	// 确保 currentUser 已保存
	const saved = uni.getStorageSync('currentUser')
	if (!saved) {
		// 尝试保存当前用户信息
		uni.request({
			url: 'http://127.0.0.1:8081/user/info',
			method: 'GET',
			header: {
				'content-type': 'application/x-www-form-urlencoded',
				'satoken': uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				if (res.data.code === 0) {
					const u = res.data.data
					uni.setStorageSync('currentUser', {
						id: u.id,
						username: u.username,
						nickname: u.nickname,
						roleCode: uni.getStorageSync('role') || 'user'
					})
				}
			},
			complete: () => {
				uni.navigateTo({ url: '/pages/im/chat?username=' + encodeURIComponent(merchantUsername) })
			}
		})
	} else {
		uni.navigateTo({ url: '/pages/im/chat?username=' + encodeURIComponent(merchantUsername) })
	}
}

// 本地相册上传
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
			shangchaunimgurl.value=res.tempFiles[0].tempFilePath
			uploadimgshow.value=false
		}
	})
}


// 拍照上传函数
function showCameraimg(){
	
	showCamera.value=true
	console.log('拍照上传')
	setTimeout(() => {
		pzbtn.value=true
	},2000)
	return
}

const takePhoto=()=>{
	const context = uni.createCameraContext()
	context.takePhoto({
		quality:'high',
		success:res=>{
			console.log(res.tempImagePath)
			//把拍照的图片赋值给图片的地址
			shangchaunimgurl.value=res.tempImagePath
		}
	})
	// bottomVal.value=-100
	showCamera.value=false
	pzbtn.value=false
	uploadimgshow.value=false
}

// 发表评论上传图片
function commentimg(){
	console.log('图片')
}


// 关闭回复弹窗
function closeReplyPopup() {
	huifshow.value = false
	maskshow.value = false
	to_comment.value = ''
}

// 回复评论函数
function huifucomment(commentId) {
	maskshow.value = true
	huifshow.value = true
	to_comment_one_id.value = commentId
}

// 加载评论列表
function commentlist(goodsId) {
	uni.request({
		url: `http://127.0.0.1:8081/comment/comment_list?goods_id=${goodsId}`,
		method: "GET",
		header: {
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},

		success: (rescom) => {

			tabcomment.value = (rescom.data.data.items || []).map(item => {

				item.showReply = false
				item.replyList = []

				return item
			})
		}
	})
}

// 删除回复评论
function deletetocomment(tocommentid,onecommentid,url) {
	uni.showToast({
		title: '请稍候',
		icon: 'loading'
	})
	console.log(onecommentid)
	uni.showModal({
		title: '提示',
		content: '确定删除该回复吗？',

		success: (modalRes) => {

			if (!modalRes.confirm) return

			uni.request({
				url: `http://127.0.0.1:8081/comment/to_delete`,
				data:{
					"id":tocommentid,
					"commentid":onecommentid,
					"file_url":url
				},
				method: 'DELETE',

				header: {
					"content-type": "application/x-www-form-urlencoded",
					"satoken": uni.getStorageSync('tokenValue')
				},

				success: (res) => {

					uni.showToast({
						title: res.data.message || '删除成功',
						icon: 'success'
					})

					commentlist(id.value)
				}
			})
		}
	})
}

// 查看回复
function tocommentfun(item) {

	// 已展开 -> 收起
	if (item.showReply) {
		item.showReply = false
		return
	}

	uni.showLoading({
		title: '加载中'
	})

	uni.request({
		url: `http://127.0.0.1:8081/comment/to_comment_list?comment_id=${item.comment_id}&pageNum=1&pageSize=10`,
		method: 'GET',

		header: {
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},

		success: (res) => {

			item.replyList = res.data.data.items || []

			item.showReply = true
		},

		complete() {
			uni.hideLoading()
		}
	})
}

function huifucommentfun(file_id){
	uni.request({
		url: `http://127.0.0.1:8081/comment/to_comment?content=${encodeURIComponent(to_comment.value)}&comment_id=${to_comment_one_id.value}&file_id=${file_id}`,
		method: "PUT",
	
		header: {
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},
	
		success: (res) => {
	
			uni.showToast({
				title: res.data.message,
				icon: res.data.code == 0 ? 'success' : 'none',
				duration: 3000
			})
	
			if (res.data.code == 0) {
	
				to_comment.value = ''
	
				maskshow.value = false
				huifshow.value = false
	
				commentlist(id.value)
			}
		}
	})
}

// 回复评论实际函数
function huifucommentbtn() {
	uni.showToast({
		title: '请稍候',
		icon: 'loading'
	})
	if (!to_comment.value || to_comment.value.trim() === '') {

		uni.showToast({
			title: '回复内容不能为空',
			icon: 'none'
		})

		return
	}

	uni.showToast({
		title: '请稍候',
		icon: 'loading'
	})
	if(shangchaunimgurl.value && shangchaunimgurl.value.trim() !== ''){
		uni.uploadFile({
			url: "http://127.0.0.1:8081/file/upload",
			filePath: shangchaunimgurl.value, 
			name: "file",
			header: {
			  "Content-Type": "multipart/form-data",
			  "satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
			  console.log("上传成功", res);
			  let data = JSON.parse(res.data);
			  console.log(data.data)
			  huifucommentfun(data.data)
			  shangchaunimgurl.value=''
			  uni.showToast({ title: "上传成功" });
			},
			fail: (err) => {
			  console.error("上传失败", err);
			  uni.showToast({ title: "上传失败", icon: "none" });
			}
		  });
		}else{
			huifucommentfun('')
		}
	
}

// 放大查看图片
function zoom(imgurl){
	uni.previewImage({
	   urls: [`http://127.0.0.1:8081/images/${imgurl}`], 
	   showmenu: true, 
	   loop: true,  
	   fail: (err) => {
	     console.log('预览失败', err)
	   }
	 })
}

// 发表评论函数
function shijicomment(file_id){
	uni.request({
		url: `http://127.0.0.1:8081/comment/add?content=${encodeURIComponent(huivalue.value)}&goods_id=${id.value}&file_id=${file_id}`,
		method: 'GET',
	
		header: {
			"content-type": "application/x-www-form-urlencoded",
			"satoken": uni.getStorageSync('tokenValue')
		},
	
		success: (res) => {
	
			if (res.data.code == 0) {
	
				uni.showToast({
					title: '回复成功',
					icon: "success",
					duration: 3000
				})
	
				commentlist(id.value)
	
				huivalue.value = ''
	
			} else {
	
				uni.showToast({
					title: res.data.message,
					icon: 'none',
					duration: 3000
				})
	
				huivalue.value = ''
			}
		}
	})
}

// 发表图片及评论
function addcomment() {
	uni.showToast({
		title: '请稍候',
		icon: 'loading'
	})
	if (huivalue.value == '') {

		uni.showToast({
			title: '内容不能为空',
			icon: 'none',
			duration: 3000
		})

		return
	}
	if(shangchaunimgurl.value && shangchaunimgurl.value.trim() !== ''){
		uni.uploadFile({
		    url: "http://127.0.0.1:8081/file/upload",
		    filePath: shangchaunimgurl.value, 
		    name: "file",
		    header: {
		      "Content-Type": "multipart/form-data",
			  "satoken": uni.getStorageSync('tokenValue')
		    },
		    success: (res) => {
		      console.log("上传成功", res);
		      let data = JSON.parse(res.data);
		      console.log(data.data)
		      shijicomment(data.data)
			  shangchaunimgurl.value=''
		      uni.showToast({ title: "上传成功" });
		    },
		    fail: (err) => {
		      console.error("上传失败", err);
		      uni.showToast({ title: "上传失败", icon: "none" });
		    }
		  });
	}else{
		shijicomment('')
	}
	
}

// 删除评论
function deletecomment(commentid,url) {
	
	uni.showModal({
		title: '提示',
		content: '确定删除该评论吗？',

		success: (modalRes) => {
			uni.showToast({
				title: '请稍候',
				icon: 'loading'
			})
			if (!modalRes.confirm) return

			uni.request({
				url: `http://127.0.0.1:8081/comment/delete?id=${commentid}&file_url=${url}`,
				method: 'DELETE',

				header: {
					"content-type": "application/x-www-form-urlencoded",
					"satoken": uni.getStorageSync('tokenValue')
				},

				success: (res) => {

					uni.showToast({
						title: res.data.message,
						icon: "success",
						duration: 3000
					})

					commentlist(id.value)
				}
			})
		}
	})
}

// 购买
function goumai() {

	uni.navigateTo({
		url: `/pages/goodsaccount/goodsaccount?id=${goods.value.id}`
	})
}

onLoad((options) => {
	uni.showToast({
		title: '加载中',
		icon: 'loading'
	})
	id.value = options.id
	
	// 商品详情
	uni.request({
		url: `http://127.0.0.1:8081/goods/selectid?id=${options.id}`,
		method: 'GET',

		header: {
			'content-type': 'application/x-www-form-urlencoded',
			'satoken': uni.getStorageSync('tokenValue')
		},

		success: (res) => {

			goods.value = res.data.data

			// 用户信息
			uni.request({

				url: `http://127.0.0.1:8081/user/infoid?userid=${goods.value.create_id}`,
				method: 'POST',

				header: {
					'content-type': 'application/x-www-form-urlencoded',
					'satoken': uni.getStorageSync('tokenValue')
				},

				success: (res1) => {
					user.value = res1.data.data
				}
			})

			// 评论列表
			commentlist(goods.value.id)
		}
	})

	uni.request({
		url: 'http://127.0.0.1:8081/user/selectid',
		method: "GET",

		header: {
			'content-type': 'application/x-www-form-urlencoded',
			'satoken': uni.getStorageSync('tokenValue')
		},

		success: (res) => {
			nowuserid.value = res.data.data
		}
	})
})
</script>

<style scoped>
page{
	background: #f5f7fb;
}

.uploadimg{
	position: fixed;
	left: 50%;
	top: 50%;
	transform: translate(-50%, -50%);
	
	width: 78%;
	padding: 50rpx 40rpx;
	box-sizing: border-box;

	background: #ffffff;
	border-radius: 32rpx;

	box-shadow: 0 12rpx 40rpx rgba(0,0,0,0.12);

	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 30rpx;

	z-index: 999;
	animation: popupShow 0.25s ease;
}

.uploadimg text{
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-top: 20rpx;
	margin-bottom: 10rpx;
}

.uploadimg .uploadimgbtn{
	width: 100%;
	height: 88rpx;
	line-height: 88rpx;

	border-radius: 999rpx;
	border: none;

	font-size: 30rpx;
	font-weight: bold;

	color: #fff;

	background: linear-gradient(to right,#2979ff,#5aa6ff);
}

.uploadimg .uploadimgbtn::after{
	border: none;
}
.off {
	  width: 80rpx;
	  height: 80rpx;
	  border-radius: 50%;
	  background: pink;
	  font-size: 26rpx;
	  color: #666;
	  position: absolute;
	  top: 20rpx;
	  right: 20rpx;
	  display: flex;
	  align-items: center;
	  justify-content: center;
	  z-index: 999;
	}
.offcommentimg{
	position: fixed;
	bottom: 315px;
	border-radius: 50%;
	z-index: 9999;
	right: 3vh;
	background-color: pink;
}
.pz{
	width: 90px;
	height: 90px;
	line-height: 90px;
	border-radius: 50%;
	position: fixed;
	z-index: 9999;
	bottom: 100px;
	left: 45%;
	background-color: #9fe6fe;
}
.mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.6);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 997;
}

.container{
	padding: 24rpx;
	padding-bottom: 140rpx;
}

.shangchaunimgurl {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none;
  width: 85%;
  max-height: 350px;

  object-fit: contain;   /* 保持比例完整显示 */
  
  border-radius: 8px;
  z-index: 999;
}
.huifutanchu{
	width: 70%;
	background-color: white;
	border-radius: 30rpx;
	position: fixed;
	padding: 40rpx;
	top: 25vh;
	left: 50%;
	transform: translateX(-50%);
	z-index: 998;
	box-sizing: border-box;
}

.close-btn {
	z-index: 999;
	width: 56rpx;
	height: 56rpx;
	border-radius: 50%;
	background: pink;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	color: #666;
	position: absolute;
	top: 20rpx;
	right: 20rpx;
}

.deletecomment{
	position: absolute;
	top: 20rpx;
	right: 20rpx;
}

.reply-item{
	position: relative;
	margin-bottom: 16rpx;
	line-height: 40rpx;
	padding-right: 60rpx;
}

.deletetocomment{
	position: absolute;
	right: 0;
	top: 0;
}

.card{
	background: #fff;
	border-radius: 24rpx;
	margin-bottom: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.05);
}

.img-wrap{
	height: 560rpx;
	background: #f5f5f5;
}

.goods-img{
	width: 100%;
	height: 100%;
}

.info-box{
	padding: 28rpx;
}

.goods-name{
	font-size: 40rpx;
	font-weight: bold;
	color: #333;
	margin: 20rpx 0;
	display: block;
	line-height: 58rpx;
}

.price-stock{
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.price{
	font-size: 54rpx;
	color: #ff4d4f;
	font-weight: bold;
}

.stock-box{
	background: #f4f6fa;
	padding: 10rpx 20rpx;
	border-radius: 999rpx;
}

.stock-text{
	font-size: 24rpx;
	color: #666;
}

.section{
	margin-top: 30rpx;
}

.section-title{
	display: flex;
	align-items: center;
	margin-bottom: 18rpx;
}

.line{
	width: 8rpx;
	height: 30rpx;
	background: #2979ff;
	border-radius: 20rpx;
	margin-right: 12rpx;
}

.section-title text{
	font-size: 30rpx;
	font-weight: bold;
}

.desc{
	background: #fafafa;
	padding: 20rpx;
	border-radius: 16rpx;
	line-height: 44rpx;
	color: #666;
	font-size: 28rpx;
	display: block;
}

.time{
	color: #999;
	font-size: 26rpx;
}

.buy-btn{
	margin-top: 40rpx;
	border-radius: 999rpx;
	height: 88rpx;
	line-height: 88rpx;
	font-size: 30rpx;
	font-weight: bold;
	background: linear-gradient(to right,#2979ff,#5aa6ff);
	border: none;
}

.buy-btn::after{
	border:none;
}

.user-card{
	padding: 28rpx;
}

.user-title{
	font-size: 34rpx;
	font-weight: bold;
}

.user-info{
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 24rpx;
}

.left-user{
	display: flex;
	align-items: center;
}

.avatar{
	width: 110rpx;
	height: 110rpx;
	border-radius: 50%;
	margin-right: 20rpx;
}

.nickname{
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.tips{
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
	display: block;
}

.contact-btn{
	background: #eef4ff;
	color: #2979ff;
	border: none;
	border-radius: 999rpx;
	padding: 0 26rpx;
}

.comment-card{
	padding: 28rpx;
	margin-bottom: 60px;
}

.comment-title{
	font-size: 34rpx;
	font-weight: bold;
	color: #333;
}

.comment-item{
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
	position: relative;
}

.comment-top{
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.comment-user-box{
	display: flex;
	align-items: center;
	position: relative;
	width: 100%;
}

.comment-avatar{
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	margin-right: 18rpx;
}

.comment-user-info{
	display: flex;
	flex-direction: column;
}

.comment-user{
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.comment-time{
	font-size: 22rpx;
	color: #999;
	margin-top: 6rpx;
}

.comment-content text{
	font-size: 35rpx;
	color: #555;
	line-height: 44rpx;
	margin-top: 18rpx;
	padding-left: 98rpx;
	word-break: break-all;
}

/* 评论图片样式 */
.comment-pic {
	width: 320rpx !important;
	max-height: 320rpx !important;
	margin-top: 16rpx;
	border-radius: 16rpx;
	margin-left: 40px;
	background-color: #f5f5f5;
}
.comment-to-pic{
	width: 100rpx !important;
	max-height: 100rpx !important;
	margin-top: 12rpx;
	border-radius: 12rpx;
	background-color: #f5f5f5;
	display: block;
}
.reply-btn-box{
	padding-left: 98rpx;
	margin-top: 18rpx;
}

.reply-btn{
	background: #f2f6ff;
	color: #2979ff;
	border: none;
	border-radius: 999rpx;
	font-size: 22rpx;
}

.reply-list{
	margin-top: 20rpx;
	margin-left: 98rpx;
	background: #f8f9fc;
	padding: 20rpx;
	border-radius: 18rpx;
}

.reply-name{
	color: #2979ff;
	font-size: 26rpx;
	font-weight: bold;
}

.reply-content{
	font-size: 26rpx;
	color: #555;
	word-break: break-all;
}

.empty-box,
.empty-reply{
	text-align: center;
	color: #999;
	font-size: 26rpx;
	padding: 30rpx 0;
}

/* 发布评论输入框 */
.addcomment{
	position: fixed;
	left: 20rpx;
	right: 20rpx;
	bottom: 5vh;
	height: 96rpx;
	background: #ffffff;
	border-radius: 999rpx;
	display: flex;
	align-items: center;
	padding: 0 14rpx 0 26rpx;
	box-shadow: 0 6rpx 24rpx rgba(0,0,0,0.08);
	z-index: 995;
}

/* 输入框 */
.addcomment input{
	flex: 1;
	height: 72rpx;
	background: #f5f7fb;
	border-radius: 999rpx;
	padding: 0 28rpx;
	font-size: 28rpx;
	color: #333;
}

/* placeholder */
.addcomment input::placeholder{
	color: #999;
	font-size: 26rpx;
}

/* 提交按钮 */
.addcomment button{
	width: 150rpx;
	height: 72rpx;
	line-height: 72rpx;
	margin-left: 18rpx;
	border-radius: 999rpx;
	border: none;
	background: linear-gradient(to right,#2979ff,#5aa6ff);
	color: #fff;
	font-size: 28rpx;
	font-weight: bold;
	padding: 0;
}

/* 去除按钮边框 */
.addcomment button::after{
	border: none;
}

/* 弹窗标题 */
.reply-title{
	display: block;
	text-align: center;
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}

/* 回复输入框 */
.huifutanchu input{
	width: 100%;
	height: 80rpx;
	border: 2rpx solid #eee;
	border-radius: 16rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	box-sizing: border-box;
	margin-bottom: 30rpx;
}

/* 回复按钮 */
.huifutanchu .tocommentbtn{
	width: 100%;
	height: 88rpx;
	line-height: 88rpx;
	border-radius: 16rpx;
	background: linear-gradient(to right, #2979ff, #5aa6ff);
	color: #fff;
	font-size: 30rpx;
	font-weight: bold;
	border: none;
}
.tocommentimg{
	width: 50%;
	height: 50px;
	border-radius: 5px;
	background-color: #f4f9d7;
}
</style>