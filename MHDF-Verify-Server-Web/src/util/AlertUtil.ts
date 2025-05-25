import {reactive, ref} from "vue";
import type {AlertProps} from "element-plus";

interface AlertItem {
    id: number;
    hide: boolean;
    title: string;
    type: AlertProps["type"];
}

export const alerts = ref<AlertItem[]>([]);
let uid = 0;

/**
 * 增加提示消息
 *
 * @param title 消息
 * @param type 类型
 */
export const addAlert = (
    title: string = "喵喵喵",
    type: AlertProps["type"] = "success"
) => {
    const alert = reactive<AlertItem>({
        id: uid++,
        hide: false,
        title,
        type
    });
    alerts.value.push(alert);

    setTimeout(() => {
        alert.hide = true;
        setTimeout(() => {
            alerts.value = alerts.value.filter(a => a.id !== alert.id);
        }, 500);
    }, 5000);
};