<script lang="ts" setup>
import {ElMessageBox} from "element-plus";
import {onMounted, reactive, ref} from "vue";
import axios from "axios";

import "../../assets/css/admin/Plugins.scss";
import {freshPluginList, pluginList} from "../../util/admin/PluginUtil.ts";
import {addAlert} from "../../util/AlertUtil.ts";
import {serverHost} from "../../util/ServerUtil.ts";
import manager from "./Manager.vue";

const createMenuVisible = ref(false);
const createData = reactive({
  name: "",
  version: "",
  client: -1
});

const createPlugin = async () => {
  if (!createData.name) {
    addAlert("请输入插件名称!", "error");
    return;
  }

  if (!createData.version) {
    addAlert("请输入插件版本!", "error");
    return;
  }

  try {
    let response = await axios.post(serverHost + "/api/plugin/create", {
      plugin: createData.name,
      version: createData.version,
      client: createData.client
    }, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      createMenuVisible.value = false;
      createData.name = "";
      createData.version = "";
      await freshPluginList();
      addAlert("创建成功!");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

const changeMenuVisible = ref(false);
const changeData = ref(reactive({
  name: "",
  version: "",
  client: -1
}));

const changePlugin = async () => {
  if (!changeData.value.version) {
    addAlert("请输入插件版本!", "error");
    return;
  }

  try {
    let response = await axios.post(serverHost + "/api/plugin/change_data", {
      plugin: changeData.value.name,
      version: changeData.value.version,
      client: changeData.value.client
    }, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      changeMenuVisible.value = false;
      await freshPluginList();
      addAlert("修改成功!");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

const sendDeletePluginMessage = async (
    plugin: string
) => {
  ElMessageBox.confirm(
      "你确定要删除这个插件吗?",
      "警告",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
  )
      .then(() => {
        deletePlugin(plugin);
      });
};

const deletePlugin = async (
    plugin: string
) => {
  try {
    let response = await axios.post(serverHost + "/api/plugin/remove", {
      plugin: plugin
    }, {
      withCredentials: true
    });
    let data = await response.data;

    if (data.id != 200) {
      addAlert(data.msg, "error");
    } else {
      await freshPluginList();
      addAlert("删除成功!");
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

onMounted(() => {
  freshPluginList(1);
});
</script>

<template>
  <manager active="1-2" class="main">
    <div class="bar">
      <div class="right">
        <el-button type="primary" @click="createMenuVisible = true">新增</el-button>
      </div>
    </div>
    <el-divider/>
    <el-table :data="pluginList?.pluginList" class="data">
      <el-table-column label="名称" prop="name"/>
      <el-table-column label="版本" prop="version"/>
      <el-table-column label="最大IP上限" prop="client"/>
      <el-table-column align="right">
        <template #default="scope">
          <el-button type="primary" @click="changeData={
            name: scope.row.name,
            version: scope.row.version,
            client: scope.row.client
          };
          changeMenuVisible = true"
          >
            编辑
          </el-button>
          <el-button type="danger" @click="sendDeletePluginMessage(scope.row.name)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-divider/>
    <div class="page">
      <el-pagination :total="(pluginList?.maxPages?? 1)*10" background layout="prev, pager, next"
                     @current-change="freshPluginList"/>
    </div>

    <el-dialog v-model="createMenuVisible" class="message-menu" title="新增插件" width="800">
      <el-form :model="createData">
        <div class="input">
          <input id="name" ref="nameInput" v-model="createData.name" placeholder=" ">
          <label for="name">插件名称</label>
        </div>
        <div class="input">
          <input id="version" ref="versionInput" v-model="createData.version" placeholder=" ">
          <label for="version">插件版本</label>
        </div>
        <div class="input">
          <input id="client" ref="clientInput" v-model="createData.client" placeholder=" ">
          <label for="client">最大IP限制</label>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="createPlugin">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="changeMenuVisible" class="message-menu" title="编辑插件" width="800">
      <el-form :model="changeData">
        <div class="input">
          <input id="version" ref="versionInput" v-model="changeData.version" placeholder=" ">
          <label for="version">插件版本</label>
        </div>
        <div class="input">
          <input id="client" ref="clientInput" v-model="changeData.client" placeholder=" ">
          <label for="client">最大IP限制</label>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="changePlugin">确认</el-button>
      </template>
    </el-dialog>
  </manager>
</template>