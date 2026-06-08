// pages/RegisterPage.ets
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import { HttpUtil } from '../common/HttpUtil';

// 定义接口类型
interface RegisterParams {
  name: string;
  phone: string;
  idCard: string;
  password: string;
  confirmPassword: string;
}

interface RegisterData {
  patientId: string;
  name: string;
  phone: string;
  birthday: string;
  age: number;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

@Entry
@Component
struct RegisterPage {
  @State name: string = '';
  @State phone: string = '';
  @State idCard: string = '';
  @State password: string = '';
  @State confirmPassword: string = '';
  @State isLoading: boolean = false;
  @State agreeProtocol: boolean = false;

  @State nameError: string = '';
  @State phoneError: string = '';
  @State idCardError: string = '';
  @State passwordError: string = '';

  private baseUrl: string = 'http://172.22.101.145:8002/auth-service';

  // 简单的前端验证
  private validateForm(): boolean {
    let isValid = true;

    if (!this.name.trim()) {
      this.nameError = '请输入姓名';
      isValid = false;
    } else if (this.name.length < 2 || this.name.length > 20) {
      this.nameError = '姓名长度应在2-20字符之间';
      isValid = false;
    } else {
      this.nameError = '';
    }

    const phoneRegex = /^1[3-9]\d{9}$/;
    if (!this.phone.trim()) {
      this.phoneError = '请输入手机号';
      isValid = false;
    } else if (!phoneRegex.test(this.phone)) {
      this.phoneError = '请输入有效的手机号码';
      isValid = false;
    } else {
      this.phoneError = '';
    }

    const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/;
    if (!this.idCard.trim()) {
      this.idCardError = '请输入身份证号';
      isValid = false;
    } else if (!idCardRegex.test(this.idCard)) {
      this.idCardError = '请输入有效的身份证号码';
      isValid = false;
    } else {
      this.idCardError = '';
    }

    if (!this.password.trim()) {
      this.passwordError = '请输入密码';
      isValid = false;
    } else if (this.password.length < 6 || this.password.length > 20) {
      this.passwordError = '密码长度应为6-20位';
      isValid = false;
    } else if (this.password !== this.confirmPassword) {
      this.passwordError = '两次输入的密码不一致';
      isValid = false;
    } else {
      this.passwordError = '';
    }

    if (!this.agreeProtocol) {
      promptAction.showToast({
        message: '请同意用户协议和隐私政策',
        duration: 2000
      });
      isValid = false;
    }

    return isValid;
  }

  // 注册
  private async register(): Promise<void> {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;

    const context = getContext(this);

    const requestData: RegisterParams = {
      name: this.name,
      phone: this.phone,
      idCard: this.idCard,
      password: this.password,
      confirmPassword: this.confirmPassword
    };

    try {
      const result = await HttpUtil.post<ApiResponse<RegisterData>>(
        this.baseUrl + '/patient/register',
        requestData,
        context
      );

      if (result.code === 200) {
        promptAction.showToast({
          message: '注册成功！请登录',
          duration: 2000
        });

        setTimeout(() => {
          router.pushUrl({
            url: 'pages/login/LoginPage',
            params: {
              phone: this.phone,
              password: this.password
            }
          }).catch((err: Error) => {
            console.error('跳转失败：' + JSON.stringify(err));
          });
        }, 1500);
      } else {
        promptAction.showToast({
          message: result.message,
          duration: 2000
        });
      }
    } catch (error) {
      console.error('注册失败：' + JSON.stringify(error));
      promptAction.showToast({
        message: '注册失败，请稍后重试',
        duration: 2000
      });
    } finally {
      this.isLoading = false;
    }
  }

  // 跳转到登录页
  // 跳转到登录页并传递参数
  private goToLogin(): void {
    router.pushUrl({
      url: 'pages/login/LoginPage',
      params: {
        phone: this.phone,      // 传递手机号
        password: this.password  // 传递密码
      }
    }).catch((err: Error) => {
      console.error('跳转失败：' + JSON.stringify(err));
    });
  }

  // 获取焦点时的样式
  @State nameFocused: boolean = false;
  @State phoneFocused: boolean = false;
  @State idCardFocused: boolean = false;
  @State passwordFocused: boolean = false;
  @State confirmPasswordFocused: boolean = false;

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
          Text('用户注册')
            .fontSize(40)  // 字体从32加大到40
            .fontWeight(FontWeight.Bold)
            .fontColor('#333333')  // 改为纯黑色，不用渐变
            .margin({ bottom: 8 })

          Text('开启您的健康之旅')
            .fontSize(14)
            .fontColor('#8a8a8a')
        }
        .width('100%')
        .margin({ top: 8, bottom: 40 })
        .alignItems(HorizontalAlign.Center)

