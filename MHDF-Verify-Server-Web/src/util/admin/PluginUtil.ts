import axios from "axios";

import {serverHost} from "../ServerUtil.ts";
import {addAlert} from "../AlertUtil.ts";
import {ref} from "vue";

interface PluginItem {
    name: string;
    version: string;
    client: number;
}

interface PluginListItem {
    maxPages: number,
    pluginList: PluginItem[]
}

export const pluginList = ref<PluginListItem>();
export let lastPage = 1;

/**
 * 获取插件列表实例
 */
export const freshPluginList = async (page: number = lastPage) => {
    try {
        lastPage = page;

        let response = await axios.get(serverHost + "/api/plugin/get_list?page=" + page, {
            withCredentials: true
        });
        let data = response.data;

        if (data.id == 200) {
            const newPluginList: PluginItem[] = [];
            Object.keys(data.data.plugins).map((plugin) => {
                newPluginList.push({
                    name: plugin,
                    version: data.data.plugins[plugin].version,
                    client: data.data.plugins[plugin].client
                });
            });

            pluginList.value = {
                maxPages: data.data.maxPages,
                pluginList: newPluginList
            };
        } else {
            addAlert(data.msg, "error");
        }
    } catch (error) {
        console.error("数据获取失败:", error);
    }
};