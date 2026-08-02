<template>
  <div class="page-wrap">
    <!-- 顶部数据卡片 -->
    <div class="stat-card-group">
      <div class="stat-card" v-for="(item, idx) in statList" :key="idx">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
      </div>
    </div>

    <!-- 图表区域 一屏网格布局 -->
    <div class="chart-container">
      <div class="chart-card chart-card-1">
        <div id="zong"></div>
      </div>
      <div class="chart-card chart-card-2">
        <div id="goodstop10"></div>
      </div>
      <div class="chart-card chart-card-3">
        <div id="order"></div>
      </div>
      <div class="chart-card chart-card-4">
        <div id="amount"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

let myChartzong = null
let myChartgoodstop10 = null
let myChartorder = null
let myChartamount = null
let timer = null
let globalTimer = null

// 判断是否深色模式
const isDark = document.documentElement.classList.contains('dark')
const statList = ref([])

// 数据总览 - 只更新数据，不重复初始化
async function zongtab() {
  const res = await axios.get("/api/admin/zongcount")
  if (res.data.code !== 0) return
  const shuju = res.data.data

  // 更新顶部卡片数据
  statList.value = [
    { label: '总用户', value: shuju.total_user },
    { label: '今日新增', value: shuju.today_new_user },
    { label: '商家', value: shuju.total_merchant },
    { label: '商品', value: shuju.total_goods },
    { label: '总订单', value: shuju.total_order },
    { label: '待发货', value: shuju.wait_delivery_order },
    { label: '待支付', value: shuju.wait_pay_order }
  ]

  const chartDom = document.getElementById('zong');
  if (!chartDom) return

  // 首次初始化
  if (!myChartzong) {
    myChartzong = echarts.init(chartDom);
  }

  const zongoption = {
    backgroundColor: isDark ? 'transparent' : '#fff',
    title: {
      text: '数据总览',
      left: 'center',
      textStyle: {
        color: isDark ? '#eee' : '#333'
      }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: isDark ? 'rgba(0,0,0,0.7)' : 'rgba(255,255,255,0.9)',
      textStyle: {
        color: isDark ? '#fff' : '#333'
      }
    },
    xAxis: {
      type: 'category',
      data: ['总用户', '今日新增用户', '商家数量', '商品总数', '总订单', '待发货订单', '待支付订单'],
      axisLine: {
        lineStyle: { color: isDark ? '#555' : '#eee' }
      },
      axisLabel: {
        color: isDark ? '#ccc' : '#666',
        interval: 0,
        rotate: 15
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: { color: isDark ? '#555' : '#eee' }
      },
      axisLabel: {
        color: isDark ? '#ccc' : '#666'
      },
      splitLine: {
        lineStyle: { color: isDark ? '#333' : '#eee' }
      }
    },
    series: [
      {
        data: [
          shuju.total_user,
          shuju.today_new_user,
          shuju.total_merchant,
          shuju.total_goods,
          shuju.total_order,
          shuju.wait_delivery_order,
          shuju.wait_pay_order
        ],
        type: 'bar',
        itemStyle: {
          color: '#409eff'
        }
      }
    ]
  };
  myChartzong.setOption(zongoption);
}

// 商品Top10
async function goodstop10() {
  const chartDom = document.getElementById('goodstop10')
  if (!chartDom) return
  if (!myChartgoodstop10) {
    myChartgoodstop10 = echarts.init(chartDom)
  }

  const res = await axios.get('/api/admin/getgoodstop10')
  if (res.data.code !== 0) return
  const list = res.data.data
  const yData = list.map(item => item.goods_name)
  const seriesData = list.map(item => item.count)

  const goodstop10option = {
    title: {
      text: '商品销量Top10',
      left: 'center',
      textStyle: { color: isDark ? '#eee' : '#333' }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: isDark ? 'rgba(0,0,0,0.7)' : '#fff',
      textStyle: { color: isDark ? '#fff' : '#333' }
    },
    xAxis: {
      max: 'dataMax',
      axisLine: { lineStyle: { color: isDark ? '#555' : '#eee' } },
      axisLabel: { color: isDark ? '#ccc' : '#666' },
      splitLine: { lineStyle: { color: isDark ? '#333' : '#eee' } }
    },
    yAxis: {
      type: 'category',
      data: yData,
      inverse: true,
      animationDuration: 300,
      animationDurationUpdate: 300,
      axisLine: { lineStyle: { color: isDark ? '#555' : '#eee' } },
      axisLabel: { color: isDark ? '#ccc' : '#666' }
    },
    series: [
      {
        realtimeSort: true,
        name: '销量',
        type: 'bar',
        data: seriesData,
        label: {
          show: true,
          position: 'right',
          valueAnimation: true,
          color: isDark ? '#fff' : '#333'
        },
        itemStyle: {
          color: '#409eff'
        }
      }
    ],
    legend: { show: false },
    animationDuration: 0,
    animationDurationUpdate: 3000,
    animationEasing: 'linear',
    animationEasingUpdate: 'linear'
  }
  myChartgoodstop10.setOption(goodstop10option)

  // 5秒刷新
  timer = setInterval(async () => {
    const newRes = await axios.get('/api/admin/getgoodstop10')
    if (newRes.data.code !== 0) return
    const newList = newRes.data.data
    const newSeriesData = newList.map(item => item.count)
    const newYData = newList.map(item => item.goods_name)
    myChartgoodstop10.setOption({
      yAxis: { data: newYData },
      series: [{ data: newSeriesData }]
    })
  }, 5000)
}

