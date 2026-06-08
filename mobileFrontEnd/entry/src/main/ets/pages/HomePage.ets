// pages/home/HomePage.ets
import router from '@ohos.router';

interface FunctionItem {
  name: string;
  icon: string;
  color: string;
  route: string;
}

interface NoticeItem {
  title: string;
  time: string;
}

@Component
export struct HomePage {
  patientId: string = '';
  userName: string = '';
  userPhone: string = '';
  @State greeting: string = '';
  @State currentTime: string = '';

  private functions: FunctionItem[] = [
    { name: '预约挂号', icon: '🏥', color: '#FF6B6B', route: 'pages/register/DeptListPage' },
    { name: '在线问诊', icon: '💬', color: '#4ECDC4', route: 'pages/aiConsult/AIConsultation' },
    { name: '检查报告', icon: '📊', color: '#45B7D1', route: 'pages/medical/MedicalReport' },
    { name: '电子病历', icon: '📋', color: '#96CEB4', route: 'pages/medical/MedicalRecord' },
    { name: '健康档案', icon: '❤️', color: '#FFEAA7', route: 'pages/profile/HealthArchive' },
    { name: '用药提醒', icon: '💊', color: '#DDA0DD', route: 'pages/medical/MedicineReminder' }
  ];

  private notices: NoticeItem[] = [
    { title: '📢 关于2026年端午节放假安排的通知', time: '2026-05-30' },
    { title: '🏥 智慧医疗系统升级公告', time: '2026-05-28' },
    { title: '🎉 新功能上线：AI智能问诊助手', time: '2026-05-25' }
  ];

  aboutToAppear() {
    this.updateGreeting();
    this.updateTime();
    console.log('HomePage - 患者ID: ' + this.patientId + ', 用户名: ' + this.userName);
  }

  private updateGreeting() {
    const hour = new Date().getHours();
    if (hour < 12) {
      this.greeting = '早上好';
    } else if (hour < 18) {
      this.greeting = '下午好';
    } else {
      this.greeting = '晚上好';
    }
  }

  private updateTime() {
    const now = new Date();
    this.currentTime = `${now.getMonth() + 1}月${now.getDate()}日 ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
  }

  build() {
    Scroll() {
      Column() {
        // 顶部头部
        Column() {
          Row() {
            Column() {
              Text(this.greeting)
                .fontSize(14)
                .fontColor('#FFFFFF')
                .opacity(0.9)
              Text(`${this.userName || '用户'}，欢迎回来`)
                .fontSize(22)
                .fontWeight(FontWeight.Bold)
                .fontColor('#FFFFFF')
                .margin({ top: 4 })
            }
            .alignItems(HorizontalAlign.Start)

            Blank()

            Stack() {
              Circle()
                .width(28)
                .height(28)
                .fill('#FFFFFF')
                .opacity(0.2)
              Text('🔔')
                .fontSize(16)
            }
            .margin({ right: 8 })
            .onClick(() => {
              router.pushUrl({ url: 'pages/Notifications' });
            })
          }
          .width('100%')
          .margin({ top: 40, bottom: 30 })
        }
        .width('100%')
        .padding({ left: 20, right: 20, bottom: 30 })
        .borderRadius({ bottomLeft: 20, bottomRight: 20 })
        .linearGradient({
          angle: 135,
          colors: [[0x007AFF, 0], [0x00C6FF, 1]]
        })

        // 快捷功能菜单 - 确保6个图标完整显示
        Column() {
          Text('快捷服务')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 16 })
            .alignSelf(ItemAlign.Start)

          Grid() {
            ForEach(this.functions, (item: FunctionItem) => {
              GridItem() {
                Column() {
                  Text(item.icon)
                    .fontSize(32)
                    .margin({ bottom: 8 })
                  Text(item.name)
                    .fontSize(14)
                    .fontColor('#333333')
                }
                .width('100%')
                .height('100%')
                .justifyContent(FlexAlign.Center)
                .alignItems(HorizontalAlign.Center)
                .onClick(() => {
                  router.pushUrl({ url: item.route, params: { patientId: this.patientId } });
                })
              }
            })
          }
          .columnsTemplate('1fr 1fr 1fr')
          .rowsTemplate('1fr 1fr')
          .rowsGap(20)
          .columnsGap(16)
          .height(160)
        }
        .width('100%')
        .padding(20)
        .backgroundColor('#FFFFFF')
        .borderRadius(16)
        .margin({ top: 20, left: 20, right: 20 })

        // 健康资讯
        Column() {
          Text('健康资讯')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 16 })
            .alignSelf(ItemAlign.Start)

          Column() {
            ForEach(this.notices, (notice: NoticeItem, index: number) => {
              Column() {
                Row() {
                  Column() {
                    Text(notice.title)
                      .fontSize(14)
                      .fontColor('#333333')
                      .margin({ bottom: 4 })
                    Text(notice.time)
                      .fontSize(12)
                      .fontColor('#999999')
                  }
                  .alignItems(HorizontalAlign.Start)
                  .layoutWeight(1)

                  Text('查看')
                    .fontSize(12)
                    .fontColor('#007AFF')
                    .margin({ left: 8 })
                    .onClick(() => {
                      router.pushUrl({ url: 'pages/NoticeDetail', params: { title: notice.title } });
                    })
                }
                .width('100%')

                if (index < this.notices.length - 1) {
                  Divider()
                    .margin({ top: 12, bottom: 12 })
                }
              }
            })
          }
          .width('100%')
        }
        .width('100%')
        .padding(20)
        .backgroundColor('#FFFFFF')
        .borderRadius(16)
        .margin({ top: 16, left: 20, right: 20, bottom: 20 })
      }
      .width('100%')
      .padding({ bottom: 20 })
    }
    .scrollable(ScrollDirection.Vertical)
    .scrollBar(BarState.Off)
    .backgroundColor('#F5F5F5')
    .width('100%')
    .height('100%')
  }
}