<template>
    <div class="container">
        <div class="left">
            <div class="left-bg">
                <div class="brand-overlay">
                    <BrandLogo :size="80" :showName="true" nameSize="32px" />
                    <h2 class="brand-slogan">汇聚好物 · 乐享生活</h2>
                    <p class="brand-desc">瞻途电商交易平台</p>
                </div>
            </div>
        </div>
        <div class="right"> 
            <div class="login" v-show="!isshow"> 
                <div class="login-header">
                    <BrandLogo :size="36" :showName="true" nameSize="20px" />
                </div>
                <div class="title">欢迎登录</div>
                <div class="form">
                    <div class="item">
                        <label for="">用户名</label>
                        <input type="text" placeholder="请输入用户名" v-model="username"></input>
                    </div>
                    <div class="item">
                        <label for="">密码</label>
                        <el-input
                            v-model="password"
                            type="password"
                            placeholder="请输入密码"
                            show-password
                            >
                            <template #password-icon="{ visible }">
                                <el-icon :size="16">
                                <Unlock v-if="visible" />
                                <Lock v-else />
                                </el-icon>
                            </template>
                            </el-input>
                    </div>
                    <div>
                        记住我:<el-switch v-model="value1" />
                    </div>
                    <div class="btn">
                        <button @click="login">登录</button>
                    </div>
                </div>
            </div>
            <div class="register" v-show="isshow">
                <div class="login-header">
                    <BrandLogo :size="36" :showName="true" nameSize="20px" />
                </div>
                <div class="title">注册账号</div>
                <div class="form">
                    <div class="item">
                        <label for="">角色</label>
                        <select id="role" v-model="selrole">
                            <option value=2>商家</option>
                            <option value=3>用户</option>
                        </select>
                    </div>
                    <div class="item">
                        <label for="">昵称</label>
                        <input type="text" placeholder="请输入昵称" v-model="nickname">
                    </div>
                    <div class="item">
                        <label for="">用户名</label>
                        <input type="text" placeholder="请输入用户名" v-model="username"></input>
                    </div>
                    <div class="item">
                        <label for="">密码</label>
                        <el-input
                            v-model="password"
                            type="password"
                            placeholder="请输入密码"
                            show-password
                            >
                            <template #password-icon="{ visible }">
                                <el-icon :size="16">
                                <Unlock v-if="visible" />
                                <Lock v-else />
                                </el-icon>
                            </template>
                            </el-input>
                    </div>
                    <div class="item">
                        <label for="">密码</label>
                        <el-input
                            v-model="rpassword"
                            type="password"
                            placeholder="请再次输入密码"
                            show-password
                        >
                            <template #password-icon="{ visible }">
                                <el-icon :size="16">
                                    <Unlock v-if="visible" />
                                    <Lock v-else />
                                </el-icon>
                            </template>
                        </el-input>
                    </div>
                    <div class="btn">
                        <button @click="register">注册</button>
                    </div>
                </div>
            </div>
            <div class="btn">
                <button @click="isshow = !isshow">{{isshow ? '已有账号？去登录' : '没有账号？去注册'}}</button>
            </div>
        </div>
    </div>
</template>

