// pages/register/DeptListPage.ets
import router from '@ohos.router';
import { HttpUtil } from '../common/HttpUtil';
import promptAction from '@ohos.promptAction';

interface Dept {
  deptId: string;
  deptName: string;
  roomId: string;
  maxCapacity: number;
  freeCapacity: number;
  status: number;
  createTime: string;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

@Entry
@Component
export struct DeptListPage {
  @State depts: Dept[] = [];
  @State isLoading: boolean = true;
  private patientId: string = '';
  private baseUrl: string = 'http://172.22.101.145:8004/patient-service';

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>;
    this.patientId = params?.['patientId'] || '';
    this.loadDepts();
  }

  async loadDepts() {
    this.isLoading = true;
    const context = getContext(this);
    try {
      const result = await HttpUtil.get<ApiResponse<Dept[]>>(
        this.baseUrl + '/register/depts',
        context
      );
      if (result.code === 200) {
        this.depts = result.data;
      } else {
        promptAction.showToast({
          message: result.message || '加载科室失败',
          duration: 2000
        });
      }
    } catch (error) {
      console.error('加载科室失败：' + JSON.stringify(error));
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
        Text('选择科室')
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
      } else {
        Scroll() {
          Column() {
            Grid() {
              ForEach(this.depts, (dept: Dept) => {
                GridItem() {
                  Column() {
                    Text('🏥')
                      .fontSize(40)
                      .margin({ bottom: 12 })
                    Text(dept.deptName)
                      .fontSize(14)
                      .fontColor('#333333')
                      .fontWeight(FontWeight.Medium)
                      .maxLines(2)
                      .textAlign(TextAlign.Center)
                    Text('剩余号源 ' + (dept.freeCapacity || 0))
                      .fontSize(11)
                      .fontColor('#999999')
                      .margin({ top: 8 })
                  }
                  .width('100%')
                  .padding(16)
                  .backgroundColor('#F8F9FA')
                  .borderRadius(16)
                  .onClick(() => {
                    router.pushUrl({
                      url: 'pages/register/DoctorListPage',
                      params: {
                        deptId: dept.deptId,
                        deptName: dept.deptName,
                        patientId: this.patientId
                      }
                    });
                  })
                }
              })
            }
            .columnsTemplate('1fr 1fr')
            .rowsGap(16)
            .columnsGap(16)
            .padding(16)
          }
          .width('100%')
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