<template>
  <!-- 遮罩层 -->
  <div class="mubu1" ref="mubu1Ref"></div>
  <div class="mubu2" ref="mubu2Ref"></div>
  <div class="mubu3" ref="mubu3Ref"></div>
  <div class="mubu4" ref="mubu4Ref"></div>

  <!-- 背景视频 -->
  <video muted autoplay class="bg_v" ref="bgVRef"></video>

  <div class="all">
    <div class="brand-intro">
      <BrandLogo :size="64" :showName="true" nameSize="28px" />
      <p class="brand-tagline">汇聚好物 · 乐享生活</p>
    </div>
    <div class="container">
      <div class="box" ref="boxRefs" v-for="(item, index) in 5" :key="index">
        瞻途电商交易平台
      </div>

      <!-- 轮播图 -->
      <div class="lun" ref="lunRef">
        <span>图片不见了</span>
        <img :src="`images/${currentImg}.png`" alt="" />
      </div>
    </div>

    <!-- 主内容 -->
    <div class="main" ref="mainRef">
      <div class="cp">
        <div>
          <ul>
            <li @mouseover="handleHover(0)">产品1</li>
            <li @mouseover="handleHover(1)">产品2</li>
            <li @mouseover="handleHover(2)">产品3</li>
          </ul>
          <div class="cp_nr" ref="cpNrRef">
            <div>这是第一个盒子</div>
            <div>这是第w个盒子</div>
            <div>这是第l个盒子</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import BrandLogo from '../components/BrandLogo.vue'

const mubu1Ref = ref(null)
const mubu2Ref = ref(null)
const mubu3Ref = ref(null)
const mubu4Ref = ref(null)
const bgVRef = ref(null)
const boxRefs = ref([])
const lunRef = ref(null)
const mainRef = ref(null)
const cpNrRef = ref(null)

// 轮播图当前图片
const currentImg = ref(1)

onMounted(() => {
  nextTick(() => {
    // 初始视频
    bgVRef.value.src = 'images/抖音视频_1774439061618.mp4'

    // 1. 自动轮播
    const timer = setInterval(() => {
      currentImg.value = currentImg.value < 4 ? currentImg.value + 1 : 1
    }, 3000)

    // 2. 遮罩动画
    setTimeout(() => {
      mubu1Ref.value.style.opacity = '50%'
      mubu2Ref.value.style.opacity = '50%'
      mubu3Ref.value.style.opacity = '50%'
      mubu4Ref.value.style.opacity = '50%'

      mubu1Ref.value.style.top = '-50vh'
      mubu2Ref.value.style.top = '100vh'
      mubu3Ref.value.style.left = '-50%'
      mubu4Ref.value.style.left = '100%'
    }, 2000)

    // 3. box 依次入场 + 轮播图入场
    setTimeout(() => {
      boxRefs.value.forEach((box, index) => {
        setTimeout(() => {
          box.style.top = '15px'
        }, index * 300)
      })
      lunRef.value.style.right = 0
    }, 7000)

    // 4. 隐藏遮罩
    setTimeout(() => {
      mubu1Ref.value.style.display = 'none'
      mubu2Ref.value.style.display = 'none'
      mubu3Ref.value.style.display = 'none'
      mubu4Ref.value.style.display = 'none'
    }, 7000)

    // 5. 显示主内容
    setTimeout(() => {
      bgVRef.value.src = 'images/【哲风壁纸】二次元-动漫女孩.mp4'
      mainRef.value.style.display = 'block'
      mainRef.value.style.transform = 'translate(0, 0)'
    }, 8000)
  })
})

// 产品切换
const handleHover = (index) => {
  if (index === 0) cpNrRef.value.style.left = '0'
  if (index === 1) cpNrRef.value.style.left = '-100%'
  if (index === 2) cpNrRef.value.style.left = '-200%'
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}
.brand-intro {
  position: fixed;
  top: 30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.brand-tagline {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  letter-spacing: 3px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
.bg_v {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: -1;
}
.container {
  display: flex;
  flex-wrap: wrap;
}
.box {
  position: relative;
  top: -100px;
  margin: 7px;
  width: 20vh;
  height: 5vh;
  line-height: 5vh;
  background-color: rgb(250, 151, 151);
  font-size: 10px;
  transition: top 1.5s ease;
  opacity: 80%;
  border-radius: 10px;
  text-align: center;
}
.box:hover {
  transform: translate(0, 10%);
}
.lun {
  position: relative;
  top: 20px;
  right: -130%;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  width: 95%;
  background-color: rgb(129, 229, 62);
  transition: all 2s ease;
  border-radius: 20px;
  margin: 0px auto;
  overflow: hidden;
}
.lun span {
  font-size: 20vh;
  text-align: center;
  color: rgb(255, 255, 255);
  line-height: 80vh;
  position: absolute;
  top: 5%;
  left: 15%;
  z-index: -1;
}
.lun img {
  width: 100%;
  height: 100%;
  transition: all 2s ease;
  border-radius: 20px;
  margin: 0;
}
li {
  list-style: none;
}
.mubu1,
.mubu2,
.mubu3,
.mubu4 {
  margin: 0 auto;
  z-index: 999;
  position: absolute;
  left: 0;
  transition: all 3s ease;
}
.mubu1 {
  width: 100%;
  height: 50vh;
  top: 0;
  background-color: rgb(209, 255, 166);
}
.mubu2 {
  width: 100%;
  height: 50vh;
  top: 50%;
  background-color: rgb(209, 255, 166);
}
.mubu3 {
  width: 50vw;
  height: 100vh;
  top: 0;
  background-color: rgb(255, 215, 238);
}
.mubu4 {
  width: 50vw;
  height: 100vh;
  top: 0;
  left: 50vw;
  background-color: rgb(255, 215, 238);
}
.cp {
  height: 80vh;
  width: 95%;
  background-color: royalblue;
  margin: 0 auto;
  margin-top: 70px;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
}
.cp ul {
  width: 100%;
  display: flex;
  justify-content: space-around;
}
.cp li {
  margin-top: 20px;
  width: 15vh;
  height: 5vh;
  border-radius: 20px;
  background-color: snow;
  text-align: center;
  line-height: 5vh;
}
.cp ul li:hover {
  background-color: rgb(178, 235, 255);
  transform: translate(0, 15%);
  transition: all 0.3s ease;
  transform: scale(150%);
}
.main {
  transform: translate(0, 100%);
  transition: all 2s ease;
  display: none;
}
.cp_nr {
  display: flex;
  width: 300%;
  transition: all 1s ease;
  transform: translate(0, 0);
  position: absolute;
  left: 0;
}
.cp_nr div {
  margin: 0 auto;
  margin-top: 20px;
  width: 30%;
  height: 65vh;
  background-color: rgb(255, 255, 255);
  border-radius: 20px;
  text-align: center;
  line-height: 50vh;
}
</style>