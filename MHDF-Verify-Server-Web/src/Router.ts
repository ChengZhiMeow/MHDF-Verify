import {createRouter, createWebHistory} from "vue-router";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/admin/dashboard",
            name: "admin dashboard",
            component: () => import("./views/admin/Dashboard.vue")
        },
        {
            path: "/admin/plugins",
            name: "admin plugins manager",
            component: () => import("./views/admin/Plugins.vue")
        },
        {
            path: "/admin/users",
            name: "admin user manager",
            component: () => import("./views/admin/Users.vue")
        },
        {
            path: "/admin/about",
            name: "about dashboard",
            component: () => import("./views/admin/About.vue")
        },
        {
            path: "/admin/login",
            name: "admin login",
            component: () => import("./views/admin/Login.vue")
        },
        {
            path: "/not_found",
            name: "Not found",
            component: () => import("./views/NotFound.vue")
        },
        {
            path: "/:catchAll(.*)",
            redirect: "/not_found"
        }
    ]
});

export default router;