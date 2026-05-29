// pages/RegisterPage.ets
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import { HttpUtil } from '../common/HttpUtil';

// 定义接口类型
interface RegisterParams {
  name: string;
  gender: number;
  phone: string;
  idCard: string;
  address: string;
  password: string;
  confirmPassword: string;
}

interface RegisterData {
  patientId: string;
  name: string;
  phone: string;
  address: string;
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
  @State gender: number = 1;
  @State phone: string = '';
  @State idCard: string = '';
  @State address: string = '';
  @State password: string = '';
  @State confirmPassword: string = '';
  @State isLoading: boolean = false;
  @State agreeProtocol: boolean = false;

  @State nameError: string = '';
  @State phoneError: string = '';
  @State passwordError: string = '';

  private baseUrl: string = 'http://172.22.101.145:8004/api/patient';

  // 简单的前端验证
  private validateForm(): boolean {
    let isValid = true;

    if (!this.name.trim()) {
      this.nameError = '请输入姓名';
      isValid = false;
    } else {
      this.nameError = '';
    }

    if (!this.phone.trim()) {
      this.phoneError = '请输入手机号';
      isValid = false;
    } else {
      this.phoneError = '';
    }

    if (!this.password.trim()) {
      this.passwordError = '请输入密码';
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
      gender: this.gender,
      phone: this.phone,
      idCard: this.idCard,
      address: this.address,
      password: this.password,
      confirmPassword: this.confirmPassword
    };

    try {
      const result = await HttpUtil.post<ApiResponse<RegisterData>>(
        this.baseUrl + '/auth/register',
        requestData,
        context
      );

      if (result.code === 200) {
        promptAction.showToast({
          message: '注册成功！请登录',
          duration: 2000
        });

        setTimeout(() => {
          router.back();
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

  build() {
    Scroll() {
      Column() {
        Text('注册账号')
          .fontSize(28)
          .fontWeight(FontWeight.Bold)
          .fontColor('#333333')
          .margin({ top: 30, bottom: 30 })
          .width('100%')
          .textAlign(TextAlign.Start)

        // 姓名
        Column() {
          Text('姓名')
            .fontSize(14)
            .fontColor('#666666')
            .margin({ bottom: 8 })

          TextInput({ placeholder: '请输入真实姓名', text: this.name })
            .width('100%')
            .height(44)
            .backgroundColor('#F5F5F5')
            .borderRadius(8)
            .padding({ left: 16, right: 16 })
            .onChange((value: string) => {
              this.name = value;
              if (this.nameError) this.nameError = '';
            })

          if (this.nameError) {
            Text(this.nameError)
              .fontSize(12)
              .fontColor('#FF4444')
              .margin({ top: 4 })
          }
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 性别
        Column() {
          Text('性别')
            .fontSize(14)
            .fontColor('#666666')
            .margin({ bottom: 8 })

          Row() {
            Button('男')
              .width('48%')
              .height(44)
              .backgroundColor(this.gender === 1 ? '#007AFF' : '#F5F5F5')
              .fontColor(this.gender === 1 ? '#FFFFFF' : '#666666')
              .borderRadius(8)
              .onClick(() => {
                this.gender = 1;
              })

            Button('女')
              .width('48%')
              .height(44)
              .backgroundColor(this.gender === 2 ? '#007AFF' : '#F5F5F5')
              .fontColor(this.gender === 2 ? '#FFFFFF' : '#666666')
              .borderRadius(8)
              .onClick(() => {
                this.gender = 2;
              })
          }
          .width('100%')
          .justifyContent(FlexAlign.SpaceBetween)
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 手机号
        Column() {
          Text('手机号')
            .fontSize(14)
            .fontColor('#666666')
            .margin({ bottom: 8 })

          TextInput({ placeholder: '请输入手机号', text: this.phone })
            .width('100%')
            .height(44)
            .backgroundColor('#F5F5F5')
            .borderRadius(8)
            .padding({ left: 16, right: 16 })
            .type(InputType.PhoneNumber)
            .maxLength(11)
            .onChange((value: string) => {
              this.phone = value;
              if (this.phoneError) this.phoneError = '';
            })

          if (this.phoneError) {
            Text(this.phoneError)
              .fontSize(12)
              .fontColor('#FF4444')
              .margin({ top: 4 })
          }
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 身份证号
        Column() {
          Text('身份证号')
            .fontSize(14)
            .fontColor('#666666')
            .margin({ bottom: 8 })

          TextInput({ placeholder: '请输入身份证号', text: this.idCard })
            .width('100%')
            .height(44)
            .backgroundColor('#F5F5F5')
            .borderRadius(8)
            .padding({ left: 16, right: 16 })
            .maxLength(18)
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 地址
        Column() {
          Text('地址')
            .fontSize(14)
            .fontColor('#666666')
            .margin({ bottom: 8 })

          TextArea({ placeholder: '请输入详细地址', text: this.address })
            .width('100%')
            .height(80)
            .backgroundColor('#F5F5F5')
            .borderRadius(8)
            .padding({ left: 16, right: 16 })
            .maxLength(100)
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 密码
        Column() {
          Text('密码')
            .fontSize(14)
            .fontColor('#666666')
            .margin({ bottom: 8 })

          TextInput({ placeholder: '请输入密码（6-20位）', text: this.password })
            .width('100%')
            .height(44)
            .backgroundColor('#F5F5F5')
            .borderRadius(8)
            .padding({ left: 16, right: 16 })
            .type(InputType.Password)
            .onChange((value: string) => {
              this.password = value;
              if (this.passwordError) this.passwordError = '';
            })
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 确认密码
        Column() {
          Text('确认密码')
            .fontSize(14)
            .fontColor('#666666')
            .margin({ bottom: 8 })

          TextInput({ placeholder: '请再次输入密码', text: this.confirmPassword })
            .width('100%')
            .height(44)
            .backgroundColor('#F5F5F5')
            .borderRadius(8)
            .padding({ left: 16, right: 16 })
            .type(InputType.Password)
            .onChange((value: string) => {
              this.confirmPassword = value;
              if (this.passwordError) this.passwordError = '';
            })

          if (this.passwordError) {
            Text(this.passwordError)
              .fontSize(12)
              .fontColor('#FF4444')
              .margin({ top: 4 })
          }
        }
        .margin({ bottom: 20 })
        .width('100%')

        // 用户协议
        Row() {
          Checkbox({ name: 'agree' })
            .width(20)
            .height(20)
            .select(this.agreeProtocol)
            .onChange((value: boolean) => {
              this.agreeProtocol = value;
            })

          Text('我已阅读并同意')
            .fontSize(12)
            .fontColor('#666666')
            .margin({ left: 8 })

          Text('《用户协议》')
            .fontSize(12)
            .fontColor('#007AFF')

          Text('和')
            .fontSize(12)
            .fontColor('#666666')

          Text('《隐私政策》')
            .fontSize(12)
            .fontColor('#007AFF')
        }
        .margin({ top: 10, bottom: 30 })

        // 注册按钮
        Button('注 册')
          .width('100%')
          .height(48)
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .backgroundColor(this.isLoading ? '#CCCCCC' : '#007AFF')
          .borderRadius(24)
          .enabled(!this.isLoading)
          .onClick(() => this.register())

        if (this.isLoading) {
          LoadingProgress()
            .width(40)
            .height(40)
            .color('#007AFF')
            .margin({ top: 16 })
        }

        // 返回登录
        Row() {
          Text('已有账号？')
            .fontSize(14)
            .fontColor('#666666')

          Text('立即登录')
            .fontSize(14)
            .fontColor('#007AFF')
            .fontWeight(FontWeight.Medium)
            .margin({ left: 8 })
            .onClick(() => {
              router.back();
            })
        }
        .margin({ top: 30, bottom: 50 })
      }
      .padding({ left: 24, right: 24 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#FFFFFF')
    .scrollable(ScrollDirection.Vertical)
    .scrollBar(BarState.Auto)
  }
}