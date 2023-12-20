package com.classscheduler.Classscheduler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureDto {
    String INDEX, 학년, 이수구분, 과목코드, 과목명, 분반, 학점, 개설대학, 개설학과, 대표교수소속, 담당교수, 수업시간, ID;
    LectureDto() {}
    LectureDto(String[] args) {
        INDEX = args[0];
        학년 = args[1];
        이수구분 = args[2];
        과목코드 = args[3];
        과목명 = args[4];
        분반 = args[5];
        학점 = args[6];
        개설대학 = args[7];
        개설학과 = args[8];
        대표교수소속 = args[9];
        담당교수 = args[10];
        수업시간 = args[11];
        ID = args[0];
    }
}
