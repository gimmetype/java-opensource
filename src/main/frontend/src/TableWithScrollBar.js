// 필요한 모듈 가져오기
import React from 'react';

// 주요 컴포넌트 정의
const TableWithScrollBar = ({ data, onRowClick, onDragStart, onDragOver, onDrop, tableRef }) => {
    // 드래그 종료 시 호출될 함수
    const handleDragEnd = (e) => {
        console.log('Drag end:', e);
    };

    // 렌더링
    return (
        <div style={{ overflowY: 'scroll', height: '1100px' }} ref={tableRef}>
            <table style={{ width: '70%' }}>
                {/* 테이블 헤더 */}
                <thead>
                <tr>
                    <th>순번</th>
                    <th>학년</th>
                    <th>이수구분</th>
                    <th>과목코드</th>
                    <th>과목명</th>
                    <th>분반</th>
                    <th>학점</th>
                    <th>개설대학</th>
                    <th>개설학과</th>
                    <th>대표교수소속</th>
                    <th>담당교수</th>
                    <th>수업시간</th>
                </tr>
                </thead>
                {/* 테이블 내용 */}
                <tbody>
                {data.map((item) => (
                    <tr
                        key={item.id}
                        onClick={() => onRowClick(item.id)}
                        draggable
                        onDragStart={(e) => onDragStart(e, item.id)}
                        onDragOver={onDragOver}
                        onDrop={(e) => onDrop(e, item.id)}
                        onDragEnd={handleDragEnd}
                    >
                        <td>{item.id}</td>
                        <td>{item.grade}</td>
                        <td>{item.courseType}</td>
                        <td>{item.subjectCode}</td>
                        <td>{item.subjectName}</td>
                        <td>{item.division}</td>
                        <td>{item.credit}</td>
                        <td>{item.department}</td>
                        <td>{item.major}</td>
                        <td>{item.professorAffiliation}</td>
                        <td>{item.professor}</td>
                        <td>{item.classTime}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

// 컴포넌트 내보내기
export default TableWithScrollBar;