<script setup>

    import { ref, watch, onMounted, reactive } from 'vue'
    import { Lock, Unlock } from '@element-plus/icons-vue'
    import BrandLogo from '../components/BrandLogo.vue'
    const isshow = ref(false)
    import axios from 'axios'
    import { useRouter } from 'vue-router'
    import { useUserStore } from '../store/user'
    import { useImStore } from '../store/im'

    const router = useRouter()
    const userStore = useUserStore()
    const imStore = useImStore()

    const username = ref('')
    const password = ref('')
    const nickname = ref('')
    const rpassword = ref('')
    const selrole = ref(3)
    const value1 = ref(false)
    function roleroute(role) {
        if(role === 'admin'){
            router.push('/admin')
        }else if(role === 'merchant'){
            router.push('/merchant')
        }else if(role === 'user'){
            router.push('/user/goodslist')
        }
    }
    function register() {
        if (username.value === '' || password.value === '' || nickname.value === '' || rpassword.value === '') {
            alert('请填写完整信息')
            return
        }
        if (password.value !== rpassword.value) {
            alert('两次密码不一致')
            return
        }
        axios.get('/api/user/register', 
        {params: {
            "username": username.value,
            "password": password.value,
            "role_id": selrole.value,
            "nickname": nickname.value
        }}).then(res => {
            console.log(res.data)
            if (res.data.code === 0) {
                alert('注册成功')
                isshow.value = false
            } else {
                alert(res.data.message)
            }
        })
    }
    function login() {
        if (username.value === '' || password.value === '') {
            alert('请填写完整信息')
            return
        }
        axios.post('/api/user/login', 
        {"username": username.value,
        "password": password.value,
        "bool":value1.value}).then(res => {
            console.log(res.data)
            if (res.data.code === 2) {
                alert('该用户已被封禁,解封时间:' + new Date(res.data.data).toLocaleString())
                return
            }
            if (res.data.code === 0) {
                const token = res.data.data.tokenValue

                axios.get('/api/user/role').then(res => {
                    if (res.data.code === 0) {
                        if(res.data.data ){
                            console.log(res.data.data)
                            userStore.login(token, res.data.data)
                            roleroute(res.data.data)
                            // 登录后初始化 IM
                            axios.get('/api/user/info').then(infoRes => {
                                if (infoRes.data.code === 0) {
                                    const u = infoRes.data.data
                                    imStore.init({
                                        id: u.id,
                                        username: u.username,
                                        nickname: u.nickname,
                                        avatar: u.file_url,
                                        roleCode: res.data.data
                                    })
                                }
                            })
                        }

                    }

                })
                
            } else {
                alert(res.data.message)
            }
        })
    }

onMounted(() => {
    if(localStorage.getItem('satoken')){
        roleroute(localStorage.getItem('role'))
    }
});
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  width: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  background: var(--el-bg-color);
  transition: background 0.3s;
}

.left {
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.left-bg {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.left-bg::before {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  top: -100px;
  right: -100px;
}

.left-bg::after {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  bottom: -50px;
  left: -50px;
}

.brand-overlay {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
}

.brand-slogan {
  font-size: 24px;
  font-weight: 600;
  margin: 20px 0 10px;
  letter-spacing: 2px;
}

.brand-desc {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.login-header {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.logo {
  width: 100%;
  height: 100%;
  display: none;
}

.logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  filter: brightness(1);
  transition: filter 0.4s;
}

/* 深色模式下图片变暗 */
:global(.dark) .logo img {
  filter: brightness(0.85);
}

.right {
  width: 50%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 8%;
}

.login, .register {
  width: 100%;
  max-width: 420px;
}

.title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 32px;
  text-align: center;
  color: var(--el-text-color-primary);
  transition: color 0.3s;
}

.form .item {
  margin-bottom: 20px;
}

.form .item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--el-text-color-regular);
  transition: color 0.3s;
}

.form .item input,
.form .item select {
  width: 100%;
  height: 46px;
  padding: 0 15px;
  border: 1px solid var(--el-border-color);
  border-radius: 10px;
  font-size: 15px;
  outline: none;
  transition: all 0.3s;
  background: var(--el-input-bg-color);
  color: var(--el-text-color-primary);
}

.form .item input:focus,
.form .item select:focus {
  border-color: #409eff;
  background: var(--el-input-hover-bg-color);
}

/* 修复密码框样式（关键！） */
.form .item .el-input {
  width: 100% !important;
}
.form .item .el-input__wrapper {
  height: 46px !important;
  border-radius: 10px !important;
  box-shadow: none !important;
  border: 1px solid var(--el-border-color) !important;
  background: var(--el-input-bg-color) !important;
  padding-right: 10px !important;
  transition: all 0.3s !important;
}
.form .item .el-input__inner {
  height: 46px !important;
  line-height: 46px !important;
  padding: 0 10px !important;
  background: transparent !important;
  color: var(--el-text-color-primary) !important;
}
.form .item .el-input__suffix {
  right: 10px !important;
}
.form .item .el-input__suffix-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
.form .item .el-icon {
  cursor: pointer !important;
  pointer-events: auto !important;
}

.btn {
  margin-top: 10px;
  text-align: center;
}

.btn button {
  width: 100%;
  height: 48px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.btn button:hover {
  background: #337ecc;
}

.btn:last-child {
  margin-top: 24px;
}

.btn:last-child button {
  background: transparent;
  color: #409eff;
  border: 1px solid #409eff;
}

.btn:last-child button:hover {
  background: #f0f7ff;
}


</style>

