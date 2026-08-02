<!-- <template>
  <div class="page-403">
    <div class="container">
      <div class="anime-403">403</div>
      <h2 class="title">禁止访问，没有权限哦！</h2>
      <p class="desc">
        当前账号权限不足，无法打开该页面<br>
        请联系管理员开通访问权限
      </p>
      <div class="btn-group">
        <a href="javascript:history.back()" class="back-btn">返回上一页</a>
        <router-link to="/" class="home-btn">回到首页</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

.page-403 {
  background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
}

.container {
  text-align: center;
  padding: 2rem;
  max-width: 500px;
}

.anime-403 {
  font-size: 8rem;
  font-weight: bold;
  color: #ff8fab;
  text-shadow: 0 0 10px rgba(255, 143, 171, 0.3);
  position: relative;
}

.anime-403::after {
  content: "＞︿＜";
  display: block;
  font-size: 2rem;
  color: #ff6b9d;
  margin-top: -1rem;
}

.title {
  font-size: 1.8rem;
  margin: 1rem 0;
  color: #555;
}

.desc {
  font-size: 1rem;
  color: #777;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.btn-group {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.back-btn,
.home-btn {
  display: inline-block;
  padding: 0.8rem 2rem;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(255, 143, 171, 0.2);
}

.back-btn {
  background: #ff8fab;
  color: #fff;
}
.back-btn:hover {
  background: #ff6b9d;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 143, 171, 0.3);
}

.home-btn {
  background: #fff;
  color: #ff6b9d;
  border: 1px solid #ff8fab;
}
.home-btn:hover {
  background: #fff5f8;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 143, 171, 0.2);
}
</style> -->


<template>
  <div>
    <button @click="playSong('1299550532')">播放音乐</button>
    <p>{{ status }}</p>
  </div>
</template>

<script setup>
import { ref } from "vue"

const status = ref("未播放")

let audio = null

async function playSong(id) {
  status.value = "获取音频地址中..."

  const res = await fetch(`http://127.0.0.1:3158/play/${id}`)
  const data = await res.json()

  status.value = "开始播放"

  if (!audio) {
    audio = new Audio()

    audio.addEventListener("playing", () => {
      status.value = "播放中"
    })

    audio.addEventListener("waiting", () => {
      status.value = "缓冲中"
    })

    audio.addEventListener("error", () => {
      status.value = "播放失败"
    })
  }

  audio.src = data.url
  audio.play().catch(err => {
    console.log(err)
    status.value = "播放失败"
  })
}
</script>