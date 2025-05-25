/**
 * 将指定文本进行SHA256处理
 *
 * @param message 文本
 * @return SHA256
 */
export const sha256 = async (
    message: string
) => {
    const encoder = new TextEncoder();
    const data = encoder.encode(message);
    const hashBuffer = await crypto.subtle.digest("SHA-256", data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));

    return hashArray.map(b => b.toString(16).padStart(2, "0")).join("").toUpperCase();
};