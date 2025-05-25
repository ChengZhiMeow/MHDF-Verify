import {ref} from "vue";

export const hello = ref("时间获取失败");

/**
 * 更新问好语句
 */
export const fetchHello = async () => {
    let currentDate = new Date();
    let hours = currentDate.getHours();
    if (hours >= 0 && hours < 6) {
        hello.value = "凌晨好";
    } else if (hours >= 6 && hours < 8) {
        hello.value = "早晨好";
    } else if (hours >= 8 && hours < 11) {
        hello.value = "早上好";
    } else if (hours >= 11 && hours < 13) {
        hello.value = "中午好";
    } else if (hours >= 13 && hours < 18) {
        hello.value = "下午好";
    } else if (hours >= 18 && hours < 20) {
        hello.value = "傍晚好";
    } else if (hours >= 20) {
        hello.value = "晚上好";
    }
};