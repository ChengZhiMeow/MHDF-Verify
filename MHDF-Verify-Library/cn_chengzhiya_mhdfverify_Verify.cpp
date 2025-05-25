#include <jni.h>
#include <string>
#include <curl/curl.h>
#include "cn_chengzhiya_mhdfverify_Verify.h"

// HTTP 响应数据
struct HttpResponse {
    std::string data;
    long status_code;
};

// curl 响应数据的回调
static size_t WriteCallback(void* contents, size_t size, size_t nmemb, HttpResponse* response) {
    size_t totalSize = size * nmemb;
    response->data.append((char*)contents, totalSize);
    return totalSize;
}

// 将Java字符串转为Cpp字符串
std::string jstringToString(JNIEnv* env, jstring jstr) {
    if (jstr == nullptr) return "";
    const char* cstr = env->GetStringUTFChars(jstr, nullptr);
    std::string result(cstr);
    env->ReleaseStringUTFChars(jstr, cstr);
    return result;
}

// 提取token的辅助函数
std::string parseToken(const std::string& json) {
    size_t tokenKeyPos = json.find("\"token\":\"");
    if (tokenKeyPos == std::string::npos) return "";
    
    size_t tokenStart = tokenKeyPos + 9; // Skip "\"token\":\""
    size_t tokenEnd = json.find("\"", tokenStart);
    if (tokenEnd == std::string::npos) return "";
    
    return json.substr(tokenStart, tokenEnd - tokenStart);
}

// 手动拼接Json请求体
std::string buildJsonBody(const std::string& user, const std::string& password, const std::string& plugin) {
    std::string json = "{"
        "\"user\":\"" + user + "\","
        "\"password\":\"" + password + "\","
        "\"plugin\":\"" + plugin + "\""
    "}";
    return json;
}


JNIEXPORT jstring JNICALL Java_cn_chengzhiya_mhdfverify_Verify_check
(JNIEnv* env, jobject obj, jstring jhost, jstring juser, jstring jpassword, jstring jpluginName) {
    
    // 字符串转换
    std::string host = jstringToString(env, jhost);
    std::string user = jstringToString(env, juser);
    std::string password = jstringToString(env, jpassword);
    std::string pluginName = jstringToString(env, jpluginName);
    
    // 初始化curl
    CURL* curl = curl_easy_init();
    if (!curl) return JNI_FALSE; // 如果失败, 返回false

    // 构建URL
    std::string url = host + "/api/verify/check";

    // 构建JSON请求体
    std::string jsonString = buildJsonBody(user, password, pluginName);
    
    // 准备接收响应
    HttpResponse response;
    
    // 配置curl参数
    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
    curl_easy_setopt(curl, CURLOPT_POST, 1L);
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonString.c_str());
    curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonString.length());
    struct curl_slist* headers = nullptr;
    headers = curl_slist_append(headers, "Content-Type: application/json");
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
    curl_easy_setopt(curl, CURLOPT_TIMEOUT, 30L);
    curl_easy_setopt(curl, CURLOPT_CONNECTTIMEOUT, 10L);
    
    // 请求
    CURLcode res = curl_easy_perform(curl);
    std::string token;
    if (res == CURLE_OK) {
        curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &response.status_code); // 请求成功获取HTTP状态码
        if (response.status_code == 503) { token = parseToken(response.data); }
    }
    
    // 清理
    curl_slist_free_all(headers);
    curl_easy_cleanup(curl);

    // 返回
    if (!token.empty()) {
        return env->NewStringUTF(token.c_str());
    } else {
        return NULL;
    }
}