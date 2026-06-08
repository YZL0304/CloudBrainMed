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

  @State phoneFocused: boolean = false;
  @State passwordFocused: boolean = false;

  private baseUrl: string = 'http://172.22.101.145:8002/auth-service';

  // 页面出现时获取路由参数
  aboutToAppear() {
    // 获取从注册页传递的参数
    const params = router.getParams() as Record<string, string>;
    if (params) {
      const phoneParam = params['phone'];
      const passwordParam = params['password'];

      if (phoneParam) {
        this.phone = phoneParam;
      }
      if (passwordParam) {
        this.password = passwordParam;
      }

      // 如果同时有手机号和密码，可以自动填充后提示用户
      if (phoneParam && passwordParam) {
        promptAction.showToast({
          message: '注册成功，请登录',
          duration: 2000
        });
      }
    }
  }

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

    console.log('========== 登录请求开始 ==========');
    console.log('手机号: ' + this.phone);
    console.log('密码: ' + this.password);
    console.log('请求URL: ' + this.baseUrl + '/patient/login');

    this.isLoading = true;

    const context = getContext(this);

    const requestData: LoginParams = {
      phone: this.phone,
      password: this.password
    };

    try {
      const result = await HttpUtil.post<ApiResponse<LoginData>>(
        this.baseUrl + '/patient/login',
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
            url: 'pages/Index',
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
      url: 'pages/login/RegisterPage'
    });
  }

  // 跳转找回密码页
  private goToForgotPassword(): void {
    router.pushUrl({
      url: 'pages/login/ForgotPasswordPage'
    });
  }

  build() {
    Scroll() {
      Column() {
        // Logo 图片
        Image($r('app.media.logo'))
          .width(80)
          .height(80)
          .margin({ top: 40, bottom: 16 })
          .objectFit(ImageFit.Contain)

        // 标题区域
        Column() {
          Text('用户登录')
            .fontSize(40)
            .fontWeight(FontWeight.Bold)
            .fontColor('#333333')
            .margin({ bottom: 8 })

          Text('欢迎回到智慧医疗')
            .fontSize(14)
            .fontColor('#8a8a8a')
        }
        .width('100%')
        .margin({ top: 8, bottom: 60 })
        .alignItems(HorizontalAlign.Center)

        // 表单区域 - 下划线样式
        Column() {
          // 手机号输入框
          Column() {
            Row() {
              Text('手机号')
                .fontSize(14)
                .fontColor('#333333')
                .width(80)
                .fontWeight(500)

              TextInput({ placeholder: '请输入手机号', text: this.phone })
                .layoutWeight(1)
                .height(44)
                .backgroundColor('transparent')
                .border({ width: { bottom: 1 }, color: this.phoneFocused ? '#007AFF' : '#e0e0e0' })
                .padding({ left: 0, right: 0, bottom: 8 })
                .type(InputType.PhoneNumber)
                .maxLength(11)
                .fontSize(14)
                .placeholderColor('#bfbfbf')
                .onChange((value: string) => {
                  this.phone = value;
                  if (this.phoneError) this.phoneError = '';
                })
                .onFocus(() => {
                  this.phoneFocused = true;
                })
                .onBlur(() => {
                  this.phoneFocused = false;
                })
            }
            .width('100%')
            .alignItems(VerticalAlign.Center)

            if (this.phoneError) {
              Text(this.phoneError)
                .fontSize(12)
                .fontColor('#ff4d4f')
                .margin({ top: 4, left: 80 })
            }
          }
          .margin({ bottom: 24 })
          .width('100%')

          // 密码输入框
          Column() {
            Row() {
              Text('密码')
                .fontSize(14)
                .fontColor('#333333')
                .width(80)
                .fontWeight(500)

              TextInput({ placeholder: '请输入密码', text: this.password })
                .layoutWeight(1)
                .height(44)
                .backgroundColor('transparent')
                .border({ width: { bottom: 1 }, color: this.passwordFocused ? '#007AFF' : '#e0e0e0' })
                .padding({ left: 0, right: 0, bottom: 8 })
                .type(this.showPassword ? InputType.Normal : InputType.Password)
                .fontSize(14)
                .placeholderColor('#bfbfbf')
                .onChange((value: string) => {
                  this.password = value;
                  if (this.passwordError) this.passwordError = '';
                })
                .onFocus(() => {
                  this.passwordFocused = true;
                })
                .onBlur(() => {
                  this.passwordFocused = false;
                })
            }
            .width('100%')
            .alignItems(VerticalAlign.Center)

            Row() {
              Text(this.showPassword ? '隐藏密码' : '显示密码')
                .fontSize(12)
                .fontColor('#007AFF')
                .onClick(() => {
                  this.showPassword = !this.showPassword;
                })
            }
            .width('100%')
            .justifyContent(FlexAlign.End)
            .margin({ top: 4 })

            if (this.passwordError) {
              Text(this.passwordError)
                .fontSize(12)
                .fontColor('#ff4d4f')
                .margin({ top: 4, left: 80 })
            }
          }
          .margin({ bottom: 16 })
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
          .margin({ bottom: 40 })
        }
        .width('100%')
        .padding({ left: 24, right: 24 })

        // 登录按钮
        Button('立即登录')
          .width('85%')
          .height(48)
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .fontColor('#ffffff')
          .backgroundColor(this.isLoading ? '#d9d9d9' : '#007AFF')
          .borderRadius(24)
          .enabled(!this.isLoading)
          .onClick(() => this.login())

        if (this.isLoading) {
          LoadingProgress()
            .width(32)
            .height(32)
            .color('#007AFF')
            .margin({ top: 12 })
        }

        // 注册入口
        Row() {
          Text('还没有账号？')
            .fontSize(14)
            .fontColor('#8a8a8a')

          Text('立即注册')
            .fontSize(14)
            .fontColor('#007AFF')
            .fontWeight(FontWeight.Medium)
            .margin({ left: 8 })
            .onClick(() => this.goToRegister())
        }
        .margin({ top: 32, bottom: 50 })
      }
      .width('100%')
      .padding({ left: 16, right: 16 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#ffffff')
    .scrollable(ScrollDirection.Vertical)
    .scrollBar(BarState.Off)
  }
}