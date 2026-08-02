<template>
  <div class="goods-detail-page">
    <!-- 商品卡片 -->
    <div class="detail-card">
      <h1 class="goods-title">{{ tableData.goods_name || '加载中...' }}</h1>

      <div class="img-box">
        <img
          v-if="tableData.file_url"
          :src="`/api/images/${tableData.file_url}`"
          alt="商品图片"
        />
        <div v-else class="loading-img">图片加载中...</div>
      </div>

      <div class="info-list">
        <div class="info-item">
          <span class="label">价格</span>
          <span class="price">¥{{ tableData.price }}</span>
        </div>
        <div class="info-item">
          <span class="label">库存</span>
          <span>{{ tableData.stock }}</span>
        </div>
        <div class="info-item">
          <span class="label">描述</span>
          <span>{{ tableData.description || '--' }}</span>
        </div>
        <div class="info-item">
          <span class="label">发布时间</span>
          <span>{{ tableData.create_time || '--' }}</span>
        </div>
      </div>
    </div>
    <!-- 用户卡片 -->
    <div class="user-card">
      <div class="avatar-box">
        <img v-if="user.file_url" :src="`/api/images/${user.file_url}`" />
      </div>
      <div class="user-info">
        <p class="nickname">{{ user.nickname || '加载中...' }}</p>
        <p class="label">商品发布人</p>
      </div>
      <div class="user-info">
        <p>卖家信誉：⭐⭐⭐⭐⭐</p>
        <p>发布于:四川成都</p>
        <p>在售商品数：12 件</p>
        <p>48 小时内发货 | 7 天无理由</p>
        <el-icon color="#ffe100" size="30"><StarFilled /></el-icon>
      </div>
      <div class="btn-row">
        <el-button type="primary" @click="communicate" plain>
          <el-icon><ChatDotRound /></el-icon>联系卖家
        </el-button>
        <el-button type="primary" @click="purchase">
          <el-icon><ShoppingCart /></el-icon>立即购买
        </el-button>
      </div>
    </div>
  </div>
  <!-- 评论区 -->
  <div class="comment_list">
    <ul>
      <li
        v-for="(items, index) in tab_comment"
        :key="index"
        @click="reply_comment(items.comment_id, items.nickname,index)"
      >
        <el-icon
          class="delete"
          v-if="items.user_id === userInfo.id"
          @click.stop="deletecomment(items.comment_id,items.comment_url)"
          size="30"
        >
          <Delete />
        </el-icon>

        <img :src="`/api/images/${items.file_url}`" alt="">

        <div class="right">
          <p class="comment-user">{{ items.nickname }}</p>
          <p class="content">{{ items.content }}</p>
          <img :src="`/api/images/${items.comment_url}`" alt="" class="rightimg" v-if="items.comment_url">
          <p class="comment-time">{{ items.createTime }}</p>
          <el-collapse style="margin-top: 10px;" v-if="items.to_coment_num > 0">
            <el-collapse-item
              title="查看回复"
              name="1"
              @click.stop="get_to_comment(items.comment_id, index)"
            >
              <div v-if="items.replyList && items.replyList.length > 0" style="padding: 0 30px ;">
                <div
                  v-for="(reply, idx) in items.replyList"
                  :key="idx"
                  style="font-size:14px;color:var(--text-color);;margin-top:4px;display:flex;align-items:center;gap:6px;display:flex;flex-direction: column;align-items: left;margin-top: 15px;"
                >
                  <div>
                    <img
                    :src="`/api/images/${reply.file_url}`"
                    alt=""
                    style="width:24px;height:24px;border-radius:50%;"
                  />
                  <span style="color:#333;font-weight:500;">{{ reply.nickname }}：</span>
                  </div>
                  <div class="conmment_to_div">
                    <span style="color:black;">{{ reply.content }}</span>
                    
                  </div>
                  <img :src="`/api/images/${reply.comment_url}`" alt="" class="rightimg" v-if="reply.comment_url">
                  <span style="color: #999999;">{{ reply.createTime }}</span>
                </div>
              </div>
              <div v-else style="font-size:14px;color:#999;">
                暂无回复
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </li>
    </ul>
  </div>
  <el-dialog
    v-model="commentaddfilebool"
    title="添加图片"
    width="500"
    align-center
    destroy-on-close
  >
   <el-upload
    class="avatar-uploader"
    :http-request="previewOnlyUpload"
    :show-file-list="false"
    :before-upload="beforeAvatarUpload"
  >
    <img v-if="imageUrl" :src="imageUrl" class="avatar" style="width:40vh; " />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="commentaddfilebool= false">取消</el-button>
        <el-button type="primary" @click="commentaddfilebool= false">提交</el-button>
      </div>
    </template>
  </el-dialog>
  <!-- 发表评论 -->
  <div class="comment-fixed-bottom">
    <div><img v-if="imageUrl" :src="imageUrl" class="avatar" style="width:25vh; margin-left: 25vh;" /></div>
    <div class="comment-input">
      <button class="commentaddfile" @click="commentaddfilebool=true">+</button>
      <el-input
        v-model="comment"
        placeholder="写下你的评论..."
        class="input-area"
        @keyup.enter="addComment"
      ></el-input>
      <el-button
        type="primary"
        @click="addComment"
        :disabled="addcomment"
        size="default"
      >发布</el-button>
    </div>
  </div>
  <!-- 回复弹出框 -->
  <el-dialog
    v-model="reply_comment_visible"
    :title="`回复@${reply_nickname}`"
    width="500"
    align-center
    destroy-on-close
  >
    <el-input
      v-model="comment"
      placeholder="请输入回复内容"
    ></el-input>
    <el-upload
      class="avatar-uploader"
      :http-request="previewOnlyUpload"
      :show-file-list="false"
      :before-upload="beforeAvatarUpload"
    >
    <img v-if="imageUrl" :src="imageUrl" class="avatar" style="width:40vh; " />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="reply_comment_visible = false">取消</el-button>
        <el-button type="primary" @click="reply_comment_btn()">提交</el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog
    v-model="orderbtn"
    title="✅ 订单结算"
    width="520"
    top="3vh"
    :before-close="handleClose"
  >
    <div class="order-dialog-body">
      
      <!-- 商品信息 -->
      <div class="order-goods-info">
        <div class="goods-label">商品信息</div>
        <div class="goods-item">
          <div class="name">商品名称：<span>{{ tableData.goods_name }}</span></div>
          <div class="price">单价：<span>¥ {{ tableData.price }}</span></div>
        </div>
      </div>

      <!-- 配送方式 -->
      <div class="order-section">
        <div class="label">配送方式</div>
        <el-radio-group v-model="fahuo" class="radio-group">
          <el-radio value="1">快递发货（包邮）</el-radio>
          <el-radio value="2">线下面交</el-radio>
        </el-radio-group>
      </div>

      <!-- 快递发货时显示地址 -->
      <div class="order-address" v-if="fahuo == '1'">
        <div class="label">收货地址</div>
        <div class="address-box">
        <button 
          @click="addressfun" 
          class="select-address-btn"
        >
          点击选择收货地址
        </button>

      <div v-if="currentAddr.name" class="selected-address-card">
        <div class="addr-item">
          <span class="label">姓名</span>
          <span class="text">{{ currentAddr.name }}</span>
        </div>
        <div class="addr-item">
          <span class="label">手机号</span>
          <span class="text">{{ currentAddr.phone }}</span>
        </div>
        <div class="addr-item addr-full">
          <span class="label">详细地址</span>
          <span class="text">{{ currentAddr.addr }}</span>
        </div>
      </div>
    </div>
        <div class="freight">运费：<span>包邮</span></div>
      </div>

      <!-- 购买数量 -->
      <div class="order-section">
        <div class="label">购买数量</div>
        <el-input-number 
          v-model="ordernum" 
          :min="1" 
          :max="tableData.stock || 99" 
          class="num-input"
        />
        <div class="stock-tip">库存剩余：{{ tableData.stock || 0 }} 件</div>
      </div>

      <!-- 支付方式 -->
      <div>
        <el-input v-model="beizhu" style="width: 240px" placeholder="请输入备注信息" />
      </div>

      <!-- 总价 -->
      <div class="order-total">
        <span>实付总价</span>
        <h2>¥ {{ total }}</h2>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="orderbtn = false">取消购买</el-button>
        <el-button type="primary" @click="submitOrder">
          去结算
        </el-button>
      </div>
    </template>