function bianli(sjlist, item) {
  let list = []
  for (let i = 0; i < sjlist.length; i++) {
    list.push(sjlist[i][item])
  }
  return list;
}

// 订单趋势
async function order() {
  const chartDom = document.getElementById('order');
  if (!chartDom) return
  if (!myChartorder) {
    myChartorder = echarts.init(chartDom);
  }

  const res = await axios.get("/api/admin/getordercount")
  if (res.data.code !== 0) return
  const shuju = res.data.data

  let orderoption = {
    title: {
      text: '一周订单趋势',
      left: 'center',
      textStyle: { color: isDark ? '#eee' : '#333' }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: isDark ? 'rgba(0,0,0,0.7)' : '#fff',
      textStyle: { color: isDark ? '#fff' : '#333' }
    },
    xAxis: {
      type: 'category',
      data: bianli(shuju, 'time_date'),
      axisLine: { lineStyle: { color: isDark ? '#555' : '#eee' } },
      axisLabel: { color: isDark ? '#ccc' : '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: isDark ? '#555' : '#eee' } },
      axisLabel: { color: isDark ? '#ccc' : '#666' },
      splitLine: { lineStyle: { color: isDark ? '#333' : '#eee' } }
    },
    series: [
      {
        data: bianli(shuju, 'count'),
        type: 'line',
        smooth: true
      }
    ]
  };
  myChartorder.setOption(orderoption);
}

// 成交金额
async function amount() {
  const chartDom = document.getElementById('amount');
  if (!chartDom) return
  if (!myChartamount) {
    myChartamount = echarts.init(chartDom);
  }

  const res = await axios.get("/api/admin/getamount")
  if (res.data.code !== 0) return
  const shuju = res.data.data

  let amountoption = {
    title: {
      text: '一周成交金额趋势(单位元)',
      left: 'center',
      textStyle: { color: isDark ? '#eee' : '#333' }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: isDark ? 'rgba(0,0,0,0.7)' : '#fff',
      textStyle: { color: isDark ? '#fff' : '#333' }
    },
    xAxis: {
      type: 'category',
      data: bianli(shuju, 'time_date'),
      axisLine: { lineStyle: { color: isDark ? '#555' : '#eee' } },
      axisLabel: { color: isDark ? '#ccc' : '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: isDark ? '#555' : '#eee' } },
      axisLabel: { color: isDark ? '#ccc' : '#666' },
      splitLine: { lineStyle: { color: isDark ? '#333' : '#eee' } }
    },
    series: [
      {
        data: bianli(shuju, 'count'),
        type: 'line',
        smooth: true
      }
    ]
  };
  myChartamount.setOption(amountoption);
}

onMounted(() => {
  // 首次初始化所有图表
  zongtab()
  goodstop10()
  order()
  amount()

  // 全局5秒刷新：总览、订单、金额
  globalTimer = setInterval(() => {
    zongtab()
    order()
    amount()
  }, 5000)
})

onUnmounted(() => {
  // 清空所有定时器
  if (timer) clearInterval(timer)
  if (globalTimer) clearInterval(globalTimer)
  // 销毁图表实例
  if (myChartzong) myChartzong.dispose()
  if (myChartgoodstop10) myChartgoodstop10.dispose()
  if (myChartorder) myChartorder.dispose()
  if (myChartamount) myChartamount.dispose()
})
</script>

<style scoped>
.page-wrap {
  padding: 10px;
  box-sizing: border-box;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.stat-card-group {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  margin-bottom: 10px;
  flex-shrink: 0;
}
.stat-card {
  flex: 1;
  padding: 10px 6px;
  border-radius: 8px;
  background: #ffc7c7;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  text-align: center;
}
:deep(.dark) .stat-card {
  background: #1f1f1f;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);
}
.stat-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}
:deep(.dark) .stat-label {
  color: #aaa;
}
.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
:deep(.dark) .stat-value {
  color: #eee;
}

.chart-container {
  display: grid;
  grid-template-columns: 58% 40%;
  grid-template-rows: 1fr 1fr;
  gap: 10px;
  flex: 1;
  min-height: 0;
}

.chart-card {
  padding: 8px;
  border-radius: 8px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  box-sizing: border-box;
}
:deep(.dark) .chart-card {
  background: #1f1f1f;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);
}

.chart-card-1 > #zong {
  width: 100%;
  height: 100%;
}
.chart-card-2 > #goodstop10 {
  width: 100%;
  height: 100%;
}
.chart-card-3 {
  grid-column: 1 / 2;
}
.chart-card-3 > #order {
  width: 100%;
  height: 100%;
}
.chart-card-4 {
  grid-column: 2 / 3;
}
.chart-card-4 > #amount {
  width: 100%;
  height: 100%;
}
</style>