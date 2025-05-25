<script lang="ts" setup>
import {onUnmounted} from "vue";

import "../assets/css/Alert.scss";
import {alerts} from "../util/AlertUtil.ts";

onUnmounted(() => {
  alerts.value = [];
});
</script>

<template>
  <div class="alerts">
    <TransitionGroup name="alertTransition">
      <el-alert
          v-for="alert in alerts"
          v-show="!alert.hide"
          :key="alert.id"
          :title="alert.title"
          :type="alert.type"
          show-icon
      />
    </TransitionGroup>
  </div>
</template>