</el-dialog>

<!-- 地址选择弹窗 -->
<el-dialog
    v-model="address"
    title="选择收货地址"
    width="500"
    :before-close="handleClose"
    class="address-dialog"
>
  <div class="address-list"> 
    <el-radio-group v-model="addr_id" class="address-radio-group">
      <el-radio 
        v-for="item in addrtable" 
        :key="item.id" 
        :label="item.id" 
        size="large"
        class="address-radio"
      >
        <div class="address-item">
          <div class="name">{{ item.name }}</div>
          <div class="phone">{{ item.phone }}</div>
          <div class="addr">{{ item.addr }}</div>
          
        </div>
        <el-icon 
            class="addr-delete-btn" 
            @click.stop="deleteAddr(item.id)" 
            size="14"
          >
            <Delete />
          </el-icon>
      </el-radio>
    </el-radio-group>

    <!-- 空数据提示 -->
    <div class="empty" v-if="addrtable.length === 0">
      暂无收货地址，请先添加地址
    </div>
  </div>
  <div class="address-add-btn" @click="addaddresstan=true">+ 新增地址</div>
  <template #footer>
    <div class="dialog-footer">
      <el-button @click="address = false">取消</el-button>
      <el-button type="primary" @click="selectaddr">确定选择</el-button>
    </div>
  </template>
