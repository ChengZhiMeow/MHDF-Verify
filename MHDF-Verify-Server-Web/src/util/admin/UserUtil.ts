import {ref} from "vue";
import axios from "axios";

import {serverHost} from "../ServerUtil.ts";
import {addAlert} from "../AlertUtil.ts";

export let username = "无名氏";

/**
 * 更新登录的用户数据
 */
export const fetchUsername = async () => {
    try {
        const response = await axios.get(serverHost + "/api/admin/get_user_info", {
            withCredentials: true
        });
        const data = response.data;

        if (data.id == 200) {
            username = data.data.name;
        }
    } catch (error) {
        username = "无名氏";
        console.error("数据获取失败:", error);
    }
};

interface UserItem {
    name: string;
    buyPluginList: string[];
}

interface UserItemItem {
    maxPages: number,
    userList: UserItem[]
}

export const userList = ref<UserItemItem>();
export let lastPage = 1;

/**
 * 获取插件列表实例
 */
export const freshUserList = async (page: number = lastPage) => {
    try {
        lastPage = page;

        let response = await axios.get(serverHost + "/api/user/get_list?page=" + page, {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            const newUserList: UserItem[] = [];
            Object.keys(data.data.users).map((user) => {
                newUserList.push({
                    name: user,
                    buyPluginList: (data.data.users[user] as { buy_plugin_list: string[] }).buy_plugin_list
                });
            });

            userList.value = {
                maxPages: data.data.maxPages,
                userList: newUserList
            };
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};