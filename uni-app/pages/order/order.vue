<template>
  <view class="page">
	  <view class="saixuan">
	  	<view class="saixuanbox" @click="orderlistfun(true)" >
	  		全部
	  	</view>
		<view class="saixuanbox" @click="orderlistfun(1)">
			待发货
		</view>
		<view class="saixuanbox" @click="orderlistfun(2)">
			已发货
		</view>
		<view class="saixuanbox" @click="orderlistfun(3)">
			待收货
		</view>
		<view class="saixuanbox" @click="orderlistfun(4)">
			已收货
		</view>
		<view class="saixuanbox" @click="orderlistfun(5)">
			已完成
		</view>
	  </view>
    <view v-if="orderlist && orderlist.length" class="order-list">
      <view class="order-item" v-for="(item, index) in orderlist" :key="item.order_id" @click="ordertiaozhuan(item.order_id)">
        
        <!-- 商家信息 -->
        <view class="merchant">
          <image 
            class="merchant-img"
            mode="aspectFill" 
            :src="`http://127.0.0.1:8081/images/${item.file_url}`">
          </image>
          <text class="merchant-name">{{ item.nickname }}</text>
        </view>

        <!-- 商品信息 -->
        <view class="goods">
          <image
            class="goods-img"
            mode="aspectFill" 
            :src="`http://127.0.0.1:8081/images/${item.goods_file_url}`">
          </image>
          <view class="goods-info">
            <text class="goods-name">{{ item.goods_name }}</text>
            <text class="goods-quantity">数量: {{ item.buyNum }}</text>
			<br /><text style="font-size: 10px;">订单编号:{{item.order_id}}</text>
			<br /><text style="font-size: 12px; color:blueviolet;">{{item.fahuo==1?"快递发货":"自提"}}</text>
          </view>
          <text class="total-price">¥{{ item.total_price.toFixed(2) }}</text>
        </view>
		<view class="order-state" :class="`state-${item.state}`">
			{{getOrderStatus(item.state)}}
		</view>
		<view class="fahuo" v-if="role=='user'" @click.stop="contactShop(item.merchant_id)">
			联系商家
		</view>
		<view class="fahuo" v-if="role=='merchant'" @click.stop="contactShop(item.user_id)">
			联系顾客
		</view>
		<view class="fahuo" v-if="item.state==3 && role=='user'" @click.stop="shouhuo(item.order_id)">
			确认收货
		</view>
		<view class="fahuo" v-if="item.state==1 && role=='merchant'" @click.stop="fahuofun(item.order_id,item.fahuo==1)">
			去发货
		</view>
		
      </view>
    </view>

    <view v-else class="no-order">暂无订单</view>
	<!-- 物流编号弹出框 -->
	<!-- 遮罩 -->
	<view class="mask" v-if="wuliuidtanc" @click="wuliuidtanc=false"></view>
	<view class="wuliuid" v-if="wuliuidtanc">
		 <view @click="wuliuidtanc=false" class='off'>X</view>
		<input type="text" placeholder="请输入物流编号" v-model="wuliuid"/>
		<button @click="fahuosjfun(orderchuanru_id,wuliuid,true)">确认发货</button>
	</view>
  </view>
  
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
const texts = ref('订单页面')
const orderlist = ref([])
const role=ref('')
const wuliuid=ref()
const wuliuidtanc=ref(false)
const orderchuanru_id=ref()
const xianshistate=ref()
function orderlistfun(states){
	uni.request({
		url:'http://127.0.0.1:8081/user/role',
		method:'GET',
		header: {
			"satoken": uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			console.log(res.data)
			role.value=res.data.data
			getOrderList(states)
			
		}
	})
}

function shouhuo(order_id){
	if(!order_id){
		uni.showToast({
			title:"未传入订单号",
			icon:'error'
		})
		return
	}
	
	uni.showModal({
	    title: '提示',          // 标题
	    content: '确认已经收到货了吗？',
		confirmText:"收到了",
		cancelText:"还没有",
		cancelColor:"#ccc",
	    success(res){
			if(res.confirm){
				uni.request({
					url:`http://127.0.0.1:8081/order/user/updatestate?order_id=${order_id}`,
					method:"PUT",
					header: {
						"satoken": uni.getStorageSync('tokenValue')
					},
					success: (res) => {
						if(res.data.code==0){
							uni.showToast({
								title:res.data.data,
								icon:'success',
								duration:2000
							})
							orderlistfun(3)
						}else{
							uni.showToast({
								title:"发生错误",
								icon:'error'
							})
						}
					}
				})
			}
		}
	});
	
}

const getOrderList = (states) => {
  const token = uni.getStorageSync('tokenValue')
  
  if (!token) {
    texts.value = '您还没有登录,请先登录后重试'
    orderlist.value = []
    return
  }
  uni.request({
    url: `http://127.0.0.1:8081/order/${role.value}/orderlist`,
    method: "GET",
    header: {
      "satoken": token
    },
    success: (res) => {
      if(res.data.data.length === 0){
        texts.value = "暂无订单"
      }else{
		  if(states===true){
			  orderlist.value=[]
			  orderlist.value=res.data.data
		  }else{
			  orderlist.value=[]
			  for(let item of res.data.data){
				  if(item.state==states){
					orderlist.value.push(item)
				  }
			  }
		  }
			  
	  }
    },
    fail: () => {
      texts.value = "加载失败"
    }
  })
}
// 发货实际函数
	