</el-dialog>

<el-dialog
    v-model="paytanc"
    title="确认支付"
    width="600"
    :before-close="handleClose"
    class="ordrpay"
>
 <div class="order-section">
      <div>订单号: {{ ordertab.order_id }}</div>
      <p>支付金额: ¥ {{ ordertab.total_price }}</p>
      <p>创建时间: {{ ordertab.create_time }}</p>
      <p>订单状态: {{ getOrderStatus(ordertab.state) }}</p>
      <div>收货地址: 
        <p>姓名: {{ ordertab.name }}</p>
        <p>手机号: {{ ordertab.phone }}</p>
        <p>详细地址: {{ ordertab.addr_detail   }}</p>
      </div>
      <p></p>
      <p></p>
        <div class="label">支付方式</div>
        <el-radio-group v-model="pay" class="radio-group">
          <el-radio value="1">💳 微信支付</el-radio>
          <el-radio value="2">💸 支付宝</el-radio>
        </el-radio-group>
      </div>
<template #footer>
    <div class="dialog-footer">
      <el-button @click="paytanc = false">取消</el-button>
      <el-button type="primary" @click="payfun">立即支付</el-button>
    </div>
  </template>
</el-dialog>

<el-dialog
    v-model="addaddresstan"
    title="添加收获地址"
    width="600"
    :before-close="handleClose"
    class="address-dialog"
>
  <div class="address-list"> 
    <el-form ref="addresstan" label-width="80px">
      <el-form-item label="收货姓名">
        <el-input v-model="addaddrtable.name" placeholder="请输入收货人姓名"></el-input>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="addaddrtable.phone" placeholder="请输入手机号"></el-input>
      </el-form-item>
      <el-form-item label="收货地址">
        <el-input v-model="addaddrtable.addr" placeholder="请输入地址"></el-input>
      </el-form-item>
    </el-form>
  </div>
