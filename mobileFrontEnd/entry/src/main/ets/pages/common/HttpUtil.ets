// common/HttpUtil.ets
import http from '@ohos.net.http';
import preferences from '@ohos.data.preferences';
import promptAction from '@ohos.promptAction';
import router from '@ohos.router';

// 定义请求配置接口
interface RequestConfig {
  url: string;
  method: http.RequestMethod;
  data?: object;
  header?: Record<string, string>;
}

export class HttpUtil {

  // 获取存储的token
  static async getToken(context: Context): Promise<string> {
    try {
      const prefs = await preferences.getPreferences(context, 'user_prefs');
      const tokenValue = await prefs.get('token', '');
      if (typeof tokenValue === 'string') {
        return tokenValue;
      }
      return '';
    } catch (error) {
      console.error('获取token失败：' + JSON.stringify(error));
      return '';
    }
  }

  // 核心请求方法
  static async request<T>(options: RequestConfig, context: Context): Promise<T> {
    let httpRequest = http.createHttp();
    const token = await HttpUtil.getToken(context);

    // 构建headers
    let headers: Record<string, string> = {
      'Content-Type': 'application/json'
    };

    // 添加自定义header
    if (options.header) {
      const headerKeys = Object.keys(options.header);
      for (let i = 0; i < headerKeys.length; i++) {
        const key = headerKeys[i];
        headers[key] = options.header[key];
      }
    }

    // 添加token
    if (token && token.length > 0) {
      headers['Authorization'] = 'Bearer ' + token;
    }

    try {
      let response = await httpRequest.request(options.url, {
        method: options.method,
        header: headers,
        extraData: options.data ? JSON.stringify(options.data) : undefined,
        connectTimeout: 30000,
        readTimeout: 30000
      });

      if (response.responseCode === 200) {
        let result = JSON.parse(response.result as string) as T;
        return result;
      } else if (response.responseCode === 401) {
        promptAction.showToast({
          message: '登录已过期，请重新登录',
          duration: 2000
        });
        router.replaceUrl({
          url: 'pages/LoginPage'
        });
        // ArkTS 要求 throw 的值必须是 Error 类型
        throw new Error('Unauthorized');
      } else {
        throw new Error('Request failed with code: ' + response.responseCode);
      }
    } catch (error) {
      console.error('网络请求失败：' + JSON.stringify(error));
      // 确保抛出 Error 类型
      if (error instanceof Error) {
        throw error;
      } else {
        throw new Error(String(error));
      }
    } finally {
      httpRequest.destroy();
    }
  }

  // GET请求
  static async get<T>(url: string, context: Context, params?: Record<string, string>): Promise<T> {
    let fullUrl = url;
    if (params) {
      const keys = Object.keys(params);
      const queryParts: string[] = [];
      for (let i = 0; i < keys.length; i++) {
        const key = keys[i];
        const value = params[key];
        queryParts.push(encodeURIComponent(key) + '=' + encodeURIComponent(value));
      }
      const queryString = queryParts.join('&');
      fullUrl = url + '?' + queryString;
    }

    const requestConfig: RequestConfig = {
      url: fullUrl,
      method: http.RequestMethod.GET
    };
    return HttpUtil.request<T>(requestConfig, context);
  }

  // POST请求
  static async post<T>(url: string, data: object, context: Context): Promise<T> {
    const requestConfig: RequestConfig = {
      url: url,
      method: http.RequestMethod.POST,
      data: data
    };
    return HttpUtil.request<T>(requestConfig, context);
  }

  // PUT请求
  static async put<T>(url: string, data: object, context: Context): Promise<T> {
    const requestConfig: RequestConfig = {
      url: url,
      method: http.RequestMethod.PUT,
      data: data
    };
    return HttpUtil.request<T>(requestConfig, context);
  }

  // DELETE请求
  static async delete<T>(url: string, context: Context): Promise<T> {
    const requestConfig: RequestConfig = {
      url: url,
      method: http.RequestMethod.DELETE
    };
    return HttpUtil.request<T>(requestConfig, context);
  }
}