function fahuosjfun(order_id,wuliuid,bools){
	if(bools){
		if(wuliuid==null ||wuliuid==''){
			uni.showToast({
				title:"物流id不能为空",
				icon:'error',
				duration:3000
			})
			return
		}
	}
	uni.showModal({
		title:"提示",
		content:"确认要进行发货吗?",
		confirmText:"确认",
		cancelText:"取消",
		success(res) {
			if(res.confirm){
				uni.request({
					url:`http://127.0.0.1:8081/order/merchant/updatestate?order_id=${order_id}&express=${wuliuid}`,
					method:"PUT",
					header: {
						"satoken": uni.getStorageSync('tokenValue')
					},
					success: (res) => {
						if(res.data.code==0){
							wuliuidtanc.value=false
							orderlistfun(1)
							uni.showToast({
								title:'发货成功',
								icon:'success',
								duration:3000
							})
						}else{
							uni.showToast({
								title:res.data.message,
								icon:'error',
								duration:3000
							})
						}
					}
				})
			}
		}
	})
}

// 发货按钮函数
function fahuofun(order_id,bools){
	wuliuid.value=''
	if(bools){
		wuliuidtanc.value=bools
		orderchuanru_id.value=order_id
	}else{
		fahuosjfun(order_id,'',false)
	}
	
}

// 联系商家/顾客
function contactShop(userId) {
	if (!userId) return
	uni.request({
		url: `http://127.0.0.1:8081/user/infoid?userid=${userId}`,
		method: 'GET',
		header: {
			'satoken': uni.getStorageSync('tokenValue')
		},
		success: (res) => {
			if (res.data.code === 0) {
				const username = res.data.data.username
				// 确保 currentUser 存在
				if (!uni.getStorageSync('currentUser')) {
					uni.request({
						url: 'http://127.0.0.1:8081/user/info',
						method: 'GET',
						header: {
							'satoken': uni.getStorageSync('tokenValue')
						},
						success: (r) => {
							if (r.data.code === 0) {
								const u = r.data.data
								uni.setStorageSync('currentUser', {
									id: u.id, username: u.username,
									nickname: u.nickname, avatar: u.file_url,
									roleCode: uni.getStorageSync('role') || 'user'
								})
							}
							uni.navigateTo({ url: '/pages/im/chat?username=' + encodeURIComponent(username) })
						}
					})
				} else {
					uni.navigateTo({ url: '/pages/im/chat?username=' + encodeURIComponent(username) })
				}
			}
		}
	})
}

function getOrderStatus(status) {
  switch (String(status).trim()) {
	case '0': case '未支付': return '未支付';
	case '1': case '待发货': return '待发货';
	case '2': case '已发货': return '已发货';
	case '3': case '待收货': return '待收货';
	case '4': case '已收货': return '已收货';
	case '5': case '已完成': return '已完成';
	case '6': case '已取消': return '已取消';
	default: return '未知状态';
  }
}
function ordertiaozhuan(order_id){
	uni.navigateTo({
		url: `/pages/ordercount/ordercount?order_id=${order_id}`
	})
}
onShow(() => {
  
  orderlistfun(true)
})
</script>

<style scoped>
.page {
  padding: 15rpx;
  background-color: #f7f8fa;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 筛选栏 */
.saixuan {
  display: flex;
  justify-content: space-between;
  gap: 8rpx;
  margin-bottom: 20rpx;
}
.saixuanbox {
  flex: 1;
  height: 60rpx;
  line-height: 60rpx;
  background-color: #ffffff;
  color: #333;
  border-radius: 30rpx;
  font-size: 24rpx;
  text-align: center;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}
.mask {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.45);
		z-index: 9;
	}
.saixuanbox:active {
  background-color: #e6f7ff;
  color: #1677ff;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.order-item {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 25rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

/* 商家信息 */
.merchant {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}
.merchant-img {
  width: 70rpx;
  height: 70rpx;
  border-radius: 50%;
  margin-right: 15rpx;
  object-fit: cover;
}
.merchant-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #222;
}

/* 商品信息 */
.goods {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}
.goods-img {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  object-fit: cover;
}
.goods-info {
  flex: 1;
  margin-left: 20rpx;
}
.goods-name {
  font-size: 28rpx;
  color: #333;
  line-height: 1.4;
  display: block;
  margin-bottom: 8rpx;
}
.goods-quantity {
  font-size: 11px;
  color: #999;
}
.total-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #fa4d4d;
}

/* 订单状态 */
.order-state {
  font-size: 26rpx;
  font-weight: 500;
  padding: 10rpx 20rpx;
  border-radius: 30rpx;
  display: inline-block;
  text-align: center;
  line-height: 1.3;
  background: #f5f7fa;
  color: #666;
}
.state-0 { color: #ff7d00; background: #fff5e6; }
.state-1 { color: #1677ff; background: #e6f7ff; }
.state-2 { color: #722ed1; background: #f9f0ff; }
.state-3 { color: #fa8c16; background: #fff7e6; }
.state-4 { color: #52c41a; background: #f6ffed; }
.state-5 { color: #00b42a; background: #e6ffec; }

/* 发货按钮 */
.fahuo {
  background-color: #1677ff;
  color: #fff;
  padding: 12rpx 24rpx;
  border-radius: 10rpx;
  font-size: 26rpx;
  text-align: center;
  margin-top: 15rpx;
  display: inline-block;
}

/* 暂无订单 */
.no-order {
  text-align: center;
  color: #999;
  font-size: 28rpx;
  margin-top: 200rpx;
}

/* 物流弹窗 */
.wuliuid {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.2);
  z-index: 999;
}

.wuliuid input {
  width: 100%;
  height: 70rpx;
  border: 2rpx solid #eee;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  margin-bottom: 30rpx;
  box-sizing: border-box;
}
.wuliuid button {
  width: 100%;
  height: 70rpx;
  background-color: #1677ff;
  color: #fff;
  border: none;
  border-radius: 10rpx;
  font-size: 28rpx;
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
</style>