<template #footer>
    <div class="dialog-footer">
      <el-button @click="addaddresstan = false">取消</el-button>
      <el-button type="primary" @click="addaddaddr">添加</el-button>
    </div>
  </template>
</el-dialog>
</template>

<script setup>
import { useRoute,useRouter } from 'vue-router'
import { ref, onMounted, watch,computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useImStore } from '../store/im'

const imStore = useImStore()
const orderbtn=ref(false)
const tableData = ref({})
const user = ref({})
const route = useRoute()
const router = useRouter()
const comment = ref('')
const tab_comment = ref([])
const addcomment = ref(false)
const userInfo = ref({})
const reply_comment_visible = ref(false)
const reply_nickname = ref('')
const reply_comment_id = ref(null)
const currentReplyIndex = ref(0)
const ordernum = ref(1)
const address=ref(false)
const addrtable = ref([])
const addrjuti=ref(true)
const addaddresstan=ref(false)
const fahuo = ref('1')
const pay = ref('1')  
const addr_id = ref(1)
const beizhu=ref('')
const role=ref()
const commentaddfilebool=ref(false)
const paytanc=ref(false)


const total = computed(() => {
const price = Number(tableData.value.price || 0)
const num = Number(ordernum.value || 0)

  return (price * num).toFixed(2)
})

const paytypes=ref()
const addaddrtable = ref({
  name: '',
  phone: '',
  addr: ''
})

const ordertab=ref()

const goods_id=ref(route.params.id)

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



  // 评论添加图片
  import { Plus } from '@element-plus/icons-vue'

  const imageUrl = ref('')
const selectFile = ref(null)
  // 仅本地预览，不请求后端
const previewOnlyUpload = (options) => {
  const file = options.file
  selectFile.value = file
  // 本地生成预览图
  imageUrl.value = URL.createObjectURL(file)
  // 手动标记上传完成，消除组件报错
  options.onSuccess()
}

// 上传前校验（格式/大小）
const beforeAvatarUpload = (rawFile) => {
  const isImg = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt10M = rawFile.size / 1024 / 1024 < 10
  if (!isImg) {
    ElMessage.error('只能上传 JPG / PNG 格式！')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片不能超过 10MB！')
    return false
  }
  return true
}
  function payfun(){
    if(pay.value==1){
			paytypes.value='wx'
		}else{
			paytypes.value='zfb'
		}
    axios.get(`/api/pay/${paytypes.value}?order_id=${ordertab.value.order_id}`).then(res => {
      if (res.data.code === 0) {
        ElMessage.success('支付成功')
        paytanc.value=false
        router.push('/user/userorder')
      } else {
        ElMessage.error('支付失败')
      }
    })
  }
// 加载评论
function getcomment(goodsId) {
  axios.get("/api/comment/comment_list", {
    params: { goods_id: goodsId }
  }).then(res => {
    tab_comment.value = res.data.data.items
  })  
}

  function getorderbyid(id){
    axios.get(`/api/order/${role.value}/orderidselect`,
      {params: { order_id: id }}
    ).then(res => {
      
      ordertab.value = res.data.data
      paytanc.value=true
      orderbtn.value=false
      console.log(ordertab.value)
    })
  }

// 提交订单
function submitOrder(){
  if(fahuo.value==2){
      currentAddr.value={}
  }else{
    if(currentAddr.value=={}){
      ElMessage.error('请选择收货地址')
      return
    }
  }
  
  axios.post("/api/order/add",  {

        fahuo: fahuo.value,
        buyNum: ordernum.value,
        ...(currentAddr.value && { addr: currentAddr.value }),
        // 优惠券
        coupon: '',
        good_id: goods_id.value,
        beizhu: beizhu.value
      
  }).then(res => {
    if (res.data.code === 3) {
      console.log('订单提交成功')
      ElMessage.success(res.data.message)
      getorderbyid(res.data.data)
    }else if(res.data.code === 0){
      ElMessage.success('订单创建成功')
      getorderbyid(res.data.data)
    }else{
      ElMessage.error(res.data.message)
    }
    currentAddr.value={}
  })
}
const currentAddr=ref({})
// 确认选择地址按钮
function selectaddr(){
  // 根据选中的id，从地址列表匹配完整地址对象
  currentAddr.value = addrtable.value.find(item => item.id === addr_id.value)
  console.log('选中的完整地址对象：', currentAddr)
  
  // currentAddr 就是一整条地址数据：name、phone、addr 全都有
  address.value = false
}
// 选择收货地址函数
function addressfun(){
  address.value=true
  axios.get("/api/address/list").then(res => {
    addrtable.value = res.data.data
  })
}

