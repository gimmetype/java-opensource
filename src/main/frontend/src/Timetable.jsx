import React, {useEffect, useState} from 'react';
import axios from 'axios';

const colors = ["#fdd", "#ffd", "#dff", "#ddf", "#fdf", "#dfd"];
function Timetable() {
    // 과목 임시 객체
    const lectureObject = {'INDEX': '',
        'GRADE': '',
        '이수구분': '',
        'SUBJECT_ID': '',
        'SUBJECT_NAME': '',
        'CLASS': '',
        'CREDIT': '',
        'COLLEGE': '',
        'DEPARTMENT': '',
        '대표교수소속': '',
        'PROF': '',
        'INFO': ''
    };
    // 과목 객체 키 배열
    const lectureKeys = ['INDEX',
        'GRADE',
        '이수구분',
        'SUBJECT_ID',
        'SUBJECT_NAME',
        'CLASS',
        'CREDIT',
        'COLLEGE',
        'DEPARTMENT',
        '대표교수소속',
        'PROF',
        'INFO'];
    // 선택한 과목 인덱스
    const [lectures, setLectures] = useState([1,2,3]);
    // 아직 미사용
    const days = ["월", "화", "수", "목", "금", "토"];
    // 과목 데이터들
    const [lectureData, setLectureData] = useState([]);

    // 쿼리(요청) : 과목 인덱스들을 주면, lectureData 에 해당 과목 정보들을 담음
    useEffect(() => {
        const url = '/lectures?ids=' + lectures.join(',');
        axios.get(url)
            .then(response => {
                let data = response.data;
                let nextLectureData = []
                for (let r = 0; r < data.length; r++) {
                    for (let i = 0; i < lectureKeys.length; i++) {
                        lectureObject[lectureKeys[i]] = data[r][i];
                    }
                    nextLectureData.push({ ...lectureObject });
                }
                setLectureData(nextLectureData);
            })
            .catch(error => console.log(error))
    }, []);
    return (
        <div>
            백엔드에서 가져온 데이터입니다 :
            <ul>
                {lectureData.map(lecture => (
                    <li key={lecture.INDEX}>{lecture.SUBJECT_NAME}</li>
                ))}
            </ul>
        </div>
    );
}

export default Timetable;