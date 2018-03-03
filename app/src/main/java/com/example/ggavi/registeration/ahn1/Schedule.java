package com.example.ggavi.registeration.ahn1;

import android.content.Context;

import com.example.ggavi.registeration.R;
import com.example.ggavi.registeration.ahn1.AutoResizeTextView;

public class Schedule {

    // 본래 시간표였지만 값이 1개만 나오게 바꿔버렸다 (0교시?)
    private String monday[] = new String[1];


    // 생성자
    public Schedule() {

        // 모든 날짜에 코스 정보가 공백이 들어가게 한다.
        for(int i=0; i<1; i++)  // 값 1개만 들어가는데 for문을 왜 남겨둔걸까
        {
            monday[i] = "";
        }
    }


    // 스케쥴 정보를 담는 특정한 텍스트가 있을때,
    // 이 데이터를 파싱해서 코스 정보가 들어가는 배열에 넣어준다
    public void addSchedule(String scheduleText) {
        int temp;

        // 스케쥴 텍스트에서 '월'이라는 단어가 포함되어 있을 때,
        // 그 스케쥴 텍스트(월이라는 단어가 포함되어 있는 곳)의
        // 위치를 반환하고 temp 라는 값에 들어간다. (예) 월:[3][4][5], 화[4], [5]
        if ((temp = scheduleText.indexOf("월")) > -1) {
            temp += 2;
            int startPoint = temp;
            int endPoint = temp;

            // i는 스케쥴 텍스트의 길이가 작을 때까지 반복하거나
            // && 현재 위치에 해당하는 것이 :(콜론)이 아닐 때 반복
            for (int i = temp; i < scheduleText.length() && scheduleText.charAt(i) != ':'; i++) {
                // 현재 위치의 단어가 [일 경우, startPoint에 현재 위치를 넣어준다.
                if (scheduleText.charAt(i) == '[') {
                    startPoint = i;
                }

                // 만약 현재 위치의 단어가 ]일 경우
                if (scheduleText.charAt(i) == ']') {
                    // 여기까지가 숫자가 들어가는 공간
                    endPoint = i;

                    // 현재가 '월'요일이기 때문에, 현재 넣을 데이터에 '교시'에 해당하는 숫자 데이터를 그대로 넣을 수 있도록 함
                    monday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = "1";  // 들어갈 내용: "1"
                }

                // 정리: 즉 괄호와 괄호 사이의 숫자를 파싱해서,
                // 그 위치에 해당하는 '월'요일에 해당하는 교시에 수업이라는 데이터를 넣어줌
            }
        }
    }


    // 새롭게 추가하려는 추천길의 데이터가,
    // 현재 자리잡고 있는 스케쥴 데이터와 중복되지 않는지 체크해주는 함수

    // 본래 시간표였지만 그냥 데이터 1개만 들어가게 해놨다.
    // 그래서 모든 DB데이터가 그냥 월[1]로 설정되어 있다.

    public boolean validate(String scheduleText) {

        // 아무것도 없는 경우
        if(scheduleText.equals(""))
        {
             return true;
        }

        int temp;




        if((temp = scheduleText.indexOf("월")) > -1)
        {
            temp += 2;
            int startPoint = temp;
            int endPoint = temp;

            // i는 스케쥴 텍스트의 길이가 작을 때까지 반복하거나
            // && 현재 위치에 해당하는 것이 :(콜론)이 아닐 때 반복
            for(int i=temp; i<scheduleText.length() && scheduleText.charAt(i) !=':'; i++)
            {
                // 현재 위치의 단어가 [일 경우, startPoint에 현재 위치를 넣어준다.
                if(scheduleText.charAt(i) == '[')
                {
                    startPoint = i;
                }

                // 만약 현재 위치의 단어가 ]일 경우
                if(scheduleText.charAt(i) == ']')
                {
                    // 여기까지가 숫자가 들어가는 공간
                    endPoint = i;


                    // 만약 현재 값이 공백이 아니라면 (어떠한 데이터가 이미 있음)
                    if(!monday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))].equals(""))
                    {
                        // 해당 시간표는 중복으로 들어갈 수 없다!
                        return false;
                    }
                }