// 添加新地址
function addaddaddr(){
  if(addaddrtable.value.name!=='' && addaddrtable.value.phone !== '' && addaddrtable.value.addr !== ''){
    axios.put("/api/address/add", addaddrtable.value).then(res => { 
      if(res.data.code === 0){
        ElMessage.success('添加成功')
        addaddresstan.value=false
        addressfun()
        addaddrtable.value={
          name: '',
          phone: '',
          addr: ''
        }
      }
    })
  }else{
    ElMessage.error('请填写完整信息')
  }
  
}
// 删除地址
function deleteAddr(id){
  axios.delete('/api/address/delete', { params: { id: id } }).then(() => {
    ElMessage.success('删除成功')
    addressfun()
  })
}
// 回复评论按钮函数
function reply_comment(comment_id, nickname, index) {
  reply_comment_visible.value = true
  reply_nickname.value = nickname
  reply_comment_id.value = comment_id
  currentReplyIndex.value = index
}
// 按下标给当前评论单独绑定回复列表，互不影响
function get_to_comment(comment_id, index) {
  axios.get("/api/comment/to_comment_list", {
    params: { comment_id: comment_id }
  }).then(res => {
    tab_comment.value[index].replyList = res.data.data.items
  })
}
// 回复图片上传
function touploadImg(file) {
  const formData = new FormData()
  formData.append('file', file)
  axios.post('/api/file/upload', formData).then(res => {
    if (res.data.code === 0) {
      ElMessage.success('上传成功')
      commetn_ton(comment.value, reply_comment_id.value, res.data.data)
    }
  })
}
// 发表回复
function commetn_ton(replycommnet, commnetid,fileid) {
  axios.put("/api/comment/to_comment", null, {
    params: {
      content:replycommnet,
      comment_id: commnetid,
      file_id:fileid
    }
  }).then(res => {
    if (res.data.code === 0) {
      ElMessage.success('回复成功')
      getcomment(route.params.id)
      get_to_comment(reply_comment_id.value, currentReplyIndex.value)
      reply_comment_visible.value = false
      comment.value = ''
      imageUrl.value = ''
    }
  })
}
async function reply_comment_btn(){
  if(imageUrl.value){
    file_id = await touploadImg(selectFile.value)
  }else{
    commetn_ton(comment.value, reply_comment_id.value,null)
  }
}
// 联系卖家 - 打开聊天抽屉并定位到商家
function communicate() {
  const token = localStorage.getItem('satoken')
  if (!token) {
    ElMessage.warning('请先登录')
    return
  }
  const merchantUsername = user.value?.username
  if (!merchantUsername) {
    ElMessage.error('无法获取商家信息')
    return
  }
  // 打开抽屉并定位到该商家
  imStore.openDrawer(merchantUsername)
}
// 购买商品
function purchase() {
  orderbtn.value = true
}
// 查询商品和发布用户信息
const getGoodsDetail = async (id) => {
  const goodsRes = await axios.get(`/api/goods/selectid?id=${id}`)
  tableData.value = goodsRes.data.data

  if (tableData.value.create_id) {
    const userRes = await axios.get('/api/user/infoid', {
      params: { userid: tableData.value.create_id }
    })
    user.value = userRes.data.data
  }
}
// 删除评论
function deletecomment(comment_id,comment_url) {
  console.log('删除评论ID：', comment_id, '文件URL：', comment_url)
  axios.delete("/api/comment/delete", { params: { id: comment_id ,file_url: comment_url } }).then(res => {
    if (res.data.code === 0) {
      ElMessage.success('删除成功')
      getcomment(route.params.id)
    }
  })
}
// 查询当前登录用户信息
function getUserInfo() {
  axios.get('/api/user/info', {}).then(res => {
    userInfo.value = res.data.data
  })
}

