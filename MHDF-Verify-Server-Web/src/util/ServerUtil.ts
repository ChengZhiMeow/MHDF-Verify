import axios from "axios";

import {addAlert} from "./AlertUtil.ts";

export const serverHost: string = window.location.origin;
export let version = "1.0.0";
export let startLong = -1;
export let pluginCount = -1;
export let userCount = -1;
export let clientCount = -1;
export let apiCallCount: any;
export let pluginOnlineCount: any;

/**
 * 更新后端服务器信息
 */
export const fetchServerInfo = async () => {
    try {
        let response = await axios.get(serverHost + "/api/server/get_info", {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            startLong = data.data.start_time;
            version = data.data.version;
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};

/**
 * 更新插件数量
 */
export const fetchPluginCount = async () => {
    try {
        let response = await axios.get(serverHost + "/api/server/get_plugin_count", {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            pluginCount = data.data.count;
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};

/**
 * 更新插件数量
 */
export const fetchUserCount = async () => {
    try {
        let response = await axios.get(serverHost + "/api/server/get_user_count", {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            userCount = data.data.count;
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};

/**
 * 更新客户端数量
 */
export const fetchClientCount = async () => {
    try {
        let response = await axios.get(serverHost + "/api/server/get_client_count", {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            clientCount = data.data.count;
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};

/**
 * 更新API调用次数
 */
export const fetchApiCallCount = async () => {
    try {
        let response = await axios.get(serverHost + "/api/server/get_api_call_count", {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            apiCallCount = data.data;
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};

/**
 * 更新API调用次数
 */
export const fetchPluginOnlineCount = async () => {
    try {
        let response = await axios.get(serverHost + "/api/server/get_plugin_online_count", {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            pluginOnlineCount = data.data;
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};