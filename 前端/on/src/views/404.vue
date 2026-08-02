<template>
  <div class="page-404">
    <div class="container">
      <div class="anime-404">404</div>
      <h2 class="title">呜哇～页面迷路了！</h2>
      <p class="desc">
        你要找的页面好像被小怪兽吃掉了<br>
        或者它偷偷跑去二次元世界玩了
      </p>
      <a href="javascript:history.back()" class="back-btn">返回上一页</a>
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

.page-404 {
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

.anime-404 {
  font-size: 8rem;
  font-weight: bold;
  color: #ff8fab;
  text-shadow: 0 0 10px rgba(255, 143, 171, 0.3);
  position: relative;
}

.anime-404::after {
  content: "≧ ﹏ ≦";
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

.back-btn {
  display: inline-block;
  padding: 0.8rem 2rem;
  background: #ff8fab;
  color: #fff;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(255, 143, 171, 0.2);
}

.back-btn:hover {
  background: #ff6b9d;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 143, 171, 0.3);
}
</style>