// 获取当前用户权限
function getRole() {
  axios.get('/api/user/role').then(res => {
    role.value = res.data.data
  })
}

onMounted(() => {
  getUserInfo()
  getGoodsDetail(route.params.id)
  getcomment(route.params.id)
  getRole()
})

watch(
  () => route.params.id,
  (newId) => {
    getGoodsDetail(newId)
    getcomment(newId)
  }
)
// 评论axios函数
function commetn(content,goods_id,file_id){
  axios.get("/api/comment/add", {
    params: { content: content, goods_id: route.params.id, file_id: file_id }
  }).then(res => {
    if (res.data.code === 0) {
      getcomment(route.params.id)
      ElMessage.success('评论成功')
      comment.value = ''
      imageUrl.value = ''
    }
  }).finally(() => {
    addcomment.value = false
  })
}
// 文件上传
function uploadImg(file) {
  const formData = new FormData()
  formData.append('file', file)
  axios.post('/api/file/upload', formData).then(res => {
    if (res.data.code === 0) {
      ElMessage.success('上传成功')
      commetn(comment.value, route.params.id, res.data.data)
    }
  })
}
// 发表评论
async function addComment() {
  addcomment.value = true
  let file_id=null
  if (!comment.value) {
    ElMessage.error('请输入内容')
    return
  }
  if(imageUrl.value){
    file_id = await uploadImg(selectFile.value)
  }else{
    commetn(comment.value, route.params.id)
  }
}
</script>

<style scoped>
    :root {
      color: #e6e200;
    --bg-color: #ffffff;
    --card-bg: #ffffff;
    --text-color: #222;
    --text-secondary: #666;
    --text-weak: #999;
    --border-color: #e5e7eb;
    --light-bg: #fafafa;
    --img-bg: #f9f9f9;
    --bottom-bar-bg: #ffffff;
  }

  html.dark {
    --bg-color: #141414;
    --card-bg: #1f1f1f;
    --text-color: #e5e5e5;
    --text-secondary: #d1d5db;
    --text-weak: #9ca3af;
    --border-color: #444444;
    --light-bg: #2a2a2a;
    --img-bg: #242424;
    --bottom-bar-bg: #1f1f1f;
  }

  .delete {
    position: absolute;
    top: 12px;
    right: 12px;
    color: #ff4d4f;
    cursor: pointer;
    transition: 0.2s;
  }
  .conmment_to_div{
    display: flex;
    
  }
  .rightimg{
    width: 200px !important;
    height: 200px !important;
    border-radius: 10px !important;
  }
  .delete:hover {
    color: #d9363e;
  }

  .goods-detail-page {
    max-width: 1100px;
    margin: 30px auto;
    padding: 0 20px;
    display: flex;
    gap: 26px;
    flex-wrap: wrap;
  }
