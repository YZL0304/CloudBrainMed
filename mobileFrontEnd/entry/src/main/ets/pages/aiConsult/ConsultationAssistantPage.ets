// pages/aiConsult/ConsultationAssistantPage.ets

interface MessageItem {
  type: string;
  content: string;
  time: string;
}

@Component
export struct ConsultationAssistantPage {
  patientId: string = '';
  userName: string = '';
  @State inputMessage: string = '';
  @State messages: MessageItem[] = [
    { type: 'bot', content: '您好！我是智能问诊助手，请问有什么可以帮助您的？', time: '10:30' }
  ];

  aboutToAppear() {
    console.log('问诊助手 - 患者ID: ' + this.patientId);
  }

  private async sendMessage() {
    if (!this.inputMessage.trim()) {
      return;
    }

    const userMessage: MessageItem = {
      type: 'user',
      content: this.inputMessage,
      time: this.getCurrentTime()
    };
    this.messages.push(userMessage);
    const question = this.inputMessage;
    this.inputMessage = '';

    setTimeout(() => {
      const botMessage: MessageItem = {
        type: 'bot',
        content: '感谢您的咨询，我正在分析您的情况...\n\n根据您的描述，建议您注意休息，多喝水。如果症状持续，请及时就医。',
        time: this.getCurrentTime()
      };
      this.messages.push(botMessage);
    }, 1000);
  }

  private getCurrentTime(): string {
    const now = new Date();
    return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
  }

  build() {
    Column() {
      Scroll() {
        Column() {
          ForEach(this.messages, (message: MessageItem) => {
            if (message.type === 'bot') {
              Row() {
                Column() {
                  Text('🤖 AI助手')
                    .fontSize(12)
                    .fontColor('#999')
                    .margin({ bottom: 4 })
                  Text(message.content)
                    .fontSize(14)
                    .fontColor('#333')
                    .backgroundColor('#F0F0F0')
                    .borderRadius(12)
                    .padding(12)
                    .maxLines(10)
                    .textOverflow({ overflow: TextOverflow.None })
                }
                .alignItems(HorizontalAlign.Start)
                .margin({ right: 40 })
              }
              .width('100%')
              .margin({ bottom: 12 })
            } else {
              Row() {
                Column() {
                  Text(message.time)
                    .fontSize(10)
                    .fontColor('#999')
                    .alignSelf(ItemAlign.End)
                    .margin({ bottom: 2 })
                  Text(message.content)
                    .fontSize(14)
                    .fontColor('#FFF')
                    .backgroundColor('#007AFF')
                    .borderRadius(12)
                    .padding(12)
                    .maxLines(10)
                    .textOverflow({ overflow: TextOverflow.None })
                }
                .alignItems(HorizontalAlign.End)
                .margin({ left: 40 })
              }
              .width('100%')
              .justifyContent(FlexAlign.End)
              .margin({ bottom: 12 })
            }
          })
        }
        .width('100%')
        .padding(16)
      }
      .layoutWeight(1)
      .backgroundColor('#F8F9FA')

      Row() {
        TextInput({ placeholder: '请输入您的症状或问题...', text: this.inputMessage })
          .layoutWeight(1)
          .height(40)
          .backgroundColor('#F0F0F0')
          .borderRadius(20)
          .padding({ left: 16, right: 16 })
          .onChange((value: string) => {
            this.inputMessage = value;
          })

        Button('发送')
          .height(40)
          .margin({ left: 12 })
          .backgroundColor('#007AFF')
          .borderRadius(20)
          .padding({ left: 20, right: 20 })
          .onClick(() => {
            this.sendMessage();
          })
      }
      .width('100%')
      .padding(12)
      .backgroundColor('#FFFFFF')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F8F9FA')
  }
}