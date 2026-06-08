// pages/register/ScheduleSelectPage.ets
import router from '@ohos.router';
import { HttpUtil } from '../common/HttpUtil';
import promptAction from '@ohos.promptAction';

// 定义时间JSON对象接口
interface TimeJsonObject {
  startTime?: string;
  endTime?: string;
  timeDesc?: string;
}

interface DoctorSchedule {
  scheduleId: string;
  doctorId: string;
  doctorName: string;
  timeJson: string;
  maxNum: number;
  remainNum: number;
  status: number;
  price: number;
  room: string;
}

interface Registration {
  registerId: string;
  patientId: string;
  doctorId: string;
  name: string;
  gender: number;
  birthday: string;
  chiefComplaint: string;
  department: string;
  consultRoom: string;
  visitDate: string;
  consultTime: string;
  price: number;
  payStatus: string;
  createTime: string;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 定义提交请求的接口
interface SubmitRegisterRequest {
  patientId: string;
  doctorId: string;
  scheduleId: string;
  chiefComplaint: string;
  visitDate: string;
  consultTime: string;
}

@Entry
@Component
export struct ScheduleSelectPage {
  @State schedules: DoctorSchedule[] = [];
  @State isLoading: boolean = true;
  @State selectedScheduleId: string = '';
  @State chiefComplaint: string = '';
  private doctorId: string = '';
  private doctorName: string = '';
  private patientId: string = '';
  private baseUrl: string = 'http://172.22.101.145:8004/patient-service';

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>;
    this.doctorId = params?.['doctorId'] || '';
    this.doctorName = params?.['doctorName'] || '';
    this.patientId = params?.['patientId'] || '';
    this.loadSchedules();
  }

  async loadSchedules() {
    this.isLoading = true;
    const context = getContext(this);
    try {
      const result = await HttpUtil.get<ApiResponse<DoctorSchedule[]>>(
        this.baseUrl + '/register/schedules/' + this.doctorId,
        context
      );
      if (result.code === 200) {
        const allSchedules = result.data;
        const availableSchedules: DoctorSchedule[] = [];
        for (let i = 0; i < allSchedules.length; i++) {
          if (allSchedules[i].remainNum > 0 && allSchedules[i].status === 1) {
            availableSchedules.push(allSchedules[i]);
          }
        }
        this.schedules = availableSchedules;
      } else {
        promptAction.showToast({
          message: result.message || '加载排班失败',
          duration: 2000
        });
      }
    } catch (error) {
      console.error('加载排班失败：' + JSON.stringify(error));
      promptAction.showToast({
        message: '网络请求失败，请稍后重试',
        duration: 2000
      });
    } finally {
      this.isLoading = false;
    }
  }

  getCurrentDate(): string {
    const date: Date = new Date();
    const year: number = date.getFullYear();
    const month: number = date.getMonth() + 1;
    const day: number = date.getDate();
    const monthStr: string = month < 10 ? '0' + month : '' + month;
    const dayStr: string = day < 10 ? '0' + day : '' + day;
    return year + '-' + monthStr + '-' + dayStr;
  }

  parseTimeFromJson(timeJson: string): string {
    if (!timeJson || timeJson.length === 0) {
      return '09:00';
    }
    try {
      const obj: TimeJsonObject = JSON.parse(timeJson) as TimeJsonObject;
      if (obj.startTime && obj.startTime.length > 0) {
        return obj.startTime;
      }
      return '09:00';
    } catch (e) {
      console.error('解析时间JSON失败：' + JSON.stringify(e));
      return '09:00';
    }
  }

  parseTimeJsonForDisplay(timeJson: string): string {
    if (!timeJson || timeJson.length === 0) {
      return '全天';
    }
    try {
      const obj: TimeJsonObject = JSON.parse(timeJson) as TimeJsonObject;
      if (obj.timeDesc && obj.timeDesc.length > 0) {
        return obj.timeDesc;
      }
      if (obj.startTime && obj.startTime.length > 0) {
        let result: string = obj.startTime;
        if (obj.endTime && obj.endTime.length > 0) {
          result = result + '-' + obj.endTime;
        }
        return result;
      }
      return '全天';
    } catch (e) {
      console.error('解析时间JSON失败：' + JSON.stringify(e));
      return '全天';
    }
  }