.select-address-btn {
  width: 100%;
  padding: 10px 12px;
  border: 1px dashed #dcdfe6;
  border-radius: 10px;
  background: #fafbfc;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}
.select-address-btn:hover {
  border-color: #ff4d4f;
  background: #fff5f5;
  color: #ff4d4f;
}

.selected-address-card {
  width: 100% !important;
  padding: 12px;
  border-radius: 10px;
  background: #fefefe;
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.addr-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}
.addr-item.addr-full {
  align-items: flex-start;
}
.addr-item .label {
  width: 60px;
  color: #999;
  flex-shrink: 0;
}

.addr-item .text {
  color: #333;
  line-height: 1.4;
  word-break: break-all;
}
  .detail-card {
    flex: 2;
    min-width: 300px;
    background: var(--card-bg);
    padding: 32px;
    border-radius: 20px;
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.06);
    border: 1px solid var(--border-color);
  }

  .goods-title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 20px;
    color: var(--text-color);
    text-align: center;
  }

  .img-box {
    width: 100%;
    height: 380px;
    background: var(--img-bg);
    border-radius: 16px;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    margin-bottom: 24px;
  }

  .img-box img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }

  .loading-img {
    color: var(--text-weak);
  }

  .info-list {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 16px 20px;
    background: var(--light-bg);
    border-radius: 12px;
    font-size: 15px;
    color: var(--text-color);
  }
  .info-item span {
    width: 85vh;
    padding-left: 20px;
  }
  .info-item .label {
    color: var(--text-secondary);
    width: 15vh !important;
    font-weight: 1000 !important;
    border-right: 1px solid #ccc;
  }

  .price {
    color: #fa3d3d;
    font-weight: bold;
    font-size: 18px;
  }

  .user-card {
    flex: 1;
    min-width: 280px;
    background: var(--card-bg);
    padding: 32px 24px;
    border-radius: 20px;
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.06);
    border: 1px solid var(--border-color);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 18px;
  }
  html.dark .img-box{
    border-bottom: 1px solid rgb(255, 255, 255);
  }
  .avatar-box img {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    border: 4px solid var(--border-color);
  }

  .user-info {
    text-align: center;
    line-height: 1.6;
    color: var(--text-secondary);
  }

  .nickname {
    font-size: 19px;
    font-weight: 600;
    color: var(--text-color);
    margin: 0;
  }

  .user-info .label {
    font-size: 13px;
    color: var(--text-weak);
  }

  .user-info p {
    margin: 6px 0;
    font-size: 14px;
    color: var(--text-secondary);
  }

  .btn-row {
    display: flex;
    gap: 10px;
    width: 100%;
  }
  .btn-row el-button {
    flex: 1;
  }

  .comment_list {
    max-width: 1100px;
    margin: 40px auto 120px;
    padding: 0 20px;
  }

  .comment_list ul {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .comment_list li {
    position: relative;
    display: flex;
    gap: 16px;
    background: var(--card-bg);
    padding: 22px;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
    border: 1px solid var(--border-color);
    align-items: flex-start;
  }

  .comment_list img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
  }

  .right {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .comment-user {
    font-weight: 600;
    font-size: 15px;
    color: var(--text-color);
  }

  .content {
    font-size: 15px;
    color: var(--text-color);
    line-height: 1.5;
  }

  .comment-time {
    font-size: 12px;
    color: var(--text-weak);
  }

  .comment-fixed-bottom {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: var(--bottom-bar-bg);
    padding: 3px 2px;
    border-top: 1px solid var(--border-color);
    z-index: 999;
    display: flex;
    align-items: center;
    padding-bottom: 50px;
    display: flex;
    flex-direction: column;
     align-items: flex-start
  }

  .comment-input {
    width: 100%;
    max-width: 1100px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .input-area {
    flex: 1;
  }

  body {
    padding-bottom: 70px;
  }
  :deep(.el-collapse) {
    --el-collapse-bg-color: transparent;
    --el-collapse-header-text-color: var(--text-color);
    --el-collapse-content-text-color: var(--text-color);
    --el-collapse-border-color: var(--border-color);
  }

  html.dark :deep(.el-collapse) {
    --el-collapse-bg-color: transparent;
    --el-collapse-header-text-color: var(--text-color);
    --el-collapse-content-text-color: var(--text-color);
    --el-collapse-border-color: var(--border-color);
  }

  :deep(.el-collapse-item__header) {
    color: var(--text-secondary) !important;
    font-size: 14px !important;
    padding-left: 0 !important;
  }

  :deep(.el-collapse-item__content) {
    color: var(--text-secondary) !important;
    background: transparent !important;
    border-top: 1px solid var(--border-color) !important;
    padding-left: 0 !important;
  }

  html.dark :deep(.el-collapse-item__header) {
    color: var(--text-secondary) !important;
  }

  html.dark :deep(.el-collapse-item__content) {
    color: var(--text-secondary) !important;
  }

  :deep(.el-collapse-item__content) div span:nth-child(1) {
    color: var(--text-color) !important;
    font-weight: 500;
  }
  :deep(.el-collapse-item__content) div span:nth-child(2) {
    color: var(--text-color) !important;
  }
  :deep(.el-collapse-item__content) div span:nth-child(3) {
    color: var(--text-weak) !important;
  }

  html.dark :deep(.el-collapse-item__content) div span:nth-child(1) {
    color: var(--text-color) !important;
  }
  html.dark :deep(.el-collapse-item__content) div span:nth-child(2) {
    color: var(--text-color) !important;
  }
  html.dark :deep(.el-collapse-item__content) div span:nth-child(3) {
    color: var(--text-weak) !important;
  }

  html.dark .detail-card,
  html.dark .user-card {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
  }

  html.dark .comment_list li {
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
  }
.order-dialog-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 10px 4px;
}

.order-goods-info .goods-label {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--text-color);
}

