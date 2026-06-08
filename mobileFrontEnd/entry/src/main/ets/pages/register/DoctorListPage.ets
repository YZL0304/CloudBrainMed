// pages/register/DoctorListPage.ets
import router from '@ohos.router';
import { HttpUtil } from '../common/HttpUtil';
import promptAction from '@ohos.promptAction';

interface Doctor {
  doctorId: string;
  name: string;
  gender: number;
  position: string;
  goodAt: string;
  introduction: string;
  avatar: string;
  departmentId: string;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

@Entry
@Component
export struct DoctorListPage {
  @State doctors: Doctor[] = [];
  @State isLoading: boolean = true;
  @State deptName: string = '';
  private deptId: string = '';
  private patientId: string = '';
  private baseUrl: string = 'http://172.22.101.145:8004/patient-service';

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>;
    this.deptId = params?.['deptId'] || '';
    this.deptName = params?.['deptName'] || '';
    this.patientId = params?.['patientId'] || '';
    this.loadDoctors();
  }

  async loadDoctors() {
    this.isLoading = true;
    const context = getContext(this);
    try {
      const result = await HttpUtil.get<ApiResponse<Doctor[]>>(
        this.baseUrl + '/register/doctors/' + this.deptId,
        context
      );
      if (result.code === 200) {
        this.doctors = result.data;
      } else {
        promptAction.showToast({
          message: result.message || '加载医生列表失败',
          duration: 2000
        });
      }
    } catch (error) {
      console.error('加载医生列表失败：' + JSON.stringify(error));
      promptAction.showToast({
        message: '网络请求失败，请稍后重试',
        duration: 2000
      });
    } finally {
      this.isLoading = false;
    }
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text('←')
          .fontSize(24)
          .onClick(() => {
            router.back();
          })
        Text(this.deptName)
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .layoutWeight(1)
          .textAlign(TextAlign.Center)
        Blank().width(24)
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#FFFFFF')

      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .width(60)
            .height(60)
            .color('#007AFF')
          Text('加载中...')
            .fontSize(14)
            .fontColor('#999')
            .margin({ top: 16 })
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
      } else if (this.doctors.length === 0) {
        Column() {
          Text('暂无医生')
            .fontSize(16)
            .fontColor('#999999')
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
      } else {
        Scroll() {
          Column() {
            ForEach(this.doctors, (doctor: Doctor) => {
              Column() {
                Row() {
                  // 头像
                  Column() {
                    Text(doctor.avatar || '👨‍⚕️')
                      .fontSize(48)
                      .backgroundColor('#E3F2FD')
                      .width(60)
                      .height(60)
                      .borderRadius(30)
                      .textAlign(TextAlign.Center)
                      .lineHeight(60)
                  }

                  Column() {
                    Row() {
                      Text(doctor.name)
                        .fontSize(18)
                        .fontWeight(FontWeight.Bold)
                        .fontColor('#333333')
                      Text(doctor.position)
                        .fontSize(12)
                        .fontColor('#FF6B6B')
                        .backgroundColor('#FFF0F0')
                        .padding({ left: 8, right: 8, top: 2, bottom: 2 })
                        .borderRadius(4)
                        .margin({ left: 8 })
                    }
                    .margin({ bottom: 6 })

                    Text(doctor.goodAt ? (doctor.goodAt.length > 35 ? doctor.goodAt.substring(0, 35) + '...' : doctor.goodAt) : '暂无简介')
                      .fontSize(13)
                      .fontColor('#999999')
                      .maxLines(2)
                      .textOverflow({ overflow: TextOverflow.Ellipsis })
                  }
                  .alignItems(HorizontalAlign.Start)
                  .layoutWeight(1)
                  .margin({ left: 12 })
                }
                .width('100%')
                .padding(16)

                Button('预约挂号')
                  .width('100%')
                  .height(40)
                  .fontSize(14)
                  .fontColor('#FFFFFF')
                  .backgroundColor('#007AFF')
                  .borderRadius(20)
                  .onClick(() => {
                    router.pushUrl({
                      url: 'pages/register/ScheduleSelectPage',
                      params: {
                        doctorId: doctor.doctorId,
                        doctorName: doctor.name,
                        patientId: this.patientId
                      }
                    });
                  })
              }
              .width('100%')
              .backgroundColor('#FFFFFF')
              .borderRadius(16)
              .margin({ bottom: 12 })
            })
          }
          .width('100%')
          .padding(16)
        }
        .scrollable(ScrollDirection.Vertical)
        .scrollBar(BarState.Off)
        .layoutWeight(1)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}