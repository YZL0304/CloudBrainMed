// pages/profile/ProfilePage.ets
import router from '@ohos.router';

interface MenuItem {
  icon: string;
  title: string;
  route: string;
}

@Component
export struct ProfilePage {
  patientId: string = '';
  userName: string = '';
  userPhone: string = '';
  @State userAvatar: string = '👤';

  private menuItems: MenuItem[] = [
    { icon: '👨‍⚕️', title: '我的医生', route: 'pages/profile/MyDoctors' },
    { icon: '💳', title: '我的就诊卡', route: 'pages/profile/MyCards' },
    { icon: '🏥', title: '我的预约', route: 'pages/profile/MyAppointments' },
    { icon: '⚙️', title: '设置', route: 'pages/profile/Settings' }
  ];

  aboutToAppear() {
    console.log('个人中心 - 患者ID: ' + this.patientId + ', 用户名: ' + this.userName);
  }

  build() {
    // 直接使用 Column，不嵌套额外容器
    Column() {
      // Scroll 组件使用 .layoutWeight(1) 填充剩余空间
      Scroll() {
        Column() {
          // 个人信息卡片
          Column() {
            Text(this.userAvatar)
              .fontSize(60)
              .backgroundColor('#E3F2FD')
              .width(80)
              .height(80)
              .borderRadius(40)
              .textAlign(TextAlign.Center)
              .lineHeight(80)
              .margin({ bottom: 12 })

            Text(this.userName || '用户')
              .fontSize(20)
              .fontWeight(FontWeight.Bold)
              .fontColor('#333')
              .margin({ bottom: 4 })

            Text(this.userPhone || '')
              .fontSize(14)
              .fontColor('#666')
              .margin({ bottom: 8 })
          }
          .width('100%')
          .padding(24)
          .backgroundColor('#FFFFFF')
          .borderRadius(16)
          .margin({ top: 16, left: 16, right: 16 })

          // 统计数据卡片
          Row() {
            this.StatItem('0', '我的预约')
            this.StatItem('0', '进行中')
            this.StatItem('0', '已完成')
          }
          .width('100%')
          .padding(20)
          .backgroundColor('#FFFFFF')
          .borderRadius(16)
          .margin({ top: 12, left: 16, right: 16 })
          .justifyContent(FlexAlign.SpaceAround)

          // 功能菜单卡片
          Column() {
            ForEach(this.menuItems, (item: MenuItem) => {
              Row() {
                Text(item.icon)
                  .fontSize(24)
                  .margin({ right: 12 })

                Text(item.title)
                  .fontSize(16)
                  .fontColor('#333')
                  .layoutWeight(1)

                Text('>')
                  .fontSize(16)
                  .fontColor('#CCCCCC')
              }
              .width('100%')
              .height(50)
              .onClick(() => {
                router.pushUrl({ url: item.route, params: { patientId: this.patientId } });
              })

              Divider().margin({ left: 36 })
            })
          }
          .width('100%')
          .padding(16)
          .backgroundColor('#FFFFFF')
          .borderRadius(16)
          .margin({ top: 12, left: 16, right: 16, bottom: 16 })
        }
        .width('100%')
      }
      .scrollable(ScrollDirection.Vertical)
      .scrollBar(BarState.Off)
      .layoutWeight(1)  // 关键：让Scroll占据剩余空间，内容可以滚动
      .backgroundColor('#F5F5F5')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }

  @Builder
  StatItem(value: string, label: string) {
    Column() {
      Text(value)
        .fontSize(24)
        .fontWeight(FontWeight.Bold)
        .fontColor('#007AFF')
      Text(label)
        .fontSize(12)
        .fontColor('#666')
        .margin({ top: 4 })
    }
    .alignItems(HorizontalAlign.Center)
  }
}