  async submitRegister() {
    if (!this.selectedScheduleId) {
      promptAction.showToast({
        message: '请选择就诊时间',
        duration: 2000
      });
      return;
    }
    if (!this.chiefComplaint.trim()) {
      promptAction.showToast({
        message: '请填写主诉',
        duration: 2000
      });
      return;
    }

    // 获取选中的排班对象
    let selectedSchedule: DoctorSchedule | null = null;
    for (let i = 0; i < this.schedules.length; i++) {
      if (this.schedules[i].scheduleId === this.selectedScheduleId) {
        selectedSchedule = this.schedules[i];
        break;
      }
    }

    if (!selectedSchedule) {
      promptAction.showToast({
        message: '请选择有效的就诊时间',
        duration: 2000
      });
      return;
    }

    const visitDate: string = this.getCurrentDate();
    const consultTime: string = this.parseTimeFromJson(selectedSchedule.timeJson);

    // 使用明确的接口类型创建请求对象
    const requestData: SubmitRegisterRequest = {
      patientId: this.patientId,
      doctorId: this.doctorId,
      scheduleId: this.selectedScheduleId,
      chiefComplaint: this.chiefComplaint,
      visitDate: visitDate,
      consultTime: consultTime
    };

    const context = getContext(this);
    this.isLoading = true;

    try {
      const result = await HttpUtil.post<ApiResponse<Registration>>(
        this.baseUrl + '/register/submit',
        requestData,
        context
      );

      if (result.code === 200) {
        promptAction.showToast({
          message: '预约成功，请前往支付',
          duration: 2000
        });
        setTimeout(() => {
          router.pushUrl({
            url: 'pages/register/OrderSuccessPage',
            params: {
              registerId: result.data.registerId
            }
          });
        }, 1500);
      } else {
        promptAction.showToast({
          message: result.message || '预约失败',
          duration: 2000
        });
      }
    } catch (error) {
      console.error('提交挂号失败：' + JSON.stringify(error));
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
        Text('选择就诊时间')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .layoutWeight(1)
          .textAlign(TextAlign.Center)
        Blank().width(24)
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#FFFFFF')

      if (this.isLoading && this.schedules.length === 0) {
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
      } else {
        Scroll() {
          Column() {
            // 医生信息
            Column() {
              Text(this.doctorName)
                .fontSize(18)
                .fontWeight(FontWeight.Bold)
                .fontColor('#333333')
              Text('主任医师')
                .fontSize(14)
                .fontColor('#666666')
                .margin({ top: 4 })
            }
            .width('100%')
            .padding(16)
            .backgroundColor('#FFFFFF')
            .margin({ bottom: 8 })

            // 排班列表
            Column() {
              Text('请选择就诊时间')
                .fontSize(16)
                .fontWeight(FontWeight.Bold)
                .fontColor('#333333')
                .margin({ bottom: 16 })
                .alignSelf(ItemAlign.Start)

              ForEach(this.schedules, (schedule: DoctorSchedule, index: number) => {
                this.ScheduleItem(schedule)
              }, (schedule: DoctorSchedule, index: number) => schedule.scheduleId)
            }
            .width('100%')
            .padding(16)
            .backgroundColor('#FFFFFF')
            .margin({ bottom: 12 })

            // 主诉输入
            Column() {
              Text('主诉')
                .fontSize(16)
                .fontWeight(FontWeight.Bold)
                .fontColor('#333333')
                .margin({ bottom: 12 })
                .alignSelf(ItemAlign.Start)

              TextArea({ placeholder: '请简要描述您的症状（如：头痛、发热等）', text: this.chiefComplaint })
                .width('100%')
                .height(100)
                .backgroundColor('#F8F9FA')
                .borderRadius(8)
                .padding(12)
                .fontSize(14)
                .placeholderColor('#BFBFBF')
                .onChange((value: string) => {
                  this.chiefComplaint = value;
                })
            }
            .width('100%')
            .padding(16)
            .backgroundColor('#FFFFFF')
            .margin({ bottom: 80 })
          }
          .width('100%')
          .padding(16)
        }
        .scrollable(ScrollDirection.Vertical)
        .scrollBar(BarState.Off)
        .layoutWeight(1)

        // 底部按钮
        Button(this.isLoading ? '提交中...' : '确认预约')
          .width('90%')
          .height(48)
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .fontColor('#FFFFFF')
          .backgroundColor(this.isLoading ? '#D9D9D9' : '#007AFF')
          .borderRadius(24)
          .margin({ bottom: 20 })
          .position({ x: '5%', y: '92%' })
          .enabled(!this.isLoading)
          .onClick(() => {
            this.submitRegister();
          })
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }

  @Builder
  ScheduleItem(schedule: DoctorSchedule) {
    Row() {
      Radio({ value: schedule.scheduleId, group: 'scheduleGroup' })
        .onChange((isChecked: boolean) => {
          if (isChecked) {
            this.selectedScheduleId = schedule.scheduleId;
          }
        })

      Column() {
        Text('时段：' + this.parseTimeJsonForDisplay(schedule.timeJson))
          .fontSize(14)
          .fontColor('#333333')
        Text('诊室：' + schedule.room + ' | 剩余号源：' + schedule.remainNum)
          .fontSize(12)
          .fontColor('#999999')
          .margin({ top: 4 })
        Text('挂号费：¥' + schedule.price)
          .fontSize(14)
          .fontColor('#FF6B6B')
          .fontWeight(FontWeight.Bold)
          .margin({ top: 4 })
      }
      .alignItems(HorizontalAlign.Start)
      .margin({ left: 12 })
      .layoutWeight(1)
    }
    .width('100%')
    .padding(12)
    .border({ width: { bottom: 0.5 }, color: '#E5E5E5' })
  }
}