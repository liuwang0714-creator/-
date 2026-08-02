<template>
	<view class="page">
		
		<!-- 顶部标题 -->
		<view class="header">
			<text class="header-title">订单详情</text>
			<text class="order-no">订单号：{{ order_id }}</text>
		</view>

		<!-- 内容 -->
		<view class="container" v-if="ordercounttab">

			<!-- 用户信息 -->
			<view class="card merchant-card">
				<view class="section-title">
					<text>{{role=='user'?'商家信息':'买家信心'}}</text>
				</view>

				<view class="merchant-info">
					<image
						class="merchant-img"
						mode="aspectFill"
						:src="`http://127.0.0.1:8081/images/${ordercounttab.file_url}`">
					</image>

					<view class="merchant-right">
						<text class="merchant-name">
							{{ ordercounttab.nickname }}
						</text>
					</view>
				</view>
			</view>

			<!-- 商品信息 -->
			<view class="card goods-card">
				<view class="section-title">
					<text>商品信息</text>
				</view>

				<view class="goods-info">
					<image
						class="goods-img"
						mode="aspectFill"
						:src="`http://127.0.0.1:8081/images/${ordercounttab.goods_file_url}`">
					</image>

					<view class="goods-right">
						<text class="goods-name">
							{{ ordercounttab.goods_name }}
						</text>

						<text class="goods-order-id">
							订单编号：{{ ordercounttab.order_id }}
						</text>
					</view>
				</view>
			</view>

			<!-- 收货信息 -->
			<view class="card address-card" v-if="ordercounttab.fahuo==1">
				<view class="section-title">
					<text>收货信息</text>
				</view>

				<view class="info-item">
					<text class="label">收货人</text>
					<text class="value">{{ ordercounttab.name }}</text>
				</view>

				<view class="info-item">
					<text class="label">联系电话</text>
					<text class="value">{{ ordercounttab.phone }}</text>
				</view>

				<view class="info-item align-start">
					<text class="label">收货地址</text>
					<text class="value address">
						{{ ordercounttab.addr_detail }}
					</text>
				</view>
			</view>

			<!-- 订单信息 -->
			<view class="card order-card">
				<view class="section-title">
					<text>订单信息</text>
				</view>

				<view class="info-item">
					<text class="label">创建时间</text>
					<text class="value">
						{{ ordercounttab.create_time }}
					</text>
				</view>

				<view class="info-item align-start">
					<text class="label">订单备注</text>
					<text class="value beizhu">
						{{ ordercounttab.beizhu || '暂无备注' }}
					</text>
				</view>
			</view>

		</view>
	</view>
</template>

<script setup>
	import { onLoad } from '@dcloudio/uni-app'
	import { ref } from 'vue'

	const order_id = ref()
	const ordercounttab = ref()
	const role=ref()

	onLoad((options) => {
		order_id.value = options.order_id
		uni.request({
			url:'http://127.0.0.1:8081/user/role',
			method:'GET',
			header: {
				"satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				role.value=res.data.data
				uni.request({
					url: `http://127.0.0.1:8081/order/${res.data.data}/orderidselect?order_id=${options.order_id}`,
					method: 'GET',
					header: {
						"content-type": "application/x-www-form-urlencoded",
						"satoken": uni.getStorageSync('tokenValue')
					},
					success: (res) => {
						if (res.data.code == 0) {
							ordercounttab.value = res.data.data
						}
					}
				})
			}
		})
		
	})
</script>

<style scoped>
	page {
		background: #f5f6fa;
	}

	.page {
		padding: 24rpx;
		box-sizing: border-box;
	}

	/* 顶部 */
	.header {
		background: linear-gradient(135deg, #4f8cff, #6ea8ff);
		border-radius: 24rpx;
		padding: 36rpx 30rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 8rpx 20rpx rgba(79, 140, 255, 0.2);
	}

	.header-title {
		display: block;
		font-size: 40rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 12rpx;
	}

	.order-no {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.9);
	}

	/* 卡片 */
	.card {
		background: #fff;
		border-radius: 24rpx;
		padding: 28rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 6rpx 18rpx rgba(0, 0, 0, 0.05);
	}

	.section-title {
		margin-bottom: 24rpx;
		padding-left: 16rpx;
		border-left: 8rpx solid #4f8cff;
	}

	.section-title text {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	/* 商家、商品信息 */
	.merchant-info,
	.goods-info {
		display: flex;
		align-items: center;
	}

	.merchant-img,
	.goods-img {
		width: 140rpx;
		height: 140rpx;
		border-radius: 20rpx;
		background: #f3f3f3;
		flex-shrink: 0;
	}

	.merchant-right,
	.goods-right {
		margin-left: 24rpx;
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.merchant-name,
	.goods-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #222;
		line-height: 46rpx;
		margin-bottom: 12rpx;
	}

	.goods-order-id {
		font-size: 24rpx;
		color: #999;
	}

	/* 信息项 */
	.info-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 18rpx 0;
		border-bottom: 1rpx solid #f2f2f2;
	}

	.info-item:last-child {
		border-bottom: none;
	}

	.align-start {
		align-items: flex-start;
	}

	.label {
		width: 150rpx;
		font-size: 28rpx;
		color: #666;
		flex-shrink: 0;
	}

	.value {
		flex: 1;
		font-size: 28rpx;
		color: #222;
		text-align: right;
		line-height: 42rpx;
		word-break: break-all;
	}

	.address,
	.beizhu {
		text-align: left;
		padding-left: 20rpx;
		color: #444;
	}

	/* 适配长文字 */
	text {
		word-break: break-all;
	}
</style>