import "./assets/css/App.scss";

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

import {createApp} from "vue";
import App from "./App.vue";
import Router from "./Router.ts";

createApp(App)
    .use(Router)
    .use(ElementPlus)
    .mount("#app");