                // 정리: 즉 괄호와 괄호 사이의 숫자를 파싱해서,
                // 그 위치에 해당하는 '  '요일에 해당하는 교시에 수업이라는 데이터를 넣어줌
            }
        }

        // 여기까지 거쳤다면 중복되지 않아서 들어갈 수 있는 것
        return true;
    }

    // 스케쥴 정보를 담는 특정한 텍스트가 있을때,
    // 이 데이터를 파싱해서 코스 정보가 들어가는 배열에 넣어준다
    public void addSchedule(String scheduleText, String courseTitle, String courseProfessor) {

        int temp;

        // 스케쥴 텍스트에서 '월'이라는 단어가 포함되어 있을 때,
        // 그 스케쥴 텍스트(월이라는 단어가 포함되어 있는 곳)의
        // 위치를 반환하고 temp 라는 값에 들어간다. (예) 월:[3][4][5], 화[4], [5]
        if((temp = scheduleText.indexOf("월")) > -1)
        {
            temp += 2;
            int startPoint = temp;
            int endPoint = temp;

            // i는 스케쥴 텍스트의 길이가 작을 때까지 반복하거나
            // && 현재 위치에 해당하는 것이 :(콜론)이 아닐 때 반복
            for(int i=temp; i<scheduleText.length() && scheduleText.charAt(i) !=':'; i++)
            {
                // 현재 위치의 단어가 [일 경우, startPoint에 현재 위치를 넣어준다.
                if(scheduleText.charAt(i) == '[')
                {
                    startPoint = i;
                }

                // 만약 현재 위치의 단어가 ]일 경우
                if(scheduleText.charAt(i) == ']')
                {
                    // 여기까지가 숫자가 들어가는 공간
                    endPoint = i;

                    // 현재가 '월'요일이기 때문에, 현재 넣을 데이터에 '교시'에 해당하는 숫자 데이터를 그대로 넣을 수 있도록 함
                    // 마지막 =에 들어갈 내용: 해당 배열 안에 제목이 들어간다.
                    monday[Integer.parseInt(scheduleText.substring(startPoint + 1, endPoint))] = courseTitle;
                }

                // 정리: 즉 괄호와 괄호 사이의 숫자를 파싱해서,
                // 그 위치에 해당하는 '월'요일에 해당하는 교시에 수업이라는 데이터를 넣어줌
            }
        }
    }

    // (15) 텍스트 뷰에 해당 코스 목록들을 보여줄 수 있도록 세팅한 함수
    // 특정한 코스가 해당 시간에 이미 들어가 있다면 monday라는 TextView에 현재 코스를 출력할 수 있도록 해야 함

    // (16) TextView를 깃허브에서 긁어온 AutoResizeTextView로 모두 교체 (자동 글자크기 조절)
    // (30) 근데 지금은 데이터가 1개 밖에 안 들어가므로 딱히 필요없긴 한데 지우기 아까워서 남겨뒀다.
    public void setting(AutoResizeTextView[] monday, AutoResizeTextView[] tuesday, AutoResizeTextView[] wednesday, AutoResizeTextView[] thursday, AutoResizeTextView[] friday, Context context) {

        // 현재 TextView에 들어가는 내용 중에서 가장 긴 텍스트를 골라서 칸을 이쁘게 맞춰준다.
        int maxLength = 0;
        String maxString = "";
        for(int i=0; i<1; i++)     // 값이 1개만 들어가도록 수정했지만 for문 지우기 아까워서 남겨둠
        {
            // 만약 현재 텍스트의 길이가 가장 긴 텍스트의 길이보다 긴 경우
            // 근데 지금은 이 부분이 쓸모없다 (어차피 값이 1개만 들어가니까)
            if(this.monday[i].length() > maxLength)
            {
                // maxLength에 최고로 긴 문장 갱신
                maxLength = this.monday[i].length();

                // 현재 가장 긴 문자를 넣어줌
                maxString = this.monday[i];
            }
        }


        // 0부터 13교시까지 → 1개로 수정 (근데 이러면 반복문이 의미가 없는데)
        for(int i = 0; i<1; i++)
        {
            // 만약 현재 배열에 들어있는 값이 비어있지 않다면
            if(!this.monday[i].equals(""))
            {
                // 이런 식으로 하면 해당 배열에 있는 내용이 저 monday라는 텍스트 뷰에 값이 들어가게 된다.
                monday[i].setText(this.monday[i]);

                // DB에서 값을 불러왔는지 if문으로 체크 (급히 화면 전환하면 터져길래 넣음)
                if (context != null)
                {
                    // 해당 코스가 존재할 때 글자색상이 바뀜
                    monday[i].setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }
            }

            // (16) else문 추가
            else
            {
                // 아래 소스 지우고, 이제 위에서 새로 생성한 maxString을 넣어서 크기를 맞춘다.
                monday[i].setText(maxString);
            }
        }
    }

}