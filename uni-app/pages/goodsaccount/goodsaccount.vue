<template>
	<view class="page">
		<!-- 商品名称 -->
		<text>{{goodstab.goods_name}}</text>
		<view class="header">
			<view>
				<text class="title">订单结算</text>
			</view>
			<view class="header-icon">🛒</view>
		</view>

		<!-- 收货方式选择 -->
		<view class="radio-group">
			请选择收货方式
			<radio-group @change="onDeliveryChange">
				<label class="radio-item">
					<radio value="1" :checked="deliveryType == 1" /> 快递配送
				</label>
				<label class="radio-item">
					<radio value="2" :checked="deliveryType == 2" /> 到店自提
				</label>
			</radio-group>
		</view>

		<!-- 快递配送显示地址 -->
		<view class="address-card" @click="tanchu" v-if="deliveryType==1">
			<view class="address-icon">📍</view>
			<view class="address-content">
				<view v-if="!jutiaddr" class="empty-box">
					<text class="address-title">请选择收货地址</text>
					<text class="address-tip">请填写收货人、手机号和详细地址</text>
				</view>
				<view v-else>
					<view class="user-row">
						<text class="name">{{ jutiaddr.name }}</text>
						<text class="phone">{{ jutiaddr.phone }}</text>
					</view>
					<view class="detail-row">{{ jutiaddr.addr }}</view>
				</view>
			</view>
			<uni-icons type="right" size="20"></uni-icons>
		</view>

		<!-- 购买数量 -->
		<view style="margin-bottom: 20px;">
			<uni-number-box :min="0" :max="goodstab.stock" v-model="num" ></uni-number-box>
		</view>

		<!-- 优惠券入口 + 选中后展示已选优惠券 -->
		<view class="youhui" @click="openYouHuiPop">
			<view>
				<text>请选择优惠券</text>
				<text v-if="selectCoupon.name" class="sel-coupon-text">已选：{{selectCoupon.name}} 减{{selectCoupon.jine}}元</text>
			</view>
			<uni-icons type="right" size="30"></uni-icons>
		</view>
		<!-- 优惠券弹窗 -->
		<view class="youhuitanchu" v-if="showYouHui">
			<view class="mask" @click="closeYouHuiPop"></view>
			<view class="youhui-pop-box">
				<button class="off" @click="closeYouHuiPop">X</button>
				<view class="hoveyouhui" v-if="youhuitab.length>=1">
					<!-- 点击选中优惠券 -->
					<view class="youhuilist" v-for="(item,index) in youhuitab" :key="index" @click="chooseCoupon(item.id)">
						<text>{{item.name}}</text>
						<text>{{item.jine}}元</text>
					</view>
				</view>
				<view class="noyouhui" v-else>您还没有优惠券</view>
			</view>
		</view>
		
		<!-- 备注入口 + 填写后页面展示备注 -->
		<view class="beizhu" @click="openBeiZhuPop">
			<view>
				<text>填写备注</text>
				<!-- 填写备注后页面可见 -->
				<text v-if="userBeizhu" class="sel-beizhu-text">备注：{{userBeizhu}}</text>
			</view>
			<uni-icons type="right" size="30"></uni-icons>
		</view>

		<!-- 备注弹窗 -->
		<view class="beizhutanchu" v-if="showBeiZhu">
			<view class="mask" @click="closeBeiZhuPop"></view>
			<view class="beizhu-pop-box">
				<button class="off" @click="closeBeiZhuPop">X</button>
				<input type="text" placeholder="请输入备注" v-model="tempBeizhu" />
				<button @click="saveBeizhu">确认</button>
			</view>
		</view>

		<!-- 底部价格+结算 -->
		<button class="total-price-box" @click="submitOrder" :disabled="jiesuanbtn" >
			<text>去结算</text>
			<!-- 可自动减去优惠券金额 -->
			<text>实付：￥{{getTotalPrice()}}</text>
		</button>

		<!-- 地址选择遮罩 -->
		<view class="mask" v-if="addr" @click="addr=false"></view>
		<!-- 地址弹窗 -->
		<view class="popup" v-if="addr">
			<view class="popup-header">
				<text class="popup-title">选择收货地址</text>
				<view class="close-btn" @click="addr=false">✕</view>
			</view>
			<scroll-view scroll-y class="addr-list">
				<view class="addr-item" v-for="(item,index) in tab_addr" :key="item.id">
					<view class="item-top">
						<view class="user-row">
							<text class="name">{{ item.name }}</text>
							<text class="phone">{{ item.phone }}</text>
						</view>
						<view class="detail-row">📍 {{ item.addr }}</view>
						<uni-icons type="trash-filled" size="30" color="red" class="deleteaddr" @click="delteaddr(item.id)"></uni-icons>
					</view>
					<button class="select-btn" @click="seladdr(index)">选择地址</button>
				</view>
			</scroll-view>
			<view class="addaddr" @click="addaddr">
				<text>+新增地址</text>
			</view>
		</view>
		<view class="mask" v-if="paytanc" @click="paytanc=false"></view>
		<view class="paytanc" v-if="paytanc">
			<view class="pay-header">
				<text class="pay-title">确认支付</text>
				<button class="pay-close" @click="paytanc=false">×</button>
			</view>
			<view class="pay-info">
				<view class="pay-row">
					<text class="label">订单编号</text>
					<text class="value">{{order_id}}</text>
				</view>
				<view class="pay-row">
					<text class="label">支付金额</text>
					<text class="price">￥{{ordertab.total_price}}</text>
				</view>
				<view class="pay-row">
					<text class="label">创建时间</text>
					<text class="value">{{ordertab.create_time}}</text>
				</view>
				<view class="pay-row">
					<text class="label">订单状态</text>
					<text class="value status">{{getOrderStatus(ordertab.state)}}</text>
				</view>
			</view>
			<view class="pay-addr" v-if="ordertab.name">
				<view class="addr-row"><text>姓名：</text><text>{{ordertab.name}}</text></view>
				<view class="addr-row"><text>电话：</text><text>{{ordertab.phone}}</text></view>
				<view class="addr-row"><text>地址：</text><text>{{ordertab.addr_detail}}</text></view>
				<view class="addr-row">
					<text>发货方式：</text>
					<text>{{ordertab.fahuo==1 ? "快递发货" : "线下交易"}}</text>
				</view>
			</view>
			<view class="pay-note" v-if="ordertab.beizhu">
				<text>备注：{{ordertab.beizhu}}</text>
			</view>
			<view class="pay-type-box">
				<text class="pay-type-title">选择支付方式</text>
				<radio-group @change="paychange">
					<label class="pay-item">
						<radio value="1" :checked="paytype == 1" />
						<uni-icons type="weixin" size="28"></uni-icons>
						<text>微信支付</text>
					</label>
					<label class="pay-item">
						<radio value="2" :checked="paytype == 2" />
						<image src="/static/images/支付宝.png" style="width:28px;height:28px"></image>
						<text>支付宝支付</text>
					</label>
				</radio-group>
			</view>
			<button class="pay-now-btn" @click="payfun(ordertab.order_id)">立即支付</button>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	import uniNumberBox from '@dcloudio/uni-ui/lib/uni-number-box/uni-number-box.vue'

	// 订单id
	const id = ref('')
	// 地址弹窗状态
	const addr = ref(false)
	// 地址列表
	const tab_addr = ref([])
	// 选中的具体地址
	const jutiaddr = ref(null)
	// 商品信息
	const goodstab = ref({
		goods_name: '',
		price: 0,
		stock: 0
	})
	// 订单信息
	const ordertab=ref(
	{
		"total_price":4
	})
	// 订单编号
	const order_id=ref()
	const paytanc=ref(false)
	// 购买数量（保留原功能不变）
	const num = ref(1)
	// 收货方式 1快递 2自提
	const deliveryType = ref(1)
	// 支付方式 1微信 2 支付宝
	const paytype=ref(1)
	
	const role=ref()
	// 结算按钮
	const jiesuanbtn=ref(false)
	const youhuitab = ref([]) // 优惠券列表
	const showYouHui = ref(false) // 优惠券弹窗显示隐藏
	const selectCoupon = ref(0) // 选中的优惠券

	const showBeiZhu = ref(false) // 备注弹窗显示隐藏
	const tempBeizhu = ref('') // 临时输入备注
	const userBeizhu = ref('') // 最终保存展示的备注

	// 切换收货方式
	function onDeliveryChange(e) {
		deliveryType.value = e.detail.value
	}
	// 切换支付方式
	function paychange(e) {
		paytype.value = e.detail.value
	}
	// 判断订单状态
	function getOrderStatus(status) {
	  switch (String(status).trim()) {
	    case '0': case '未支付': return '未支付';
	    case '1': case '待发货': return '待发货';
	    case '2': case '已发货': return '已发货';
	    case '3': case '待收货': return '待收货';
	    case '4': case '已收货': return '已收货';
	    case '5': case '已完成': return '已完成';
	    default: return '未知状态';
	  }
	}
	
	// 获取地址列表
	function addrlistfin() {
		uni.request({
			url: 'http://127.0.0.1:8081/address/list',
			method: 'PUT',
			header: {
				"content-type": "application/x-www-form-urlencoded",
				"satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				tab_addr.value = res.data.data
			}
		})
	}

	// 删除地址
	function delteaddr(id) {
		uni.request({
			url: `http://127.0.0.1:8081/address/delete?id=${id}`,
			method: 'DELETE',
			header: {
				"content-type": "application/x-www-form-urlencoded",
				"satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				addrlistfin()
				uni.showToast({title:'删除成功'})
			}
		})
	}
	// 支付函数
	function payfun(order_id){		
		let paytypes=''
		if(paytype.value==1){
			paytypes='wx'
		}else{
			paytypes='zfb'
		}
		uni.request({
			url:`http://127.0.0.1:8081/pay/${paytypes}?order_id=${order_id}`,
			method:"GET",
			header: {
				"content-type": "application/x-www-form-urlencoded",
				"satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				console.log(res)
				if(res.data.code==0){
					uni.showToast({
						title:res.data.data,
						icon:'success'
					})
					uni.switchTab({
						url:"/pages/order/order"
					})
				}else{
					uni.showToast({
						title:res.data.data,
						icon:'error'
					})
				}
			}
		})
	}
	// 选择地址
	function seladdr(index) {
		jutiaddr.value = tab_addr.value[index]
		addr.value = false
		uni.showToast({
			title: '地址选择成功',
			icon: 'success'
		})
	}

	// 跳转新增地址
	function addaddr() {
		uni.navigateTo({
			url: `/pages/addaddr/addaddr?id=${id.value}`
		})
	}

	// 打开地址弹窗
	function tanchu() {
		addr.value = true
		addrlistfin()
	}

	// ========== 优惠券方法 ==========
	// 打开优惠券弹窗
	function openYouHuiPop(){
		showYouHui.value = true
	}
	// 关闭优惠券弹窗
	function closeYouHuiPop(){
		showYouHui.value = false
	}
	// 选择优惠券
	function chooseCoupon(id){
		selectCoupon.value = id
		closeYouHuiPop()
		uni.showToast({title:'优惠券选择成功'})
	}

	// ========== 备注方法 ==========
	// 打开备注弹窗
	function openBeiZhuPop(){
		tempBeizhu.value = userBeizhu.value
		showBeiZhu.value = true
	}
	// 关闭备注弹窗
	function closeBeiZhuPop(){
		showBeiZhu.value = false
	}
	// 保存备注 页面可见
	function saveBeizhu(){
		userBeizhu.value = tempBeizhu.value
		closeBeiZhuPop()
		uni.showToast({title:'备注已保存'})
	}

	// ========== 计算最终价格（抵扣优惠券） ==========
	function getTotalPrice(){
		let total = goodstab.value.price * num.value
		// 有选中优惠券就抵扣
		if(selectCoupon.value!==0){
			total = total - Number(selectCoupon.value.jine)
		}
		// 最低价格不低于0
		if(total < 0) total = 0
		return total.toFixed(2)
	}
	// 订单id查询订单信息
	function orderidselect(id){
		uni.request({
			url:`http://127.0.0.1:8081/order/${role.value}/orderidselect?order_id=${id}`,
			method:'get',
			header: {
				"satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				if(res.data.code==0){
					ordertab.value=res.data.data
					if(ordertab.value.state && ordertab.value.state!==0){
						uni.showToast({
							title:'订单已支付',
							icon:'success',
							duration:3000
						})
						uni.switchTab({
							url:"/pages/order/order"
						})
						return
					}
				}
			}
		})
	}
	
	//结算提交订单
	function submitOrder(){
		if(deliveryType.value==2){
			jutiaddr.value=''
		}
		console.log(num.value)
		if(!jutiaddr.value && deliveryType.value==1){
			uni.showToast({
				title:'请选择收货地址',
				icon:'none'
			})
			return
		}
		jiesuanbtn.value=true
		
		uni.request({
			url:"http://127.0.0.1:8081/order/add",
			method:'POST',
			header: {
				"satoken": uni.getStorageSync('tokenValue')
			},
			data:{
				good_id:id.value,
				buyNum:num.value,
				...(jutiaddr.value && { addr: jutiaddr.value }),
				coupon:selectCoupon.value || 0,
				fahuo:deliveryType.value,
				beizhu:userBeizhu.value
			},
			success: (res) => {
				if(res.data.code==0 && res.data.data){
					order_id.value=res.data.data
					orderidselect(res.data.data)
					paytanc.value=true
					jiesuanbtn.value=false
				}else if(res.data.code==3){
					uni.showToast({
						title:res.data.message,
						icon:'error',
						duration:2000
					})
					
					orderidselect(res.data.data)
					
					order_id.value=res.data.data
					paytanc.value=true
					jiesuanbtn.value=false
				}else if(res.data.code=1){
					uni.showToast({
						title:res.data.message,
						icon:'error',
						duration:2000
					})
					jiesuanbtn.value=false
				}
			}
		})
		
	}

	// 页面加载
	onLoad((options) => {
		id.value = options.id
		uni.request({
			url:'http://127.0.0.1:8081/user/role',
			method:'GET',
			header: {
				"satoken": uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				role.value=res.data.data
				},
			})
		// 请求商品详情
		uni.request({
			url: `http://127.0.0.1:8081/goods/selectid?id=${options.id}`,
			method: 'GET',
			header: {
				'content-type': 'application/x-www-form-urlencoded',
				'satoken': uni.getStorageSync('tokenValue')
			},
			success: (res) => {
				goodstab.value=res.data.data
				if(res.data.data.stock>=1){
					num.value=1
				}else{
					num.value=0
				}
			}
		})

	})
