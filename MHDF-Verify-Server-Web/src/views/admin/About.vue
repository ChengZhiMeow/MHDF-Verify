<script lang="ts" setup>
import {Tickets} from "@element-plus/icons-vue";

import "../../assets/css/admin/About.scss";
import manager from "./Manager.vue";
import {version} from "../../util/ServerUtil.ts";
</script>

<template>
  <manager active="1-4" class="main">
    <div class="info">
      <div class="title">
        <el-icon class="icon" size="32px">
          <Tickets/>
        </el-icon>
        <h3>服务信息</h3>
      </div>
      <div class="subtitle">
        <h4>梦之验证版本: {{ version }}</h4>
      </div>
      <div class="subtitle">
        <h4>作者: 橙汁喵</h4>
      </div>
      <div class="subtitle">
        <h4>精神支柱: 小默米(Xiao-MoMi)、宁枫镇腐竹 (MFD7-10)</h4>
      </div>
      <div class="subtitle">
        <h4>技术支持: 雪旭 (Sn0wo2)、那谁 (Catnies)</h4>
      </div>
      <div class="subtitle">
        <h4>特别感谢: 使用本程序的你</h4>
      </div>
    </div>
  </manager>
</template>