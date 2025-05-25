import {ref} from "vue";
import axios from "axios";

export const hitokoto = ref("网络开小差了~");

/**
 * 更新一言
 */
export const fetchHitokoto = async () => {
    try {
        const response = await axios.get("https://v1.hitokoto.cn/");
        hitokoto.value = response.data.hitokoto;
    } catch (error) {
        console.error("获取一言失败:", error);
        hitokoto.value = "网络开小差了~";
    }
};