<template>
	<view class="container">

		<view class="title">首页</view>

		<!-- 搜索区域 -->
		<view class="search">

			<input
				class="search-input"
				type="text"
				placeholder="请输入商品名称"
				v-model="searchText"
			/>

			<button
				class="search-btn"
				size="mini"
				@click="search"
			>
				搜索
			</button>

			<button
				class="all-btn"
				size="mini"
				@click="goodslist"
			>
				全部
			</button>

		</view>

		<!-- 商品列表 -->
		<view class="goods-list">

			<view
				class="goods-item"
				v-for="item in tabs"
				:key="item.id"
				@click="todetails(item.id)"
			>

				<image
					class="goods-img"
					mode="aspectFill"
					:src="`http://127.0.0.1:8081/images/${item.file_url}`"
				></image>

				<view class="goods-content">

					<text class="goods-name">
						{{ item.goods_name }}
					</text>

					<text class="goods-price">
						￥{{ item.price }}
					</text>

					<text class="goods-stock">
						库存：{{ item.stock }}
					</text>

					<text class="goods-desc">
						{{
							item.description.length > 15
								? item.description.slice(0, 15) + '...'
								: item.description
						}}
					</text>

				</view>

			</view>

		</view>

	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const tabs = ref([])
const searchText = ref('')

function search() {

	if (!searchText.value.trim()) {
		goodslist()
		return
	}

	uni.request({
		url: `http://127.0.0.1:8081/goods/search?name=${searchText.value}`,
		method: 'GET',
		header: {
			'content-type': 'application/x-www-form-urlencoded',
			'satoken': uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			tabs.value = res.data.data || []
		}
	})
}

function goodslist() {

	uni.request({
		url: 'http://127.0.0.1:8081/goods/goodslist',
		method: 'GET',
		header: {
			'content-type': 'application/x-www-form-urlencoded',
			'satoken': uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			tabs.value = res.data.data || []
		}
	})
}

function todetails(id) {

	uni.navigateTo({
		url: `/pages/goodsdetails/goodsdetails?id=${id}`
	})
}

onShow(() => {
	goodslist()
})
</script>

<style scoped>

page{
	background:#f5f5f5;
}

.container{
	padding:20rpx;
}

.title{
	font-size:40rpx;
	font-weight:bold;
	color:#333;
	margin-bottom:20rpx;
}


.search{
	display:flex;
	align-items:center;
	gap:10rpx;
	margin-bottom:20rpx;
}

.search-input{
	flex:1;
	height:70rpx;
	background:#fff;
	border:1rpx solid #ddd;
	border-radius:12rpx;
	padding:0 20rpx;
	font-size:28rpx;
	box-sizing:border-box;
}

.search-btn,
.all-btn{
	width:120rpx;
	height:70rpx;
	line-height:70rpx;
	padding:0;
	margin:0;
	font-size:26rpx;
	border:none;
}

.search-btn{
	background:#007aff;
	color:#fff;
}

.all-btn{
	background:#ff9797;
	color:#fff;
}

.goods-list{
	display:flex;
	flex-wrap:wrap;
	justify-content:space-between;
}

.goods-item{
	width:48%;
	background:#fff;
	border-radius:20rpx;
	overflow:hidden;
	margin-bottom:20rpx;
	box-shadow:0 4rpx 12rpx rgba(0,0,0,0.08);
}

.goods-img{
	width:100%;
	height:300rpx;
	background:#eee;
}

.goods-content{
	padding:20rpx;
	display:flex;
	flex-direction:column;
}

.goods-name{
	font-size:30rpx;
	font-weight:bold;
	color:#333;
	margin-bottom:10rpx;

	overflow:hidden;
	text-overflow:ellipsis;
	white-space:nowrap;
}

.goods-price{
	font-size:32rpx;
	color:#ff4d4f;
	font-weight:bold;
	margin-bottom:10rpx;
}

.goods-stock{
	font-size:24rpx;
	color:#888;
	margin-bottom:10rpx;
}

.goods-desc{
	font-size:24rpx;
	color:#666;
	line-height:36rpx;
	word-break:break-all;
}

</style>