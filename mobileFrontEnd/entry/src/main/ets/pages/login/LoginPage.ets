// pages/LoginPage.ets
import router from '@ohos.router';
import preferences from '@ohos.data.preferences';
import promptAction from '@ohos.promptAction';
import { HttpUtil } from '../common/HttpUtil';

// 定义接口类型
interface LoginParams {
  phone: string;
  password: string;
}

interface LoginData {
  patientId: string;
  name: string;
  phone: string;
  token: string;
  expireTime: number;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

@Entry
@Component
struct LoginPage {
  @State phone: string = '';
  @State password: string = '';
  @State isLoading: boolean = false;
  @State phoneError: string = '';
  @State passwordError: string = '';
  @State showPassword: boolean = false;

  private baseUrl: string = 'http://你的服务器IP:8080/api/patient';

  // 简单的前端验证
  private validateForm(): boolean {
    let isValid = true;

    if (!this.phone.trim()) {
      this.phoneError = '请输入手机号';
      isValid = false;
    } else {
      this.phoneError = '';
    }

    if (!this.password.trim()) {
      this.passwordError = '请输入密码';
      isValid = false;
    } else {
      this.passwordError = '';
    }

    return isValid;
  }

  // 保存用户信息
  private async saveUserInfo(userInfo: LoginData): Promise<void> {
    try {
      const context = getContext(this);
      const prefs = await preferences.getPreferences(context, 'user_prefs');
      await prefs.put('isLogin', true);
      await prefs.put('patientId', userInfo.patientId);
      await prefs.put('name', userInfo.name);
      await prefs.put('phone', userInfo.phone);
      await prefs.put('token', userInfo.token);
      await prefs.flush();
    } catch (error) {
      console.error('保存用户信息失败：' + JSON.stringify(error));
    }
  }

  // 登录
  private async login(): Promise<void> {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;

    const context = getContext(this);

    const requestData: LoginParams = {
      phone: this.phone,
      password: this.password
    };

    try {
      const result = await HttpUtil.post<ApiResponse<LoginData>>(
        this.baseUrl + '/auth/login',
        requestData,
        context
      );

      if (result.code === 200) {
        await this.saveUserInfo(result.data);
        promptAction.showToast({
          message: '登录成功',
          duration: 2000
        });

        setTimeout(() => {
          router.pushUrl({
            url: 'pages/HomePage',
            params: {
              patientId: result.data.patientId,
              name: result.data.name
            }
          });
        }, 500);
      } else {
        promptAction.showToast({
          message: result.message,
          duration: 2000
        });
      }
    } catch (error) {
      console.error('登录失败：' + JSON.stringify(error));
      promptAction.showToast({
        message: '网络请求失败，请稍后重试',
        duration: 2000
      });
    } finally {
      this.isLoading = false;
    }
  }

  // 跳转注册页
  private goToRegister(): void {
    router.pushUrl({
      url: 'pages/RegisterPage'
    });
  }

  // 跳转找回密码页
  private goToForgotPassword(): void {
    router.pushUrl({
      url: 'pages/ForgotPasswordPage'
    });
  }

  build() {
    Column() {
      Column() {
        Text('智慧医疗')
          .fontSize(28)
          .fontWeight(FontWeight.Bold)
          .fontColor('#333333')
          .margin({ top: 80, bottom: 60 })
      }
      .width('100%')

      Column() {
        // 手机号输入框
        Column() {
          Row() {
            Text('📱')
              .fontSize(20)
              .margin({ right: 12 })

            TextInput({ placeholder: '请输入手机号', text: this.phone })
              .layoutWeight(1)
              .height(48)
              .backgroundColor(Color.Transparent)
              .fontSize(16)
              .type(InputType.PhoneNumber)
              .maxLength(11)
              .onChange((value: string) => {
                this.phone = value;
                if (this.phoneError) {
                  this.phoneError = '';
                }
              })
          }
          .width('100%')
          .height(52)
          .padding({ left: 16, right: 16 })
          .borderRadius(8)
          .backgroundColor('#F5F5F5')

          if (this.phoneError) {
            Text(this.phoneError)
              .fontSize(12)
              .fontColor('#FF4444')
              .margin({ top: 4, left: 16 })
          }
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 密码输入框
        Column() {
          Row() {
            Text('🔒')
              .fontSize(20)
              .margin({ right: 12 })

            TextInput({ placeholder: '请输入密码', text: this.password })
              .layoutWeight(1)
              .height(48)
              .backgroundColor(Color.Transparent)
              .fontSize(16)
              .type(this.showPassword ? InputType.Normal : InputType.Password)
              .onChange((value: string) => {
                this.password = value;
                if (this.passwordError) {
                  this.passwordError = '';
                }
              })

            Text(this.showPassword ? '👁️' : '👁️‍🗨️')
              .fontSize(20)
              .onClick(() => {
                this.showPassword = !this.showPassword;
              })
          }
          .width('100%')
          .height(52)
          .padding({ left: 16, right: 16 })
          .borderRadius(8)
          .backgroundColor('#F5F5F5')

          if (this.passwordError) {
            Text(this.passwordError)
              .fontSize(12)
              .fontColor('#FF4444')
              .margin({ top: 4, left: 16 })
          }
        }
        .margin({ bottom: 12 })
        .width('100%')

        // 忘记密码
        Row() {
          Text('忘记密码？')
            .fontSize(14)
            .fontColor('#007AFF')
            .onClick(() => this.goToForgotPassword())
        }
        .width('100%')
        .justifyContent(FlexAlign.End)
        .margin({ bottom: 30 })

        // 登录按钮
        Button('登 录')
          .width('100%')
          .height(48)
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .backgroundColor(this.isLoading ? '#CCCCCC' : '#007AFF')
          .borderRadius(24)
          .enabled(!this.isLoading)
          .onClick(() => this.login())

        if (this.isLoading) {
          LoadingProgress()
            .width(40)
            .height(40)
            .color('#007AFF')
            .margin({ top: 16 })
        }

        // 注册入口
        Row() {
          Text('还没有账号？')
            .fontSize(14)
            .fontColor('#666666')

          Text('立即注册')
            .fontSize(14)
            .fontColor('#007AFF')
            .fontWeight(FontWeight.Medium)
            .margin({ left: 8 })
            .onClick(() => this.goToRegister())
        }
        .margin({ top: 30 })
      }
      .width('100%')
      .padding(24)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#FFFFFF')
  }
}