.order-goods-info .goods-item {
  background: var(--light-bg);
  font-size: 14px;
  padding: 3px 2px;
  border-radius: 10px;
  color: var(--text-color);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-goods-info .price span {
  color: #ff4d4f;
  font-weight: bold;
  font-size: 15px;
}

.order-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.order-section .label {
  font-weight: 600;
  font-size: 15px;
  color: var(--text-color);
}

.order-address {
  background: var(--light-bg);
  padding: 3px 2px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-address .address-box {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
}

.order-address .freight {
  color: #ff4d4f;
  font-weight: 500;
  font-size: 14px;
}

.num-input {
  width: 140px;
  transform: scale(0.95);
}

.stock-tip {
  font-size: 12px;
  color: var(--text-weak);
}

.order-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff7f8;
  padding: 3px 2px;
  border-radius: 10px;
  margin-top: 4px;
  border: 1px solid #ffebef;
}

.order-total span {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-color);;
}

.order-total h2 {
  margin: 0;
  color: #ff4d4f;
  font-size: 20px;
}

html.dark .order-total {
  background: #2c1a1d;
  border-color: #4b2529;
}

.radio-group {
  display: flex;
  gap: 16px;
}

:deep(.address-dialog) {
  --el-dialog-padding-primary: 20px;
}
.orderpay{
  z-index: 1000;
}
.address-list {
  padding: 10px 0;
  max-height: 400px;
  overflow-y: auto;
}
.address-radio-group {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
:deep(.address-radio) {
  width: 80% !important;
  padding: 25px 12px;
  border-radius: 12px;
  border: 1px solid var(--el-border-color-light);
  transition: all 0.2s ease;
  margin: 0 !important;
  --el-radio-font-size: 14px;
}
:deep(.address-radio.is-checked) {
  background-color: #fef0f0;
  border-color: #ff4d4f;
}
.address-item {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.address-item .name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-color);;
}

.addr-delete-btn {
  position: absolute;
  top: 4px;
  right: 5vh;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #fff2f2;
  color: #ff4d4f;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #ffd1d1;
}
.addr-delete-btn:hover {
  background: #ff4d4f;
  color: #fff;
  transform: scale(1.1);
}
.address-item .phone {
  font-size: 14px;
  color:var(--text-color);;
}
.address-item .addr {
  font-size: 13px;
  color: var(--text-color);;
  line-height: 1.4;
}
.empty {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 14px;
}
.address-add-btn{
  cursor: pointer;
  color: var(--text-color);
  width: 100%;
  text-align: center;
  height: 40px;
  line-height: 40px;
}
</style>