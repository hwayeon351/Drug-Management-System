# Drug Management Solution
# 독거 노인 복약 관리 솔루션
 정해진 시간에 약을 복용하도록 도와주는 Smart Pill Case(IoT 제품)와 복용 여부를 실시간으로 확인 할 수 있는 Application으로 구성되어 있다.<br/>


## 개발 동기
 고령사회에 들어서면서 독거노인의 비율은 전체의 14.2%를 차지하며, 한국 보건 사회 연구원의 ‘2017년도 노인실태조사’에 의하면 만성 질병이 있다고 응답 한 비율은 전체 노인의 89.5%이다. 이에 따라 정해진 시간에 맞춰 약을 챙겨 복용하는 것은 매우 중요하다.
 그러나 홀로 사는 노인은 복약이 원활하게 이루어지고 있는지의 여부를 실시간으로 확인하기 어렵다.<br/>
 
 따라서 독거 노인 복약 관리 서비스를 통해 이러한 문제를 해결하고자 한다.<br/>


## System Architecture  
<img src="./Images/System Architecture.PNG" width="600px" height="300px" title="img" alt="img"></img><br/>
<img src="./Images/System Configuration.PNG" width="700px" height="400px" title="img" alt="img"></img><br/>


## Smart Pill Case(IoT product)
### 사용 방법
1. 병원이나 약국에서 Smart Pill Case를 동작시키기 위해 어플리케이션의 Pill-case Setting을 통해 복약 횟수와 시간을 설정한다.
2. 1.의 설정 값이 Server를 통해 Smart Pill Case에 전달되어 설정된 시간에 맞추어 일정시간동안 알람이 울린다.
3. Pill Case 내부에 장착된 조도센서가 Pill Case가 열림을 인식하면 Pill Case 내부에 장착된 LED가 일정시간동안 켜지며, 열린 시각이 Server에 전송되어 복약 횟수가 카운팅된다.
4. 사용자뿐만 아니라 병원이나 생활 관리사, 보호자는 어플리케이션을 통해 Pill-case Monitoring을 할 수 있다.<br/>

### Results
<img src="./Images/Smart Pill Case.jpg" width="280px" height="350px" title="img" alt="img"></img><br/>

## 기대효과
1. 원활한 의약품 복용 관리
2. 올바른 시간에 의약품 복용 확률 증가
4. 의약품 복용 환경 개선
