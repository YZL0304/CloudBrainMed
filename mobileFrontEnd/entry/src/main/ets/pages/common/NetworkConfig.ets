// common/NetworkConfig.ets
export class NetworkConfig {
  // 开发环境地址
  static readonly DEV_BASE_URL: string = 'http://172.22.101.145:8002/api';

  // 生产环境地址
  static readonly PROD_BASE_URL: string = 'https://api.cloudbrainmed.com/api';

  // 当前环境
  static readonly IS_DEV: boolean = true;

  static getBaseUrl(): string {
    // 不使用 this，直接使用类名
    return NetworkConfig.IS_DEV ? NetworkConfig.DEV_BASE_URL : NetworkConfig.PROD_BASE_URL;
  }

  static getPatientApi(): string {
    return NetworkConfig.getBaseUrl() + '/patient';
  }
}