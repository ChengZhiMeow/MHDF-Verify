<script lang="ts" setup>
import {onMounted, ref} from "vue";
import axios from "axios";

import "../../assets/css/admin/Login.scss";
import {addAlert} from "../../util/AlertUtil.ts";
import {sha256} from "../../util/Sha256Util.ts";
import {serverHost} from "../../util/ServerUtil.ts";

const userInput = ref<HTMLInputElement>();
const passwordInput = ref<HTMLInputElement>();

const login = async () => {
  let user = userInput.value?.value || "";
  let password = passwordInput.value?.value || "";

  if (!user) {
    addAlert("请输入账户!", "error");
    return;
  }

  if (!password) {
    addAlert("请输入密码!", "error");
    return;
  }

  try {
    let response = await axios.post(serverHost + "/api/admin/login", {
      data: await sha256(user + "|" + password)
    }, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      window.location.assign("/admin/dashboard");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

onMounted(async () => {
  let response = await axios.get(serverHost + "/api/server/get_info", {
    withCredentials: true
  });
  let data = response.data;

  if (data.id == 200) {
    window.location.assign("/admin/dashboard");
  }
});
</script>

<template>
  <div class="container">
    <div class="sidebar"/>
    <div class="menu">
      <div class="action">
        <div class="title">
          <h4>登录账号</h4>
          <p>欢迎来到梦之验证，请登录账号！</p>
        </div>
        <div class="bar">
          <ul>
            <li>
              <button style="color: #55bbfb" type="button">
                <span>登录</span>
              </button>
            </li>
          </ul>
          <span class="line"></span>
        </div>
        <div class="data">
          <div class="input">
            <input id="user" ref="userInput" placeholder=" " type="text">
            <label for="user">账号</label>
          </div>
          <div class="input">
            <input id="password" ref="passwordInput" placeholder=" " type="password">
            <label for="password">密码</label>
          </div>
        </div>
        <div class="confirm">
          <div class="button">
            <button type="button" @click="login">
              <span>登录</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
