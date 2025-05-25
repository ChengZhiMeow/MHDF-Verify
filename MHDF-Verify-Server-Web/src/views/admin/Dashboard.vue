<script lang="ts" setup>
import {Clock, Monitor, TakeawayBox, User} from "@element-plus/icons-vue";
import {onMounted, onUnmounted, ref} from "vue";

import "../../assets/css/admin/Dashboard.scss";
import {
  apiCallCount,
  clientCount,
  fetchApiCallCount,
  fetchClientCount,
  fetchPluginCount,
  fetchServerInfo,
  fetchUserCount,
  pluginCount,
  startLong,
  userCount
} from "../../util/ServerUtil.ts";
import manager from "./Manager.vue";
import * as echarts from "echarts";

const onlineTime = ref("0小时0分0秒");
const fetchOnlineTime = async () => {
  if (startLong <= 0) {
    onlineTime.value = "0小时0分0秒";
    return;
  }
  const now = Date.now();
  const diff = now - startLong;

  const totalSeconds = Math.floor(diff / 1000);
  const hours = Math.floor(totalSeconds / 3600);
  const remainingSeconds = totalSeconds % 3600;
  const minutes = Math.floor(remainingSeconds / 60);
  const seconds = remainingSeconds % 60;
  onlineTime.value = `${hours}小时${minutes}分${seconds}秒`;
};

const getApiCallCountSeries = async (): Promise<any> => {
  const sortedTimes = Object.keys(apiCallCount).sort((a, b) => {
    const [aH, aM] = a.split(":").map(Number);
    const [bH, bM] = b.split(":").map(Number);
    return (aH * 60 + aM) - (bH * 60 + bM);
  });

  const apis = Array.from(
      sortedTimes.reduce((set, time) => {
        Object.keys(apiCallCount[time]).forEach(api => set.add(api));
        return set;
      }, new Set<string>())
  );

  return apis.map(api => ({
    name: api,
    type: "line",
    smooth: true,
    areaStyle: {opacity: 0.05},
    data: sortedTimes.map(time => (apiCallCount[time]?.[api] ?? 0))
  }));
};

const apiCallCountChart = ref();
const drawApiCallCountChart = async () => {
  if (!apiCallCountChart.value) {
    apiCallCountChart.value = echarts.init(document.getElementById("apiCallCount"), null, {renderer: "svg"});
    window.addEventListener("resize", function () {
      apiCallCountChart.value.resize();
    });
    apiCallCountChart.value.showLoading();
  }

  fetchApiCallCount().finally(async () => {
    const series = await getApiCallCountSeries();
    const legendData = series.map((entity: unknown) => (entity as { name: string }).name);
    const xAxisData = apiCallCount ? Object.keys(apiCallCount) : [];

    apiCallCountChart.value.hideLoading();
    apiCallCountChart.value.setOption({
      title: {
        text: "API调用情况"
      },
      tooltip: {},
      legend: {
        align: "left",
        left: "3%",
        top: "15%",
        data: legendData
      },
      grid: {
        top: "30%",
        left: "5%",
        right: "5%",
        bottom: "5%",
        containLabel: true
      },

      xAxis: {
        type: "category",
        axisTick: {
          alignWithLabel: true
        },
        data: xAxisData

      },
      yAxis: {
        type: "value",
        splitNumber: 4,
        interval: 20
      },

      series: series
    });
  });
};

const taskId: number[] = [];

onMounted(() => {
  fetchServerInfo();
  fetchPluginCount();
  fetchUserCount();
  fetchClientCount();

  setTimeout(() => {
    fetchOnlineTime();
    drawApiCallCountChart();

    taskId.push(setInterval(drawApiCallCountChart, 5000));
    taskId.push(setInterval(fetchOnlineTime, 1000));
  }, 500);
});

onUnmounted(() => {
  taskId.map((id) => {
    clearInterval(id);
  });
});
</script>

<template>
  <manager active="1-1" class="main">
    <div class="data-row">
      <div class="data">
        <div class="title">
          <el-icon class="icon" size="32px">
            <Clock/>
          </el-icon>
          <h3>运行时间</h3>
        </div>
        <div class="subtitle">
          <h4>{{ onlineTime }}</h4>
        </div>
      </div>
      <div class="data">
        <div class="title">
          <el-icon class="icon" size="32px">
            <TakeawayBox/>
          </el-icon>
          <h3>插件数量</h3>
        </div>
        <div class="subtitle">
          <h4>{{ pluginCount }}个</h4>
        </div>
      </div>
      <div class="data">
        <div class="title">
          <el-icon class="icon" size="32px">
            <User/>
          </el-icon>
          <h3>用户数量</h3>
        </div>
        <div class="subtitle">
          <h4>{{ userCount }} 个</h4>
        </div>
      </div>
      <div class="data">
        <div class="title">
          <el-icon class="icon" size="32px">
            <Monitor/>
          </el-icon>
          <h3>总客户端在线数量</h3>
        </div>
        <div class="subtitle">
          <h4>{{ clientCount }} 个</h4>
        </div>
      </div>
    </div>
    <el-divider/>
    <div class="data-chart">
      <div id="apiCallCount" class="data">
      </div>
    </div>
  </manager>
</template>