        // 表单区域 - 下划线样式
        Column() {
          // 姓名
          Column() {
            Row() {
              Text('姓名')
                .fontSize(14)
                .fontColor('#333333')
                .width(80)
                .fontWeight(500)

              TextInput({ placeholder: '请输入真实姓名', text: this.name })
                .layoutWeight(1)
                .height(44)
                .backgroundColor('transparent')
                .border({ width: { bottom: 1 }, color: this.nameFocused ? '#007AFF' : '#e0e0e0' })  // 焦点改为蓝色
                .padding({ left: 0, right: 0, bottom: 8 })
                .fontSize(14)
                .placeholderColor('#bfbfbf')
                .onChange((value: string) => {
                  this.name = value;
                  if (this.nameError) this.nameError = '';
                })
                .onFocus(() => {
                  this.nameFocused = true;
                })
                .onBlur(() => {
                  this.nameFocused = false;
                })
            }
            .width('100%')
            .alignItems(VerticalAlign.Center)

            if (this.nameError) {
              Text(this.nameError)
                .fontSize(12)
                .fontColor('#ff4d4f')
                .margin({ top: 4, left: 80 })
            }
          }
          .margin({ bottom: 24 })
          .width('100%')

          // 手机号
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
                .border({ width: { bottom: 1 }, color: this.phoneFocused ? '#007AFF' : '#e0e0e0' })  // 焦点改为蓝色
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

          // 身份证号
          Column() {
            Row() {
              Text('身份证号')
                .fontSize(14)
                .fontColor('#333333')
                .width(80)
                .fontWeight(500)

              TextInput({ placeholder: '请输入身份证号', text: this.idCard })
                .layoutWeight(1)
                .height(44)
                .backgroundColor('transparent')
                .border({ width: { bottom: 1 }, color: this.idCardFocused ? '#007AFF' : '#e0e0e0' })  // 焦点改为蓝色
                .padding({ left: 0, right: 0, bottom: 8 })
                .maxLength(18)
                .fontSize(14)
                .placeholderColor('#bfbfbf')
                .onChange((value: string) => {
                  this.idCard = value;
                  if (this.idCardError) this.idCardError = '';
                })
                .onFocus(() => {
                  this.idCardFocused = true;
                })
                .onBlur(() => {
                  this.idCardFocused = false;
                })
            }
            .width('100%')
            .alignItems(VerticalAlign.Center)

            if (this.idCardError) {
              Text(this.idCardError)
                .fontSize(12)
                .fontColor('#ff4d4f')
                .margin({ top: 4, left: 80 })
            }
          }
          .margin({ bottom: 24 })
          .width('100%')

          // 密码
          Column() {
            Row() {
              Text('密码')
                .fontSize(14)
                .fontColor('#333333')
                .width(80)
                .fontWeight(500)

              TextInput({ placeholder: '请输入密码（6-20位）', text: this.password })
                .layoutWeight(1)
                .height(44)
                .backgroundColor('transparent')
                .border({ width: { bottom: 1 }, color: this.passwordFocused ? '#007AFF' : '#e0e0e0' })  // 焦点改为蓝色
                .padding({ left: 0, right: 0, bottom: 8 })
                .type(InputType.Password)
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
          }
          .margin({ bottom: 24 })
          .width('100%')

          // 确认密码
          Column() {
            Row() {
              Text('确认密码')
                .fontSize(14)
                .fontColor('#333333')
                .width(80)
                .fontWeight(500)

              TextInput({ placeholder: '请再次输入密码', text: this.confirmPassword })
                .layoutWeight(1)
                .height(44)
                .backgroundColor('transparent')
                .border({ width: { bottom: 1 }, color: this.confirmPasswordFocused ? '#007AFF' : '#e0e0e0' })  // 焦点改为蓝色
                .padding({ left: 0, right: 0, bottom: 8 })
                .type(InputType.Password)
                .fontSize(14)
                .placeholderColor('#bfbfbf')
                .onChange((value: string) => {
                  this.confirmPassword = value;
                  if (this.passwordError) this.passwordError = '';
                })
                .onFocus(() => {
                  this.confirmPasswordFocused = true;
                })
                .onBlur(() => {
                  this.confirmPasswordFocused = false;
                })
            }
            .width('100%')
            .alignItems(VerticalAlign.Center)

            if (this.passwordError) {
              Text(this.passwordError)
                .fontSize(12)
                .fontColor('#ff4d4f')
                .margin({ top: 4, left: 80 })
            }
          }
          .margin({ bottom: 32 })
          .width('100%')
        }
        .width('100%')
        .padding({ left: 24, right: 24 })

        // 用户协议
        Row() {
          Checkbox({ name: 'agree' })
            .width(16)
            .height(16)
            .select(this.agreeProtocol)
            .onChange((value: boolean) => {
              this.agreeProtocol = value;
            })

          Text('我已阅读并同意')
            .fontSize(12)
            .fontColor('#8a8a8a')

          Text('《用户协议》')
            .fontSize(12)
            .fontColor('#007AFF')  // 改为蓝色

          Text('和')
            .fontSize(12)
            .fontColor('#8a8a8a')

          Text('《隐私政策》')
            .fontSize(12)
            .fontColor('#007AFF')  // 改为蓝色
        }
        .margin({ bottom: 40 })

        // 注册按钮 - 纯蓝色背景
        Button('立即注册')
          .width('85%')
          .height(48)
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .fontColor('#ffffff')
          .backgroundColor(this.isLoading ? '#d9d9d9' : '#007AFF')  // 改为纯蓝色
          .borderRadius(24)
          .enabled(!this.isLoading)
          .onClick(() => this.register())

        if (this.isLoading) {
          LoadingProgress()
            .width(32)
            .height(32)
            .color('#007AFF')  // 改为蓝色
            .margin({ top: 12 })
        }

        // 已有账号 - 跳转登录
        Row() {
          Text('已有账号？')
            .fontSize(14)
            .fontColor('#8a8a8a')

          Text('立即登录')
            .fontSize(14)
            .fontColor('#007AFF')  // 改为蓝色
            .fontWeight(FontWeight.Medium)
            .margin({ left: 8 })
            .onClick(() => this.goToLogin())
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