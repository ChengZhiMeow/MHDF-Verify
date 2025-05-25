<script lang="ts" setup>
import {onMounted} from "vue";

import "../assets/css/NotFound.scss";
import {fetchHitokoto, hitokoto} from "../util/HitokotoUtil.ts";

onMounted(() => {
  fetchHitokoto();
});
</script>

<template>
  <div class="container">
    <div class="title">
      <img alt="" src="../assets/icons/Error.png">
      <h2>页面走掉惹喵QAQ</h2>
    </div>
    <div class="info">
      <h4>{{ hitokoto }}</h4>
    </div>
  </div>
</template>