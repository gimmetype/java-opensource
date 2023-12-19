import React, {useEffect, useState} from 'react';
import axios from 'axios';
import styles from './AppStyle.scss';

const colors = ["#fdd", "#ffd", "#dff", "#ddf", "#fdf", "#dfd", "#d8d", "#8d8"];

function Timetable() {
    let times = [];
    for (let i = 0; i < 14; i++) {
        const timeindex = String(i).padStart(2, '0');
        const hour = String(8 + i).padStart(2, '0');
        times.push(
            timeindex + '교시\n' + hour + ':00~' + hour + ':50'
        )
    }
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

    // 시간표 슬롯
    let table = [];
    {
        let temp = []
        for (let j = 0; j < 14; j++) {
            temp.push({'idx': 0, 'length': 0});
        }
        for (let i = 0; i < 6; i++) {
            table.push([...temp]);
        }
    }
    const [slots, setSlots] = useState(table);

    useEffect(() => {
        const url = '/gettimetable?ids=' + lectures.join(',');
        axios.get(url)
            .then(response => {
                let data = response.data;
                setSlots(data);
            })
    }, []);

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
                console.log(0);
                console.log(nextLectureData);
                console.log(1);
                console.log([...nextLectureData]);
                setLectureData([...nextLectureData]);
            })
            .catch(error => console.log(error))
    }, []);
    console.log(2);
    console.log(lectureData);
    return (
        <div className="timetable">
            <header>
                <ul>
                    <li>
                        <span>교시</span>
                    </li>
                    {days.map(day => (
                        <li>
                            <span> {day} </span>
                        </li>
                    ))}
                </ul>
            </header>
            <div className="wrap">
                <ul className="scheduleLabels">
                    {[0,1,2,3,4,5,6,7,8,9,10,11,12,13].map(time => (
                        <li>{times[time]}</li>
                    ))}
                </ul>
                {slots.map(slotline => (
                    <ul className="instance">
                        {slotline.map(slot => (
                            <li>
                                { slot.idx !== -1 && (
                                    <div className="inner" style={{
                                        height: `calc(${(slot.length) * 100}% + ${slot.length}px)`,
                                        backgroundColor: colors[slot.idx % colors.length]}}>
                                        { lectureData[slot.idx] !== undefined &&(
                                            <p>
                                                <strong> { lectureData[slot.idx].SUBJECT_NAME } </strong><br/>
                                                <strong> { lectureData[slot.idx].PROF } </strong>
                                            </p>
                                        )}
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>
                ))}
            </div>
        </div>
    );
}

export default Timetable;