</script>

<style scoped>
	page {
		background: #f5f5f7;
	}
	.page {
		padding: 24rpx;
		box-sizing: border-box;
		padding-bottom: 120rpx;
	}
	/* 顶部头部 */
	.header {
		background: linear-gradient(135deg, #ff9f43, #ff6b01);
		border-radius: 28rpx;
		padding: 40rpx 30rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		color: white;
		margin-bottom: 28rpx;
		box-shadow: 0 8rpx 24rpx rgba(255, 107, 1, 0.2);
	}
	.title {
		display: block;
		font-size: 40rpx;
		font-weight: bold;
		margin-bottom: 12rpx;
	}
	.order-id {
		font-size: 24rpx;
		opacity: 0.9;
	}
	.header-icon {
		font-size: 60rpx;
	}
	/* 收货方式 */
	.radio-group {
		margin: 20rpx 0;
	}
	.radio-item {
		margin-right: 30rpx;
	}
	/* 地址卡片 */
	.address-card {
		background: white;
		border-radius: 28rpx;
		padding: 30rpx 24rpx;
		display: flex;
		align-items: center;
		box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.04);
		margin-bottom: 20rpx;
	}
	.address-icon {
		width: 80rpx;
		height: 80rpx;
		background: #fff3eb;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 38rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}
	.address-content {
		flex: 1;
		overflow: hidden;
	}
	.empty-box {
		display: flex;
		flex-direction: column;
	}
	.address-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}
	.address-tip {
		font-size: 24rpx;
		color: #999;
		line-height: 36rpx;
	}
	.user-row {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		margin-bottom: 10rpx;
	}
	.name {
		font-size: 32rpx;
		font-weight: bold;
		color: #222;
		margin-right: 20rpx;
	}
	.phone {
		font-size: 26rpx;
		color: #666;
	}
	.detail-row {
		font-size: 26rpx;
		color: #555;
		line-height: 40rpx;
		word-break: break-all;
	}
	/* 优惠券、备注布局 */
	.youhui,.beizhu {
		background: #fff;
		padding: 30rpx;
		border-radius: 24rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 15rpx 0;
	}
	.sel-coupon-text,.sel-beizhu-text{
		font-size:24rpx;
		color:#ff6700;
		margin-top:8rpx;
		display:block;
	}
	/* 遮罩 */
	.mask {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.45);
		z-index: 9;
	}
	/* 优惠券弹窗样式 */
	.youhui-pop-box{
		position:fixed;
		bottom:0;
		left:0;
		right:0;
		background:#fff;
		padding:30rpx;
		border-radius:30rpx 30rpx 0 0;
		z-index:999;
		max-height:60vh;
		height: 50%;
	}
	.youhuilist{
		padding:25rpx 10rpx;
		border-bottom:1rpx solid #eee;
		display:flex;
		justify-content:space-between;
	}
	.noyouhui{
		text-align:center;
		padding:50rpx 0;
		color:#999;
	}
	/* 备注弹窗样式 */
	.beizhu-pop-box{
		position:fixed;
		bottom:0;
		left:0;
		right:0;
		background:#fff;
		padding:30rpx;
		border-radius:30rpx 30rpx 0 0;
		z-index:999;
		height: 50%;
	}
	input{
		border:1rpx solid #eee;
		padding:20rpx;
		border-radius:10rpx;
		margin:20rpx 0;
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
	/* 结算 */
	.total-price-box{
		position:fixed;
		bottom:0;
		left:0;
		right:0;
		height:120rpx;
		line-height:90rpx;
		background:#ff6700;
		color:#fff;
		display:flex;
		justify-content:space-between;
		padding:0 30rpx;
		font-size:30rpx;
	}
	/* 地址弹窗 */
	.popup {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		height: 78%;
		background: #fff;
		border-radius: 36rpx 36rpx 0 0;
		z-index: 100;
		padding: 30rpx;
		box-sizing: border-box;
		animation: popupShow 0.25s ease;
		padding-bottom: 100px;
		z-index: 999;
	}
	@keyframes popupShow {
		from {transform: translateY(100%);}
		to {transform: translateY(0);}
	}
	.popup-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 24rpx;
	}
	.popup-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #222;
	}
	.close-btn {
		width: 56rpx;
		height: 56rpx;
		border-radius: 50%;
		background: #f3f3f3;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 26rpx;
		color: #666;
	}
	.addr-list {
		height: 92%;
	}
	.addr-item {
		position: relative;
		background: #fafafa;
		border-radius: 24rpx;
		padding: 24rpx;
		margin-bottom: 22rpx;
		border: 2rpx solid #f2f2f2;
	}
	.item-top {
		margin-bottom: 20rpx;
	}
	.select-btn {
		height: 74rpx;
		line-height: 74rpx;
		border-radius: 60rpx;
		font-size: 28rpx;
		background: linear-gradient(90deg, #ff9f43, #ff6b01);
		color: white;
		border: none;
	}
	.select-btn::after {border: none;}
	.addaddr{
		background-color: #ff9f43;
		position: absolute;
		bottom: 20px;
		width: 80%;
		height: 40px;
		color: rosybrown;
		border: 1px solid pink;
		border-radius: 20px;
		text-align: center;
		line-height: 40px;
		left: 5vh;
	}
	.addaddr text{color: white;}
	.deleteaddr{
		position: absolute;
		top: 2vh;
		right: 3vh;
	}

	/* 支付弹窗 */
	.paytanc {
		position:fixed;
		bottom:0;
		left:0;
		right:0;
		background:#fff;
		border-radius:24rpx 24rpx 0 0;
		z-index:999;
		height: 72%;
		padding:20rpx;
		box-sizing:border-box;
	}
	.pay-header{
		display:flex;
		justify-content:space-between;
		align-items:center;
		padding-bottom:10rpx;
		border-bottom:1rpx solid #eee;
		margin-bottom:10rpx;
	}
	.pay-title{
		font-size:30rpx;
		font-weight:bold;
	}
	.pay-close{
		font-size:28rpx;
		background:#f5f5f5;
		border-radius:50%;
		width:44rpx;
		height:44rpx;
		display:flex;
		align-items:center;
		justify-content:center;
		border:none;
	}
	.pay-info{
		padding:0 10rpx;
	}
	.pay-row{
		display:flex;
		justify-content:space-between;
		font-size:26rpx;
		line-height:40rpx;
	}
	.pay-row .price{
		color:#f53f3f;
		font-weight:bold;
	}
	.pay-addr{
		padding:10rpx;
		background:#f8f8f8;
		border-radius:12rpx;
		margin:10rpx 0;
	}
	.addr-row{
		font-size:25rpx;
		line-height:36rpx;
	}
	.pay-note{
		font-size:25rpx;
		padding:0 10rpx;
		line-height:36rpx;
	}
	.pay-type-title{
		font-size:28rpx;
		margin:10rpx 0 5rpx;
	}
	.pay-item{
		display:flex;
		align-items:center;
		gap:15rpx;
		font-size:27rpx;
		padding:12rpx 0;
	}
	.pay-now-btn{
		position:absolute;
		left:20rpx;
		right:20rpx;
		bottom:25rpx;
		height:70rpx;
		line-height:70rpx;
		background:#ff6700;
		color:#fff;
		font-size:30rpx;
		border-radius:12rpx;
		border:none;
	}
</style>