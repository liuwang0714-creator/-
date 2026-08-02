
<template>
  <div class="search">
      <input type="text" placeholder="请输入商品名称" v-model="searchText">
      <button @click="search">搜索</button></input>
      <button @click="goodslist" style="background-color: #ff9797;">显示全部</button>
    </div>
  <!-- 商品列表容器 -->
  <div class="goods-container">
    
    <!-- 单个商品卡片 -->
    <div class="goods-item" v-for="item in tableData" :key="item.id" @click="todetails(item.id)">
      <img class="goods-img" :src="`/api/images/${item.file_url}`" alt="商品图片">
      <div class="goods-info">
        <h2 class="goods-name">{{ item.goods_name }}</h2>
        <p class="goods-price">价格: ¥{{ item.price }}</p>
        <p class="goods-stock">库存: {{ item.stock }}</p>
        <p class="goods-desc">描述: {{ item.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const tableData = ref([])
const searchText = ref('')
function search() {
  axios.get('/api/goods/search', {
    params: {
      name: searchText.value
    }
  })
  .then((res) => {
    tableData.value = res.data.data
  })
}

function goodslist(){
  axios.get('/api/goods/goodslist', )
  .then((res) => {
    tableData.value = res.data.data
  })
}

function todetails(id) {
  router.push(`/goodsdetails/${id}`)
}

onMounted(() => {
  goodslist()
})

</script>

<style scoped>
.search{
  display: flex;
  justify-content: center;
  margin: 20px 0;
  width: 100%;
  text-align: center;
}
.search input {
  width: 300px;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
}
.search button {
  margin-left: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}
/* 全局基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 商品列表容器 - 固定一行3个卡片 */
.goods-container {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 15px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

/* 商品卡片 */
.goods-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}
html.dark  .goods-item,html.dark p { 
  color :white;
  background: #363636;
}
html.dark p:nth-child(2){
  color: #ff3333;
}
html.dark p:nth-child(3){
  color: #ffe120;
}
html.dark h2{
  color :rgb(255, 180, 180);
}


.goods-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

/* 商品图片 - 百分百适配容器 */
.goods-img {
  width: 100%;
  height: 240px;
  object-fit: cover;
  display: block;
}

/* 商品信息区 */
.goods-info {
  padding: 18px;
}

/* 商品名称 */
.goods-name {
  font-size: 18px;
  font-weight: 600;
  color: #222;
  margin-bottom: 10px;
  line-height: 1.4;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 价格 */
.goods-price {
  font-size: 19px;
  font-weight: bold;
  color: #f5222d;
  margin-bottom: 8px;
}

/* 库存 */
.goods-stock {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

/* 描述 */
.goods-desc {
  font-size: 13px;
  color: #888;
  line-height: 1.4;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 深色模式适配 */
:deep(.dark) .goods-item {
  background: #1f2937;
}
:deep(.dark) .goods-name {
  color: #f9fafb;
}
:deep(.dark) .goods-stock,
:deep(.dark) .goods-desc {
  color: #d1d5db;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .goods-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  .goods-img {
    height: 180px;
  }
}

@media (max-width: 480px) {
  .goods-container {
    grid-template-columns: 1fr;
  }
}
</style>