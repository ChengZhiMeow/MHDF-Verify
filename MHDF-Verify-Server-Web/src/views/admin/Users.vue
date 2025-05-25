<script lang="ts" setup>
import {ElMessageBox} from "element-plus";
import {onMounted, reactive, ref} from "vue";
import axios from "axios";

import "../../assets/css/admin/User.scss";
import {freshUserList, userList} from "../../util/admin/UserUtil.ts";
import {addAlert} from "../../util/AlertUtil.ts";
import {serverHost} from "../../util/ServerUtil.ts";
import manager from "./Manager.vue";

const createMenuVisible = ref(false);
const createData = reactive({
  name: "",
  password: ""
});

const createUser = async () => {
  if (!createData.name) {
    addAlert("请输入用户名称!", "error");
    return;
  }

  if (!createData.password) {
    addAlert("请输入用户密码!", "error");
    return;
  }

  try {
    let response = await axios.post(serverHost + "/api/user/create", {
      user: createData.name,
      password: createData.password
    }, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      createMenuVisible.value = false;
      createData.name = "";
      createData.password = "";
      await freshUserList();
      addAlert("创建成功!");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

const changeMenuVisible = ref(false);
const changeData = ref(reactive({
  name: "",
  password: "",
  plugins: []
}));

const changeUser = async () => {
  try {
    let response = await axios.post(serverHost + "/api/user/change_data", {
      user: changeData.value.name,
      password: changeData.value.password ?? "noChange",
      plugins: ((changeData.value.plugins || []) as any[]).join(",")
    }, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      changeMenuVisible.value = false;
      await freshUserList();
      addAlert("修改成功!");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

const sendDeleteUserMessage = async (
    user: string
) => {
  ElMessageBox.confirm(
      "你确定要删除这个用户吗?",
      "警告",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
  )
      .then(() => {
        deleteUser(user);
      });
};

const deleteUser = async (
    user: string
) => {
  try {
    let response = await axios.post(serverHost + "/api/user/remove", {
      user: user
    }, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      await freshUserList();
      addAlert("删除成功!");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

onMounted(() => {
  freshUserList(1);
});
</script>

<template>
  <manager active="1-3" class="main">
    <div class="bar">
      <div class="right">
        <el-button type="primary" @click="createMenuVisible = true">新增</el-button>
      </div>
    </div>

    <el-divider/>

    <el-table :data="userList?.userList" class="data">
      <el-table-column label="名称" prop="name"/>
      <el-table-column label="拥有的插件">
        <template #default="scope">
          <el-tag v-for="data in scope.row.buyPluginList"
                  class="tag"
                  type="primary"
          >
            {{ data }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="right">
        <template #default="scope">
          <el-button type="primary"
                     @click="changeData={
                       name: scope.row.name,
                       password: '',
                       plugins: scope.row.buyPluginList
                     }; changeMenuVisible = true;"
          >
            编辑
          </el-button>
          <el-button type="danger" @click="sendDeleteUserMessage(scope.row.name)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-divider/>

    <div class="page">
      <el-pagination :total="(userList?.maxPages?? 1)*10" background layout="prev, pager, next"
                     @current-change="freshUserList"/>
    </div>

    <el-dialog v-model="createMenuVisible" class="message-menu" title="新增用户" width="800">
      <el-form :model="createData">
        <div class="input">
          <input id="name" ref="nameInput" v-model="createData.name" placeholder=" ">
          <label for="name">用户名称</label>
        </div>
        <div class="input">
          <input id="password" ref="passwordInput" v-model="createData.password" placeholder=" ">
          <label for="password">用户密码</label>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="createUser">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="changeMenuVisible" class="message-menu" title="编辑用户" width="800">
      <el-form :model="changeData">
        <div class="input">
          <input id="password" ref="passwordInput" v-model="changeData.password" placeholder=" ">
          <label for="password">新密码</label>
        </div>

        <div class="tag-input">
          <el-input-tag
              id="plugins"
              ref="pluginsInput"
              v-model="changeData.plugins"
              clearable
              delimiter=","
              tag-type="primary"
          />
          <label for="plugins">插件列表</label>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="changeUser">确认</el-button>
      </template>
    </el-dialog>
  </manager>
</template>