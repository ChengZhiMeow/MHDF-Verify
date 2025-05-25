<script lang="ts" setup>
import {SwitchButton, Ticket} from "@element-plus/icons-vue";
import {onMounted} from "vue";

import "../../assets/css/admin/Manager.scss";
import {fetchUsername, username} from "../../util/admin/UserUtil.ts";
import {fetchHitokoto, hitokoto} from "../../util/HitokotoUtil.ts";
import {fetchHello, hello} from "../../util/TimeUtil.ts";
import axios from "axios";
import {serverHost} from "../../util/ServerUtil.ts";
import {addAlert} from "../../util/AlertUtil.ts";

defineProps({
  active: {
    type: String,
    required: true
  }
});

const logout = async () => {
  try {
    let response = await axios.post(serverHost + "/api/admin/logout", {}, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      window.location.assign("/admin/login");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

onMounted(() => {
  fetchHello();
  fetchUsername();
  fetchHitokoto();
});
</script>

<template>
  <el-container class="container">
    <el-aside class="sidebar">
      <el-scrollbar>
        <div class="title">
          <el-icon class="icon" size="24px">
            <Ticket/>
          </el-icon>
          <h1 class="text">梦之验证-控制台</h1>
        </div>

        <el-menu :default-active="active" :default-openeds="['1']" :router="true" class="menu">
          <el-sub-menu index="1">
            <template #title>验证管理</template>
            <el-menu-item class="button" index="1-1" route="dashboard">仪表盘</el-menu-item>
            <el-menu-item class="button" index="1-2" route="plugins">插件管理</el-menu-item>
            <el-menu-item class="button" index="1-3" route="users">用户管理</el-menu-item>
          </el-sub-menu>
          <el-menu-item class="button" index="1-4" route="about">关于</el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="menu">
      <el-header class="header">
        <div class="left">
          <div class="user">
            <h3>{{ username }}, {{ hello }}, 加油噢!</h3>
          </div>
          <div class="hitokoto">
            <h3>{{ hitokoto }}</h3>
          </div>
        </div>

        <div class="right">
          <button class="logout" type="button" @click="logout">
            <el-icon :size="32" class="icon">
              <SwitchButton/>
            </el-icon>
            <span>退出</span>
          </button>
        </div>
      </el-header>
      <el-main class="main">
        <slot></slot>
      </el-main>
    </el-container>
  </el-container>
</template>